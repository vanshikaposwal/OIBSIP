package com.vanshika.oibsip.reservation.ui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {

        setTitle("Online Reservation System - Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 45, 65));
        header.setPreferredSize(new Dimension(800, 80));

        JLabel title = new JLabel("  Online Reservation System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // Center
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(2, 2, 25, 25));
        center.setBorder(BorderFactory.createEmptyBorder(
                40, 60, 40, 60
        ));
        center.setBackground(new Color(245, 247, 250));

        JButton searchTrainButton = new JButton("Search Trains");
        JButton reservationButton = new JButton("Book Ticket");
        JButton cancellationButton = new JButton("Cancel Ticket");
        JButton historyButton = new JButton("Booking History");

        styleButton(searchTrainButton);
        styleButton(reservationButton);
        styleButton(cancellationButton);
        styleButton(historyButton);

        center.add(searchTrainButton);
        center.add(reservationButton);
        center.add(cancellationButton);
        center.add(historyButton);

        mainPanel.add(center, BorderLayout.CENTER);

        // Bottom - Back to Login / Logout
        JPanel bottom = new JPanel();
        bottom.setBackground(new Color(245, 247, 250));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JButton backButton = new JButton("Logout");
        backButton.setPreferredSize(new Dimension(140, 38));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(100, 116, 139));
        backButton.setForeground(Color.BLACK);

        bottom.add(backButton);

        mainPanel.add(bottom, BorderLayout.SOUTH);

        // Button actions
        // Flow: Dashboard -> Train Search -> Select Train -> Reserve Ticket
        searchTrainButton.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });

        reservationButton.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });

        cancellationButton.addActionListener(e -> {
            new CancellationFrame().setVisible(true);
            dispose();
        });

        historyButton.addActionListener(e -> {
            new HistoryFrame().setVisible(true);
            dispose();
        });

        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private void styleButton(JButton button) {

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
    }
}