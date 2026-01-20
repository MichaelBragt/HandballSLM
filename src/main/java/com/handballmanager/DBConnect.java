package com.handballmanager;

import com.handballmanager.utils.UIErrorReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DBConnect {

    /**
     * Our ENUM can only have this ONE constant
     * Therefore this makes out ENUM into singleton
     * database connection, so we are sure we always only have this one connection
     */
    UNIQUE_CONNECT;

    /**
     * WE COULD make these as .env variables
     * But that is for improvements
     * But should ALWAYS be in a live environment
     */
    private static final String DATABASE = "HandballApp";
    private static final String USER = "handballUser";
    private static final String PASSWORD = "test";
    private static final String CONNECT_STRING = "jdbc:sqlserver://localhost:1433;" +
                                                 "databaseName=" + DATABASE + ";" +
                                                 "encrypt=true;" +
                                                 "trustServerCertificate=true;" +
                                                 "loginTimeout=30";

    private Connection connection;

    /**
     * connection is created when ENUM is used the first time at runtime
     */
    DBConnect() {
        try {
            connection = DriverManager.getConnection(CONNECT_STRING, USER, PASSWORD);
        }
        catch (SQLException e) {
            UIErrorReport.showDatabaseError(e);
            e.printStackTrace();
        }
    }

    /**
     * method to get connection
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    // close database connection method
    // just in case we need it :-)
    public void close() {
        try {
            if(connection != null && !connection.isClosed()) {
                connection.close();
//                System.out.println("Database connection is closed");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


