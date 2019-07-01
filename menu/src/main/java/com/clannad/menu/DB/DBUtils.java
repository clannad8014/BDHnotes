package com.clannad.menu.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * 数据库工具类：连接数据库用、获取数据库数据用
 * 相关操作数据库的方法均可写在该类
 */
public class DBUtils {

    private static String driver = "com.mysql.jdbc.Driver";// MySql驱动
    private static String dburl = "jdbc:mysql://188.131.255.217:3306/";// 数据库路径
    private static String dbname = "bdh_notes?useUnicode=true&characterEncoding=UTF-8";// 库名
    private static String user = "bdh_notes";// 用户名
    private static String password = "123456";// 密码

    public static Connection getConn(){

        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            connection = DriverManager.getConnection(dburl+ dbname,
                    user, password);
            System.out.println("连接成功");

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接失败");
        }

        return connection;
    }



}