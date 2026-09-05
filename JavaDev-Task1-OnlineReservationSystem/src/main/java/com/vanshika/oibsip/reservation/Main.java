package com.vanshika.oibsip.reservation;

import com.vanshika.oibsip.reservation.util.DBConnection;

import static com.vanshika.oibsip.reservation.util.PNRGenerator.getPNR;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        try (Connection connection = DBConnection.getConnection()) {

            System.out.println("Database Connected Successfully!");

        } catch (Exception e) {

            System.out.println("Database Connection Failed!");
            e.printStackTrace();

        }
    }
}
