package com.vanshika.oibsip.reservation.ui;

import com.vanshika.oibsip.reservation.model.Reservation;
import com.vanshika.oibsip.reservation.service.ReservationService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CancellationFrame extends JFrame {

    private JTextField pnrField;
    private JLabel passengerNameValue;
    private JLabel trainNumberValue;
    private JLabel classTypeValue;
    private JLabel journeyDateValue;
    private JLabel routeValue;
    private JLabel bookingDateValue;

    private JButton fetchButton;
    private JButton cancelButton;
    private JButton backButton;

    private ReservationService reservationService;
    private Reservation currentReservation;

    public CancellationFrame() {

        reservationService = new ReservationService();

        setTitle("Online Reservation System - Cancel Ticket");
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ================= HEADER =================
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 45, 65));
        header.setPreferredSize(new Dimension(650, 75));

        JLabel title = new JLabel("  Cancel Ticket");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        header.add(title, BorderLayout.WEST);
        mainPanel.add(header, BorderLayout.NORTH);

        // ================= CENTER PANEL =================
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(null);
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // PNR search row
        JLabel pnrLabel = new JLabel("Enter PNR Number:");
        pnrLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pnrLabel.setBounds(50, 25, 160, 30);

        pnrField = new JTextField();
        pnrField.setBounds(210, 25, 200, 32);

        fetchButton = new JButton("Fetch Ticket");
        fetchButton.setBounds(425, 25, 130, 32);
        styleButton(fetchButton, new Color(37, 99, 235));

        centerPanel.add(pnrLabel);
        centerPanel.add(pnrField);
        centerPanel.add(fetchButton);

        // Details Panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(null);
        detailsPanel.setBounds(50, 80, 505, 240);
        detailsPanel.setBackground(new Color(248, 250, 252));
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                "Ticket Details",
                0,
                0,
                new Font("Arial", Font.BOLD, 13),
                new Color(51, 65, 85)
        ));

        JLabel pNameLabel = new JLabel("Passenger Name:");
        pNameLabel.setBounds(30, 30, 140, 25);
        passengerNameValue = new JLabel("-");
        passengerNameValue.setFont(new Font("Arial", Font.BOLD, 13));
        passengerNameValue.setBounds(180, 30, 300, 25);

        JLabel tNumLabel = new JLabel("Train Number:");
        tNumLabel.setBounds(30, 65, 140, 25);
        trainNumberValue = new JLabel("-");
        trainNumberValue.setFont(new Font("Arial", Font.BOLD, 13));
        trainNumberValue.setBounds(180, 65, 300, 25);

        JLabel classLabel = new JLabel("Class Type:");
        classLabel.setBounds(30, 100, 140, 25);
        classTypeValue = new JLabel("-");
        classTypeValue.setFont(new Font("Arial", Font.BOLD, 13));
        classTypeValue.setBounds(180, 100, 300, 25);

        JLabel jDateLabel = new JLabel("Date of Journey:");
        jDateLabel.setBounds(30, 135, 140, 25);
        journeyDateValue = new JLabel("-");
        journeyDateValue.setFont(new Font("Arial", Font.BOLD, 13));
        journeyDateValue.setBounds(180, 135, 300, 25);

        JLabel routeLabel = new JLabel("Route (From -> To):");
        routeLabel.setBounds(30, 170, 140, 25);
        routeValue = new JLabel("-");
        routeValue.setFont(new Font("Arial", Font.BOLD, 13));
        routeValue.setBounds(180, 170, 300, 25);

        JLabel bDateLabel = new JLabel("Booked On:");
        bDateLabel.setBounds(30, 205, 140, 25);
        bookingDateValue = new JLabel("-");
        bookingDateValue.setFont(new Font("Arial", Font.PLAIN, 12));
        bookingDateValue.setBounds(180, 205, 300, 25);

        detailsPanel.add(pNameLabel);
        detailsPanel.add(passengerNameValue);
        detailsPanel.add(tNumLabel);
        detailsPanel.add(trainNumberValue);
        detailsPanel.add(classLabel);
        detailsPanel.add(classTypeValue);
        detailsPanel.add(jDateLabel);
        detailsPanel.add(journeyDateValue);
        detailsPanel.add(routeLabel);
        detailsPanel.add(routeValue);
        detailsPanel.add(bDateLabel);
        detailsPanel.add(bookingDateValue);

        centerPanel.add(detailsPanel);

        // ================= BOTTOM BUTTONS =================
        cancelButton = new JButton("Confirm Cancellation");
        cancelButton.setBounds(130, 345, 180, 38);
        cancelButton.setEnabled(false);
        styleButton(cancelButton, new Color(218, 36, 36));

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(330, 345, 170, 38);
        styleButton(backButton, new Color(100, 116, 139));

        centerPanel.add(cancelButton);
        centerPanel.add(backButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ================= ACTION LISTENERS =================
        pnrField.addActionListener(e -> fetchTicket());
        fetchButton.addActionListener(e -> fetchTicket());

        cancelButton.addActionListener(e -> cancelTicket());

        backButton.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private void fetchTicket() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a PNR number.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            Reservation res = reservationService.getReservationByPNR(pnr);

            if (res != null) {
                currentReservation = res;
                passengerNameValue.setText(res.getPassengerName());
                trainNumberValue.setText(String.valueOf(res.getTrainNumber()));
                classTypeValue.setText(res.getClassType());

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                journeyDateValue.setText(res.getJourneyDate() != null ? res.getJourneyDate().format(dateFormatter) : "-");

                routeValue.setText(res.getSourceStation() + " -> " + res.getDestinationStation());

                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                bookingDateValue.setText(res.getBookingDate() != null ? res.getBookingDate().format(timeFormatter) : "-");

                cancelButton.setEnabled(true);

            } else {
                clearDetails();
                JOptionPane.showMessageDialog(
                        this,
                        "No reservation found for PNR: " + pnr,
                        "Ticket Not Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void cancelTicket() {
        if (currentReservation == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fetch a valid ticket first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the ticket for:\n" +
                        "PNR: " + currentReservation.getPnrNumber() + "\n" +
                        "Passenger: " + currentReservation.getPassengerName() + "\n" +
                        "Train: " + currentReservation.getTrainNumber(),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean success = reservationService.cancelTicket(currentReservation.getPnrNumber());

            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ticket cancelled successfully!\nThe seat has been returned to available inventory.",
                        "Cancellation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE
                );
                pnrField.setText("");
                clearDetails();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to cancel ticket. Please verify the PNR.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while cancelling ticket: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void clearDetails() {
        currentReservation = null;
        passengerNameValue.setText("-");
        trainNumberValue.setText("-");
        classTypeValue.setText("-");
        journeyDateValue.setText("-");
        routeValue.setText("-");
        bookingDateValue.setText("-");
        cancelButton.setEnabled(false);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CancellationFrame frame = new CancellationFrame();
            frame.setVisible(true);
        });
    }


//    =================================== Main =================================
}