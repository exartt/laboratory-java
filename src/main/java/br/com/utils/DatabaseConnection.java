package br.com.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://babar.db.elephantsql.com:5432/ysetqhyu";
    private static final String USER = "ysetqhyu";
    private static final String PASSWORD = "7eQOGqqkFp3HH3Fy0-32Zt_Z04f5Pg46";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
