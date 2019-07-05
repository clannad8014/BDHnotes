package com.clannad.menu.FTP;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class File {

    public  void aboutTakePhotoUp(java.io.File photoFile) {
        try {
            FileInputStream in = new FileInputStream(photoFile);
            //将下面的信息换成自己需要的即可
            boolean flag = FileTool.uploadFile("188.131.255.217", 21,"haolayo_club", "TDiX5ph5kCKWsKia", "photo", photoFile.getName(), in);

            if (flag == true) {
                //handler.sendEmptyMessage(1);
            } else {
               // handler.sendEmptyMessage(2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
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
}
