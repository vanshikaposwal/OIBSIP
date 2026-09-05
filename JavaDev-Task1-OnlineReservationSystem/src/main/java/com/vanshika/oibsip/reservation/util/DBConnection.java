package com.vanshika.oibsip.reservation.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final static String URL = "jdbc:mysql://127.0.0.1:3306/online_reservation_system";
    private final static String USER = "root";
    private final static String PASSWORD = "1234";

    public static Connection getConnection(){
        
        try{
            return DriverManager.getConnection(URL, USER, PASSWORD);

        }catch (SQLException e){

            System.out.println("Database Connection failed!!! ");
            e.printStackTrace();
            return null;
        }
    }
}
