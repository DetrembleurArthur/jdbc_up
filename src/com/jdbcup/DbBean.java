package com.jdbcup;

import java.io.Serializable;
import java.sql.Connection;

public class DbBean implements Serializable
{
    private final String table;
    private final transient Connection connection;

    public DbBean(String table, Connection connection)
    {
        this.table = table;
        this.connection = connection;
    }

    public String getTable()
    {
        return table;
    }

    public Connection getConnection()
    {
        return connection;
    }
}
