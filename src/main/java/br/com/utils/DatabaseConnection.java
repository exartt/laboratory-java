package br.com.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://babar.db.elephantsql.com:5432/ysetqhyu";
    private static final String USER = "ysetqhyu";
    private static final String PASSWORD = "7eQOGqqkFp3HH3Fy0-32Zt_Z04f5Pg46";
    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);

        config.setMaximumPoolSize(9);

        dataSource = new HikariDataSource(config);
    }


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
