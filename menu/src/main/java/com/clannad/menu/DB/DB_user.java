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


    //查询用户信息
    public static ArrayList<user> SearchUser(String uid) throws SQLException{

        Connection conn = getConn();
        String sql="select * from user where uid = ?";
        PreparedStatement psmt = conn.prepareStatement(sql);
        psmt.setString(1,uid);

        //执行SQL语句
        ResultSet rs = psmt.executeQuery();
        ArrayList<user> user = new ArrayList<user>();
        user u = new user();
        while(rs.next()){
            u = new user();
            u.setUname(rs.getString("uname"));
            u.setUinfo(rs.getString("uinfo"));
            u.setUphoto1(rs.getString("uphoto1"));
            u.setUphoto2(rs.getString("uphoto2"));
            u.setEmail(rs.getString("email"));
            user.add(u);
        }
        rs.close();
        psmt.close();
        return user;
    }

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
