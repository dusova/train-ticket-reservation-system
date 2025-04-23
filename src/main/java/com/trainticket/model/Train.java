package com.trainticket.model;

import java.time.LocalDateTime;

public class Train {
    private int id;
    private String trainNumber;
    private String trainName;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;

    // Default constructor
    public Train() {
    }

    // Constructor with parameters
    public Train(int id, String trainNumber, String trainName, String departureStation, String arrivalStation,
                 LocalDateTime departureTime, LocalDateTime arrivalTime, double price) {
        this.id = id;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.departureStation = departureStation;
        this.arrivalStation = arrivalStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getDepartureStation() {
        return departureStation;
    }

    public void setDepartureStation(String departureStation) {
        this.departureStation = departureStation;
    }

    public String getArrivalStation() {
        return arrivalStation;
    }

    public void setArrivalStation(String arrivalStation) {
        this.arrivalStation = arrivalStation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Seyahat süresini hesapla (saat olarak)
    public double calculateTravelDuration() {
        long diffInMinutes = java.time.Duration.between(departureTime, arrivalTime).toMinutes();
        return diffInMinutes / 60.0;
    }

    @Override
    public String toString() {
        return trainNumber + " - " + trainName + " (" + departureStation + " -> " + arrivalStation + ")";
    }

    // Ekstra metot: Görüntüleme için formatlı tarih ve saat
    public String getFormattedDepartureTime() {
        return departureTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }

    public String getFormattedArrivalTime() {
        return arrivalTime.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}