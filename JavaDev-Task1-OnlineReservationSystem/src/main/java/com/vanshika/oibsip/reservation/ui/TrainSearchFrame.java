package com.vanshika.oibsip.reservation.ui;

import com.vanshika.oibsip.reservation.model.Train;
import com.vanshika.oibsip.reservation.service.TrainService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TrainSearchFrame extends JFrame {

    private JTextField sourceField;
    private JTextField destinationField;
    private JTextField dateField;

    private JTable trainTable;
    private DefaultTableModel tableModel;

    private JButton searchButton;
    private JButton selectButton;
    private JButton backButton;

    private TrainService trainService;

    public TrainSearchFrame() {

        trainService = new TrainService();

        setTitle("Search Available Trains");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ================= MAIN PANEL =================

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );
        mainPanel.setBackground(new Color(245, 247, 250));

        // ================= HEADER =================

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 41, 59));
        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        );

        JLabel titleLabel = new JLabel("Search Available Trains");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel subtitleLabel = new JLabel(
                "Find trains for your journey"
        );
        subtitleLabel.setForeground(new Color(203, 213, 225));
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // ================= SEARCH FORM =================

        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(Color.WHITE);

        searchPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(220, 225, 230)
                        ),
                        BorderFactory.createEmptyBorder(
                                15, 15, 15, 15
                        )
                )
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ---------- FROM ----------

        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel sourceLabel = new JLabel("From");
        sourceLabel.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        searchPanel.add(sourceLabel, gbc);

        sourceField = new JTextField(12);

        gbc.gridx = 1;
        searchPanel.add(sourceField, gbc);

        // ---------- TO ----------

        gbc.gridx = 2;

        JLabel destinationLabel = new JLabel("To");
        destinationLabel.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        searchPanel.add(destinationLabel, gbc);

        destinationField = new JTextField(12);

        gbc.gridx = 3;
        searchPanel.add(destinationField, gbc);

        // ---------- DATE ----------

        gbc.gridx = 4;

        JLabel dateLabel = new JLabel("Journey Date");
        dateLabel.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        searchPanel.add(dateLabel, gbc);

        dateField = new JTextField(10);
        dateField.setToolTipText("Format: DD-MM-YYYY");

        gbc.gridx = 5;
        searchPanel.add(dateField, gbc);

        // ---------- SEARCH BUTTON ----------

        searchButton = new JButton("Search Trains");
//        styleButton(searchButton);
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setForeground(Color.BLACK);
        searchButton.setBackground(new Color(100, 116, 139));

        gbc.gridx = 6;
        searchPanel.add(searchButton, gbc);

        // ================= TABLE =================

        String[] columns = {
                "Train No.",
                "Train Name",
                "From",
                "To",
                "Departure",
                "Arrival",
                "Available Seats",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }
        };

        trainTable = new JTable(tableModel);

        trainTable.setRowHeight(30);

        trainTable.setFont(
                new Font("Arial", Font.PLAIN, 13)
        );

        trainTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        trainTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollPane = new JScrollPane(trainTable);

        // ================= BOTTOM BUTTONS =================

        JPanel bottomPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        bottomPanel.setOpaque(false);

        backButton = new JButton("Back to Dashboard");
//        styleButton(backButton);
//        backButton.setPreferredSize(new Dimension(140, 38));
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(new Color(129, 154, 190));
        backButton.setForeground(Color.BLACK);


        selectButton = new JButton("Select & Reserve Ticket");
//        styleButton(selectButton);
        selectButton.setFont(new Font("Arial", Font.BOLD, 14));
        selectButton.setForeground(Color.BLACK);
        selectButton.setBackground(new Color(112, 132, 161));

        bottomPanel.add(backButton);
        bottomPanel.add(selectButton);

        // ================= ADD PANELS =================

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        JPanel centerPanel =
                new JPanel(new BorderLayout(15, 15));

        centerPanel.setOpaque(false);

        centerPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);

        // ================= BUTTON ACTIONS =================

        searchButton.addActionListener(
                e -> searchTrains()
        );

        selectButton.addActionListener(
                e -> selectTrain()
        );

        backButton.addActionListener(e -> {

            new DashboardFrame().setVisible(true);
            dispose();

        });
    }

    // =====================================================
    // SEARCH TRAINS
    // =====================================================

    private void searchTrains() {

        String source =
                sourceField.getText().trim();

        String destination =
                destinationField.getText().trim();

        String date =
                dateField.getText().trim();

        // ---------- VALIDATION ----------

        if (source.isEmpty()
                || destination.isEmpty()
                || date.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter From, To and Journey Date.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ---------- CLEAR OLD RESULTS ----------

        tableModel.setRowCount(0);

        try {

            // Get trains from database
            List<Train> trains =
                    trainService.searchTrains(
                            source,
                            destination
                    );

            // ---------- NO RESULTS ----------

            if (trains == null || trains.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "No trains found for this route.",
                        "No Trains",
                        JOptionPane.INFORMATION_MESSAGE
                );

                return;
            }

            // ---------- ADD DATABASE RESULTS ----------

            for (Train train : trains) {

                // Only show active trains
                if (train.getStatus() != null
                        && !train.getStatus()
                        .equalsIgnoreCase("ACTIVE")) {

                    continue;
                }

                tableModel.addRow(
                        new Object[]{
                                train.getTrainNumber(),
                                train.getTrainName(),
                                train.getSourceStation(),
                                train.getDestinationStation(),
                                train.getDepartureTime(),
                                train.getArrivalTime(),
                                train.getAvailableSeats(),
                                train.getStatus()
                        }
                );
            }

            // If all returned trains were inactive
            if (tableModel.getRowCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No active trains found for this route.",
                        "No Active Trains",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to search trains.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // SELECT TRAIN
    // =====================================================

    private void selectTrain() {

        int selectedRow =
                trainTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a train first.",
                    "No Train Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String trainNumber =
                trainTable
                        .getValueAt(selectedRow, 0)
                        .toString();

        String trainName =
                trainTable
                        .getValueAt(selectedRow, 1)
                        .toString();

        String source =
                trainTable
                        .getValueAt(selectedRow, 2)
                        .toString();

        String destination =
                trainTable
                        .getValueAt(selectedRow, 3)
                        .toString();

        int availableSeats =
                Integer.parseInt(
                        trainTable
                                .getValueAt(selectedRow, 6)
                                .toString()
                );

        // ---------- CHECK SEATS ----------

        if (availableSeats <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "No seats are available on this train.",
                    "Train Full",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // ---------- OPEN RESERVATION FRAME ----------

        String journeyDate = dateField.getText().trim();

        ReservationFrame reservationFrame =
                new ReservationFrame(
                        trainNumber,
                        trainName,
                        source,
                        destination,
                        journeyDate,
                        true
                );

        reservationFrame.setVisible(true);

        dispose();
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================

    private void styleButton(JButton button) {

        button.setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        button.setFocusPainted(false);

        button.setBackground(
                new Color(37, 99, 235)
        );

        button.setForeground(Color.BLACK);

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        18,
                        10,
                        18
                )
        );
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            TrainSearchFrame frame =
                    new TrainSearchFrame();

            frame.setVisible(true);
        });
    }
}