package com.clannad.menu.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Room {
    public int getRid() {
        return rid;
    }

    public void setRid(int rid) {
        this.rid = rid;
    }

    int rid;

    public String getRtitle() {
        return rtitle;
    }

    public void setRtitle(String rtitle) {
        this.rtitle = rtitle;
    }

    public String getRtime() {
        return rtime;
    }

    public void setRtime(String rtime) {
        this.rtime = rtime;
    }

    public String getRpwd() {
        return rpwd;
    }

    public void setRpwd(String rpwd) {
        this.rpwd = rpwd;
    }

    public String getRboss() {
        return rboss;
    }

    public void setRboss(String rboss) {
        this.rboss = rboss;
    }

    public String rtitle,rtime,rpwd,rboss;
    public Room(){
        Date date=new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        this.rtime=dateStr;
    }


}
