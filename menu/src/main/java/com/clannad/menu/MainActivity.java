package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.*;
import com.clannad.menu.models.*;
import com.clannad.menu.weight.CircleImageView;

import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    int delPosition=-1;            //用来删除的一个变量，因为内部类要用
    int testnum=0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    NoteAdapter noteAdapter;
    ListView listView;
    String uid;//用户名
    ImageView addBtn;          //新建笔记的按钮
    ImageView main_menu;
    CircleImageView person;
    TextView uname;
    TextView uinfo;
    NavigationView nav;
    ArrayList<show_list> show_lists;//用户笔记列表



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x20: case 0x21:case 0x23:case 0x24:case 0x25:case 0x26:
                    String s = (String) msg.obj;
                    System.out.println(s);
                    Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                    break;
                case 0x22:
                    show_lists= (ArrayList<show_list>) msg.obj;

                    noteAdapter=new NoteAdapter(MainActivity.this,R.layout.flag,show_lists);
                    listView = findViewById(R.id.lv_flags);
                    listView.setAdapter(noteAdapter);

                    System.out.println("加载成功！！！！！！！");
                    Toast.makeText(MainActivity.this,"加载成功！！！！！！！", Toast.LENGTH_SHORT).show();
                    //增加笔记
                    addNote();
                    //点击一个笔记进入
                    clickOneNote();
                    //长按删除一个笔记
                    longClickNote();break;


            }

        }
    };

//*************************未使用
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };
 //****************



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBtn=findViewById(R.id.iv_add_flag);
        main_menu =findViewById(R.id.main_menu);
        drawerLayout=findViewById(R.id.activity_na);
        nav=findViewById(R.id.nav);

        //个人信息
        navigationView=findViewById(R.id.nav);
        View view=navigationView.getHeaderView(0);
        person =(CircleImageView)view.findViewById(R.id.person);
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("===================个人中心");
                Intent intent = new Intent(MainActivity.this,Lmenu_user.class);
                System.out.println("===================个人中心2");
                startActivity(intent);
            }
        });
        uname=view.findViewById(R.id.uname);
        uinfo=view.findViewById(R.id.uinfo);

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");



//**********************读写 目前还未用
        int permission_WRITE = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
////////////


    }
    //region 当重新进入该activity后，需要重新初始化一下
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("请等待几秒钟加载列表");
        Toast.makeText(MainActivity.this, "请等待几秒钟加载列表", Toast.LENGTH_SHORT).show();
        loadUserNoteList();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //加载listview
    void loadUserNoteList(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<show_list> lists=sqls.sel_user_note_list(uid);
                    if (lists !=null) {

                        message.what = 0x22;
                        message.obj =lists ;
                    }
                    else {
                        message.what = 0x21;
                        message.obj ="该用户没有笔记" ;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x20;
                    message.obj ="查询过程出错" ;
                }
                handler.sendMessage(message);
            }
        }).start();

    }
    //增加一个笔记
    void addNote(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_note_list unl=new user_note_list(uid);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = handler.obtainMessage();
                        try {
                            note_content nc =new note_content();
                            nc.setBid(unl.getBid());
                            nc.setXcontent("");
                            nc.setXtime(unl.getCtime());
                            nc.setXid(unl.getUid());

                            Sqls sqls=new Sqls();

                            sqls.addOneNote(unl,nc);
                            //sqls.startNoteContent(nc);
                            message.what = 0x24;
                            message.obj ="新建笔记成功" ;
                        } catch (SQLException e) {
                            e.printStackTrace();
                            message.what = 0x23;
                            message.obj ="新建笔记失败" ;
                        }

                        handler.sendMessage(message);
                    }
                }).start();
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bid",unl.getBid());
                bundle.putString("title",unl.getTitle());
                bundle.putString("ctime",unl.getCtime());
                bundle.putString("xid",uid);
                bundle.putString("neirong","");
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }


//listview点击事件
    public void clickOneNote(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                show_list sl= show_lists.get(i);
                Intent intent = new Intent(MainActivity.this,AddActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("bid",sl.getBid());
                bundle.putString("title",sl.getTitle());
                bundle.putString("ctime",sl.getCtime());
                bundle.putString("xid",uid);
                bundle.putString("neirong", sl.getA_content());
                //System.out.println(sl.getA_content()+"------------------");
                intent.putExtras(bundle);
               // intent.putExtra("sl", (Parcelable) sl);
                startActivity(intent);

            }
        });
    }

    public void longClickNote(){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                delPosition = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.flag4_2);
                builder.setTitle("接下来你要面临一个抉择");
                builder.setMessage("是否要删除？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("请等待几秒钟加载列表");
                        //确定删除
                            Sqls sqls=new Sqls();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = handler.obtainMessage();
                                    try {
                                        sqls.deleteOneNote(show_lists.get(delPosition).getBid());
                                        message.what = 0x26;
                                        message.obj ="删除笔记成功" ;
                                        loadUserNoteList();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                        message.what = 0x25;
                                        message.obj ="删除笔记失败" ;
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


