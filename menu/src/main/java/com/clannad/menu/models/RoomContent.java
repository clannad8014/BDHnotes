package com.clannad.menu.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RoomContent {
    public String uid,xcontent,xtime;
    public int rid,xnum;

    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getXcontent() {
        return xcontent;
    }

    public void setXcontent(String xcontent) {
        this.xcontent = xcontent;
    }

    public String getXtime() {
        return xtime;
    }

    public void setXtime(String xtime) {
        this.xtime = xtime;
    }

    public int getXnum() {
        return xnum;
    }

    public void setXnum(int xnum) {
        this.xnum = xnum;
    }
    public RoomContent(){
        Date date=new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        this.xtime=dateStr;
    }
}
