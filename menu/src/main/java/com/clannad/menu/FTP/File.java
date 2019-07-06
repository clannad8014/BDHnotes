package com.clannad.menu.FTP;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class File {
    String creatTime;

    //上传图片
    public  Boolean aboutTakePhotoUp(java.io.File photoFile) {
        try {
            FileInputStream in = new FileInputStream(photoFile);
            //将下面的信息换成自己需要的即可
            boolean flag = FileTool.uploadFile("188.131.255.217", 21,"haolayo_club", "TDiX5ph5kCKWsKia", "photo", photoFile.getName(), in);

            if (flag == true) {
                return true;
                //handler.sendEmptyMessage(1);
            } else {
                return false;
               // handler.sendEmptyMessage(2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
             return false;
        }

    }

    //下载图片
    public boolean aboutTakePhotoDown(String fileName,String localPath) {

        //    boolean flag = FileTool.uploadFile("FTP服务器hostname", 21,"登录名", "登录密码", "要传入的文件夹名字(若服务器没有这个文件夹，可以自动建文件夹，无需手动建)", photoFile.getName(), in);
        // /storage/emulated/0/HelloNotes/
//        boolean flag = FileTool.downloadFile("188.131.255.217", 21,"haolayo_club", "TDiX5ph5kCKWsKia", "photo","img13.jpg",
//                "/storage/emulated/0/BDH.notes");
        boolean flag = FileTool.downloadFile("188.131.255.217", 21,"haolayo_club", "TDiX5ph5kCKWsKia", "photo",
                fileName,localPath);
        if (flag == true) {
                return true;
           // handler.sendEmptyMessage(3);
        } else {
               return false;
          //  handler.sendEmptyMessage(4);
        }
    }


    private void gainCurrenTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
         creatTime = formatter.format(curDate);
    }






    //=========删除手机文件目录====================================
    public void deletePhotoWithPath(String path) {
        if (path != null && path.length() > 0) {
            java.io.File file = new java.io.File(path);

            if (file.exists()) { // 判断文件是否存在
                if (file.isFile()) { // 判断是否是文件
                    file.delete(); // delete()方法 你应该知道 是删除的意思;
                } else if (file.isDirectory()) { // 否则如果它是一个目录
                    java.io.File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                    for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                        deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                    }
                }
                file.delete();
            }
        }

    }

    public void deleteFile(java.io.File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                java.io.File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        }
    }

}
