package com.vanshika.oibsip.reservation;

import com.vanshika.oibsip.reservation.ui.LoginFrame;
import com.vanshika.oibsip.reservation.util.DBConnection;

import javax.swing.*;

import static com.vanshika.oibsip.reservation.util.PNRGenerator.getPNR;

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });


    }
}
