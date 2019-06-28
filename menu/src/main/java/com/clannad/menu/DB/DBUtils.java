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

//    private static String url = "jdbc:mysql://localhost:3306/map_designer_test_db";

    private static String user = "root";// 用户名

    private static String password = "123456";// 密码

    public static Connection getConn(String dbName){

        Connection connection = null;
        try{
            Class.forName(driver);// 动态加载类
            String ip = "192.168.43.113";// 写成本机地址，不能写成localhost，同时手机和电脑连接的网络必须是同一个

            String URL = "jdbc:mysql://188.131.255.217:3306/";
            String USERNAME = "haolayo_club";
            String PASSWORD = "riheCjsM6A4X5TFJ";

            // 尝试建立到给定数据库URL的连接
//            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":8806/" + dbName,
//                    user, password);
            connection = DriverManager.getConnection(URL+ dbName,
                    "haolayo_club", "riheCjsM6A4X5TFJ");
            System.out.println("连接成功");

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("连接失败");
        }

        return connection;
    }



    public static ArrayList<test> getInfoByName() throws SQLException{

        //Connection conn = DBUtil.getConnection();
        Connection conn = getConn("haolayo_club");
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
//    public static HashMap<String, Object> getInfoByName(String name){
//
//        HashMap<String, Object> map = new HashMap<>();
//        // 根据数据库名称，建立连接
//       // Connection connection = getConn("map_designer_test_db");
//        Connection connection = getConn("haolayo_club");
//        try {
//            // mysql简单的查询语句。这里是根据MD_CHARGER表的NAME字段来查询某条记录
//          //  String sql = "select * from MD_CHARGER where NAME = ?";
//            String sql = "select * from user";
////            String sql = "select * from MD_CHARGER";
//            if (connection != null){// connection不为null表示与数据库建立了连接
//                /*
//                PreparedStatement ps = connection.prepareStatement(sql);
//                if (ps != null){
//                    // 设置上面的sql语句中的？的值为name
//                   // ps.setString(1, name);
//                    // 执行sql查询语句并返回结果集
//
//                    ResultSet rs = ps.executeQuery();
//                    if (rs != null){
//                        int count = rs.getMetaData().getColumnCount();
//                        Log.e("DBUtils","列总数：" + count);
//                        while (rs.next()){
//                            // 注意：下标是从1开始的
//                            for (int i = 1;i <= count;i++){
//                                String field = rs.getMetaData().getColumnName(i);
//                                map.put(field, rs.getString(field));
//                            }
//                        }
//                        */
//
//                Statement stmt = null;
//                try {
//                    stmt = connection.createStatement();
//                    ResultSet rs =  stmt.executeQuery(sql);
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//
//                        connection.close();
//                        ps.close();
//                        return  map;
//                    }else {
//                        return null;
//                    }
//                }else {
//                    return  null;
//                }
//            }else {
//                return  null;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            Log.e("DBUtils","异常：" + e.getMessage());
//            return null;
//        }
//
//    }

}