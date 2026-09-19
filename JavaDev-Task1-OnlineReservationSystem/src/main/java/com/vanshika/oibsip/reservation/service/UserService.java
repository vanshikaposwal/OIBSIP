package com.vanshika.oibsip.reservation.service;

import com.vanshika.oibsip.reservation.dao.UserDAO;
import com.vanshika.oibsip.reservation.model.User;

import java.sql.SQLException;
import java.util.regex.Pattern;

public class UserService {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public boolean login(String email, String password) throws SQLException {
        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            return false;
        }
        return userDAO.login(email.trim(), password);
    }

    public boolean registerUser(String userName, String email, String password)
            throws SQLException {

        if (userName == null || userName.trim().isEmpty()) {
            return false;
        }

        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            return false;
        }

        if (password == null || password.length() < 6) {
            return false;
        }

        return userDAO.registerUser(userName.trim(), email.trim(), password);
    }

    public User getUserByEmail(String email) throws SQLException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userDAO.getUserByEmail(email.trim());
    }
}