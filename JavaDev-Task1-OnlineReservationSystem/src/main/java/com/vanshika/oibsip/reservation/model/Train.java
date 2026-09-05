package com.vanshika.oibsip.reservation.model;

public class Train {
    
    private int trainNumber;
    private String trainName;
    private String sourceStation;
    private String destinationtation;

    // Constructor
    public Train() {

    }

    public Train(int trainNumber, String trainName, String sourceStation, String destinationtation) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.sourceStation = sourceStation;
        this.destinationtation = destinationtation;
    }

    // Getters and Setters


    public String getDestinationtation() {
        return destinationtation;
    }

    public void setDestinationtation(String destinationtation) {
        this.destinationtation = destinationtation;
    }

    public String getSourceStation() {
        return sourceStation;
    }

    public void setSourceStation(String sourceStation) {
        this.sourceStation = sourceStation;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }
    
}

