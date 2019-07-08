package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.Sqls;
import com.clannad.menu.FTP.FileUtill;
import com.clannad.menu.FTP.NetWorkUtil;
import com.clannad.menu.models.note_content;
import com.clannad.menu.models.user_note_list;
import com.clannad.menu.weight.CircleImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddActivity extends AppCompatActivity {
    int delPosition=-1;            //用来删除的一个变量，因为内部类要用
    String bid ; //笔记号
    String ttt ; //笔记标题
    String ctime ;//创建时间
    String xid; //编辑人
    String neirong; //初始内容
    String beforettt;//编辑后前一步的标题
    String beforeneirong;//编辑后前一步的内容
    ArrayList<note_content> note_contents; //历史记录列表
    HistoryAdapter historyAdapter;



    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    //插入图片的Activity的返回的code
    static final int IMAGE_CODE = 99;


    //内容对象
    Date date;      //点进来的时候的时间
    Boolean isChanged = false;      //判断内容是否被修改过


    //控件申明
    Toolbar toolbar;            //ToolBar
    ImageView pic;              //插入图片的imageView
    ImageView fresh;
    EditText content;           //内容
    ScrollView scrollView;      //整个view
    EditText title;             //标题
    DrawerLayout drawerLayout;   //整个页面

    NavigationView navigationView; //历史菜单页
    View view;  //拿到右滑菜单
    Button btn_history;//历史菜单的btn
    ListView lv_history;  //历史菜单的listview

    LinearLayout awindow; //查看某个历史那一块区域
    EditText content_history;//文本
    Button btn_allupdate; //全部替换
    Button btn_closewindow;//关闭该区域


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x22:case 0x23:
                    //上传图片
                    String ss = (String) msg.obj;
                    System.out.println(ss);
                    f.deletePhotoWithPath("/storage/emulated/0/BDH.notes/upload");
                    Toast.makeText(AddActivity.this, ss, Toast.LENGTH_LONG).show();
                    break;
                case 0x25:
                    //String con =msg;
                    System.out.println("=================加载中...........");
                    content.setText((CharSequence) msg.obj);
                    System.out.println("=================加载成功...........");
                    break;
                case 0x27:case 0x28:case 0x29:case 0x30:case 0x31: case 0x33: case 0x34:
                    String s = (String) msg.obj;
                    System.out.println(s);
                    Toast.makeText(AddActivity.this, s, Toast.LENGTH_LONG).show();
                    break;
                case 0x32:
                    note_contents= (ArrayList<note_content>) msg.obj;
                    historyAdapter=new HistoryAdapter(AddActivity.this,R.layout.history_cell,note_contents);
                  lv_history.setAdapter(historyAdapter);
                    setListViewHeightBasedOnChildren(lv_history);//显示多行
                    System.out.println("历史加载成功！！！！！！！");
                   //Toast.makeText(AddActivity.this,"历史加载成功！！！！！！！", Toast.LENGTH_SHORT).show();
                   //点击事件
                    lvHistoryClick();
                   //长按事件
                   lvHistoryLongClick();
                    break;


            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add);
        Bundle bundle = getIntent().getExtras();
        bid = bundle.getString("bid");
        ttt = bundle.getString("title");
        ctime = bundle.getString("ctime");
        xid = bundle.getString("xid");
        neirong=bundle.getString("neirong");
        beforettt=ttt;
        beforeneirong=neirong;
        //初始化基本参数
        init();

        //初始化控件各种事件
        initWidget();

    }
    //region 进行初始化
    private void init() {
        //region 绑定控件
        toolbar = findViewById(R.id.toolbar_edit);
        pic = findViewById(R.id.iv_edit_pic);
        fresh=findViewById(R.id.iv_edit_fresh);
        content = findViewById(R.id.et_edit_content);
        scrollView = findViewById(R.id.sv_edit_view);
        title = findViewById(R.id.et_edit_title);
        drawerLayout=findViewById(R.id.drawerlayout);

        navigationView=findViewById(R.id.nv_history);
        view=navigationView.getHeaderView(0);
        lv_history=view.findViewById(R.id.lv_history);
        btn_history=view.findViewById(R.id.btn_history);

        awindow=findViewById(R.id.awindow);
        content_history=findViewById(R.id.et_edit_content_history);
        btn_allupdate=findViewById(R.id.btn_allupdate);
        btn_closewindow=findViewById(R.id.btn_closewindow);



        //region 初始化内容对象并初始化值
        title.setText(ttt);
        initContent();
        //初始化toolBar
        initToolBar();

        //默认让内容获取焦点，但是并不弹出软键盘
        //content.setFocusable(true);
        //content.setFocusableInTouchMode(true);
        //content.requestFocus();
        //content.setSelection(content.getText().length());
       // AddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    //endregion


    //region 初始化控件的各种事件
    private void initWidget(){

        //region 插入图片的点击事件
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用图库
                callGallery();
            }
        });

        //endregion

        //region 刷新图片
        fresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刷新图片
                //callGallery();
                System.out.println(" 刷新图片");
                initContent();

            }
        });
        //region 整个Scrollview的点击事件

        scrollView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //通知父控件请勿拦截本控件touch事件
                view.getParent().requestDisallowInterceptTouchEvent(true);
                Log.d("YYPT", "click the scrollView");
                //点击整个页面都会让内容框获得焦点，且弹出软键盘
                content.setFocusable(true);
                content.setFocusableInTouchMode(true);
                content.requestFocus();
                content.setSelection(content.getText().length());
                //AddFlagActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                //显示或隐藏软键盘，如果已显示则隐藏，反之显示
                //参考网址： https://www.jianshu.com/p/dc9387417914
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);

            }
        });


        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                //scrollView.callOnClick();
                return false;
            }
        });

        scrollView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });


        //endregion

      //历史菜单点击 事件 执行右滑
       toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem menuItem) {
               if(menuItem.getItemId()==R.id.menu_history){
                   //***********************************测试
                   //左滑打开

                   drawerLayout.openDrawer(GravityCompat.END);
                   //*******************
               }

               return false;
           }
       });
        //endregion

        //region toolbar的返回键点击事件
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //endregion
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!beforettt.equals(title.getText().toString()))
                {
                    saveTitle();
                    beforettt=title.getText().toString();
                    System.out.println("保存完标题");
                }
                else
                {
                    System.out.println("标题未做任何修改");
                }
            }
        });

        //内容 焦点事件

        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 获得焦点 开始编辑
                    System.out.println("开始编辑内容");

                } else {
                    // 失去焦点  执行保存
                    // Toast.makeText(AddActivity.this, "保存编辑标题", Toast.LENGTH_LONG).show();
                    if(!beforeneirong.equals(content.getText().toString()))
                    {
                        saveNoteContent();
                        beforeneirong=content.getText().toString();
                        System.out.println("保存完内容");
                    }
                    else {
                        System.out.println("内容未做修改");
                    }
                }
            }

        });
        //查看历史点击事件
        /*
        //menu版本   目前不需要用 先保存防身
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(menuItem.getItemId()==R.id.sel_history)
                {

                    Sqls sqls=new Sqls();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Message message = handler.obtainMessage();
                            try {
                                ArrayList<note_content> note_contents=sqls.selAllNoteContent(bid);
                                    message.what = 0x32;
                                    message.obj =note_contents ;
                            } catch (SQLException e) {
                                e.printStackTrace();
                                message.what = 0x31;
                                message.obj ="查询过程出错" ;
                            }
                            handler.sendMessage(message);
                        }
                    }).start();
                }
                return false;
            }
        });*/

        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHistoryList();

            }
        });


        btn_allupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setText(content_history.getText());

            }
        });

        btn_closewindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                awindow.setVisibility(View.GONE);

            }
        });







    }




    //region 初始化toolBar
    private void initToolBar() {

        //设置显示的文字
        toolbar.setTitle("(っ•̀ω•́)っ✎⁾⁾  我爱学习");

        //将toolBar设置为该界面的Bar
        setSupportActionBar(toolbar);

        //设置返回键，要在serSupportAction之后
        toolbar.setNavigationIcon(R.drawable.back);
    }
    //endregion


    //region 设置toolbar的菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit,menu);
        return true;
    }
    //endregion

    //region 调用图库
    private void callGallery(){

        int permission_WRITE = ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }

        //调用系统图库
        //Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");  //相片类型
        //startActivityForResult(intent,1);

        Intent getAlbum = new Intent(Intent.ACTION_PICK);
        getAlbum.setType("image/*");
        startActivityForResult(getAlbum,IMAGE_CODE);


    }
    //endregion

    //************************
 /*
     * 重命名文件
     *
     * oldPath 原来的文件地址
     *  newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }


    /**
     * 复制单个文件
     *
     * @param oldPath String  原文件路径  如：c:/fqf.txt
     * @param newPath String  复制后路径  如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
//           int  bytesum  =  0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {  //文件存在时
                InputStream inStream = new FileInputStream(oldPath);  //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];

                while ((byteread = inStream.read(buffer)) != -1) {
//                   bytesum  +=  byteread;  //字节数  文件大小
//                   System.out.println(bytesum);
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
                Toast.makeText(AddActivity.this, "没有图片要上传", Toast.LENGTH_SHORT).show();
            } else {
                for (final File photoFile : photoAllfiles) {
                    if (NetWorkUtil.isNetworkAvailable(AddActivity.this)) {

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
                        Toast.makeText(AddActivity.this, "对不起，没有网络！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else{
            Toast.makeText(AddActivity.this, "没有图片要上传！", Toast.LENGTH_SHORT).show();
        }
    }
    //*************************
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
               String path_name ="/storage/emulated/0/BDH.notes/content/"+xid+"_"+ttt+formatter.format(curDate)+".jpg";
                copyFile(path,path_name);
                File file = new File(Environment.getExternalStorageDirectory(), "BDH.notes/upload/");
                if (!file.mkdirs()) {
                    System.out.println("========================文件创建失败"+file.mkdirs());
                }
                copyFile(path_name,"/storage/emulated/0/BDH.notes/upload/"+xid+"_"+ttt+formatter.format(curDate)+".jpg");

                //上传下载图片
                upload(b);

                //显示图片
                insertImg(path_name);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(AddActivity.this,"图片插入失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //region 插入图片
    private void insertImg(String path){
        //Log.e("插入图片", "insertImg:" + path);
        Message message = handler.obtainMessage();
        String tagPath = "<img src=\""+path+"\"/>";//为图片路径加上<img>标签
        String photo_name=path.substring(path.lastIndexOf("/") + 1, path.length());
        System.out.println("显示图片成功==="+photo_name);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if(bitmap != null){
            SpannableString ss = getBitmapMime(path, tagPath);
            insertPhotoToEditText(ss);
            content.append("\n");
            //Log.e("YYPT_Insert", content.getText().toString());


        }else{
            //Log.d("YYPT_Insert", "tagPath: "+tagPath);
            Toast.makeText(AddActivity.this,"插入失败，无读写存储权限，请到权限中心开启",Toast.LENGTH_LONG).show();
        }
        //handler.sendMessage(message);
    }
    //endregion

    //region 将图片插入到EditText中
    private void insertPhotoToEditText(SpannableString ss){
        Editable et = content.getText();
        int start = content.getSelectionStart();
        et.insert(start,ss);
        content.setText(et);
        content.setSelection(start+ss.length());
        content.setFocusableInTouchMode(true);
        content.setFocusable(true);
    }
    //endregion

    //region 根据图片路径利用SpannableString和ImageSpan来加载图片
    private SpannableString getBitmapMime(String path,String tagPath) {
        SpannableString ss = new SpannableString(tagPath);//这里使用加了<img>标签的图片路径

        int width = ScreenUtils.getScreenWidth(AddActivity.this);
        int height = ScreenUtils.getScreenHeight(AddActivity.this);

        Log.d("YYPT_IMG_SCREEN", "高度:"+height+",宽度:"+width);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        Log.d("YYPT_IMG_IMG", "高度:"+bitmap.getHeight()+",宽度:"+bitmap.getWidth());
        bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));

        //Bitmap bitmap = ImageUtils.getSmallBitmap(path,600,480);


        /*
        //高:754，宽1008
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(path,options);
        */
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
    private void initContent(){

        // LoadingDialog.getInstance(AddActivity.this).show();
        System.out.println("222=======");
        showText("加载中...");
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
                  //  handler.sendMessage(message);
  //--------------------------------------------------

                        System.out.println("------------------加载path："+path);
                        System.out.println("------------------加载name:："+b);
                        //Log.d("YYPT_AFTER", path);

                        //利用spannableString和ImageSpan来替换掉这些图片
                        int width = ScreenUtils.getScreenWidth(AddActivity.this);
                        int height = ScreenUtils.getScreenHeight(AddActivity.this);

                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                            bitmap = ImageUtils.zoomImage(bitmap,(width-32)*0.8,bitmap.getHeight()/(bitmap.getWidth()/((width-32)*0.8)));
                            ImageSpan imageSpan = new ImageSpan(AddActivity.this, bitmap);
                            spannable.setSpan(imageSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    //content.setText(spannable);

                    message.what = 0x25;
                    message.obj=spannable;
                    System.out.println("=================加载中0...........");

                    handler.sendMessage(message);
                    //content.append("\n");
                    //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
                }


            }).start();

    }
    //endregion


    //region 生命周期结束后自动保存
    @Override
    protected void onStop() {
        super.onStop();
        //保存
        if(!beforettt.equals(title.getText().toString()))
        {
            saveTitle();
            beforettt=title.getText().toString();
            System.out.println("保存完标题(关闭触发)");
        }
        else
        {
            System.out.println("标题未做任何修改");
        }

        if(!beforeneirong.equals(content.getText().toString()))
        {
            saveNoteContent();
            beforeneirong=content.getText().toString();
            System.out.println("保存完内容(关闭触发)");
        }
        else {
            System.out.println("内容未做修改");
        }



    }
    //endregion

    void saveTitle(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                user_note_list unl=new user_note_list();
                unl.setTitle(title.getText().toString());
                unl.setBid(bid);
                try {
                    sqls.updateNote(unl);
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x27;
                    message.obj ="修改标题失败" ;
                }
                handler.sendMessage(message);
            }
        }).start();
    }

    //内容保存函数
    void saveNoteContent(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                note_content nc=new note_content();
                nc.setBid(bid);
                nc.setXcontent(content.getText().toString());
                Date date=new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateStr = format.format(date);
                nc.setXtime(dateStr);
                nc.setXid(xid);
               try {
                    sqls.addOneNoteContent(nc);
                   // message.what = 0x29;
                   // message.obj ="内容保存成功" ;
                   System.out.println("内容保存成功(焦点)");
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x30;
                   System.out.println("内容保存失败");
                    message.obj ="内容保存失败" ;
                }
                handler.sendMessage(message);
            }
        }).start();

    }

    public void showText(String text){
        Toast toast=Toast.makeText(getApplicationContext(), "  "+text,Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);

        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(messageTextView.getTextSize()/2);
        //创建图片视图对象
        CircleImageView imageView= new CircleImageView(getApplicationContext());
        //设置图片
        // imageView.setImageResource(R.drawable.miku1);
///storage/emulated/0/BDH.notes/
        Bitmap pic1= BitmapFactory.decodeFile("storage/emulated/0/BDH.notes/miku.jpg");
        imageView.setImageBitmap(pic1);

        //获得toast的布局
        LinearLayout toastView = (LinearLayout) toast.getView();

        //设置此布局为横向的
        //toastView.setOrientation(LinearLayout.HORIZONTAL);
        toastView.setOrientation(LinearLayout.VERTICAL);
        //将ImageView在加入到此布局中的第一个位置
        toastView.addView(imageView, 0);
        toast.show();
    }

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
    //加载历史列表
    void loadHistoryList(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<note_content> note_contents=sqls.selAllNoteContent(bid);
                    message.what = 0x32;
                    message.obj =note_contents ;
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x31;
                    message.obj ="查询过程出错" ;
                }
                handler.sendMessage(message);
            }
        }).start();
    }


    //历史列表的点击事件
    void lvHistoryClick(){
        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                note_content nc=note_contents.get(position);
                awindow.setVisibility(View.VISIBLE);
                content_history.setText(nc.getXcontent());
                Toast.makeText(AddActivity.this, "已加载", Toast.LENGTH_SHORT).show();
            }
        });

    }
    //历史列表的长按事件
    void lvHistoryLongClick(){
        lv_history.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                delPosition = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                builder.setIcon(R.drawable.flag4_2);
                builder.setTitle("当前版本号："+note_contents.get(delPosition).getXhnum());
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
                                   sqls.deleteOneNoteHistory(note_contents.get(delPosition));
                                    message.what = 0x33;
                                    message.obj ="删除该历史成功" ;
                                    loadHistoryList();
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





}
