package com.clannad.menu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.DB_user;
import com.clannad.menu.models.user;

import java.util.ArrayList;

public class Login extends AppCompatActivity {
    private Button loginBtn;
    TextView registerBtn;
    EditText loginName;
    EditText loginPwd;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x11:
                    String s = (String) msg.obj;
                    //tv_data.setText(s);
                    Toast.makeText(Login.this, s, Toast.LENGTH_LONG).show();
                    break;
                case 0x12:
                    String ss = (String) msg.obj;
                    Toast.makeText(Login.this, ss, Toast.LENGTH_LONG).show();
                    break;
                case 0x13:
                    String sss = (String) msg.obj;
                    Toast.makeText(Login.this, sss, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginName=(EditText)findViewById(R.id.login_name);
        loginPwd=(EditText)findViewById(R.id.login_pwd);
        loginBtn=(Button)findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //返回键

                   // String ip = sqlipedit.getText().toString();
    /*远程服务器的ip跟端口号，使用账号、密码，不同的数据库使用的连接端口、命令都不同
        mysql使用的连接命令：jdbc:mysql//192.168.1.xxx:3306
    */ //jdbc:mysql://localhost:8806/test
/*
                    try{
                        Class.forName("com.mysql.jdbc.Driver");
//                    Connection con = DriverManager.getConnection("jdbc:mysql:"+"//188.131.255.217:3306",
//                            "haolayo_club","riheCjsM6A4X5TFJ");
                    System.out.println("连接成功");
                }catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("失败");
                    }
*/

                System.out.println("================================");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        try {
                            user u=new user();
                            // DB_user user=new DB_user();
                           // Message message = handler.obtainMessage();
                            String name=loginName.getText().toString();
                            String pwd=loginPwd.getText().toString();
                            ArrayList<user> list= DB_user.SearchId(name);
                            if(list!= null){

                                String s = "查询成功";
                                String str="";
                                for(user U : list){
                                    str += U.getPwd();

                                }
                               //uname.equals(s1.trim())
                                if(str.trim().equals(pwd.trim())){
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    Bundle bundle = new Bundle();

                                    bundle.putString("uid","sa");

                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                    message.what = 0x11;
                                    message.obj = "登录成功";
                                }else {
                                    message.what = 0x12;
                                    message.obj = "密码输入错误！！！";
                                }
                                System.out.println("name="+name+" pwd="+pwd);
                                System.out.println("登录成功:+str="+str+"pwd="+pwd);

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
    }
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