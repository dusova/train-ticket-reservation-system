package com.trainticket.dao;

import com.trainticket.model.Seat;
import com.trainticket.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    // Yeni koltuk başlat
    public boolean initializeSeat(int wagonId, int seatNumber) {
        String query = "INSERT INTO seats (wagonId, seatNumber, isReserved, reservedByGender) " +
                "VALUES (?, ?, 0, NULL)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, wagonId);
            pstmt.setInt(2, seatNumber);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Koltuk başlatma hatası: " + e.getMessage());
        }

        return false;
    }

    // ID ile koltuk getir
    public Seat getSeatById(int id) {
        String query = "SELECT * FROM seats WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractSeatFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Koltuk getirme hatası: " + e.getMessage());
        }

        return null;
    }

    // Vagon ID'sine göre koltukları getir
    public List<Seat> getSeatsByWagonId(int wagonId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE wagonId = ? ORDER BY seatNumber";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, wagonId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = extractSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }

        } catch (SQLException e) {
            System.err.println("Koltukları listeleme hatası: " + e.getMessage());
        }

        return seats;
    }

    // Koltuğu rezerve et
    public boolean reserveSeat(int id, String gender) {
        String query = "UPDATE seats SET isReserved = 1, reservedByGender = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, gender);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Koltuk rezerve etme hatası: " + e.getMessage());
            return false;
        }
    }

    // Koltuk rezervasyonunu iptal et
    public boolean cancelReservation(int id) {
        String query = "UPDATE seats SET isReserved = 0, reservedByGender = NULL WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Rezervasyon iptal hatası: " + e.getMessage());
            return false;
        }
    }

    // Belirli bir vagondaki boş koltukları getir
    public List<Seat> getAvailableSeatsByWagonId(int wagonId) {
        List<Seat> seats = new ArrayList<>();
        String query = "SELECT * FROM seats WHERE wagonId = ? AND isReserved = 0 ORDER BY seatNumber";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, wagonId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Seat seat = extractSeatFromResultSet(rs);
                    seats.add(seat);
                }
            }

        } catch (SQLException e) {
            System.err.println("Boş koltukları listeleme hatası: " + e.getMessage());
        }

        return seats;
    }

    // ResultSet'ten Seat nesnesi oluştur
    private Seat extractSeatFromResultSet(ResultSet rs) throws SQLException {
        Seat seat = new Seat();
        seat.setId(rs.getInt("id"));
        seat.setWagonId(rs.getInt("wagonId"));
        seat.setSeatNumber(rs.getInt("seatNumber"));
        seat.setReserved(rs.getInt("isReserved") == 1);
        seat.setReservedByGender(rs.getString("reservedByGender"));
        return seat;
    }
}