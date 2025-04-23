package com.trainticket.model;

public class Seat {
    private int id;
    private int wagonId;
    private int seatNumber;
    private boolean isReserved;
    private String reservedByGender; // Null ise rezervasyon yok, değilse cinsiyet bilgisi

    // Default constructor
    public Seat() {
    }

    // Constructor with parameters
    public Seat(int id, int wagonId, int seatNumber, boolean isReserved, String reservedByGender) {
        this.id = id;
        this.wagonId = wagonId;
        this.seatNumber = seatNumber;
        this.isReserved = isReserved;
        this.reservedByGender = reservedByGender;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWagonId() {
        return wagonId;
    }

    public void setWagonId(int wagonId) {
        this.wagonId = wagonId;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public String getReservedByGender() {
        return reservedByGender;
    }

    public void setReservedByGender(String reservedByGender) {
        this.reservedByGender = reservedByGender;
    }

    // Bir koltuğun belirli cinsiyet için rezerve edilip edilemeyeceğini kontrol et
    public boolean canBeReservedBy(String gender) {
        // Koltuk boşsa herkes rezerve edebilir
        if (!isReserved) {
            return true;
        }

        // Koltuk doluysa ve cinsiyet bilgisi yoksa rezerve edilemez
        if (reservedByGender == null) {
            return false;
        }

        // Kadın yanına erkek veya erkek yanına kadın oturamaz kuralı
        if ((gender.equals("Erkek") && reservedByGender.equals("Kadın")) ||
                (gender.equals("Kadın") && reservedByGender.equals("Erkek"))) {
            return false;
        }

        // Aynı cinsiyet ise rezerve edilebilir
        return true;
    }

    @Override
    public String toString() {
        String status = isReserved ? "Dolu" : "Boş";
        String genderInfo = isReserved ? " (" + reservedByGender + ")" : "";
        return "Koltuk " + seatNumber + ": " + status + genderInfo;
    }
}