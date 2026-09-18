package com.vanshika.oibsip.reservation.ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame(){

        // Frame settings
        setTitle("Online Reservation System");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(211, 236, 236));

        // Title
        JLabel titleLabel = new JLabel("ONLINE RESERVATION SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(105, 40, 350, 30);

        // Username label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        usernameLabel.setBounds(90, 110, 100, 30);

        // Username field
        JTextField usernameField = new JTextField();
        usernameField.setBounds(190, 110, 200, 30);

        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordLabel.setBounds(90, 160, 100, 30);

        // Password field
        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(190, 160, 200, 30);

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(190, 220, 90, 35);

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(300, 220, 90, 35);

        // Add components
        panel.add(titleLabel);

        panel.add(usernameLabel);
        panel.add(usernameField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(loginButton);
        panel.add(clearButton);

        // Add panel to frame
        add(panel);

        // Button actions
        clearButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
        });

        loginButton.addActionListener(e -> login());
    }

    private void login() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Login Successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Later:
            // Connect with database here
            // Open ReservationFrame
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);

        });
    }
}
