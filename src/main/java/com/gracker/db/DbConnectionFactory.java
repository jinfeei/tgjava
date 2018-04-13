package com.gracker.db;
import java.sql.*;

/**
 * Created by JinFeei on 7/15/2014.
 */
public class DbConnectionFactory {
    private static DbConnectionFactory connectionFactory=null;

    //private static String OS = System.getProperty("os.name").toLowerCase();
    private static String MYSQL_HOST;
    private static String MYSQL_USER;
    private static String MYSQL_PASSWORD;

    private DbConnectionFactory()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        MYSQL_HOST = System.getenv("MYSQL_HOST") == null ? "localhost" : System.getenv("MYSQL_HOST");
        MYSQL_USER = System.getenv("MYSQL_USER") == null ? "telegram_user" : System.getenv("MYSQL_USER");
        MYSQL_PASSWORD = System.getenv("MYSQL_PASSWORD") == null ? "d08080e" : System.getenv("MYSQL_PASSWORD");
    }

    //public static boolean isUnix()
    //{
    //    return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    //}

    public Connection getConnection() throws SQLException
    {
        Connection conn=null;

        conn=DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + ":3306/telegram?verifyServerCertificate=false&useSSL=true",MYSQL_USER,MYSQL_PASSWORD);
        return conn;
    }

    public static DbConnectionFactory getInstance()
    {
        if(connectionFactory==null)
        {
            connectionFactory=new DbConnectionFactory();
        }
        return connectionFactory;
    }
}
