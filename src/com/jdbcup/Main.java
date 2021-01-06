package com.jdbcup;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException
    {
	    DbQuery query = DbQuery.create(DBConnector.connect("db_test", "root", "13Mysql53"));

	    DbResult result = query.select().from("users").prepareAndExecute();
	    System.out.println(result);
    }
}
