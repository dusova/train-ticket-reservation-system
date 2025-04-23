package com.trainticket.dao;

import com.trainticket.model.Wagon;
import com.trainticket.model.Seat;
import com.trainticket.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WagonDAO {

    // Vagon ekle
    public boolean addWagon(Wagon wagon) {
        String query = "INSERT INTO wagons (trainId, wagonNumber, wagonType, totalSeats) VALUES (?, ?, ?, ?)";

        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, wagon.getTrainId());
            pstmt.setInt(2, wagon.getWagonNumber());
            pstmt.setString(3, wagon.getWagonType());
            pstmt.setInt(4, wagon.getTotalSeats());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int wagonId = generatedKeys.getInt(1);
                    wagon.setId(wagonId);

                    // Vagon eklendiğinde otomatik olarak koltukları da ekleyelim
                    SeatDAO seatDAO = new SeatDAO();
                    for (int i = 1; i <= wagon.getTotalSeats(); i++) {
                        seatDAO.initializeSeat(wagon.getId(), i);
                    }

                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Vagon ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    // ID ile vagon getir
    public Wagon getWagonById(int id) {
        String query = "SELECT * FROM wagons WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Wagon wagon = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                wagon = new Wagon();
                wagon.setId(rs.getInt("id"));
                wagon.setTrainId(rs.getInt("trainId"));
                wagon.setWagonNumber(rs.getInt("wagonNumber"));
                wagon.setWagonType(rs.getString("wagonType"));
                wagon.setTotalSeats(rs.getInt("totalSeats"));
            }

        } catch (SQLException e) {
            System.err.println("Vagon getirme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Ayrı bir işlemde koltukları yükle
        if (wagon != null) {
            SeatDAO seatDAO = new SeatDAO();
            List<Seat> seats = seatDAO.getSeatsByWagonId(wagon.getId());
            wagon.setSeats(seats);
        }

        return wagon;
    }

    // Tren ID'sine göre vagonları getir
    public List<Wagon> getWagonsByTrainId(int trainId) {
        List<Wagon> wagons = new ArrayList<>();
        String query = "SELECT * FROM wagons WHERE trainId = ? ORDER BY wagonNumber";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, trainId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Wagon wagon = new Wagon();
                wagon.setId(rs.getInt("id"));
                wagon.setTrainId(rs.getInt("trainId"));
                wagon.setWagonNumber(rs.getInt("wagonNumber"));
                wagon.setWagonType(rs.getString("wagonType"));
                wagon.setTotalSeats(rs.getInt("totalSeats"));
                wagons.add(wagon);
            }

        } catch (SQLException e) {
            System.err.println("Vagonları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Vagonların koltuklarını ayrı bir işlemde yükle
        SeatDAO seatDAO = new SeatDAO();
        for (Wagon wagon : wagons) {
            List<Seat> seats = seatDAO.getSeatsByWagonId(wagon.getId());
            wagon.setSeats(seats);
        }

        return wagons;
    }

    // WagonDAO sınıfına eklenecek metot
    public List<Wagon> getAllWagons() {
        List<Wagon> wagons = new ArrayList<>();
        String query = "SELECT * FROM wagons ORDER BY trainId, wagonNumber";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Wagon wagon = new Wagon();
                wagon.setId(rs.getInt("id"));
                wagon.setTrainId(rs.getInt("trainId"));
                wagon.setWagonNumber(rs.getInt("wagonNumber"));
                wagon.setWagonType(rs.getString("wagonType"));
                wagon.setTotalSeats(rs.getInt("totalSeats"));
                wagons.add(wagon);
            }

        } catch (SQLException e) {
            System.err.println("Tüm vagonları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return wagons;
    }

    // Vagon güncelle
    public boolean updateWagon(Wagon wagon) {
        String query = "UPDATE wagons SET trainId = ?, wagonNumber = ?, wagonType = ?, totalSeats = ? WHERE id = ?";

        PreparedStatement pstmt = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, wagon.getTrainId());
            pstmt.setInt(2, wagon.getWagonNumber());
            pstmt.setString(3, wagon.getWagonType());
            pstmt.setInt(4, wagon.getTotalSeats());
            pstmt.setInt(5, wagon.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Vagon güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Vagon sil
    public boolean deleteWagon(int id) {
        // Öncelikle vagona ait koltukları silmeliyiz (foreign key constraint)
        String deleteSeatsQuery = "DELETE FROM seats WHERE wagonId = ?";
        String deleteWagonQuery = "DELETE FROM wagons WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Önce koltukları sil
            pstmt1 = conn.prepareStatement(deleteSeatsQuery);
            pstmt1.setInt(1, id);
            pstmt1.executeUpdate();

            // Sonra vagonu sil
            pstmt2 = conn.prepareStatement(deleteWagonQuery);
            pstmt2.setInt(1, id);
            int affectedRows = pstmt2.executeUpdate();

            if (affectedRows > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Vagon silme hatası: " + e.getMessage());
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("Rollback hatası: " + ex.getMessage());
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Bağlantı kapatma hatası: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}