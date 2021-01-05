package com.jdbcup;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector
{
    public static Connection connect(String propFile) throws SQLException
    {
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(propFile));
            return connect(properties.getProperty("database"), properties.getProperty("user"), properties.getProperty("password"));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection connect(String database, String user, String password) throws SQLException
    {
        System.err.println(database + " " + user + " " + password);
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + database + "?serverTimezone=UTC", user, password);
    }
}
