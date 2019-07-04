package com.clannad.menu.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class user_note_list {

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    String uid,bid,title,ctime;
    public user_note_list(){}
    public user_note_list(String uid){
        this.uid=uid;
        Date date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        String d=dateFormat.format(date);
        this.ctime=dateStr;
        this.bid=uid+"-"+d;
        this.title="New Note";
    }
}
