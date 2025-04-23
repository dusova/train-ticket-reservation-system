package com.trainticket.model;

import java.time.LocalDateTime;

public class Ticket {
    private int id;
    private int userId;
    private int trainId;
    private int wagonId;
    private int seatId;
    private LocalDateTime purchaseDate;
    private String passengerName;
    private String passengerTcNo;
    private String passengerGender;
    private double price;
    private boolean isPaid;

    // Referanslar
    private Train train;
    private Wagon wagon;
    private Seat seat;

    // Default constructor
    public Ticket() {
    }

    // Constructor with parameters
    public Ticket(int id, int userId, int trainId, int wagonId, int seatId, LocalDateTime purchaseDate,
                  String passengerName, String passengerTcNo, String passengerGender, double price, boolean isPaid) {
        this.id = id;
        this.userId = userId;
        this.trainId = trainId;
        this.wagonId = wagonId;
        this.seatId = seatId;
        this.purchaseDate = purchaseDate;
        this.passengerName = passengerName;
        this.passengerTcNo = passengerTcNo;
        this.passengerGender = passengerGender;
        this.price = price;
        this.isPaid = isPaid;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTrainId() {
        return trainId;
    }

    public void setTrainId(int trainId) {
        this.trainId = trainId;
    }

    public int getWagonId() {
        return wagonId;
    }

    public void setWagonId(int wagonId) {
        this.wagonId = wagonId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerTcNo() {
        return passengerTcNo;
    }

    public void setPassengerTcNo(String passengerTcNo) {
        this.passengerTcNo = passengerTcNo;
    }

    public String getPassengerGender() {
        return passengerGender;
    }

    public void setPassengerGender(String passengerGender) {
        this.passengerGender = passengerGender;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Wagon getWagon() {
        return wagon;
    }

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    // Bilet bilgilerini özet olarak göster
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Bilet No: ").append(id).append("\n");
        summary.append("Yolcu: ").append(passengerName).append("\n");
        summary.append("TC No: ").append(passengerTcNo).append("\n");

        if (train != null) {
            summary.append("Tren: ").append(train.getTrainNumber()).append(" - ").append(train.getTrainName()).append("\n");
            summary.append("Güzergah: ").append(train.getDepartureStation()).append(" -> ").append(train.getArrivalStation()).append("\n");
            summary.append("Tarih: ").append(train.getFormattedDepartureTime()).append("\n");
        }

        if (wagon != null) {
            summary.append("Vagon: ").append(wagon.getWagonNumber()).append(" (").append(wagon.getWagonType()).append(")\n");
        }

        if (seat != null) {
            summary.append("Koltuk: ").append(seat.getSeatNumber()).append("\n");
        }

        summary.append("Ücret: ").append(String.format("%.2f TL", price)).append("\n");
        summary.append("Ödeme Durumu: ").append(isPaid ? "Ödenmiş" : "Ödenmemiş").append("\n");

        return summary.toString();
    }

    @Override
    public String toString() {
        return "Bilet #" + id + " - " + passengerName;
    }
}