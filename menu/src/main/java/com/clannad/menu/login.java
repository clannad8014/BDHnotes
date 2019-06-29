package com.clannad.menu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class login extends AppCompatActivity {

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
        Bundle bundle = getIntent().getExtras();
        String uid = bundle.getString("uid");

//**********************读写 目前还未用
        int permission_WRITE = ActivityCompat.checkSelfPermission(login.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission_READ = ActivityCompat.checkSelfPermission(login.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission_WRITE != PackageManager.PERMISSION_GRANTED || permission_READ != PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(login.this,PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
        }
////////////
        System.out.println(uid+"*************************");













    }
    //region 当重新进入该activity后，需要重新初始化一下
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    //加载listview
    void init(){

    }
}


