package com.gauk;

import java.sql.*;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class App {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Connection Pool Test App!");

        // get db connection string from env variable
        String connectionString = System.getenv("DB_CONNECTION_STRING");

        App app = new App();

        // setup connection pool
        DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool(connectionString, 10);

        // keep creating new threads which run a query on DB
        // it will use one connection from the connection pool; if there are no connections left, the thread will get blocked
        // after it's done, it will return that connection back to the pool
        while(true){

            Thread t = new Thread(new Worker(databaseConnectionPool));
            t.start();

            // sleep for some time to avoid create a lot of thread in a very short time
            Thread.sleep(new Random().nextInt(100));

            System.out.println("number of active connections: " + databaseConnectionPool.getActiveConnectionCount());

        }

    }

}
