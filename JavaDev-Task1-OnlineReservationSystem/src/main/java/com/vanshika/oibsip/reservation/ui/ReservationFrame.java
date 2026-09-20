package com.vanshika.oibsip.reservation.ui;

import com.vanshika.oibsip.reservation.model.Reservation;
import com.vanshika.oibsip.reservation.model.Train;
import com.vanshika.oibsip.reservation.service.ReservationService;
import com.vanshika.oibsip.reservation.service.TrainService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ReservationFrame extends JFrame {

    private JTextField passengerNameField;
    private JTextField trainNumberField;
    private JTextField trainNameField;
    private JComboBox<String> classTypeBox;
    private JTextField dateField;
    private JTextField sourceField;
    private JTextField destinationField;

    private TrainService trainService;
    private ReservationService reservationService;

    public ReservationFrame() {
        this(null, null, null, null, null, false);
    }

    public ReservationFrame(
            String trainNumber,
            String trainName,
            String source,
            String destination) {
        this(trainNumber, trainName, source, destination, null, false);
    }

    public ReservationFrame(
            String trainNumber,
            String trainName,
            String source,
            String destination,
            String date) {
        this(trainNumber, trainName, source, destination, date, false);
    }

    public ReservationFrame(
            String trainNumber,
            String trainName,
            String source,
            String destination,
            String date,
            boolean fromSearch) {

        trainService = new TrainService();
        reservationService = new ReservationService();

        setTitle("Online Reservation System - Book Ticket");
        setSize(750, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 247, 250));

        // ================= HEADER =================

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(35, 45, 65));
        header.setPreferredSize(new Dimension(750, 75));

        JLabel title = new JLabel("  Book Your Ticket");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        header.add(title, BorderLayout.WEST);

        mainPanel.add(header, BorderLayout.NORTH);

        // ================= FORM =================

        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 40, 20, 40)
        );

        JLabel passengerLabel = new JLabel("Passenger Name");
        passengerLabel.setBounds(70, 30, 150, 30);

        passengerNameField = new JTextField();
        passengerNameField.setBounds(240, 30, 350, 32);

        JLabel trainNumberLabel = new JLabel("Train Number");
        trainNumberLabel.setBounds(70, 75, 150, 30);

        trainNumberField = new JTextField();
        trainNumberField.setBounds(240, 75, 150, 32);

        JButton fetchButton = new JButton("Fetch");
        fetchButton.setBounds(400, 75, 85, 32);
        styleSmallButton(fetchButton);

        JButton searchTrainsButton = new JButton("Search");
        searchTrainsButton.setBounds(495, 75, 95, 32);
        styleSmallButton(searchTrainsButton);

        JLabel trainNameLabel = new JLabel("Train Name");
        trainNameLabel.setBounds(70, 120, 150, 30);

        trainNameField = new JTextField();
        trainNameField.setBounds(240, 120, 350, 32);
        trainNameField.setEditable(false);

        JLabel classLabel = new JLabel("Class Type");
        classLabel.setBounds(70, 165, 150, 30);

        String[] classes = {
                "Select Class",
                "Sleeper",
                "AC 3 Tier",
                "AC 2 Tier",
                "AC First Class"
        };

        classTypeBox = new JComboBox<>(classes);
        classTypeBox.setBounds(240, 165, 350, 32);

        JLabel dateLabel = new JLabel("Date of Journey");
        dateLabel.setBounds(70, 210, 150, 30);

        dateField = new JTextField();
        dateField.setBounds(240, 210, 350, 32);
        dateField.setToolTipText("Format: DD-MM-YYYY or YYYY-MM-DD");

        JLabel sourceLabel = new JLabel("Source");
        sourceLabel.setBounds(70, 255, 150, 30);

        sourceField = new JTextField();
        sourceField.setBounds(240, 255, 350, 32);

        JLabel destinationLabel = new JLabel("Destination");
        destinationLabel.setBounds(70, 300, 150, 30);

        destinationField = new JTextField();
        destinationField.setBounds(240, 300, 350, 32);

        // ================= BUTTONS =================

        JButton bookButton = new JButton("Book Ticket");
        bookButton.setBounds(100, 360, 155, 40);

        JButton backToSearchButton = new JButton("Back to Search");
        backToSearchButton.setBounds(270, 360, 165, 40);

        JButton backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setBounds(450, 360, 175, 40);

        styleButton(bookButton, new Color(31, 116, 238));
        bookButton.setForeground(Color.BLACK);
        styleButton(backToSearchButton, new Color(129, 154, 190));
        backToSearchButton.setForeground(Color.BLACK);
        styleButton(backToDashboardButton, new Color(129, 154, 190));
        backToDashboardButton.setForeground(Color.BLACK);

        // ================= ADD COMPONENTS =================

        formPanel.add(passengerLabel);
        formPanel.add(passengerNameField);

        formPanel.add(trainNumberLabel);
        formPanel.add(trainNumberField);
        formPanel.add(fetchButton);
        formPanel.add(searchTrainsButton);

        formPanel.add(trainNameLabel);
        formPanel.add(trainNameField);

        formPanel.add(classLabel);
        formPanel.add(classTypeBox);

        formPanel.add(dateLabel);
        formPanel.add(dateField);

        formPanel.add(sourceLabel);
        formPanel.add(sourceField);

        formPanel.add(destinationLabel);
        formPanel.add(destinationField);

        formPanel.add(bookButton);
        formPanel.add(backToSearchButton);
        formPanel.add(backToDashboardButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // ================= INITIALIZE VALUES =================

        if (trainNumber != null && !trainNumber.trim().isEmpty()) {
            trainNumberField.setText(trainNumber.trim());
        }
        if (trainName != null && !trainName.trim().isEmpty()) {
            trainNameField.setText(trainName.trim());
        }
        if (source != null && !source.trim().isEmpty()) {
            sourceField.setText(source.trim());
        }
        if (destination != null && !destination.trim().isEmpty()) {
            destinationField.setText(destination.trim());
        }
        if (date != null && !date.trim().isEmpty()) {
            dateField.setText(date.trim());
        }

        // ================= ACTION LISTENERS =================

        trainNumberField.addActionListener(e -> fetchTrainDetails());
        fetchButton.addActionListener(e -> fetchTrainDetails());

        searchTrainsButton.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });

        bookButton.addActionListener(e -> bookTicket());

        backToSearchButton.addActionListener(e -> {
            new TrainSearchFrame().setVisible(true);
            dispose();
        });

        backToDashboardButton.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    // ================= FETCH TRAIN DETAILS =================

    private void fetchTrainDetails() {

        String trainNumberText = trainNumberField.getText().trim();

        if (trainNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a train number first.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int trainNumber;
        try {
            trainNumber = Integer.parseInt(trainNumberText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be numeric.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        try {
            Train train = trainService.getTrainByNumber(trainNumber);

            if (train != null) {
                trainNameField.setText(train.getTrainName());
                sourceField.setText(train.getSourceStation());
                destinationField.setText(train.getDestinationStation());

                if (train.getAvailableSeats() <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Warning: No seats available on " + train.getTrainName() + " (Available: 0)",
                            "Train Full",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            } else {
                trainNameField.setText("Train Not Found");
                JOptionPane.showMessageDialog(
                        this,
                        "No train found with number: " + trainNumber,
                        "Train Not Found",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while fetching train: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ================= BOOK TICKET METHOD =================

    private void bookTicket() {

        String passengerName = passengerNameField.getText().trim();
        String trainNumberText = trainNumberField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String classType = (String) classTypeBox.getSelectedItem();
        String dateText = dateField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destinationField.getText().trim();

        // Validation
        if (passengerName.isEmpty() ||
                trainNumberText.isEmpty() ||
                trainName.isEmpty() ||
                "Train Not Found".equals(trainName) ||
                dateText.isEmpty() ||
                source.isEmpty() ||
                destination.isEmpty() ||
                classType == null ||
                "Select Class".equals(classType)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields with valid information!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        int trainNumber;
        try {
            trainNumber = Integer.parseInt(trainNumberText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Train number must be a valid number!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LocalDate journeyDate;
        try {
            journeyDate = parseDate(dateText);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid date format! Please use DD-MM-YYYY (e.g. 25-12-2026).",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (journeyDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(
                    this,
                    "Journey date cannot be in the past!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Build Reservation
        Reservation reservation = new Reservation(
                null,
                passengerName,
                trainNumber,
                classType,
                journeyDate,
                source,
                destination
        );

        try {
            String pnr = reservationService.bookTicket(reservation);

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket Booked Successfully!\n\n" +
                            "PNR Number: " + pnr +
                            "\nPassenger Name: " + passengerName +
                            "\nTrain: " + trainName + " (" + trainNumber + ")" +
                            "\nJourney Date: " + journeyDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) +
                            "\nRoute: " + source + " -> " + destination +
                            "\nClass: " + classType,
                    "Booking Confirmation",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Clear fields after successful booking
            passengerNameField.setText("");
            trainNumberField.setText("");
            trainNameField.setText("");
            classTypeBox.setSelectedIndex(0);
            dateField.setText("");
            sourceField.setText("");
            destinationField.setText("");

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Booking Error",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private LocalDate parseDate(String dateStr) throws DateTimeParseException {
        // Try dd-MM-yyyy first
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (DateTimeParseException ignored) {
        }

        // Try yyyy-MM-dd
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ignored) {
        }

        // Try dd/MM/yyyy
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ================= BUTTON STYLE =================

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(37, 99, 235));
        button.setForeground(Color.WHITE);
    }

    private void styleSmallButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setFocusPainted(false);
        button.setBackground(new Color(220, 225, 230));
    }

    // ================= MAIN METHOD =================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReservationFrame frame = new ReservationFrame();
            frame.setVisible(true);
        });
    }
}