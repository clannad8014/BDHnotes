package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.Sqls;
import com.clannad.menu.FTP.FileTool;
import com.clannad.menu.FTP.FileUtill;
import com.clannad.menu.FTP.NetWorkUtil;
import com.clannad.menu.models.RoomContent;
import com.clannad.menu.models.user;
import com.clannad.menu.weight.CircleImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnlineEditActivity extends AppCompatActivity {
    long exitTime=0;//退出变量

    String uid;
    int rid;
    String rtitle;
    String rtime;
    String rboss;
    String Master_path;
    int delPosition=-1;            //用来删除的一个变量，因为内部类要用
    ArrayList<RoomContent> roomContents;
    ArrayList<user> users;
    CircleImageView main_person;
    Toolbar online_toolbar;
    TextView online_textview;
    EditText online_edittext;
    Button online_member;
    Button online_addimg;
    Button online_loadimg;
    Button online_save;
    Button online_history;
    NavigationView online_left;
    NavigationView online_right;
    DrawerLayout online_drawerlayout;
    OnlineHistoryAdapter onlineHistoryAdapter;
    UserAdapter userAdapter;
    Timer timer;

    //左菜单
    NavigationView navigationView_left;
    View view_left;
    ListView listView_left;
    //右菜单
    NavigationView navigationView_right;
    View view_right;
    ListView listView_right;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    //插入图片的Activity的返回的code
    static final int IMAGE_CODE = 99;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x11:
                 //   String neirong=(String) msg.obj;
                    online_textview.setText((CharSequence) msg.obj);
                    //online_textview.setText(neirong);
                    //loadImg(neirong);
                    break;
                case 0x14:
                    users=(ArrayList<user>)msg.obj;
                  userAdapter=new UserAdapter(OnlineEditActivity.this, R.layout.online_left_cell, users);
                    listView_left.setAdapter(userAdapter);
                    setListViewHeightBasedOnChildren(listView_left);//显示多行
                    break;

                case 0x35:
                    String s1 = (String) msg.obj;
                    System.out.println(s1);
                    loadOnlineHistory();
                    Toast.makeText(OnlineEditActivity.this, s1, Toast.LENGTH_SHORT).show();
                    break;
                case 0x41: case 0x12:case 0x33:case 0x34:
                    String s = (String) msg.obj;
                    System.out.println(s);
                    Toast.makeText(OnlineEditActivity.this, s, Toast.LENGTH_SHORT).show();
                    break;
                case 0x40:
                    String ss = (String) msg.obj;
                    System.out.println(ss);
                    Toast.makeText(OnlineEditActivity.this, ss, Toast.LENGTH_SHORT).show();
                    online_edittext.setText("");
                    break;
                case 0x13:
                    roomContents=(ArrayList<RoomContent>) msg.obj;
                    onlineHistoryAdapter=new OnlineHistoryAdapter(OnlineEditActivity.this, R.layout.online_right_cell, roomContents);
                    listView_right.setAdapter(onlineHistoryAdapter);
                    setListViewHeightBasedOnChildren(listView_right);//显示多行
                    //点击事件
                    lvClick();
                    //长按事件
                    lvLongClick();
                    break;
                case 0x22:case 0x23:
                    //上传图片
                    String sss = (String) msg.obj;
                    System.out.println(sss);
                    f.deletePhotoWithPath("/storage/emulated/0/BDH.notes/upload");
                    Toast.makeText(OnlineEditActivity.this, sss, Toast.LENGTH_LONG).show();
                    break;
                case 0x24:case 0x25:
                    //上传图片
                    String s4 = (String) msg.obj;
                    System.out.println(s4);
                    initToolBar();
                    System.out.println("============0x25 加载成功00--  ");
                    //Toast.makeText(OnlineEditActivity.this, s4, Toast.LENGTH_LONG).show();
                    break;


            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_edit);
        Bundle bundle = getIntent().getExtras();
        uid= bundle.getString("uid");
        rid=bundle.getInt("rid");
        rtitle=bundle.getString("rtitle");
        rtime=bundle.getString("rtime");
        rboss=bundle.getString("rboss");
        Master_path=bundle.getString("Master_path");
        //初始化基本参数

        init();
        initMaster();
        initToolBar();
    }

    void initMaster(){
        Sqls db=new Sqls();
        Message message = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    user U =db.selRoomMaster(rid);
                    Master_path=U.getUphoto2()+U.getUphoto1();
                    File file1=new File(U.getUphoto2()+U.getUphoto1());

                    //判断图片是否存在
                    FileUtill file=new FileUtill();
                    if(!file1.exists()){
                        System.out.println("============不存在  下载图片--  ");
                        //如果不存在  下载图片
                        Boolean flag=file.aboutTakePhotoDown(U.getUphoto1(),U.getUphoto2());
                        if(flag){
                            System.out.println("============下载图片成功--  ");
                            message.what = 0x24;
                            message.obj="加载成功";
                        }

                    }else{
                        message.what = 0x25;
                        message.obj="加载成功";
                    }
                    System.out.println("============0x25 加载成功--  "+Master_path);


                } catch (SQLException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(message);
            }
        }).start();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //启动监测文本变化
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                String neirong = "";
                Message message = new Message();
                Sqls sqls = new Sqls();
                try {
                    ArrayList<RoomContent> roomContents = sqls.selAllRoomContent(rid);
                    for (RoomContent rc : roomContents) {
                        neirong += rc.getUid() + ":" + rc.getXcontent() + "      ";
                    }
                    //=========================********************************************************

                        String input =neirong;
                        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
                        Matcher m = p.matcher(input);
                        SpannableString spannable = new SpannableString(input);
                      //  Message message = handler.obtainMessage();

                        while(m.find()){
                            //Log.d("YYPT_RGX", m.group());
                            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
                            String s = m.group();
                            int start = m.start();
                            int end = m.end();

                            //path是去掉<img src=""/>的中间的图片路径
                            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();
                            String b = path.substring(path.lastIndexOf("/") + 1, path.length());
                            //--------------------------------------------------


                            File file1=new File(path);
                            //判断图片是否存在
                            if(!file1.exists()){
                                System.out.println("============不存在  下载图片--  ");
                                //如果不存在  下载图片
                                Boolean flag=f.aboutTakePhotoDown(b,"/storage/emulated/0/BDH.notes/content/");
                                if(flag){
                                    System.out.println("============下载图片成功--  ");
                                }else{
                                    System.out.println("============下载图片失败--  ");
                                }

                            }


                            System.out.println("------------------加载path："+path);
                            System.out.println("------------------加载name:："+b);


                            //利用spannableString和ImageSpan来替换掉这些图片
                            int width = ScreenUtils.getScreenWidth(OnlineEditActivity.this);
                            int height = ScreenUtils.getScreenHeight(OnlineEditActivity.this);

                            try {
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                                bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));
                                ImageSpan imageSpan = new ImageSpan(OnlineEditActivity.this, bitmap);
                                spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                      //  message.what = 0x25;
                        //message.obj=spannable;
                        System.out.println("=================加载中0...........");

                       // handler.sendMessage(message);
                        //content.append("\n");
                        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());

                    //============================*************************************************
                    message.what = 0x11;
                    message.obj=spannable;
                    //message.obj = neirong;
                } catch (SQLException e) {
                    message.what = 0x12;
                    message.obj = "查询内容失败";
                }
                handler.sendMessage(message);
            }
        }, 0, 5000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行


    }

    void init(){
        online_toolbar=findViewById(R.id.online_toolbar);
        online_textview=findViewById(R.id.online_textview);
        online_edittext=findViewById(R.id.online_edittext);
        online_member=findViewById(R.id.online_member);
        online_addimg=findViewById(R.id.online_addimg);
        online_loadimg=findViewById(R.id.online_loadimg);
        online_save=findViewById(R.id.online_save);
        online_history=findViewById(R.id.online_history);
        online_left=findViewById(R.id.online_left);
        online_right=findViewById(R.id.online_right);
        online_drawerlayout=findViewById(R.id.online_drawerlayout);
        main_person=findViewById(R.id.main_uphoto);
        //左
        navigationView_left=findViewById(R.id.online_left);
        view_left=navigationView_left.getHeaderView(0);
        listView_left=view_left.findViewById(R.id.lv_members);

        //右
        navigationView_right=findViewById(R.id.online_right);
        view_right=navigationView_right.getHeaderView(0);
        listView_right=view_right.findViewById(R.id.lv_online_history);
        //初始化控件各种事件
        initWidget();
        //初始化toolBar


    }
    void initWidget(){
        //按钮 成员
        online_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_drawerlayout.openDrawer(GravityCompat.START);
                loadMembers();
            }
        });

        //添加图片
        online_addimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用图库
                callGallery();
            }
        });

        //加载图片
        online_loadimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // loadImg();
            }
        });

        //保存
        online_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sqls sqls=new Sqls();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        RoomContent roomContent =new RoomContent();
                        roomContent.setRid(rid);
                        roomContent.setUid(uid);
                        roomContent.setXcontent(online_edittext.getText().toString());
                        Date date=new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateStr = simpleDateFormat.format(date);
                        roomContent.setXtime(dateStr);

                        try {
                            sqls.addOneRecord(roomContent);
                            message.what = 0x40;
                            message.obj ="内容保存成功" ;
                        } catch (SQLException e) {
                            e.printStackTrace();
                            message.what = 0x41;
                            System.out.println("记录保存失败");
                            message.obj ="记录保存失败" ;
                        }
                        handler.sendMessage(message);
                    }
                }).start();

            }
        });

        //历史
        online_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                online_drawerlayout.openDrawer(GravityCompat.END);
                loadOnlineHistory();

            }
        });

    }
    void initToolBar() {

        //设置显示的文字
        online_toolbar.setTitle("房间号:"+rid+"      房间名:"+rtitle+"      用户:  ");
        //Master_path
        Bitmap pic1= BitmapFactory.decodeFile(Master_path);
        main_person.setImageBitmap(pic1);
        //将toolBar设置为该界面的Bar
        setSupportActionBar(online_toolbar);
    }

    //region 调用图库
    private void callGallery(){

        int permission_WRITE = ActivityCompat.checkSelfPermission(OnlineEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(OnlineEditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(OnlineEditActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
        Intent getAlbum = new Intent(Intent.ACTION_PICK);
        getAlbum.setType("image/*");
        startActivityForResult(getAlbum,IMAGE_CODE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bm = null;
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = getContentResolver();
        if(requestCode == IMAGE_CODE){
            try{
                // 获得图片的uri
                Uri originalUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(resolver,originalUri);
                String[] proj = {MediaStore.Images.Media.DATA};
                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = managedQuery(originalUri,proj,null,null,null);
                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                String path = cursor.getString(column_index);
                String b = path.substring(path.lastIndexOf("/") + 1, path.length());
                //将目标图片放入目标目录
                SimpleDateFormat formatter = new SimpleDateFormat("HHmmss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                String path_name ="/storage/emulated/0/BDH.notes/content/"+rid+"_"+rtitle+formatter.format(curDate)+".jpg";
                copyFile(path,path_name);
                File file = new File(Environment.getExternalStorageDirectory(), "BDH.notes/upload/");
                if (!file.mkdirs()) {
                    System.out.println("========================文件创建失败"+file.mkdirs());
                }
                copyFile(path_name,"/storage/emulated/0/BDH.notes/upload/"+rid+"_"+rtitle+formatter.format(curDate)+".jpg");

                //上传下载图片
                upload(b);

                //显示图片
                insertImg(path_name);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(OnlineEditActivity.this,"图片插入失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void copyFile(String oldPath, String newPath) {
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);  //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];

                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                System.out.println("复制单个文件操作1111");
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错"+e);
            e.printStackTrace();

        }

    }
    FileUtill f=new FileUtill();
    //上传图片
    public void upload(String photoname){   ///storage/emulated/0/BDH.notes/upload/
        File filePhoto = new File(Environment.getExternalStorageDirectory(),"BDH.notes/upload");
        File[] photoAllfiles = filePhoto.listFiles();
        System.out.println("文件已存在！"+photoAllfiles);
        if (photoAllfiles!=null) {
            if (photoAllfiles.length == 0) {
                Toast.makeText(OnlineEditActivity.this, "没有图片要上传", Toast.LENGTH_SHORT).show();
            } else {
                for (final File photoFile : photoAllfiles) {
                    if (NetWorkUtil.isNetworkAvailable(OnlineEditActivity.this)) {

                        new Thread() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage();

                                boolean up_flag= f.aboutTakePhotoUp(photoFile);
                                if(up_flag){

                                    System.out.println("============上传图片成功--  "+photoFile.toString());
                                    message.what = 0x22;
                                    message.obj ="上传图片成功" ;

                                }else{
                                    message.what = 0x23;
                                    message.obj ="上传图片失败" ;
                                }
                                handler.sendMessage(message);
                            }
                        }.start();
                    } else {
                        Toast.makeText(OnlineEditActivity.this, "对不起，没有网络！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else{
            Toast.makeText(OnlineEditActivity.this, "没有图片要上传！", Toast.LENGTH_SHORT).show();
        }
    }
    //==========================================


    //region 插入图片
    private void insertImg(String path){
        //Log.e("插入图片", "insertImg:" + path);
        String photo_name=path.substring(path.lastIndexOf("/") + 1, path.length());
        String tagPath = "<img src=\""+path+"\"/>";//为图片路径加上<img>标签
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap != null){
            SpannableString ss = getBitmapMime(path, tagPath);
            insertPhotoToEditText(ss);
            online_edittext.append("\n");
            //Log.e("YYPT_Insert", content.getText().toString());

        }else{
            //Log.d("YYPT_Insert", "tagPath: "+tagPath);
            Toast.makeText(OnlineEditActivity.this,"插入失败，无读写存储权限，请到权限中心开启",Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region 将图片插入到EditText中
    private void insertPhotoToEditText(SpannableString ss){
        Editable et = online_edittext.getText();
        int start = online_edittext.getSelectionStart();
        et.insert(start,ss);
        online_edittext.setText(et);
        online_edittext.setSelection(start+ss.length());
        online_edittext.setFocusableInTouchMode(true);
        online_edittext.setFocusable(true);
    }
    //endregion

    //region 根据图片路径利用SpannableString和ImageSpan来加载图片
    private SpannableString getBitmapMime(String path,String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径

        int width = ScreenUtils.getScreenWidth(OnlineEditActivity.this);
        int height = ScreenUtils.getScreenHeight(OnlineEditActivity.this);

        Log.d("YYPT_IMG_SCREEN", "高度:"+height+",宽度:"+width);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Log.d("YYPT_IMG_IMG", "高度:"+bitmap.getHeight()+",宽度:"+bitmap.getWidth());
        bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));
        Log.d("YYPT_IMG_COMPRESS", "高度："+bitmap.getHeight()+",宽度:"+bitmap.getWidth());
        ImageSpan imageSpan = new ImageSpan(this, bitmap);
        ss.setSpan(imageSpan, 0, tagPath.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;


    }
    //endregion





    //region 初始化content内容，参考：
    //  http://blog.sina.com.cn/s/blog_766aa3810100u8tx.html#cmt_523FF91E-7F000001-B8CB053C-7FA-8A0
    //  https://segmentfault.com/q/1010000004268968
    //  http://www.jb51.net/article/102683.htm
  private void loadImg(String neirong){

      System.out.println("222=======");
    //  showText("加载中...");
      // LoadingDialog.getInstance(AddActivity.this).dismiss();
      new Thread(new Runnable() {
          @Override
          public void run() {
              String input =neirong;
              Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
              Matcher m = p.matcher(input);
              SpannableString spannable = new SpannableString(input);
              Message message = handler.obtainMessage();

              while(m.find()){
                  //Log.d("YYPT_RGX", m.group());
                  //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
                  String s = m.group();
                  int start = m.start();
                  int end = m.end();

                  //path是去掉<img src=""/>的中间的图片路径
                  String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();
                  String b = path.substring(path.lastIndexOf("/") + 1, path.length());
                  //--------------------------------------------------


                  File file1=new File(path);
                  //判断图片是否存在
                  if(!file1.exists()){
                      System.out.println("============不存在  下载图片--  ");
                      //如果不存在  下载图片
                      Boolean flag=f.aboutTakePhotoDown(b,"/storage/emulated/0/BDH.notes/content/");
                      if(flag){
                          System.out.println("============下载图片成功--  ");
                      }else{
                          System.out.println("============下载图片失败--  ");
                      }

                  }


                  System.out.println("------------------加载path："+path);
                  System.out.println("------------------加载name:："+b);


                  //利用spannableString和ImageSpan来替换掉这些图片
                  int width = ScreenUtils.getScreenWidth(OnlineEditActivity.this);
                  int height = ScreenUtils.getScreenHeight(OnlineEditActivity.this);

                  try {
                      BitmapFactory.Options options = new BitmapFactory.Options();
                      Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                      bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));
                      ImageSpan imageSpan = new ImageSpan(OnlineEditActivity.this, bitmap);
                      spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                  }catch (Exception e){
                      e.printStackTrace();
                  }
              }

              message.what = 0x25;
              message.obj=spannable;
              System.out.println("=================加载中0...........");

              handler.sendMessage(message);
              //content.append("\n");
              //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
          }


      }).start();
      /*    String input =;
        Pattern p = Pattern.compile("\\<img src=\".*?\"\\/>");
        Matcher m = p.matcher(input);
        SpannableString spannable = new SpannableString(input);
        while(m.find()){
            //Log.d("YYPT_RGX", m.group());
            //这里s保存的是整个式子，即<img src="xxx"/>，start和end保存的是下标
            String s = m.group();
            int start = m.start();
            int end = m.end();
            //path是去掉<img src=""/>的中间的图片路径
            String path = s.replaceAll("\\<img src=\"|\"\\/>","").trim();
            //Log.d("YYPT_AFTER", path);

            //利用spannableString和ImageSpan来替换掉这些图片
            int width = ScreenUtils.getScreenWidth(OnlineEditActivity.this);
            int height = ScreenUtils.getScreenHeight(OnlineEditActivity.this);

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));
                ImageSpan imageSpan = new ImageSpan(this, bitmap);
                spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        online_textview.setText(spannable);
        //content.append("\n");
        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
        */
    }
    //endregion

    //解决listview只显示一条数据的bug
    public void setListViewHeightBasedOnChildren(ListView listView) {
// 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
// listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
// 计算子项View 的宽高
            listItem.measure(0, 0);
// 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
// listView.getDividerHeight()获取子项间分隔符占用的高度
// params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    //房间成员列表加载
    void loadMembers(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<user> users=sqls.selAllMember(rid);
                    message.what =0x14;
                    message.obj =users ;
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x31;
                    message.obj ="查询过程出错" ;
                }
                handler.sendMessage(message);
            }
        }).start();



    }

    //加载列表
    void loadOnlineHistory(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    RoomContent rc=new RoomContent();
                    rc.setRid(rid);
                    rc.setUid(uid);
                    ArrayList<RoomContent> roomContents=sqls.selOneMemberRoomContent(rc);
                    message.what =0x13;
                    message.obj =roomContents ;
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x31;
                    message.obj ="查询过程出错" ;
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    //点击事件
   void lvClick()
    {
        listView_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                delPosition = position;
                RoomContent rr=roomContents.get(delPosition);
                final  EditText name_editText = new EditText(OnlineEditActivity.this);
                name_editText.setText(rr.getXcontent());
                name_editText.setFocusable(true);
                new AlertDialog.Builder(OnlineEditActivity.this).setTitle("请输入编辑的内容")
                        .setView(name_editText)
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                rr.setXcontent(name_editText.getText().toString());
                                Sqls sqls=new Sqls();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = handler.obtainMessage();
                                        try {
                                            sqls.updateOneOnline(rr);
                                            message.what = 0x35;
                                            message.obj ="编辑该历史成功" ;
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            message.what = 0x34;
                                            message.obj ="编辑该历史失败" ;
                                        }

                                        handler.sendMessage(message);
                                    }
                                }).start();



                            }
                        }).show();

            }
        });



    }


    //长按事件
    void lvLongClick(){
        listView_right.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                delPosition = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(OnlineEditActivity.this);
                builder.setIcon(R.drawable.flag4_2);
                builder.setTitle("当前版本号："+roomContents.get(delPosition).getXnum());
                builder.setMessage("是否要删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("请等待几秒钟加载列表");
                        //Toast.makeText(AddActivity.this, "请等待几秒钟加载历史列表", Toast.LENGTH_SHORT).show();
                        //确定删除
                        Sqls sqls=new Sqls();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage();
                                try {
                                    sqls.deleteOneOnline(roomContents.get(delPosition));
                                    message.what = 0x33;
                                    message.obj ="删除该历史成功" ;
                                    loadOnlineHistory();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    message.what = 0x34;
                                    message.obj ="删除该历史失败" ;
                                }

                                handler.sendMessage(message);
                            }
                        }).start();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //取消
                    }
                });
                builder.show();

                return true;
            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void exit(){
        if((System.currentTimeMillis()-exitTime)>2000){
            Toast.makeText(OnlineEditActivity.this, "再按一次返回主界面", Toast.LENGTH_SHORT).show();
            exitTime=System.currentTimeMillis();
            Log.i("exittime", ""+exitTime);
        }else {
            finish();
        }
    }



    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timer.purge();
    }
}