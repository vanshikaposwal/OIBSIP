package com.vanshika.oibsip.reservation.dao;

import com.vanshika.oibsip.reservation.model.Train;
import com.vanshika.oibsip.reservation.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    public Train getTrainByNumber(int trainNumber) throws SQLException {

        String query = "SELECT train_number, train_name, source_station, " +
                "destination_station, departure_time, arrival_time, " +
                "available_seats, status FROM trains WHERE train_number=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, trainNumber);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Train train = new Train();

                    train.setTrainNumber(rs.getInt("train_number"));
                    train.setTrainName(rs.getString("train_name"));
                    train.setSourceStation(rs.getString("source_station"));
                    train.setDestinationStation(
                            rs.getString("destination_station")
                    );
                    train.setDepartureTime(
                            rs.getString("departure_time")
                    );
                    train.setArrivalTime(
                            rs.getString("arrival_time")
                    );
                    train.setAvailableSeats(
                            rs.getInt("available_seats")
                    );
                    train.setStatus(
                            rs.getString("status")
                    );

                    return train;
                }
            }
        }

        return null;
    }

    public List<Train> getAllTrains() throws SQLException {

        String query = "SELECT train_number, train_name, source_station, " +
                "destination_station, departure_time, arrival_time, " +
                "available_seats, status FROM trains";

        List<Train> trains = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Train train = new Train();

                train.setTrainNumber(rs.getInt("train_number"));
                train.setTrainName(rs.getString("train_name"));
                train.setSourceStation(rs.getString("source_station"));
                train.setDestinationStation(
                        rs.getString("destination_station")
                );
                train.setDepartureTime(
                        rs.getString("departure_time")
                );
                train.setArrivalTime(
                        rs.getString("arrival_time")
                );
                train.setAvailableSeats(
                        rs.getInt("available_seats")
                );
                train.setStatus(
                        rs.getString("status")
                );

                trains.add(train);
            }
        }

        return trains;
    }

    public List<Train> searchTrains(String source, String destination) throws SQLException {

        List<Train> trains = new ArrayList<>();

        String sql = "SELECT train_number, train_name, source_station, " +
                "destination_station, departure_time, arrival_time, " +
                "available_seats, status " +
                "FROM trains " +
                "WHERE LOWER(TRIM(source_station)) = LOWER(TRIM(?)) " +
                "AND LOWER(TRIM(destination_station)) = LOWER(TRIM(?))";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, source != null ? source.trim() : "");
            statement.setString(2, destination != null ? destination.trim() : "");

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Train train = new Train();

                    train.setTrainNumber(
                            resultSet.getInt("train_number")
                    );

                    train.setTrainName(
                            resultSet.getString("train_name")
                    );

                    train.setSourceStation(
                            resultSet.getString("source_station")
                    );

                    train.setDestinationStation(
                            resultSet.getString("destination_station")
                    );

                    train.setDepartureTime(
                            resultSet.getString("departure_time")
                    );

                    train.setArrivalTime(
                            resultSet.getString("arrival_time")
                    );

                    train.setAvailableSeats(
                            resultSet.getInt("available_seats")
                    );

                    train.setStatus(
                            resultSet.getString("status")
                    );

                    trains.add(train);
                }
            }
        }

        return trains;
    }

    public boolean updateAvailableSeats(int trainNumber, int seats) throws SQLException {

        String status = (seats <= 0) ? "FULL" : "ACTIVE";
        String query = "UPDATE trains SET available_seats = ?, status = ? WHERE train_number = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, Math.max(0, seats));
            ps.setString(2, status);
            ps.setInt(3, trainNumber);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean changeAvailableSeats(int trainNumber, int delta) throws SQLException {

        Train train = getTrainByNumber(trainNumber);
        if (train == null) {
            return false;
        }

        int newSeats = train.getAvailableSeats() + delta;
        if (newSeats < 0) {
            return false;
        }

        return updateAvailableSeats(trainNumber, newSeats);
    }
}