package com.clannad.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clannad.menu.DB.DB_user;

import java.sql.SQLException;

public class Regist_Activity extends AppCompatActivity {
    EditText uname;
    EditText pwd,Rpwd; //et_username
    Button Regist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      // setContentView(R.layout.activity_register);
        setContentView(R.layout.activity_register);
        uname=findViewById(R.id.et_username);
        pwd=findViewById(R.id.et_password);
        Rpwd=findViewById(R.id.et_password);
        Regist=findViewById(R.id.btn_register);
        Regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //str.trim().equalsI/System.out: name= pwd=(pwd)
                System.out.println("uname:("+uname.getText()+") pwd:("+ pwd.getText()+") Rpwd:("+Rpwd.getText()+")");
                if(uname.getText().toString().trim().length()>0 && pwd.getText().toString().trim().length()>0 && Rpwd.getText().toString().trim().length()>0 ){
                    if(pwd.getText().toString().equals(Rpwd.getText().toString())){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    System.out.println("注册..........");
                                    DB_user db=new DB_user();
                                    db.Regist(uname.getText().toString(),pwd.getText().toString());
                                    System.out.println("注册成功..........");
                                    Intent intent = new Intent(Regist_Activity.this, Login.class);
                                    startActivity(intent);
                                } catch (SQLException e) {
                                    e.printStackTrace();

                                }


                            }
                        }).start();

                    }else{
                        System.out.println("两次密码不相等..........");
                    }
                }else{
                    System.out.println("不能为空..........");
                }
            }
        });
    }

}
