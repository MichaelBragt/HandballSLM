package com.handballmanager;

import com.handballmanager.utils.UIErrorReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DBConnect {

    UNIQUE_CONNECT;

    private static final String DATABASE = "HandballApp";
    private static final String USER = "handballUser";
    private static final String PASSWORD = "test";
    private static final String CONNECT_STRING = "jdbc:sqlserver://localhost:1433;" +
                                                 "databaseName=" + DATABASE + ";" +
                                                 "encrypt=true;" +
                                                 "trustServerCertificate=true;" +
                                                 "loginTimeout=30";

    private Connection connection;

    DBConnect() {
        try {
            connection = DriverManager.getConnection(CONNECT_STRING, USER, PASSWORD);
            System.out.println("Database connected");
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Databse connection is closed");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


