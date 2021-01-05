package com.jdbcup;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class DbResult
{
    private ArrayList<DbLine> lines = new ArrayList<>();

    public static DbResult create()
    {
        return new DbResult();
    }

    public DbResult load(ResultSet resultSet) throws SQLException
    {
        lines.clear();
        while(resultSet.next())
        {
            lines.add(new DbLine().load(resultSet));
        }
        resultSet.close();
        return this;
    }

    public ArrayList<DbLine> getLines()
    {
        return lines;
    }
}
