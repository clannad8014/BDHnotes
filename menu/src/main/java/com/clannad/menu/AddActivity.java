package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.Sqls;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.clannad.menu.models.*;

public class AddActivity extends AppCompatActivity {
    String bid ;
    String ttt ;
    String ctime ;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    //插入图片的Activity的返回的code
    static final int IMAGE_CODE = 99;

    //数据库帮助类
   // DataBaseUtil dbUtil;

    //内容对象
   // Flag flag;      //传进来的flag
    Date date;      //点进来的时候的时间
    Boolean isChanged = false;      //判断内容是否被修改过


    //控件申明
    Toolbar toolbar;            //ToolBar
    ImageView pic;              //插入图片的imageView
    EditText content;           //内容
    ScrollView scrollView;      //整个view
    EditText title;             //标题
    Dialog dialog;              //底部弹窗
    View hrView;                //最下方菜单栏的那个横线
    View bottomMenu;            //最下方菜单栏


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x27:
                    String s = (String) msg.obj;
                    Toast.makeText(AddActivity.this, s, Toast.LENGTH_LONG).show();
                    break;
               // case 0x28:
                 //    msg.obj;

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
        content = findViewById(R.id.et_edit_content);
        scrollView = findViewById(R.id.sv_edit_view);
        title = findViewById(R.id.et_edit_title);
        hrView = findViewById(R.id.view_edit_1);
        bottomMenu = findViewById(R.id.rl_edit_bottom);



        //region 初始化内容对象并初始化值
        title.setText(ttt);

        initContent();

        //初始化toolBar
        initToolBar();

        //默认让内容获取焦点，但是并不弹出软键盘
        content.setFocusable(true);
        content.setFocusableInTouchMode(true);
        content.requestFocus();
        content.setSelection(content.getText().length());
        AddActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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

        //region toolbar的菜单的点击事件——即保存按钮的点击事件**************************************先不设置功能
       /* toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menu_save){
                    if(flag.getId() == -1){
                        //这是新建的
                        if(isChanged){
                            addFlag();
                            isChanged = false;
                        }

                    }else{
                        //这个flag是进行修改的
                        if(isChanged){
                            //只有当进行了修改了才更新数据库
                            editFlag();
                            isChanged = false;
                        }

                    }
                }
                return true;
            }
        });*/
        //endregion

        //region 监听内容、标题、状态的变化

        //region 监听title
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("YYPT_TITLE_CHANGE", "onTextChanged: ");
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion

        //region 监听content
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("YYPT_CONTENT_CHANGE", "onTextChanged: ");
                isChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //endregion

        //状态的监听会在状态改变的时候设置

        //endregion

        //region toolbar的返回键点击事件
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //自动保存
               /* if(flag.getId() == -1){
                    //这是新建的
                    if(isChanged){
                        addFlag();
                    }

                }else{
                    //这个flag是进行修改的
                    if(isChanged){
                        //只有当进行了修改了才更新数据库
                        editFlag();
                    }

                }*/
                finish();
            }
        });
        //endregion


        //标题栏  光标的焦点事件
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // 获得焦点 开始编辑
                    Toast.makeText(AddActivity.this, "开始编辑标题", Toast.LENGTH_LONG).show();

                } else {
                    // 失去焦点  执行保存
                   // Toast.makeText(AddActivity.this, "保存编辑标题", Toast.LENGTH_LONG).show();
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
            }
        });

        //内容改变
       content.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
               //text  输入框中改变前的字符串信息
               //start 输入框中改变前的字符串的起始位置
               //count 输入框中改变前后的字符串改变数量一般为0
               //after 输入框中改变后的字符串与起始位置的偏移量


           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
               //text  输入框中改变后的字符串信息
               //start 输入框中改变后的字符串的起始位置
               //before 输入框中改变前的字符串的位置 默认为0
               //count 输入框中改变后的一共输入字符串的数量
               System.out.println(start+"***"+before+"*****"+count);
               int xxxx=getCurrentCursorLine(content);
               System.out.println(xxxx+"+++++++++++++++++++");
               System.out.println(content.getText());


           }

           @Override
           public void afterTextChanged(Editable s) {
               //edit  输入结束呈现在输入框中的信息

           }
       });









    }




    //region 初始化toolBar
    private void initToolBar() {

        //设置显示的文字
        date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:MM");
        toolbar.setTitle(sdf.format(date));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //参考网址：http://blog.csdn.net/abc__d/article/details/51790806

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
                //Log.e("insertIMG", "onActivityResult: ");
                insertImg(path);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(AddActivity.this,"图片插入失败",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //region 插入图片
    private void insertImg(String path){
        //Log.e("插入图片", "insertImg:" + path);
        String tagPath = "<img src=\""+path+"\"/>";//为图片路径加上<img>标签
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


    //region 新建flag，并将id赋给这个flag
   /* private void addFlag(){

        //String s = Html.fromHtml(content.getText());
        //Log.d("YYPT_ADD",);
        try {
            Date thisDate = new Date();
            SQLiteDatabase db = dbUtil.getWritableDatabase();
            String sql = "insert into flag(state,color,title,content,date) " +
                    "values('" + state.getText().toString() + "'," +
                    "'" + flag.getColor() + "'," +
                    "'" + title.getText().toString() + "'," +
                    "'" + content.getText().toString() + "'," +
                    "'" + thisDate + "')";
            Log.d("YYPT", sql);
            db.execSQL(sql);
            //Toast.makeText(AddFlagActivity.this,"保存成功",Toast.LENGTH_SHORT).show();

            //保存后必须将这个flag的Id赋给它，不然会一直新建
            setFlagId();

        }catch (Exception e){
            e.printStackTrace();
        }

    }*/
    //endregion

    //region 编辑flag
    /*
    private void editFlag(){
        int id = flag.getId();
        try {
            Date thisDate = new Date();
            SQLiteDatabase db = dbUtil.getWritableDatabase();
            String sql = "update flag set state='"+state.getText().toString().trim()+"'," +
                    "color='"+flag.getColor()+"'," +
                    "title='"+title.getText().toString()+"'," +
                    "content='"+content.getText().toString()+"'," +
                    "date='"+thisDate+"' where id="+id+"";
            Log.d("YYPT", sql);
            db.execSQL(sql);
            //Toast.makeText(AddFlagActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
    //endregion


    //region 初始化content内容，参考：
    //  http://blog.sina.com.cn/s/blog_766aa3810100u8tx.html#cmt_523FF91E-7F000001-B8CB053C-7FA-8A0
    //  https://segmentfault.com/q/1010000004268968
    //  http://www.jb51.net/article/102683.htm
    private void initContent(){
        String input ="";
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
            int width = ScreenUtils.getScreenWidth(AddActivity.this);
            int height = ScreenUtils.getScreenHeight(AddActivity.this);

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
        content.setText(spannable);
        //content.append("\n");
        //Log.d("YYPT_RGX_SUCCESS",content.getText().toString());
    }
    //endregion


    //region 生命周期结束后自动保存
   /* @Override
    protected void onStop() {
        super.onStop();
        //自动保存
        if(flag.getId() == -1){
            //这是新建的
            if(isChanged){
                addFlag();
            }

        }else{
            //这个flag是进行修改的
            if(isChanged){
                //只有当进行了修改了才更新数据库
                editFlag();
            }
        }
        isChanged = false;
    }*/
    //endregion

    //region 点击返回退出时也会自动保存
   /* @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            //自动保存
           if(flag.getId() == -1){
                //这是新建的
                if(isChanged){
                    addFlag();
                }

            }else{
                //这个flag是进行修改的
                if(isChanged){
                    //只有当进行了修改了才更新数据库
                    editFlag();
                }
            }
            isChanged = false;
        }
        return super.onKeyDown(keyCode, event);
    }
*/
    //endregion

    //获得光标所在行号
    private int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {

            //Integer[] indices = getIndices(layout.getText().toString().trim(), ' ');
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }


}
