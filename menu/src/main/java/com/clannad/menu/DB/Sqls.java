package com.clannad.menu.DB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.clannad.menu.models.*;
import static com.clannad.menu.DB.DBUtils.getConn;
public class Sqls {

    //查询某用户的所有笔记
    public  ArrayList<user_note_list> sel_user_note_list(String uid) throws SQLException {
        Connection conn = getConn();
        ArrayList<user_note_list> lists=new ArrayList<>();
        String sql= "select * from user_note_list where uid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        user_note_list u;
        while(rs.next()){
            u = new user_note_list();
            u.setUid(rs.getString("uid"));
            u.setBid(rs.getString("bid"));
            u.setTitle(rs.getString("title"));
            u.setCtime(rs.getString("ctime"));
            lists.add(u);
        }
        rs.close();
        psmt.close();
        return lists;
    }

    //查询某笔记某一行
    public note_content sel_hnum_content(String bid,String xhnum) throws SQLException{
        Connection conn = getConn();
        note_content content=new note_content();
        String sql= "select * from note_content where bid=? and xhnum=?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,bid);
        psmt.setString(2,xhnum);
        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        while(rs.next()){
            content.setBid(rs.getString("bid"));
            content.setXhnum(rs.getString("xhnum"));
            content.setXcontent(rs.getString("xcontent"));
            content.setXtime(rs.getString("xtime"));
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






}
