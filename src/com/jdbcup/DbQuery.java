package com.jdbcup;

import java.sql.*;
import java.util.ArrayList;

public class DbQuery implements AutoCloseable
{
    private StringBuilder buffer;
    private final Connection connection;
    private PreparedStatement preparedStatement;
    private final ArrayList<Object> preparedValues;
    private boolean update = false;

    public static DbQuery create(Connection connection)
    {
        return new DbQuery(connection);
    }

    private DbQuery(Connection connection)
    {
        this.connection = connection;
        preparedValues = new ArrayList<>();
    }

    public DbQuery select(String ... columns)
    {
        update = false;
        buffer = new StringBuilder();
        if(columns.length == 0)
        {
            buffer.append("select * ");
        }
        else
        {
            buffer.append("select ").append(columns[0]).append(" ");
            for(int i = 1; i < columns.length; i++)
            {
                buffer.append(", ").append(columns[i]).append(" ");
            }
        }
        return this;
    }

    public DbQuery insert_into(String table, String ... columns)
    {
        update = true;
        buffer = new StringBuilder();
        buffer.append("insert into ").append(table).append(" ");
        if(columns.length != 0)
        {
            buffer.append("(").append(columns[0]).append(" ");
            for(int i = 1; i < columns.length; i++)
            {
                buffer.append(", ").append(columns[i]).append(" ");
            }
            buffer.append(") ");
        }
        return this;
    }

    public DbQuery update_set(String table)
    {
        update = true;
        buffer = new StringBuilder();
        buffer.append("update ").append(table).append(" set ");
        return this;
    }

    public DbQuery delete()
    {
        update = true;
        buffer = new StringBuilder();
        buffer.append("delete ");
        return this;
    }

    public DbQuery values(Object ... values)
    {
        buffer.append("values(?");
        preparedValues.add(values[0]);
        for(int i = 1; i < values.length; i++)
        {
            buffer.append(", ").append("?");
            preparedValues.add(values[i]);
        }
        buffer.append(") ");
        return this;
    }

    public DbQuery from(String table)
    {
        buffer.append("from ").append(table).append(" ");
        return this;
    }

    public DbQuery where()
    {
        buffer.append("where ");
        return this;
    }

    public DbQuery isNull(String column)
    {
        buffer.append(column).append("=null ");
        return this;
    }

    public <T> DbQuery equal(String column, T value)
    {
        buffer.append(column).append("=? ");
        preparedValues.add(value);
        return this;
    }

    public <T> DbQuery greater(String column, T value)
    {
        buffer.append(column).append(">? ");
        preparedValues.add(value);
        return this;
    }

    public <T> DbQuery lesser(String column, T value)
    {
        buffer.append(column).append("<? ");
        preparedValues.add(value);
        return this;
    }

    public DbQuery and()
    {
        buffer.append("and ");
        return this;
    }

    public DbQuery or()
    {
        buffer.append("or ");
        return this;
    }

    public DbQuery not()
    {
        buffer.append("not ");
        return this;
    }

    public DbQuery openBlock()
    {
        buffer.append("( ");
        return this;
    }

    public DbQuery closeBlock()
    {
        buffer.append(") ");
        return this;
    }

    public DbQuery inner_join(String table)
    {
        buffer.append("inner join ").append(table).append(" ");
        return this;
    }

    public DbQuery on()
    {
        buffer.append("on ");
        return this;
    }

    public DbQuery using(String column)
    {
        buffer.append("using ").append(column).append(" ");
        return this;
    }

    public <T> DbQuery between(String column, Object value1, Object value2)
    {
        buffer.append(column).append(" between ? and ? ");
        preparedValues.add(value1);
        preparedValues.add(value2);
        return this;
    }

    public DbQuery order_by(String ... columns)
    {
        buffer.append(columns[0]).append(" ");
        for(int i = 1; i < columns.length; i++)
        {
            buffer.append(", ").append(columns[i]).append(" ");
        }
        return this;
    }

    public DbQuery asc()
    {
        buffer.append("asc ");
        return this;
    }

    public DbQuery desc()
    {
        buffer.append("desc ");
        return this;
    }

    public DbQuery innerQuery(DbQuery other)
    {
        preparedValues.addAll(other.getPreparedValues());
        openBlock();
        buffer.append(other.getBuffer());
        closeBlock();
        return this;
    }

    public DbQuery prepare()
    {
        try
        {
            preparedStatement = connection.prepareStatement(buffer.toString());
            int i = 1;
            for(Object obj : preparedValues)
            {
                if(obj == null)
                {
                    preparedStatement.setNull(i++, Types.NULL);
                    continue;
                }
                if(obj.getClass().equals(String.class))
                {
                    preparedStatement.setString(i++, (String)obj);
                }
                else if(obj.getClass().equals(Integer.class))
                {
                    preparedStatement.setInt(i++, (Integer)obj);
                }
                else if(obj.getClass().equals(Float.class))
                {
                    preparedStatement.setFloat(i++, (Float)obj);
                }
                else if(obj.getClass().equals(Double.class))
                {
                    preparedStatement.setDouble(i++, (Double)obj);
                }
                else if(obj.getClass().equals(Boolean.class))
                {
                    preparedStatement.setBoolean(i++, (Boolean)obj);
                }
                else if(obj.getClass().equals(Date.class))
                {
                    preparedStatement.setDate(i++, (Date)obj);
                }
            }
            close();
            System.out.println("Statement: " + preparedStatement);
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return this;
    }

    public DbResult execute()
    {
        try
        {
            if(update)
                preparedStatement.executeUpdate();
            else
                return DbResult.create().load(preparedStatement.executeQuery());
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        return null;
    }

    public DbResult prepareAndExecute()
    {
        return prepare().execute();
    }

    public StringBuilder getBuffer()
    {
        return buffer;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public PreparedStatement getPreparedStatement()
    {
        return preparedStatement;
    }

    public ArrayList<Object> getPreparedValues()
    {
        return preparedValues;
    }

    public boolean isUpdate()
    {
        return update;
    }

    @Override
    public void close()
    {
        buffer = new StringBuilder();
        preparedValues.clear();
        try
        {
            preparedStatement.close();
        } catch (SQLException throwables)
        {
            throwables.printStackTrace();
        }
        preparedStatement = null;
    }
}
