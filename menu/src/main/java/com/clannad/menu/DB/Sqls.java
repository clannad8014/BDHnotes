package com.clannad.menu.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.clannad.menu.models.*;
import static com.clannad.menu.DB.DBUtils.getConn;
public class Sqls {

    //查询某用户的所有笔记
    public  ArrayList<show_list> sel_user_note_list(String uid) throws SQLException {
        Connection conn = getConn();
        ArrayList<show_list> lists=new ArrayList<>();
        String sql= "SELECT * from (select n.title,c.bid,c.xcontent,n.ctime from note_content c,user_note_list n where c.bid=n.bid and n.uid=? ORDER BY c.bid desc ,c.xhnum desc)b group by b.bid";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        show_list sl;
        while(rs.next()){
            sl=new show_list();
            sl.setBid(rs.getString("bid"));
            sl.setTitle(rs.getString("title"));
            sl.setA_content(rs.getString("xcontent"));
            sl.setCtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("ctime")));
            lists.add(sl);
        }
        rs.close();
        psmt.close();
        return lists;
    }

    //新建一个笔记
    public void addOneNote(user_note_list unl,note_content nc)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql1= "insert into user_note_list"+
                "(uid,bid,title,ctime) "+
                "values(?,?,?,?)";//参数用?表示，相当于占位符;
        String sql2= "insert into note_content (bid,xhnum,xcontent,xtime,xid) values(?,0,?,?,?)";

        //预编译sql语句
        PreparedStatement psmt1 = conn.prepareStatement(sql1);
        PreparedStatement psmt2 = conn.prepareStatement(sql2);

        //先对应SQL语句，给SQL语句传递参数
        psmt1.setString(1,unl.getUid());
        psmt1.setString(2,unl.getBid());
        psmt1.setString(3,unl.getTitle());
        psmt1.setString(4,unl.getCtime());

        psmt2.setString(1,nc.getBid());
        psmt2.setString(2,nc.getXcontent());
        psmt2.setString(3,nc.getXtime());
        psmt2.setString(4,nc.getXid());
        //执行SQL语句
        psmt1.execute();psmt2.execute();
        psmt1.close();psmt2.close();
    }

    //删除某个笔记
    public void deleteOneNote(String bid) throws SQLException{
        Connection conn = getConn();
        String sql1="delete from user_note_list where bid = ?";
        String sql2="delete from note_content where bid = ?";
        PreparedStatement psmt1 = conn.prepareStatement(sql1);
        PreparedStatement psmt2 = conn.prepareStatement(sql2);
        psmt1.setString(1,bid);
        psmt2.setString(1,bid);
        //执行SQL语句
        psmt1.execute(); psmt2.execute();
        psmt1.close();psmt2.close();
    }

    //更改笔记标题
    public void updateNote(user_note_list unl)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql="update user_note_list set title = ? where bid = ? ";
        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);
        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1,unl.getTitle());
        psmt.setString(2,unl.getBid());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }





    //增量式 存储当前文本内容
    public void addOneNoteContent(note_content nc)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql= "insert into note_content set bid=?,xhnum=(SELECT a.xhnum+1 from(select xhnum from note_content where bid=? ORDER BY xhnum desc LIMIT 1)a),xcontent=?,xtime=?,xid=?";

        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);

        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1,nc.getBid());
        psmt.setString(2,nc.getBid());
        psmt.setString(3,nc.getXcontent());
        psmt.setString(4,nc.getXtime());
        psmt.setString(5,nc.getXid());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }

    //查询某笔记所有历史
    public  ArrayList<note_content> selAllNoteContent(String bid) throws SQLException {
        Connection conn = getConn();
        ArrayList<note_content> lists=new ArrayList<>();
        String sql= "select * from note_content where bid = ? and not xhnum=0 order by xhnum desc";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,bid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        note_content n;
        while(rs.next()){
            n = new note_content();
            n.setBid(rs.getString("bid"));
            n.setXhnum(rs.getInt("xhnum"));
            n.setXcontent(rs.getString("xcontent"));
            n.setXtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("xtime")));
            n.setXid(rs.getString("xid"));
            lists.add(n);
        }
        rs.close();
        psmt.close();
        return lists;
    }

    //删除某条历史
    public void deleteOneNoteHistory(note_content nc) throws SQLException{
        Connection conn = getConn();
        String sql="delete from note_content where bid = ? and xhnum=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,nc.getBid());
        psmt.setInt(2,nc.getXhnum());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }

    //查询某条历史
    public  note_content selOneNoteContent(note_content nc) throws SQLException {
        Connection conn = getConn();
        String sql= "select * from note_content where bid = ? and xhnum=? ";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,nc.getBid());
        psmt.setInt(2,nc.getXhnum());
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        note_content n= new note_content();
        while(rs.next()){
            n.setBid(rs.getString("bid"));
            n.setXhnum(rs.getInt("xhnum"));
            n.setXcontent(rs.getString("xcontent"));
            n.setXtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("xtime")));
            n.setXid(rs.getString("xid"));
        }
        rs.close();
        psmt.close();
        return n;
    }

    //查询自己加入的房间列表
    public  ArrayList<Room> selAllJoinRoom(String uid) throws SQLException {
        Connection conn = getConn();
        String sql= "select * FROM room where rid in (select rid from room_content where uid=?) and rboss !=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);
        psmt.setString(2,uid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        ArrayList<Room> arrayList=new ArrayList<>();
        Room room =null;
        while(rs.next()){
            room=new Room();
            room.setRid(rs.getInt("rid"));
            room.setRtitle(rs.getString("rtitle"));
            room.setRtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("rtime")));
            room.setRboss(rs.getString("rboss"));
            arrayList.add(room);
        }
        rs.close();
        psmt.close();
        return arrayList;
    }

    //查询自己创建的房间列表
    public  ArrayList<Room> selAllAddRoom(String uid) throws SQLException {
        Connection conn = getConn();
        String sql= "select * FROM room where rboss =?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        ArrayList<Room> arrayList=new ArrayList<>();
        Room room =null;
        while(rs.next()){
            room=new Room();
            room.setRid(rs.getInt("rid"));
            room.setRtitle(rs.getString("rtitle"));
            room.setRtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("rtime")));
            room.setRboss(rs.getString("rboss"));
            arrayList.add(room);
        }
        rs.close();
        psmt.close();
        return arrayList;
    }

    //添加一条记录
    public void addOneRecord(RoomContent rc)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql= "insert into room_content set rid=?,uid=?,xcontent=?,xnum=(SELECT a.xnum+1 from(select xnum from room_content where rid=? and uid=? ORDER BY xnum desc LIMIT 1)a),xtime=?";//参数用?表示，相当于占位符;
        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);

        //先对应SQL语句，给SQL语句传递参数
        psmt.setInt(1,rc.getRid());
        psmt.setString(2,rc.getUid());
        psmt.setString(3,rc.getXcontent());
        psmt.setInt(4,rc.getRid());
        psmt.setString(5,rc.getUid());
        psmt.setString(6,rc.getXtime());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }

    //查询是否有该房间
    public Room selRoom(int rid) throws SQLException {
        Connection conn = getConn();
        ArrayList<Room> lists=new ArrayList<>();
        String sql= "select * from room where rid=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1, rid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        Room room=new Room();
        while(rs.next()){
            room.setRid(rs.getInt("rid"));
            room.setRtitle(rs.getString("rtitle"));
            room.setRtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("rtime")));
            room.setRpwd(rs.getString("rpwd"));
            room.setRboss(rs.getString("rboss"));
        }
        rs.close();
        psmt.close();
        return room;
    }
    //查询是否已经在该房间
    public void addOneMember(RoomContent roomContent)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql= "insert into room_content set rid=?,uid=?,xcontent='',xnum=0,xtime=?";

        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);

        //先对应SQL语句，给SQL语句传递参数
        psmt.setInt(1,roomContent.getRid());
        psmt.setString(2,roomContent.getUid());
        psmt.setString(3,roomContent.getXtime());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }

    //查询房间所有内容
    public  ArrayList<RoomContent> selAllRoomContent(int rid) throws SQLException {
        Connection conn = getConn();
        String sql= "select * FROM room_content where rid =? and xcontent !='' and xnum !=0 order by xtime";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,rid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        ArrayList<RoomContent> arrayList=new ArrayList<>();
        RoomContent rc =null;
        while(rs.next()){
            rc=new RoomContent();
            rc.setRid(rs.getInt("rid"));
            rc.setUid(rs.getString("uid"));
            rc.setXcontent(rs.getString("xcontent"));
            rc.setXnum(rs.getInt("xnum"));
            rc.setXtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("xtime")));
            arrayList.add(rc);
        }
        rs.close();
        psmt.close();
        return arrayList;
    }

    //新建房间
    public void addOneRoom(Room ro)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql1= "insert into room set rtitle=?,rtime=?,rid=(SELECT a.rid+1 from(select rid from room  ORDER BY rid desc LIMIT 1)a),rpwd=?,rboss=?";//参数用?表示，相当于占位符;
        //预编译sql语句
        PreparedStatement psmt1 = conn.prepareStatement(sql1);

        //先对应SQL语句，给SQL语句传递参数
        psmt1.setString(1,ro.getRtitle());
        psmt1.setString(2,ro.getRtime());
        psmt1.setString(3,ro.getRpwd());
        psmt1.setString(4,ro.getRboss());

        //执行SQL语句
        psmt1.execute();
        psmt1.close();
    }
    //查询房间信息
    public Room selLastRoom(String rboss) throws SQLException {
        Connection conn = getConn();
        String sql= "select * from room where rboss=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1, rboss);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        Room room=new Room();
        while(rs.next()){
            room.setRid(rs.getInt("rid"));
            room.setRtitle(rs.getString("rtitle"));
            room.setRtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("rtime")));
            room.setRpwd(rs.getString("rpwd"));
            room.setRboss(rs.getString("rboss"));
        }
        rs.close();
        psmt.close();
        return room;
    }

    //查询房间里的某个人的历史版本
    public ArrayList<RoomContent> selOneMemberRoomContent(RoomContent rc) throws SQLException {
        Connection conn = getConn();
        ArrayList<RoomContent> roomContents=new ArrayList<>();
        String sql= "select * from room_content where rid=? and uid=? and xnum !=0 order by xnum desc";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,rc.getRid());
        psmt.setString(2, rc.getUid());
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        RoomContent roomContent=null;
        while(rs.next()){
            roomContent=new RoomContent();
            roomContent.setRid(rs.getInt("rid"));
            roomContent.setUid(rs.getString("uid"));
            roomContent.setXcontent(rs.getString("xcontent"));
            roomContent.setXnum(rs.getInt("xnum"));
            roomContent.setXtime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rs.getTimestamp("xtime")));
            roomContents.add(roomContent);
        }
        rs.close();
        psmt.close();
        return roomContents;
    }
    //修改某条在线历史
    public void updateOneOnline(RoomContent rc)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql="update room_content set xcontent=? where rid=? and uid=? and xnum=? ";
        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);
        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1,rc.getXcontent());
        psmt.setInt(2,rc.getRid());
        psmt.setString(3,rc.getUid());
        psmt.setInt(4,rc.getXnum());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }


    //删除某条在线历史
    public void deleteOneOnline(RoomContent rc) throws SQLException{
        Connection conn = getConn();
        String sql="delete from room_content where rid=? and uid=? and xnum=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setInt(1,rc.getRid());
        psmt.setString(2,rc.getUid());
        psmt.setInt(3,rc.getXnum());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }









}
