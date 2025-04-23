package com.trainticket.controller;

import com.trainticket.dao.TrainDAO;
import com.trainticket.model.Train;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrainController {

    private TrainDAO trainDAO;

    public TrainController() {
        this.trainDAO = new TrainDAO();
    }

    // Tüm trenleri getir
    public List<Train> getAllTrains() {
        return trainDAO.getAllTrains();
    }

    // ID ile tren getir
    public Train getTrainById(int id) {
        if (id <= 0) {
            return null;
        }

        return trainDAO.getTrainById(id);
    }

    // Güzergaha göre tren ara
    public List<Train> searchTrains(String departureStation, String arrivalStation, LocalDate date) {
        if (departureStation == null || departureStation.isEmpty() ||
                arrivalStation == null || arrivalStation.isEmpty() ||
                date == null) {
            return new ArrayList<>();
        }

        // Aynı istasyonların seçilmesini engelle
        if (departureStation.equals(arrivalStation)) {
            return new ArrayList<>();
        }

        // Güzergaha göre trenleri bul
        List<Train> trainsByRoute = trainDAO.getTrainsByRoute(departureStation, arrivalStation);

        // Belirtilen tarihe göre filtrele
        List<Train> filteredTrains = new ArrayList<>();
        for (Train train : trainsByRoute) {
            if (train.getDepartureTime().toLocalDate().equals(date)) {
                filteredTrains.add(train);
            }
        }

        return filteredTrains;
    }

    // Tüm istasyonları getir
    public List<String> getAllStations() {
        return trainDAO.getAllStations();
    }

    // Yeni tren ekle
    public boolean addTrain(Train train) {
        if (train == null || train.getTrainNumber() == null || train.getTrainNumber().isEmpty() ||
                train.getTrainName() == null || train.getTrainName().isEmpty() ||
                train.getDepartureStation() == null || train.getDepartureStation().isEmpty() ||
                train.getArrivalStation() == null || train.getArrivalStation().isEmpty() ||
                train.getDepartureTime() == null || train.getArrivalTime() == null ||
                train.getPrice() <= 0) {
            return false;
        }

        return trainDAO.addTrain(train);
    }

    // Tren güncelle
    public boolean updateTrain(Train train) {
        if (train == null || train.getId() <= 0) {
            return false;
        }

        return trainDAO.updateTrain(train);
    }

    // Tren sil
    public boolean deleteTrain(int id) {
        if (id <= 0) {
            return false;
        }

        return trainDAO.deleteTrain(id);
    }
}