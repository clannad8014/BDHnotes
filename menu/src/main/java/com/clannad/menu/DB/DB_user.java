package com.clannad.menu.DB;

import com.clannad.menu.models.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.clannad.menu.DB.DBUtils.getConn;

public class DB_user {

    Connection conn = null ;
    ResultSet rs = null ;
    PreparedStatement psmt= null ;
    Statement stmt =null;


    public static ArrayList<user> SearchId(String uid) throws SQLException{

        Connection conn = getConn();
        String sql="" +
                "select pwd from user where uid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);

        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        ArrayList<user> user = new ArrayList<user>();
        user u = new user();
        while(rs.next()){
            u = new user();
            u.setPwd(rs.getString("pwd"));
            user.add(u);
        }
        return user;
    }
    /*
    public static ArrayList<user> SearchUpw(String uname) throws SQLException{
        //conn=MyDBManger.getDBManger().getconnection();
        Connection conn = getConn("haolayo_club");
        Statement stmt = conn.createStatement();
       // ResultSet rs =  stmt.executeQuery("select pwd from files_user where uid=?");
        String sql="select pwd from files_user where uid=?";
        PreparedStatement ps = conn.prepareStatement(sql);

//                if (ps != null){
//                    // 设置上面的sql语句中的？的值为name
//                   // ps.setString(1, name);
//                    // 执行sql查询语句并返回结果集
        ArrayList<test> test = new ArrayList<test>();
        user u = null;
        while(ps.next()){//如果对象中有数据，就会循环打印出来
            u = new user();
            u.setUid(ps.getString("uid"));
            user.add(u);
        }

        String sql="select pwd from files_user where uid=?";
        psmt = conn.prepareStatement(sql);
        psmt.setString(1, uname);
        //执行SQL语句
        rs = psmt.executeQuery();
        ArrayList<user> user = new ArrayList<user>();
        user u = null;
        while(rs.next()){//如果对象中有数据，就会循环打印出来
            u = new user();
            u.setUid(rs.getString("uid"));
            user.add(u);
        }
        conn.close();
        rs.close();
        psmt.close();

        return user;


    }
    */
    public static ArrayList<test> login() throws SQLException {

        //Connection conn = DBUtil.getConnection();
        Connection conn = getConn();
        Statement stmt = conn.createStatement();
        ResultSet rs =  stmt.executeQuery("select * from user");
        ArrayList<test> test = new ArrayList<test>();
        test b = null;
        while(rs.next()){//如果对象中有数据，就会循环打印出来
            b = new test();
            b.setUid(rs.getString("uid"));
            b.setPwd(rs.getString("pwd"));
            b.setUphone(rs.getInt("uphone"));
            test.add(b);
        }
        return test;
    }
}
