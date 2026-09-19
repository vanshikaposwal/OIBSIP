package com.vanshika.oibsip.reservation.service;

import com.vanshika.oibsip.reservation.dao.ReservationDAO;
import com.vanshika.oibsip.reservation.dao.TrainDAO;
import com.vanshika.oibsip.reservation.model.Reservation;
import com.vanshika.oibsip.reservation.model.Train;
import com.vanshika.oibsip.reservation.util.PNRGenerator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {

    private ReservationDAO reservationDAO;
    private TrainDAO trainDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.trainDAO = new TrainDAO();
    }

    public ReservationService(ReservationDAO reservationDAO, TrainDAO trainDAO) {
        this.reservationDAO = reservationDAO;
        this.trainDAO = trainDAO;
    }

    public String bookTicket(Reservation reservation) throws SQLException, IllegalArgumentException, IllegalStateException {

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation details cannot be null.");
        }

        if (reservation.getPassengerName() == null || reservation.getPassengerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Passenger name is required.");
        }

        if (reservation.getTrainNumber() <= 0) {
            throw new IllegalArgumentException("Invalid train number.");
        }

        if (reservation.getClassType() == null ||
                reservation.getClassType().trim().isEmpty() ||
                "Select Class".equalsIgnoreCase(reservation.getClassType())) {
            throw new IllegalArgumentException("Please select a valid class type.");
        }

        if (reservation.getJourneyDate() == null) {
            throw new IllegalArgumentException("Journey date is required.");
        }

        if (reservation.getJourneyDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Journey date cannot be in the past.");
        }

        if (reservation.getSourceStation() == null || reservation.getSourceStation().trim().isEmpty()) {
            throw new IllegalArgumentException("Source station is required.");
        }

        if (reservation.getDestinationStation() == null || reservation.getDestinationStation().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination station is required.");
        }

        // Verify train exists and has seats
        Train train = trainDAO.getTrainByNumber(reservation.getTrainNumber());
        if (train == null) {
            throw new IllegalArgumentException("Train number " + reservation.getTrainNumber() + " does not exist.");
        }

        if (train.getAvailableSeats() <= 0) {
            throw new IllegalStateException("No seats available on train " + train.getTrainNumber() + " (" + train.getTrainName() + ").");
        }

        // Generate unique PNR
        String pnr = PNRGenerator.getPNR();
        while (reservationDAO.pnrExists(pnr)) {
            pnr = PNRGenerator.getPNR();
        }
        reservation.setPnrNumber(pnr);

        // Save reservation
        boolean inserted = reservationDAO.insertReservation(reservation);
        if (!inserted) {
            throw new SQLException("Failed to save reservation to the database.");
        }

        // Decrement train seats
        trainDAO.changeAvailableSeats(reservation.getTrainNumber(), -1);

        return pnr;
    }

    public Reservation getReservationByPNR(String pnr) throws SQLException {

        if (pnr == null || pnr.trim().isEmpty()) {
            return null;
        }

        return reservationDAO.getReservationByPNR(pnr.trim().toUpperCase());
    }

    public boolean cancelTicket(String pnr) throws SQLException, IllegalArgumentException {

        if (pnr == null || pnr.trim().isEmpty()) {
            throw new IllegalArgumentException("PNR number cannot be empty.");
        }

        String cleanedPNR = pnr.trim().toUpperCase();
        Reservation reservation = reservationDAO.getReservationByPNR(cleanedPNR);

        if (reservation == null) {
            return false;
        }

        boolean deleted = reservationDAO.deleteReservationByPNR(cleanedPNR);
        if (deleted) {
            // Restore available seats on the train
            trainDAO.changeAvailableSeats(reservation.getTrainNumber(), 1);
            return true;
        }

        return false;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.getAllReservations();
    }
}