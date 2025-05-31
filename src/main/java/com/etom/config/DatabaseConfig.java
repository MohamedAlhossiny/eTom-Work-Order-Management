package com.etom.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/workorder_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    private static final int MAX_POOL_SIZE = 10;

    private static DataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(JDBC_URL);
                config.setUsername(USERNAME);
                config.setPassword(PASSWORD);
                config.setMaximumPoolSize(MAX_POOL_SIZE);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load MySQL driver", e);
            }
        }
        return dataSource;
    }
} 