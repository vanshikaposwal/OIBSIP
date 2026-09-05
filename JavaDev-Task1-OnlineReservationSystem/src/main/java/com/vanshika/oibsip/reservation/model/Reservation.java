package com.vanshika.oibsip.reservation.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reservation {

    private int reservationId;

    private String pnrNumber;

    private String passengerName;

    private int trainNumber;

    private String classType;

    private LocalDate journeyDate;

    private String sourceStation;

    private String destinationStation;

    private LocalDateTime bookingDate;


    // Constructors

    public Reservation(){

    }

    public Reservation(String classType, LocalDateTime bookingDate, String destinationStation, LocalDate journeyDate,
                       String pnrNumber, String passengerName, int reservationId, String sourceStation,
                       int trainNumber) {
        this.classType = classType;
        this.bookingDate = bookingDate;
        this.destinationStation = destinationStation;
        this.journeyDate = journeyDate;
        this.pnrNumber = pnrNumber;
        this.passengerName = passengerName;
        this.reservationId = reservationId;
        this.sourceStation = sourceStation;
        this.trainNumber = trainNumber;
    }


    //Getters and Setters


    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPnrNumber() {
        return pnrNumber;
    }

    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }


}
