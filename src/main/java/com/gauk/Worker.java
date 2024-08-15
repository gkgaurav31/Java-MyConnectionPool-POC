package com.gauk;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class Worker implements Runnable{

    private DatabaseConnectionPool databaseConnectionPool;

    public Worker(DatabaseConnectionPool connectionPool){
        databaseConnectionPool = connectionPool;
    }

    @Override
    public void run() {

        String query = "SELECT NOW() AS `current_time`;";

        try {

            // get one of the connection from the pool
            Connection connection = databaseConnectionPool.getConnectionFromPool();

            // create a statement and execute the query
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // sleep for random amount of time, max 100ms
            Thread.sleep(new Random().nextInt(100));

            // return the connection back to the pool
            databaseConnectionPool.returnConnectionToPool(connection);

        } catch (InterruptedException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
