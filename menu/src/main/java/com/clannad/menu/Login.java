package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.DB_user;
import com.clannad.menu.FTP.FileUtill;
import com.clannad.menu.models.user;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
public class Login extends AppCompatActivity {
    private Button loginBtn;
    TextView registerBtn;
    EditText loginName;
    EditText loginPwd;
    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                 case 0x12: case 0x13:
                    String s = (String) msg.obj;
                    //tv_data.setText(s);
                    Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();
                    break;

            }

        }
    };
    private NumberFormat PermissionHelper;
    private NumberFormat permissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginName=(EditText)findViewById(R.id.login_name);
        loginPwd=(EditText)findViewById(R.id.login_pwd);
        loginBtn=(Button)findViewById(R.id.btn_login);
        registerBtn=(TextView)findViewById(R.id.btn_register);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }


    }



FileUtill f=new FileUtill();
    @Override
    protected void onStart() {
        super.onStart();
        login();
        regist();
        CreatFile("/storage/emulated/0/BDH.notes/");
        CreatFile("/storage/emulated/0/BDH.notes/content");


    }
    public void CreatFile(String path){
        File file = new File(path);
        if (!file.exists()) {
            /**  注意这里是 mkdirs()方法  可以创建多个文件夹 */
            file.mkdirs();
        }
    }
    public void login(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回键

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        try {
                            user u=new user();
                            String name=loginName.getText().toString().trim();
                            String pwd=loginPwd.getText().toString().trim();
                            ArrayList<user> list= DB_user.SearchId(name);
                            if(list!= null){

                                String s = "查询成功";
                                String str="sa";
                                for(user U : list){
                                    str=U.getPwd();
                                }
                              //  DB_user.login(name,pwd);
                              //  System.out.println("登录 DB_user.login(name,pwd)="+ DB_user.login(name,pwd));
                                //uname.equals(s1.trim())
                                if(str.trim().equals(pwd)){
                                    System.out.println("登录成功:+str="+str+"pwd="+pwd);
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("uid",name);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }else {
                                    message.what = 0x12;
                                    message.obj = "密码输入错误！！！";
                                }
                                System.out.println("name="+name+" pwd="+pwd);


                                //  Toast.makeText(Login.this, "ojbk\n", Toast.LENGTH_LONG).show();
                            }else {
                                System.out.println("查询结果为空");
                                message.what = 0x13;
                                message.obj = "用户不存在！！!";
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            message.what = 0x13;
                            message.obj = "！！!";
                            System.out.println("登录失败"+e);
                        }
                        handler.sendMessage(message);
                    }
                }).start();
                System.out.println("登录成功");

                System.out.println("================================");

            }
        });
//        registerBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //返回键
//                Intent intent = new Intent(Login.this, Regist_Activity.class);
//                startActivity(intent);
//            }
//        });
    }
    public void regist(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("注册=========================");
                Intent intent = new Intent(Login.this, Regist_Activity.class);
                startActivity(intent);
            }
        });
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;



}
/*
public class Login extends Activity implements View.OnClickListener {
    private DrawerLayout drawerLayout;
    private SystemBarTintManager tintManager;
    private NavigationView navigationView;
    ImageView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initWindow();
        drawerLayout = (DrawerLayout) findViewById(R.id.activity_na);
        navigationView = (NavigationView) findViewById(R.id.nav);
        menu= (ImageView) findViewById(R.id.main_menu);
        View headerView = navigationView.getHeaderView(0);//获取头布局
        menu.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //item.setChecked(true);
                Toast.makeText(Login.this,item.getTitle().toString(),Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(navigationView);
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_menu://点击菜单，跳出侧滑菜单
                if (drawerLayout.isDrawerOpen(navigationView)){
                    drawerLayout.closeDrawer(navigationView);
                }else{
                    drawerLayout.openDrawer(navigationView);
                }
                break;
        }
    }
    private void initWindow() {//初始化窗口属性，让状态栏和导航栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            int statusColor = Color.parseColor("#1976d2");
            tintManager.setStatusBarTintColor(statusColor);
            tintManager.setStatusBarTintEnabled(true);
        }
    }
}
*/