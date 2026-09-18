package com.vanshika.oibsip.reservation.dao;

import com.vanshika.oibsip.reservation.model.User;
import com.vanshika.oibsip.reservation.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean login(String email, String passwaord) throws SQLException {
        if (userExists(email)){
            String query = "SELECT password FROM users WHERE email=?";

            Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery(query);
            rs.next();
            String actualPassword = rs.getString(1);

            if (passwaord.equals(actualPassword)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public boolean registerUser(String userName, String email, String password) throws SQLException{
        if(userExists(email)){
            return false;
        }
        else{
            String query = "INSERT INTO USERS VALUES(?, ?, ?)";
            Connection connection = DBConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, userName);
            ps.setString(2, email);
            ps.setString(3, password);

            ps.executeUpdate();
        }
        return true;
    }

    public boolean userExists(String email) throws SQLException {
        String query = "SELECT email where email=?";

        Connection connection = DBConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, email);

        ResultSet rs = ps.executeQuery(query);
        rs.next();
        return rs!=null ? true:false;
    }
}
