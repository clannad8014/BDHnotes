package com.clannad.menu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clannad.menu.FTP.FileTool;
import com.clannad.menu.FTP.FileUtill;
import com.clannad.menu.models.*;
import com.clannad.menu.weight.CircleImageView;

import org.w3c.dom.Text;

import java.io.File;
import java.util.List;

public class UserAdapter extends ArrayAdapter<user> {

    private int resourceId;

    public UserAdapter(Context context, int textViewResourceId, List<user> objects) {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       user user= getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,null);

        CircleImageView uphoto=view.findViewById(R.id.main_uphoto);
        TextView uname=view.findViewById(R.id.main_uname);
        TextView uid=view.findViewById(R.id.main_uid);
        String url=user.getUphoto2()+user.getUphoto1();

        uid.setText(user.getUid());
        uname.setText(user.getUname());


        FileUtill f=new FileUtill();
        File file1=new File(url);
        //判断图片是否存在
        if(!file1.exists()){
            System.out.println("============不存在  下载图片--  ");
            //如果不存在  下载图片
            Boolean flag=f.aboutTakePhotoDown(user.getUphoto1(),"/storage/emulated/0/BDH.notes/content/");
            if(flag){
                System.out.println("============下载图片成功--  ");
            }else{
                System.out.println("============下载图片失败--  ");
            }

        }
        Bitmap pic1= BitmapFactory.decodeFile(url);
        uphoto.setImageBitmap(pic1);

        return view;
  }





}
