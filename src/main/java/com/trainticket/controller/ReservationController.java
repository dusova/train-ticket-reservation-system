package com.trainticket.controller;

import com.trainticket.dao.WagonDAO;
import com.trainticket.dao.SeatDAO;
import com.trainticket.model.Wagon;
import com.trainticket.model.Seat;
import com.trainticket.model.Train;

import java.util.ArrayList;
import java.util.List;

public class ReservationController {

    private WagonDAO wagonDAO;
    private SeatDAO seatDAO;

    public ReservationController() {
        this.wagonDAO = new WagonDAO();
        this.seatDAO = new SeatDAO();
    }

    // Tren için vagonları getir
    public List<Wagon> getWagonsByTrain(Train train) {
        if (train == null || train.getId() <= 0) {
            return new ArrayList<>();
        }

        List<Wagon> wagons = wagonDAO.getWagonsByTrainId(train.getId());

        // Eğer vagon yoksa, otomatik olarak oluştur
        if (wagons.isEmpty()) {
            createDefaultWagons(train.getId());
            wagons = wagonDAO.getWagonsByTrainId(train.getId());
        }

        return wagons;
    }

    // Vagon için koltukları getir
    public List<Seat> getSeatsByWagon(Wagon wagon) {
        if (wagon == null || wagon.getId() <= 0) {
            return new ArrayList<>();
        }

        return seatDAO.getSeatsByWagonId(wagon.getId());
    }

    // Boş koltukları getir
    public List<Seat> getAvailableSeats(Wagon wagon) {
        if (wagon == null || wagon.getId() <= 0) {
            return new ArrayList<>();
        }

        return seatDAO.getAvailableSeatsByWagonId(wagon.getId());
    }

    // Koltuk rezerve edilebilir mi?
    public boolean canSeatBeReserved(Seat seat, String gender) {
        if (seat == null || gender == null || gender.isEmpty()) {
            return false;
        }

        return seat.canBeReservedBy(gender);
    }

    // Koltuğu rezerve et
    public boolean reserveSeat(Seat seat, String gender) {
        if (seat == null || seat.getId() <= 0 || gender == null || gender.isEmpty()) {
            return false;
        }

        // Cinsiyet kısıtlaması kontrolü
        if (!seat.canBeReservedBy(gender)) {
            return false;
        }

        return seatDAO.reserveSeat(seat.getId(), gender);
    }

    // Rezervasyonu iptal et
    public boolean cancelReservation(Seat seat) {
        if (seat == null || seat.getId() <= 0) {
            return false;
        }

        return seatDAO.cancelReservation(seat.getId());
    }

    // Varsayılan vagonları oluştur
    private void createDefaultWagons(int trainId) {
        // Ekonomi vagon
        Wagon economyWagon = new Wagon();
        economyWagon.setTrainId(trainId);
        economyWagon.setWagonNumber(1);
        economyWagon.setWagonType("Ekonomi");
        economyWagon.setTotalSeats(40);
        wagonDAO.addWagon(economyWagon);

        // Business vagon
        Wagon businessWagon = new Wagon();
        businessWagon.setTrainId(trainId);
        businessWagon.setWagonNumber(2);
        businessWagon.setWagonType("Business");
        businessWagon.setTotalSeats(20);
        wagonDAO.addWagon(businessWagon);
    }

    // ID ile vagon getir
    public Wagon getWagonById(int id) {
        if (id <= 0) {
            return null;
        }

        return wagonDAO.getWagonById(id);
    }

    // ID ile koltuk getir
    public Seat getSeatById(int id) {
        if (id <= 0) {
            return null;
        }

        return seatDAO.getSeatById(id);
    }
}