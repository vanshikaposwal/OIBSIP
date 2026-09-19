package com.vanshika.oibsip.reservation.dao;

import com.vanshika.oibsip.reservation.model.Reservation;
import com.vanshika.oibsip.reservation.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public boolean insertReservation(Reservation reservation) throws SQLException {

        String query = "INSERT INTO reservations " +
                "(pnr_number, passenger_name, train_number, class_type, journey_date, source_station, destination_station) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, reservation.getPnrNumber());
            ps.setString(2, reservation.getPassengerName());
            ps.setInt(3, reservation.getTrainNumber());
            ps.setString(4, reservation.getClassType());
            ps.setDate(5, Date.valueOf(reservation.getJourneyDate()));
            ps.setString(6, reservation.getSourceStation());
            ps.setString(7, reservation.getDestinationStation());

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        reservation.setReservationId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

            return false;
        }
    }

    public Reservation getReservationByPNR(String pnr) throws SQLException {

        String query = "SELECT reservation_id, pnr_number, passenger_name, train_number, " +
                "class_type, journey_date, source_station, destination_station, booking_date " +
                "FROM reservations WHERE pnr_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, pnr);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        }

        return null;
    }

    public List<Reservation> getAllReservations() throws SQLException {

        List<Reservation> list = new ArrayList<>();

        String query = "SELECT reservation_id, pnr_number, passenger_name, train_number, " +
                "class_type, journey_date, source_station, destination_station, booking_date " +
                "FROM reservations ORDER BY reservation_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        }

        return list;
    }

    public boolean deleteReservationByPNR(String pnr) throws SQLException {

        String query = "DELETE FROM reservations WHERE pnr_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, pnr);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean pnrExists(String pnr) throws SQLException {

        String query = "SELECT 1 FROM reservations WHERE pnr_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, pnr);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {

        Reservation reservation = new Reservation();

        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setPnrNumber(rs.getString("pnr_number"));
        reservation.setPassengerName(rs.getString("passenger_name"));
        reservation.setTrainNumber(rs.getInt("train_number"));
        reservation.setClassType(rs.getString("class_type"));

        Date journeyDate = rs.getDate("journey_date");
        if (journeyDate != null) {
            reservation.setJourneyDate(journeyDate.toLocalDate());
        }

        reservation.setSourceStation(rs.getString("source_station"));
        reservation.setDestinationStation(rs.getString("destination_station"));

        Timestamp bookingDate = rs.getTimestamp("booking_date");
        if (bookingDate != null) {
            reservation.setBookingDate(bookingDate.toLocalDateTime());
        }

        return reservation;
    }
}