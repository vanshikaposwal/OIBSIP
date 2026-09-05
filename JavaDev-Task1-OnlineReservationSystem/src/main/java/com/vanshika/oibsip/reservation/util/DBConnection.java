package com.vanshika.oibsip.reservation.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static String url;
    private static String username;
    private static String password;

    static {

        try {

            // Try environment variables first
            url = System.getenv("DB_URL");
            username = System.getenv("DB_USERNAME");
            password = System.getenv("DB_PASSWORD");


            // If environment variables are missing,
            // load db.properties
            if (url == null || username == null || password == null) {

                Properties properties = new Properties();

                try (InputStream input =
                             DBConnection.class
                                     .getClassLoader()
                                     .getResourceAsStream("db.properties")) {

                    if (input == null) {
                        throw new RuntimeException(
                                "db.properties file not found!"
                        );
                    }

                    properties.load(input);

                    url = properties.getProperty("db.url");
                    username = properties.getProperty("db.username");
                    password = properties.getProperty("db.password");
                }
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load database configuration.", e
            );
        }
    }


    public static Connection getConnection()
            throws SQLException {

        if (url == null ||
                username == null ||
                password == null) {

            throw new SQLException(
                    "Database configuration is missing."
            );
        }

        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }
}