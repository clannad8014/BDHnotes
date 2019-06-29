package com.clannad.menu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.clannad.menu.DB.*;
import com.clannad.menu.models.*;

import java.sql.SQLException;
import java.util.ArrayList;

public class login extends AppCompatActivity {
    NoteAdapter noteAdapter;
    ListView listView;//用户笔记列表
    String uid;//用户名
    ImageView addBtn;          //新建笔记的按钮


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case 0x20: case 0x21:
                    String s = (String) msg.obj;
                    //tv_data.setText(s);
                    Toast.makeText(login.this, s, Toast.LENGTH_LONG).show();
                    break;
                case 0x22:
                    ArrayList<show_list> show_lists= (ArrayList<show_list>) msg.obj;
                    //测试
                   /* for (show_list sl:show_lists){
                        System.out.println(sl.getA_content()+"++++++++++++"+sl.getCtime());
                    }*/
                    noteAdapter=new NoteAdapter(login.this,R.layout.flag,show_lists);
                    listView = findViewById(R.id.lv_flags);
                    listView.setAdapter(noteAdapter);


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
        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");

//**********************读写 目前还未用
        int permission_WRITE = ActivityCompat.checkSelfPermission(login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(login.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(login.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
////////////
        //加载笔记列表
        loadUserNoteList();
        //增加笔记
        addNote();













    }
    //region 当重新进入该activity后，需要重新初始化一下
    @Override
    protected void onResume() {
        super.onResume();
        loadUserNoteList();
    }

    //加载listview
    void loadUserNoteList(){

        sqls sqls=new sqls();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = handler.obtainMessage();
                try {
                    ArrayList<user_note_list> user_note_lists=sqls.sel_user_note_list(uid);
                    if (user_note_lists !=null) {
                        ArrayList<show_list> show_lists=new ArrayList<>();
                        show_list sl=null;
                        for(user_note_list unl:user_note_lists){
                            sl=new show_list();
                            sl.setBid(unl.getBid());
                            sl.setTitle(unl.getTitle());
                            //取第一行作为显示内容
                            String con=sqls.sel_hnum_content(unl.getBid(), "null").getXcontent();
                            if(con!=null)
                            {sl.setA_content(con);}
                            else
                            {sl.setA_content("1");}
                            sl.setCtime(unl.getCtime());
                            show_lists.add(sl);
                        }

                        message.what = 0x22;
                        message.obj =show_lists ;
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
                Intent intent = new Intent(login.this,AddActivity.class);
                user_note_list unl=new user_note_list(uid);
                Bundle bundle = new Bundle();
                bundle.putString("uid",unl.getUid());
                bundle.putString("bid",unl.getBid());
                bundle.putString("title",unl.getTitle());
                bundle.putString("ctime",unl.getCtime());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }



}


