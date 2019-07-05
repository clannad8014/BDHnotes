package com.clannad.menu.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    //查询某笔记最新内容
    public note_content sel_New_content(String bid) throws SQLException{
        Connection conn = getConn();
        note_content content=new note_content();
        String sql= "select * from note_content where bid=? order by xhnum asc";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,bid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            content.setBid(rs.getString("bid"));
            content.setXhnum(rs.getInt("xhnum"));
            content.setXcontent(rs.getString("xcontent"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content.setXtime(format.format( rs.getTimestamp("xtime")));
            content.setXid(rs.getString("xid"));
        }
        rs.close();
        psmt.close();
        return content;
    }

    //新建一个笔记
    public void addOneNote(user_note_list unl)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql= "insert into user_note_list"+
                "(uid,bid,title,ctime) "+
                "values(?,?,?,?)";//参数用?表示，相当于占位符;

        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);

        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1,unl.getUid());
        psmt.setString(2,unl.getBid());
        psmt.setString(3,unl.getTitle());
        psmt.setString(4,unl.getCtime());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }
    //初始化笔记内容
    public void startNoteContent(note_content nc)throws SQLException{
        //首先拿到数据库的连接
        Connection conn = getConn();
        String sql= "insert into note_content (bid,xhnum,xcontent,xtime,xid) values(?,0,?,?,?)";//参数用?表示，相当于占位符;

        //预编译sql语句
        PreparedStatement psmt = conn.prepareStatement(sql);

        //先对应SQL语句，给SQL语句传递参数
        psmt.setString(1,nc.getBid());
        psmt.setString(2,nc.getXcontent());
        psmt.setString(3,nc.getXtime());
        psmt.setString(4,nc.getXid());
        //执行SQL语句
        psmt.execute();
        psmt.close();
    }


    //删除某个笔记
    public void deleteOneNote(String bid) throws SQLException{
        Connection conn = getConn();
        String sql="delete from user_note_list where bid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,bid);
        //执行SQL语句
        psmt.execute();
        psmt.close();
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





}
