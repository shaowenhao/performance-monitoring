package com.siemens.datalayer.iot.util;

import java.io.IOException;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// 获取到db.properties文件中的数据库信息
public class JdbcMysqlUtil {
    // 私有变量
    private static String driver;
    private static String url;
    private static String user;
    private static String password;

    // 返回数据库连接
    public static Connection getConnection(String file){
        try{
            // 1.新建属性集对象
            Properties properties = new Properties();

            // 2.通过反射，新建字符输入流，读取db.properties文件
            InputStream input = JdbcMysqlUtil.class.getClassLoader().getResourceAsStream("db_properties/" + file);

            // 3.将输入流中读取到的属性，加载到properties属性集对象中
            properties.load(input);

            // 4.根据键，获取properties中对应的值
            driver = properties.getProperty("driver");
            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

        }catch (IOException e){
            e.printStackTrace();
        }

        try {
            System.out.println(String.format(" SQL connection config %s %s %s %s", driver, url, user, password));
            // 注册数据库的驱动
            Class.forName(driver);

            // 获取数据库连接（里面内容依次是：主机名和端口、用户名、密码）
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println(String.format("%s", connection == null ? "null": connection.toString()));
            // 返回数据库连接
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

