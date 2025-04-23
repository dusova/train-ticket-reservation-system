package com.trainticket.dao;

import com.trainticket.model.Train;
import com.trainticket.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrainDAO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Tren seferi ekle
    public boolean addTrain(Train train) {
        String query = "INSERT INTO trains (trainNumber, trainName, departureStation, arrivalStation, " +
                "departureTime, arrivalTime, price) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, train.getTrainNumber());
            pstmt.setString(2, train.getTrainName());
            pstmt.setString(3, train.getDepartureStation());
            pstmt.setString(4, train.getArrivalStation());
            pstmt.setString(5, train.getDepartureTime().format(formatter));
            pstmt.setString(6, train.getArrivalTime().format(formatter));
            pstmt.setDouble(7, train.getPrice());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        train.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Tren ekleme hatası: " + e.getMessage());
        }

        return false;
    }

    // ID ile tren seferi getir
    public Train getTrainById(int id) {
        String query = "SELECT * FROM trains WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractTrainFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Tren getirme hatası: " + e.getMessage());
        }

        return null;
    }

    // Tüm tren seferlerini getir
    public List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM trains ORDER BY departureTime";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Train train = extractTrainFromResultSet(rs);
                trains.add(train);
            }

        } catch (SQLException e) {
            System.err.println("Trenleri listeleme hatası: " + e.getMessage());
        }

        return trains;
    }

    // İstasyonlar arası tren seferlerini getir
    public List<Train> getTrainsByRoute(String departureStation, String arrivalStation) {
        List<Train> trains = new ArrayList<>();
        String query = "SELECT * FROM trains WHERE departureStation = ? AND arrivalStation = ? " +
                "ORDER BY departureTime";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, departureStation);
            pstmt.setString(2, arrivalStation);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Train train = extractTrainFromResultSet(rs);
                    trains.add(train);
                }
            }

        } catch (SQLException e) {
            System.err.println("Güzergah seferleri listeleme hatası: " + e.getMessage());
        }

        return trains;
    }

    // Belirli tarihte tren seferlerini getir
    public List<Train> getTrainsByDate(LocalDateTime date) {
        List<Train> trains = new ArrayList<>();
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String query = "SELECT * FROM trains WHERE date(departureTime) = ? ORDER BY departureTime";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, formattedDate);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Train train = extractTrainFromResultSet(rs);
                    trains.add(train);
                }
            }

        } catch (SQLException e) {
            System.err.println("Tarih bazlı seferleri listeleme hatası: " + e.getMessage());
        }

        return trains;
    }

    // Tren seferini güncelle
    public boolean updateTrain(Train train) {
        String query = "UPDATE trains SET trainNumber = ?, trainName = ?, departureStation = ?, " +
                "arrivalStation = ?, departureTime = ?, arrivalTime = ?, price = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, train.getTrainNumber());
            pstmt.setString(2, train.getTrainName());
            pstmt.setString(3, train.getDepartureStation());
            pstmt.setString(4, train.getArrivalStation());
            pstmt.setString(5, train.getDepartureTime().format(formatter));
            pstmt.setString(6, train.getArrivalTime().format(formatter));
            pstmt.setDouble(7, train.getPrice());
            pstmt.setInt(8, train.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Tren güncelleme hatası: " + e.getMessage());
            return false;
        }
    }

    // Tren seferini sil
    public boolean deleteTrain(int id) {
        String query = "DELETE FROM trains WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Tren silme hatası: " + e.getMessage());
            return false;
        }
    }

    // ResultSet'ten Train nesnesi oluştur
    private Train extractTrainFromResultSet(ResultSet rs) throws SQLException {
        Train train = new Train();
        train.setId(rs.getInt("id"));
        train.setTrainNumber(rs.getString("trainNumber"));
        train.setTrainName(rs.getString("trainName"));
        train.setDepartureStation(rs.getString("departureStation"));
        train.setArrivalStation(rs.getString("arrivalStation"));

        // String'den LocalDateTime'a dönüştürme
        train.setDepartureTime(LocalDateTime.parse(rs.getString("departureTime"), formatter));
        train.setArrivalTime(LocalDateTime.parse(rs.getString("arrivalTime"), formatter));

        train.setPrice(rs.getDouble("price"));
        return train;
    }

    // Kullanılabilir istasyonları getir (tekrarsız)
    public List<String> getAllStations() {
        List<String> stations = new ArrayList<>();
        String query = "SELECT DISTINCT departureStation FROM trains " +
                "UNION SELECT DISTINCT arrivalStation FROM trains ORDER BY 1";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                stations.add(rs.getString(1));
            }

        } catch (SQLException e) {
            System.err.println("İstasyonları listeleme hatası: " + e.getMessage());
        }

        return stations;
    }
}