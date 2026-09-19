package com.vanshika.oibsip.reservation.ui;

import com.vanshika.oibsip.reservation.model.Reservation;
import com.vanshika.oibsip.reservation.service.ReservationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryFrame extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton backButton;

    private JTable historyTable;
    private DefaultTableModel tableModel;

    private ReservationService reservationService;

    public HistoryFrame() {

        reservationService = new ReservationService();

        setTitle("Online Reservation System - Booking History");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Booking History");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitleLabel = new JLabel("View and search all booked tickets in the system");
        subtitleLabel.setForeground(new Color(203, 213, 225));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // ================= SEARCH PANEL =================
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel filterLabel = new JLabel("Search (PNR or Passenger):");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 13));

        searchField = new JTextField(18);

        searchButton = new JButton("Search");
        styleButton(searchButton, new Color(37, 99, 235));

        refreshButton = new JButton("Show All / Refresh");
        styleButton(refreshButton, new Color(71, 85, 105));

        searchPanel.add(filterLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);

        // ================= TABLE =================
        String[] columns = {
                "PNR",
                "Passenger Name",
                "Train No.",
                "Class",
                "Journey Date",
                "From",
                "To",
                "Booking Date"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setRowHeight(28);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 13));
        historyTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(historyTable);

        // ================= BOTTOM PANEL =================
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);

        backButton = new JButton("Back to Dashboard");
        styleButton(backButton, new Color(100, 116, 139));
        bottomPanel.add(backButton);

        // ================= ASSEMBLE =================
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ================= LISTENERS =================
        searchField.addActionListener(e -> filterReservations());
        searchButton.addActionListener(e -> filterReservations());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadAllReservations();
        });

        backButton.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            dispose();
        });

        // Load initial data
        loadAllReservations();
    }

    private void loadAllReservations() {
        tableModel.setRowCount(0);

        try {
            List<Reservation> list = reservationService.getAllReservations();

            if (list == null || list.isEmpty()) {
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Reservation r : list) {
                String journeyStr = r.getJourneyDate() != null ? r.getJourneyDate().format(dateFormatter) : "-";
                String bookingStr = r.getBookingDate() != null ? r.getBookingDate().format(timeFormatter) : "-";

                tableModel.addRow(new Object[]{
                        r.getPnrNumber(),
                        r.getPassengerName(),
                        r.getTrainNumber(),
                        r.getClassType(),
                        journeyStr,
                        r.getSourceStation(),
                        r.getDestinationStation(),
                        bookingStr
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while loading reservations: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void filterReservations() {
        String filter = searchField.getText().trim().toLowerCase();

        if (filter.isEmpty()) {
            loadAllReservations();
            return;
        }

        tableModel.setRowCount(0);

        try {
            List<Reservation> list = reservationService.getAllReservations();

            if (list == null || list.isEmpty()) {
                return;
            }

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            for (Reservation r : list) {
                boolean matchesPnr = r.getPnrNumber() != null && r.getPnrNumber().toLowerCase().contains(filter);
                boolean matchesName = r.getPassengerName() != null && r.getPassengerName().toLowerCase().contains(filter);

                if (matchesPnr || matchesName) {
                    String journeyStr = r.getJourneyDate() != null ? r.getJourneyDate().format(dateFormatter) : "-";
                    String bookingStr = r.getBookingDate() != null ? r.getBookingDate().format(timeFormatter) : "-";

                    tableModel.addRow(new Object[]{
                            r.getPnrNumber(),
                            r.getPassengerName(),
                            r.getTrainNumber(),
                            r.getClassType(),
                            journeyStr,
                            r.getSourceStation(),
                            r.getDestinationStation(),
                            bookingStr
                    });
                }
            }

            if (tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No reservations found matching \"" + filter + "\"",
                        "No Matches",
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

    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HistoryFrame frame = new HistoryFrame();
            frame.setVisible(true);
        });
    }
}