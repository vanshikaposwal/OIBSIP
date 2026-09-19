package com.vanshika.oibsip.reservation.ui;

import com.vanshika.oibsip.reservation.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private UserService userService;

    public LoginFrame() {

        userService = new UserService();

        setTitle("Online Reservation System");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create both panels
        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();

        // Add panels to CardLayout
        cardPanel.add(loginPanel, "LOGIN");
        cardPanel.add(signupPanel, "SIGNUP");

        add(cardPanel);

        // Show Login first
        cardLayout.show(cardPanel, "LOGIN");
    }

    // ================= LOGIN PANEL =================

    private JPanel createLoginPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(211, 236, 236));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBounds(210, 60, 350, 30);

        JButton loginB = new JButton("Login");
        loginB.setBounds(0, 0, 251, 35);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(249, 0, 251, 35);

        panel.add(title);
        panel.add(signupButton);
        panel.add(loginB);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(90, 120, 100, 30);

        JTextField emailField = new JTextField();
        emailField.setBounds(190, 120, 200, 30);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(90, 170, 100, 30);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(190, 170, 200, 30);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(190, 230, 90, 35);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(290, 230, 90, 35);

        panel.add(emailLabel);
        panel.add(emailField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(clearButton);

        // Login
        loginButton.addActionListener(e -> {

            String email = emailField.getText().trim();

            String password =
                    new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter email and password!"
                );

                return;
            }

            try {

                boolean success =
                        userService.login(email, password);

                if (success) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Login Successful!"
                    );

                    new DashboardFrame().setVisible(true);
                    dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid email or password!"
                    );
                }

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Database error: " + ex.getMessage()
                );
            }
        });

        // clear
        clearButton.addActionListener(e -> {
            emailField.setText("");
            passwordField.setText("");
        });

        // Go to Sign Up
        signupButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "SIGNUP");
        });

        return panel;
    }

    // ================= SIGN UP PANEL =================

    private JPanel createSignupPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(211, 236, 236));

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(0, 0, 250, 35);

        JButton signupB = new JButton("Sign Up");
        signupB.setBounds(250, 0, 250, 35);

        JLabel title = new JLabel("CREATE ACCOUNT");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(150, 60, 350, 30);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(70, 105, 130, 30);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(210, 105, 200, 30);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(70, 155, 130, 30);

        JTextField emailField = new JTextField();
        emailField.setBounds(210, 155, 200, 30);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(70, 205, 130, 30);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(210, 205, 200, 30);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setBounds(70, 255, 130, 30);

        JPasswordField confirmPasswordField =
                new JPasswordField();

        confirmPasswordField.setBounds(210, 255, 200, 30);

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(125, 320, 110, 35);

        JButton backToLoginButton = new JButton("Back to Login");
        backToLoginButton.setBounds(245, 320, 140, 35);

        panel.add(signupB);
        panel.add(title);

        panel.add(usernameLabel);
        panel.add(usernameField);

        panel.add(emailLabel);
        panel.add(emailField);

        panel.add(passwordLabel);
        panel.add(passwordField);

        panel.add(confirmLabel);
        panel.add(confirmPasswordField);

        panel.add(signupButton);
        panel.add(backToLoginButton);
        panel.add(loginButton);

        backToLoginButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "LOGIN");
        });

        // Sign Up
        signupButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();

            String password =
                    new String(passwordField.getPassword());

            String confirmPassword =
                    new String(confirmPasswordField.getPassword());

            if (username.isEmpty() ||
                    email.isEmpty() ||
                    password.isEmpty() ||
                    confirmPassword.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields!"
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Passwords do not match!"
                );

                return;
            }

            if (password.length() < 6) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password must be at least 6 characters long!"
                );
                return;
            }

            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid email address (e.g. user@example.com)!"
                );
                return;
            }

            try {

                boolean success =
                        userService.registerUser(
                                username,
                                email,
                                password
                        );

                if (success) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Account created successfully!"
                    );

                    // Clear fields
                    usernameField.setText("");
                    emailField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");

                    cardLayout.show(cardPanel, "LOGIN");

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Email already registered!"
                    );
                }

            } catch (SQLException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Database error: " + ex.getMessage()
                );
            }
        });

        // Back to Login
        loginButton.addActionListener(e -> {
            cardLayout.show(cardPanel, "LOGIN");
        });

        return panel;
    }

    // ================= MAIN =================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);

        });
    }
}