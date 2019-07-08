package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.clannad.menu.DB.*;
import com.clannad.menu.models.*;
import com.clannad.menu.weight.CircleImageView;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {
    int delPosition=-1;            //用来删除的一个变量，因为内部类要用
    int testnum=0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    NoteAdapter noteAdapter;
    RoomAdapter roomAdapter;
    ListView listView;
    ListView listView_room;
    String uid;//用户名
    ImageView addBtn;          //新建笔记的按钮
    ImageView main_menu;
    CircleImageView person;
    TextView uname;
    TextView uinfo;
    NavigationView nav;
    ArrayList<show_list> show_lists;//用户笔记列表
    ArrayList<user> user_lists;//用户信息列表
    ArrayList<Room> room_lists;//房间表

    //在线编辑弹窗
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;
    Context context ;
    LayoutInflater inflater;
    View layout ;
    ListView myListView ;
    Button btn_adds;
    Button btn_joins;
    Button btn_add_room;
    Button btn_join_room;
    int open=0;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x20: case 0x21:case 0x23:case 0x24:case 0x25:case 0x26:case 0x28:
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
                    //增加笔记
                    addNote();
                    //点击一个笔记进入
                    clickOneNote();
                    //长按删除一个笔记
                    longClickNote();
                    break;
                case 0x27:
                    room_lists=(ArrayList<Room>)msg.obj;
                    roomAdapter=new RoomAdapter(MainActivity.this,R.layout.room_cell,room_lists);
                    myListView.setAdapter(roomAdapter);
                    open=(open+1)%2;//防止多次点击
                    goRoom();//列表的点击事件
                    break;

                case 0x07:
                    System.out.println("==================================用户信息初始化失败");
                    String sss = (String) msg.obj;
                    Toast.makeText(MainActivity.this,"用户信息初始化失败！！！！！！！ "+sss, Toast.LENGTH_SHORT).show();

                    // Toast.makeText(login.this, s7, Toast.LENGTH_LONG).show();
                    break;

                case 0x09:
                    //加载图片
                    user_lists= (ArrayList<user>) msg.obj;
                    String path="";
                    for(user U:user_lists){
                        path=U.getUphoto2()+U.getUphoto1();
                        uname.setText(U.getUname());
                        uinfo.setText(U.getUinfo());
                    }


                        System.out.println("============ -- "+path);
                        File file=new File(path);
                        //判断文件是否存在
                        if(file.exists()){
                            Bitmap pic1= BitmapFactory.decodeFile(path);
                            person.setImageBitmap(pic1);
                            System.out.println("===================图片加载成功！！！！！！！");
                        }else{
                            System.out.println("==================================加载图片失败"+path);
                            Toast.makeText(MainActivity.this,"图片加载失败！！！！！！！", Toast.LENGTH_SHORT).show();
                        }


//                    noteAdapter=new NoteAdapter(MainActivity.this,R.layout.flag,show_lists);
//                    listView = findViewById(R.id.lv_flags);
//                    listView.setAdapter(noteAdapter);
                    break;
                case 0x30:
                    Room room = (Room)msg.obj;
                    Intent intent = new Intent(MainActivity.this,OnlineEditActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", uid);
                    bundle.putInt("rid",room.getRid());
                    bundle.putString("rtitle",room.getRtitle());
                    bundle.putString("rtime",room.getRtime());
                    bundle.putString("rboss",room.getRboss());
                    //System.out.println(sl.getA_content()+"------------------");
                    intent.putExtras(bundle);
                    // intent.putExtra("sl", (Parcelable) sl);
                    startActivity(intent);


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

        //声明相关参数
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
                Intent intent = new Intent(MainActivity.this,Lmenu_user.class);
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
        loadUserNoteList();

        userinfo(); //用户信息初始化
        show();  //左滑界面弹出
        show_Lmenu();
//        show();
//        show_Lmenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //点击显示左滑界面
    void show(){

//    Bitmap pic=BitmapFactory.decodeFile("/storage/emulated/0/BDH.notes/img181.jpg");
//    person.setImageBitmap(pic);
        main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("显示中...");
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }


    void show_Lmenu(){


        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                //设置
                if(menuItem.getItemId()==R.id.setting)
                {
                    System.out.println("读取图像中...");
                    uname.setText("读取图像中..");
                    Bitmap pic=BitmapFactory.decodeFile("/storage/emulated/0/BDH.notes/img13.jpg");


                }
                //跳转个人中心
                if(menuItem.getItemId()==R.id.personalcenter)
                {

                    System.out.println("读取图像中..."+person+person.toString());

                    Intent intent = new Intent(MainActivity.this,Lmenu_user.class);

//                    user_lists unl=new user_note_list(uid);
                 Bundle bundle = new Bundle();
                    for(user U:user_lists){
                        bundle.putString("uid",uid);
                        bundle.putString("uname",U.getUname());
                        bundle.putString("uinfo",U.getUinfo());
                        bundle.putString("uphoto1",U.getUphoto1());
                        bundle.putString("uphoto2",U.getUphoto2());
                        bundle.putString("email",U.getEmail());

                    }
                    intent.putExtra("user_list",bundle);

                    startActivity(intent);

                }
//在线编辑
                if(menuItem.getItemId()==R.id.onlineedit)
                {
                    onlineEdit();
                    Log.i("button", "点击在线编辑");
                    selAllJoinRoom();
                }
                return false;
            }
        });



    }
    //加载用户信息
    public void userinfo(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                    Message message = handler.obtainMessage();

                try {
                    ArrayList<user> user_lists=DB_user.SearchUser(uid);
                    com.clannad.menu.FTP.File file=new com.clannad.menu.FTP.File();
                    if(user_lists !=null){
                       for(user U:user_lists){

                           File file1=new File(U.getUphoto2()+U.getUphoto1());
                            //判断图片是否存在
                            if(!file1.exists()){
                                System.out.println("============不存在  下载图片--  ");
                                //如果不存在  下载图片
                                Boolean flag=file.aboutTakePhotoDown(U.getUphoto1(),U.getUphoto2());
                                if(flag){
                                    System.out.println("============下载图片成功--  ");
                                }

                            }

                       }

                    }

                    message.what = 0x09;
                    message.obj =user_lists;
                } catch (SQLException e) {
                    e.printStackTrace();
                    message.what = 0x07;
                    message.obj ="查询过程出错" ;
                }
                handler.sendMessage(message);
            }
        }).start();

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
                       // Toast.makeText(MainActivity.this, "请等待几秒钟加载列表", Toast.LENGTH_SHORT).show();
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

    //查询用户加入房间列表
    void selAllJoinRoom(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<Room> lists=sqls.selAllJoinRoom(uid);
                    if (lists !=null) {

                        message.what = 0x27;
                        message.obj =lists ;
                    }
                    else {
                        message.what = 0x28;
                        message.obj ="该用户没有房间" ;
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
    //查询用户创建的列表
    void selAllAddRoom(){
        Sqls sqls=new Sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<Room> lists=sqls.selAllAddRoom(uid);
                    if (lists !=null) {

                        message.what = 0x27;
                        message.obj =lists ;
                    }
                    else {
                        message.what = 0x28;
                        message.obj ="该用户没有房间" ;
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
    void goRoom(){
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Room room= room_lists.get(i);
                Intent intent = new Intent(MainActivity.this,OnlineEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uid", uid);
                bundle.putInt("rid",room.getRid());
                bundle.putString("rtitle",room.getRtitle());
                bundle.putString("rtime",room.getRtime());
                bundle.putString("rboss",room.getRboss());
                //System.out.println(sl.getA_content()+"------------------");
                intent.putExtras(bundle);
                // intent.putExtra("sl", (Parcelable) sl);
                startActivity(intent);

            }
        });
    }


    void onlineEdit(){
        context = MainActivity.this;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.room, null);

        myListView = layout.findViewById(R.id.room_list);
        btn_joins=layout.findViewById(R.id.btn_joins);
        btn_adds=layout.findViewById(R.id.btn_adds);
        btn_join_room=layout.findViewById(R.id.btn_joinroom);
        btn_add_room=layout.findViewById(R.id.btn_addroom);

        builder = new AlertDialog.Builder(context);
        builder.setView(layout);
        alertDialog = builder.create();
        alertDialog.show();

        View.OnClickListener clickListener=new View.OnClickListener(){
            public void onClick(View v) {
                //加入的
                if(v.getId()==R.id.btn_joins && open==0){
                    Log.i("btn", "joins");
                    selAllJoinRoom();
                }  //创建的
                if(v.getId()==R.id.btn_adds && open==1){
                    selAllAddRoom();
                    Log.i("btn", "adds");
                }
                //加入
                if(v.getId()==R.id.btn_joinroom){
                    Log.i("btn", "joinroom");
                   Context context1 = MainActivity.this;
                   LayoutInflater inflater1 = (LayoutInflater) context1.getSystemService(LAYOUT_INFLATER_SERVICE);
                   View layout1 = inflater1.inflate(R.layout.roomjoin, null);
                   EditText et_rid=layout1.findViewById(R.id.et_rid);et_rid.setText("");
                   EditText et_pwd=layout1.findViewById(R.id.et_pwd);et_pwd.setText("");

                   AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("输入房间号和密码").setIcon(android.R.drawable.ic_dialog_info).setView(layout1)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton("确定",null);
                    Dialog dialog=builder.create();
                    dialog.show();
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String rid1=et_rid.getText().toString();
                            String pwd1=et_pwd.getText().toString();
                            if(!rid1.equals("") && !pwd1.equals("")){
                               // dialog.dismiss();
                                Sqls sqls=new Sqls();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = handler.obtainMessage();
                                        try {
                                            Room room=sqls.selRoom(Integer.parseInt(rid1));
                                            if (room.getRpwd() !=null && room.getRpwd().equals(pwd1)) {
                                                Log.i("mess", "可以查询到");
                                                //查询自己是否已经在房间了
                                                RoomContent rc=new RoomContent();
                                                rc.setRid(Integer.parseInt(rid1));
                                                rc.setUid(uid);
                                                try{
                                                    sqls.addOneMember(rc);
                                                    Log.i("测试","插入成功");
                                                    message.what=0x30;
                                                    message.obj=room;
                                                }catch (SQLException e)
                                                {
                                                    Log.i("测试", "插入出错，已存在");
                                                    message.what=0x30;
                                                    message.obj=room;
                                                }
                                                dialog.dismiss();
                                            }
                                            else {
                                                message.what = 0x28;
                                                message.obj ="房间号或密码错误" ;
                                            }

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            message.what = 0x20;
                                            message.obj ="查询过程出错" ;
                                        }
                                        handler.sendMessage(message);
                                    }
                                }).start();

                            }else {
                                Toast.makeText(MainActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                                System.out.println("输入不能为空");
                        }

                        }
                    });


                }
                //创建
                if(v.getId()==R.id.btn_addroom){
                    Log.i("btn", "addroom");
                    Context context2 = MainActivity.this;
                    LayoutInflater inflater2 = (LayoutInflater) context2.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View layout2 = inflater2.inflate(R.layout.roomadd, null);
                    EditText et_name=layout2.findViewById(R.id.room_name);et_name.setText("");
                    EditText et_pwd=layout2.findViewById(R.id.room_pwd);et_pwd.setText("");

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("输入房间名和密码").setIcon(android.R.drawable.ic_dialog_info).setView(layout2)
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.setPositiveButton("确定",null);
                    Dialog dialog=builder.create();
                    dialog.show();
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name2=et_name.getText().toString();
                            String pwd2=et_pwd.getText().toString();
                            if(!name2.equals("") && !pwd2.equals("")){
                                // dialog.dismiss();
                                Sqls sqls=new Sqls();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Message message = handler.obtainMessage();
                                        try {
                                            Room room=new Room();
                                            room.setRtitle(name2);
                                            room.setRpwd(pwd2);
                                            room.setRboss(uid);
                                            sqls.addOneRoom(room);
                                            room=sqls.selLastRoom(uid);

                                            RoomContent rc=new RoomContent();
                                            rc.setRid(room.getRid());
                                            rc.setUid(room.getRboss());
                                            sqls.addOneMember(rc);

                                            message.what=0x30;
                                            message.obj=room;

                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            message.what = 0x20;
                                            message.obj ="查询过程出错" ;
                                        }
                                        handler.sendMessage(message);
                                    }
                                }).start();

                            }else {
                                Toast.makeText(MainActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                                System.out.println("输入不能为空");
                            }

                        }
                    });





                }


            }
        };
        btn_adds.setOnClickListener(clickListener);
        btn_add_room.setOnClickListener(clickListener);
        btn_joins.setOnClickListener(clickListener);
        btn_join_room.setOnClickListener(clickListener);
    }

}


