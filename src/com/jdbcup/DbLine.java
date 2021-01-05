package com.jdbcup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DbLine extends HashMap<String, Object>
{
    public DbLine load(ResultSet resultSet) throws SQLException
    {
        for(int i = 1; i < resultSet.getMetaData().getColumnCount(); i++)
        {
            put(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i));
        }
        return this;
    }
}
