package com.anurastores.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Properties settings = DatabaseInitializer.loadSettings();

            String url = settings.getProperty("url");
            String username = settings.getProperty("username");
            String password = settings.getProperty("password");

            if (url == null || username == null || password == null) {
                throw new SQLException(
                        "Database configuration is incomplete. "
                                + "Please run database setup again.");
            }

            return DriverManager.getConnection(url, username, password);

        } catch (IOException e) {
            throw new SQLException(
                    "Database configuration was not found. "
                            + "Please run database setup first.",
                    e);
        }
    }
}
