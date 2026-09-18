package com.vanshika.oibsip.reservation.service;

import com.vanshika.oibsip.reservation.dao.UserDAO;

import java.sql.SQLException;

public class UserService {

    private UserDAO userDAO;

    public UserService() {
        userDAO = new UserDAO();
    }

    public boolean login(String email, String password) throws SQLException {
        return userDAO.login(email, password);
    }

    public boolean registerUser(String userName, String email, String password)
            throws SQLException {

//        if (userName == null || userName.trim().isEmpty()) {
//            return false;
//        }
//
//        if (email == null || !email.contains("@")) {
//            return false;
//        }
//
//        if (password == null || password.length() < 6) {
//            return false;
//        }

        return userDAO.registerUser(userName, email, password);
    }
}
