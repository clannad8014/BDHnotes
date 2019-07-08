package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.DB_user;
import com.clannad.menu.FTP.FileUtill;
import com.clannad.menu.FTP.NetWorkUtil;
import com.clannad.menu.models.user;
import com.clannad.menu.weight.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Lmenu_user extends AppCompatActivity {

    ArrayList<user> user_lists;//用户信息列表
    CircleImageView mine_avatar;
    ImageButton img_back;
    TextView mine_nick,sign_content,account_content,match_name,match_email;
    String name,info,photo1,photo2,email,uid;
    String result="";
    Boolean flag=false;
    String creatTime;
    FileUtill f=new FileUtill();

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                //修改昵称
                case 0x20: case 0x21:
                    String s = (String) msg.obj;
                    System.out.println(s);
                    Toast.makeText(Lmenu_user.this, s, Toast.LENGTH_LONG).show();
                    mine_nick.setText(result);
                    match_name.setText(result);
                    break;
                    //上传图片
                case 0x22:case 0x23:
                    String ss = (String) msg.obj;
                    System.out.println(ss);
                    Toast.makeText(Lmenu_user.this, ss, Toast.LENGTH_LONG).show();
                    break;

                    //显示头像
                case 0x24:
                    String photo_name = (String) msg.obj;
                    System.out.println("=0X24===============0X24+===/storage/emulated/0/BDH.notes/"+photo_name);
                    Bitmap photo= BitmapFactory.decodeFile("/storage/emulated/0/BDH.notes/"+photo_name);
                    mine_avatar.setImageBitmap(photo);
                    f.deletePhotoWithPath("/storage/emulated/0/BDH.notes/BDH.upload");
                    break;
                    //修改签名
                case 0x25:
                    String s1= (String) msg.obj;
                    System.out.println(s1);
                    Toast.makeText(Lmenu_user.this, s1, Toast.LENGTH_LONG).show();
                    sign_content.setText(result);
                    break;
                //修改邮箱
                case 0x26:
                    String s2= (String) msg.obj;
                    System.out.println(s2);
                    Toast.makeText(Lmenu_user.this, s2, Toast.LENGTH_LONG).show();
                    match_email.setText(result);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmenu_user);

    }

    void init(){
        mine_avatar=(CircleImageView)findViewById(R.id.mine_avatar);
        mine_nick=findViewById(R.id.mine_nick);
        sign_content=findViewById(R.id.sign_content);
        account_content=findViewById(R.id.account_content);
        match_name=findViewById(R.id.match_name);
        match_email=findViewById(R.id.match_email);
        img_back=findViewById(R.id.img_back);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("user_list");
        //获取数据
        name = bundle.getString("uname");
        info = bundle.getString("uinfo");
        photo1 = bundle.getString("uphoto1");
        photo2= bundle.getString("uphoto2");
        email= bundle.getString("email");
        uid=bundle.getString("uid");


        //显示数据
        Bitmap pic1= BitmapFactory.decodeFile(photo2+photo1);
        mine_avatar.setImageBitmap(pic1);

        sign_content.setText(info);
        mine_nick.setText(name);
        account_content.setText(uid);
        match_name.setText(name);
        match_email.setText(email);
    }
    @Override
    protected void onStart() {
        super.onStart();
        gainCurrenTime();
        init();//初始化信息
        update();//修改信息


    }

    //修改信息
    void update(){
        //修改昵称
        match_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_uname();
            }
        });
        mine_nick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_uname();
            }
        });

        //修改email
        match_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(Lmenu_user.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(Lmenu_user.this);
                inputDialog.setTitle("修改邮箱").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("修改邮箱........  开始："+email);
                                result=editText.getText().toString().trim();
                                if(result.length()==0){
                                    result=name;
                                }
                                DB_user DB=new  DB_user();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Message message = handler.obtainMessage();
                                        user U=new user();
                                        U.setUid(uid);
                                        U.setEmail(result);


                                        try {
                                            DB.updateEmail(U);
                                            System.out.println("=========修改成功");

                                            message.what = 0x26;
                                            message.obj ="修改邮箱成功" ;

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            System.out.println("=========修改邮箱失败"+e);
                                            message.what = 0x26;
                                            message.obj ="修改邮箱失败" ;
                                        }
                                        handler.sendMessage(message);
                                        System.out.println("修改邮箱........  结束："+result);


                                    }

                                }).start();
                            }
                        }).show();
            }
        });

        //修改签名
        sign_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText editText = new EditText(Lmenu_user.this);
                AlertDialog.Builder inputDialog =
                        new AlertDialog.Builder(Lmenu_user.this);
                inputDialog.setTitle("修改签名").setView(editText);
                inputDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Toast.makeText(Lmenu_user.this, editText.getText().toString(),  Toast.LENGTH_SHORT).show();
                                System.out.println("修改签名........  开始："+name);
                                result=editText.getText().toString().trim();
                                if(result.length()==0){
                                    result=name;
                                }
                                DB_user DB=new  DB_user();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        Message message = handler.obtainMessage();
                                        user U=new user();
                                        U.setUinfo(result);
                                        U.setUid(uid);

                                        try {
                                            DB.updateUinfo(U);
                                            System.out.println("=========修改成功");

                                            message.what = 0x25;
                                            message.obj ="修改签名成功" ;

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            message.what = 0x25;
                                            message.obj ="修改失败"+e;
                                        }
                                        handler.sendMessage(message);
                                        System.out.println("修改签名........  结束："+result);


                                    }

                                }).start();
                            }
                        }).show();


            }
        });

        //修改头像
        mine_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (ContextCompat.checkSelfPermission(Lmenu_user.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Lmenu_user.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
                    } else {
                        openAlbum();
                    }
                }
            }
        });

        account_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showText(uid);
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    //修改昵称
    public void update_uname(){
        final EditText editText = new EditText(Lmenu_user.this);
        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(Lmenu_user.this);
        inputDialog.setTitle("修改昵称").setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Toast.makeText(Lmenu_user.this, editText.getText().toString(),  Toast.LENGTH_SHORT).show();
                        System.out.println("修改昵称........  开始："+name);
                        result=editText.getText().toString().trim();
                        if(result.length()==0){
                            result=name;
                        }
                        DB_user DB=new  DB_user();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                Message message = handler.obtainMessage();
                                user U=new user();
                                U.setUname(result);
                                U.setUid(uid);

                                try {
                                    DB.updateUname(U);
                                    System.out.println("=========修改成功");

                                    message.what = 0x20;
                                    message.obj ="修改昵称成功" ;

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    message.what = 0x20;
                                    message.obj ="修改昵称" ;
                                }
                                handler.sendMessage(message);
                                System.out.println("修改昵称........  结束："+result);


                            }

                        }).start();
                    }
                }).show();
    }

    //获取手机尺寸
    public void getAndroiodScreenProperty() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)


        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }
    public int getImageWidth() {
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        //130是在xml文件中那一排recyclerview之间的间距加上recyclerview和父控件的间距的和
        int imageWidth = (int)(width -130*density) / 4;
        return imageWidth;
    }
public void showText(String text){
            Toast toast=Toast.makeText(getApplicationContext(), "  "+text,Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);

            LinearLayout linearLayout = (LinearLayout) toast.getView();
            TextView messageTextView = (TextView) linearLayout.getChildAt(0);
                 System.out.println(" ============= 尺寸0： "+messageTextView.getTextSize());
            messageTextView.setTextSize(messageTextView.getTextSize());
        //创建图片视图对象
            //ImageView imageView= new ImageView(getApplicationContext());
        CircleImageView imageView= new CircleImageView(getApplicationContext());
             //设置图片
           // imageView.setImageResource(R.drawable.miku1);

            Bitmap pic1= BitmapFactory.decodeFile(photo2+photo1);
            imageView.setImageBitmap(pic1);
            System.out.println(" ============= 尺寸1： "+getImageWidth());
           System.out.println(" ============= 尺寸2 ："+pic1.getHeight());
        //获得toast的布局
            LinearLayout toastView = (LinearLayout) toast.getView();

    //设置此布局为横向的
            toastView.setOrientation(LinearLayout.HORIZONTAL);
           // toastView.setOrientation(LinearLayout.VERTICAL);
        //将ImageView在加入到此布局中的第一个位置
            toastView.addView(imageView, 0);
            toast.show();
}
    //弹出对话框
    private void showInputDialog(String title) {
        /*@setView 装入一个EditView
         */
        final EditText editText = new EditText(Lmenu_user.this);

        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(Lmenu_user.this);
        inputDialog.setTitle(title).setView(editText);
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // Toast.makeText(Lmenu_user.this, editText.getText().toString(),  Toast.LENGTH_SHORT).show();
                        result=editText.getText().toString();
                        System.out.println("==========result"+result);
                    }
                }).show();

    }


    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStorageDirectory(), albumName);
        if (!file.mkdirs()) {
            System.out.println("========================文件创建失败"+file.mkdirs());
        }
        return file;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            if (data != null) {
                resizeImage(data.getData());
            }
        }else if (requestCode==2){
            if (data != null) {
                showResizeImage(data);
            }
        }


    }
    private void gainCurrenTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        creatTime = formatter.format(curDate);
    }
    private void showResizeImage(Intent data) {
        Bundle extras = data.getExtras();
        Message message = handler.obtainMessage();
        if (extras != null) {
            String photo_name = uid+"_"+creatTime + ".jpg";
            Bundle bundle = data.getExtras();
            // 获取相机返回的数据，并转换为Bitmap图片格式
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FileOutputStream b = null;
            FileOutputStream upload = null;
            File file = new File(getAlbumStorageDir("BDH.notes/BDH.upload"), photo_name);
            //保存至本地
            File file1 = new File(getAlbumStorageDir("BDH.notes"),photo_name);
            try {
                b = new FileOutputStream(file);
                upload= new FileOutputStream(file1);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, upload);// 把数据写入文件

//                Bitmap pic= BitmapFactory.decodeFile("/storage/emulated/0/BDH.notes/"+photo_name);
//                mine_avatar.setImageBitmap(pic);
                System.out.println("===================/storage/emulated/0/BDH.notes/"+photo_name);
                message.what = 0x24;
                message.obj =photo_name;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (b != null) {
                        b.flush();
                        b.close();

                        upload.flush();
                        upload.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            upload(photo_name);
            System.out.println("===========保存成功1"+bitmap);
            // 将图片显示在ImageView里
//            Bitmap pic= BitmapFactory.decodeFile("/storage/emulated/0/BDH.notes/"+name);
//            mine_avatar.setImageBitmap(pic);
          //  sign_content.setText("/storage/emulated/0/BDH.notes/"+name);
            System.out.println("===========保存成功1 ==  "+"/storage/emulated/0/BDH.notes/"+photo_name);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            Lmenu_user.this.sendBroadcast(mediaScanIntent);

            handler.sendMessage(message);
        }
    }

    //上传图片
    public void upload(String photoname){
        File filePhoto = new File(Environment.getExternalStorageDirectory(),"BDH.notes/BDH.upload");
        File[] photoAllfiles = filePhoto.listFiles();
        DB_user DB=new  DB_user();
        System.out.println("文件已存在！"+photoAllfiles);
        if (photoAllfiles!=null) {
            if (photoAllfiles.length == 0) {
                Toast.makeText(Lmenu_user.this, "没有图片要上传", Toast.LENGTH_SHORT).show();
            } else {
                for (final File photoFile : photoAllfiles) {
                    if (NetWorkUtil.isNetworkAvailable(Lmenu_user.this)) {

                        new Thread() {
                            @Override
                            public void run() {
                                Message message = handler.obtainMessage();
                                boolean up_flag=f.aboutTakePhotoUp(photoFile);
                                if(up_flag){

                                    try {
                                        user U=new user();
                                        U.setUphoto1(photoname);
                                        U.setUphoto2("/storage/emulated/0/BDH.notes/");
                                        U.setUid(uid);
                                        DB.updatePhoto(U);
                                        System.out.println("============ 上传图片 修改成功----"+photoname);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

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
                        Toast.makeText(Lmenu_user.this, "对不起，没有网络！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else{
            Toast.makeText(Lmenu_user.this, "没有图片要上传！", Toast.LENGTH_SHORT).show();
        }
    }
    public void resizeImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 2);
    }
    //打开手机相册
    private void openAlbum() {
        // 使用意图直接调用手机相册
        Intent intentPhoto = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // 打开手机相册,设置请求码
        startActivityForResult(intentPhoto, 1);
    }
    public void selectOnclick(View view){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(Lmenu_user.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(Lmenu_user.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4);
            } else {
                openAlbum();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 4:
                if(grantResults.length>0 &&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(this, "请允许读取相册！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


}
