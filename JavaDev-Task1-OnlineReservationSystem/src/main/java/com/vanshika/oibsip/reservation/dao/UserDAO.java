package com.vanshika.oibsip.reservation.dao;

import com.vanshika.oibsip.reservation.model.User;
import com.vanshika.oibsip.reservation.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean login(String email, String password) throws SQLException {

        if (!userExists(email)) {
            return false;
        }

        String query = "SELECT password FROM users WHERE email=?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String actualPassword = rs.getString("password");
            return password.equals(actualPassword);
        }

        return false;
    }


    public boolean registerUser(String userName, String email, String password)
            throws SQLException {

        if (userExists(email)) {
            return false;
        }

        String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, userName);
        ps.setString(2, email);
        ps.setString(3, password);

        int rowsInserted = ps.executeUpdate();

        return rowsInserted > 0;
    }


    public boolean userExists(String email) throws SQLException {

        String query = "SELECT email FROM users WHERE email=?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);

        ps.setString(1, email);

        ResultSet rs = ps.executeQuery();

        return rs.next();
    }
}