package com.trainticket.model;

import java.util.ArrayList;
import java.util.List;

public class Wagon {
    private int id;
    private int trainId;
    private int wagonNumber;
    private String wagonType; // "Economy", "Business", "Standard" gibi
    private int totalSeats;
    private List<Seat> seats;

    // Default constructor
    public Wagon() {
        seats = new ArrayList<>();
    }

    // Constructor with parameters
    public Wagon(int id, int trainId, int wagonNumber, String wagonType, int totalSeats) {
        this.id = id;
        this.trainId = trainId;
        this.wagonNumber = wagonNumber;
        this.wagonType = wagonType;
        this.totalSeats = totalSeats;
        this.seats = new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public int getWagonNumber() {
        return wagonNumber;
    }

    public void setWagonNumber(int wagonNumber) {
        this.wagonNumber = wagonNumber;
    }

    public String getWagonType() {
        return wagonType;
    }

    public void setWagonType(String wagonType) {
        this.wagonType = wagonType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    // Koltuk ekle
    public void addSeat(Seat seat) {
        if (seats.size() < totalSeats) {
            seats.add(seat);
        }
    }

    // Koltuk bul
    public Seat findSeatByNumber(int seatNumber) {
        return seats.stream()
                .filter(seat -> seat.getSeatNumber() == seatNumber)
                .findFirst()
                .orElse(null);
    }

    // Mevcut koltuk sayısını kontrol et
    public int getAvailableSeatsCount() {
        return (int) seats.stream()
                .filter(seat -> !seat.isReserved())
                .count();
    }

    @Override
    public String toString() {
        return "Vagon " + wagonNumber + " (" + wagonType + ")";
    }
}