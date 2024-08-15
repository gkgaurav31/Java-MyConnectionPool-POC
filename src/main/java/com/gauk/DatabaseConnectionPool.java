package com.gauk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// database connection pool POC using queue
public class DatabaseConnectionPool {

    private Queue<Connection> connectionPool;
    private int connectionPoolSizeLimit;

    public DatabaseConnectionPool(String connectionString, int connectionPoolSizeLimit){

        connectionPool = new ConcurrentLinkedQueue<>();
        this.connectionPoolSizeLimit = connectionPoolSizeLimit;

        initializeConnectionPool(connectionString);

        System.out.println("Connection Pool Initialized!");

    }

    private void initializeConnectionPool(String connectionString){

        for(int i=0; i<connectionPoolSizeLimit; i++){
            connectionPool.add(createDBConnection(connectionString));
        }

    }

    public int getActiveConnectionCount(){
        return connectionPool.size();
    }

    public Connection createDBConnection(String connectionString){

        try {

            Connection conn = DriverManager.getConnection(connectionString);
            return conn;


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    // use connection
    public synchronized Connection getConnectionFromPool() throws InterruptedException {

        if(connectionPool.size() == 0){
            wait();
        }

        notifyAll();

        return connectionPool.poll();

    }

    // return
    public synchronized void returnConnectionToPool(Connection connection) throws InterruptedException {
        connectionPool.add(connection);
        notifyAll();
    }

}
