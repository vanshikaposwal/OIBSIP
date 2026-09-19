package com.vanshika.oibsip.reservation.service;

import com.vanshika.oibsip.reservation.dao.TrainDAO;
import com.vanshika.oibsip.reservation.model.Train;

import java.sql.SQLException;
import java.util.List;

public class TrainService {

    private TrainDAO trainDAO;

    public TrainService() {
        trainDAO = new TrainDAO();
    }

    public Train getTrainByNumber(int trainNumber) throws SQLException {

        if (trainNumber <= 0) {
            return null;
        }

        return trainDAO.getTrainByNumber(trainNumber);
    }

    public List<Train> getAllTrains() throws SQLException {
        return trainDAO.getAllTrains();
    }

    public List<Train> searchTrains(
            String source,
            String destination
    ) throws SQLException {

        return trainDAO.searchTrains(
                source,
                destination
        );
    }

    public boolean updateAvailableSeats(int trainNumber, int seats) throws SQLException {
        return trainDAO.updateAvailableSeats(trainNumber, seats);
    }

    public boolean changeAvailableSeats(int trainNumber, int delta) throws SQLException {
        return trainDAO.changeAvailableSeats(trainNumber, delta);
    }
}