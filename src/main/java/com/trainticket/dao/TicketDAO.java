package com.trainticket.dao;

import com.trainticket.model.Ticket;
import com.trainticket.model.Train;
import com.trainticket.model.Wagon;
import com.trainticket.model.Seat;
import com.trainticket.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Bilet ekle
    public boolean addTicket(Ticket ticket) {
        String query = "INSERT INTO tickets (userId, trainId, wagonId, seatId, purchaseDate, " +
                "passengerName, passengerTcNo, passengerGender, price, isPaid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, ticket.getUserId());
            pstmt.setInt(2, ticket.getTrainId());
            pstmt.setInt(3, ticket.getWagonId());
            pstmt.setInt(4, ticket.getSeatId());
            pstmt.setString(5, ticket.getPurchaseDate().format(formatter));
            pstmt.setString(6, ticket.getPassengerName());
            pstmt.setString(7, ticket.getPassengerTcNo());
            pstmt.setString(8, ticket.getPassengerGender());
            pstmt.setDouble(9, ticket.getPrice());
            pstmt.setInt(10, ticket.isPaid() ? 1 : 0);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    ticket.setId(generatedKeys.getInt(1));

                    // Koltuk rezervasyonu yap
                    SeatDAO seatDAO = new SeatDAO();
                    seatDAO.reserveSeat(ticket.getSeatId(), ticket.getPassengerGender());

                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Bilet ekleme hatası: " + e.getMessage());
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

    // ID ile bilet getir
    public Ticket getTicketById(int id) {
        String query = "SELECT * FROM tickets WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Ticket ticket = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                ticket = extractTicketFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Bilet getirme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Referansları ayrı bir işlemde yükle
        if (ticket != null) {
            loadTicketReferences(ticket);
        }

        return ticket;
    }

    // Kullanıcı ID'sine göre biletleri getir
    public List<Ticket> getTicketsByUserId(int userId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE userId = ? ORDER BY purchaseDate DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, userId);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Ticket ticket = extractTicketFromResultSet(rs);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı biletlerini listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Referansları ayrı ayrı yükle
        for (Ticket ticket : tickets) {
            loadTicketReferences(ticket);
        }

        return tickets;
    }

    // Bilet ödeme durumunu güncelle
    public boolean updateTicketPaymentStatus(int id, boolean isPaid) {
        String query = "UPDATE tickets SET isPaid = ? WHERE id = ?";

        PreparedStatement pstmt = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, isPaid ? 1 : 0);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Bilet ödeme durumu güncelleme hatası: " + e.getMessage());
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

    // Bilet iptal et
    public boolean cancelTicket(int id) {
        // Önce bileti alalım
        Ticket ticket = getTicketById(id);
        if (ticket == null) {
            System.err.println("İptal edilecek bilet bulunamadı: ID = " + id);
            return false;
        }

        // Koltuk rezervasyonunu iptal et
        SeatDAO seatDAO = new SeatDAO();
        boolean seatCancelled = seatDAO.cancelReservation(ticket.getSeatId());

        if (!seatCancelled) {
            System.err.println("Koltuk rezervasyonu iptal edilemedi: SeatID = " + ticket.getSeatId());
            return false;
        }

        // Bileti sil
        String query = "DELETE FROM tickets WHERE id = ?";

        PreparedStatement pstmt = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Bilet iptal hatası: " + e.getMessage());
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

    // TC Kimlik No'ya göre biletleri getir
    public List<Ticket> getTicketsByTcNo(String tcNo) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT * FROM tickets WHERE passengerTcNo = ? ORDER BY purchaseDate DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, tcNo);

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Ticket ticket = extractTicketFromResultSet(rs);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            System.err.println("TC No ile bilet listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Referansları ayrı ayrı yükle
        for (Ticket ticket : tickets) {
            loadTicketReferences(ticket);
        }

        return tickets;
    }

    // ResultSet'ten Ticket nesnesi oluştur
    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getInt("id"));
        ticket.setUserId(rs.getInt("userId"));
        ticket.setTrainId(rs.getInt("trainId"));
        ticket.setWagonId(rs.getInt("wagonId"));
        ticket.setSeatId(rs.getInt("seatId"));

        // String'den LocalDateTime'a dönüştürme
        String purchaseDate = rs.getString("purchaseDate");
        if (purchaseDate != null && !purchaseDate.isEmpty()) {
            ticket.setPurchaseDate(LocalDateTime.parse(purchaseDate, formatter));
        } else {
            ticket.setPurchaseDate(LocalDateTime.now());
        }

        ticket.setPassengerName(rs.getString("passengerName"));
        ticket.setPassengerTcNo(rs.getString("passengerTcNo"));
        ticket.setPassengerGender(rs.getString("passengerGender"));
        ticket.setPrice(rs.getDouble("price"));
        ticket.setPaid(rs.getInt("isPaid") == 1);

        return ticket;
    }

    // Bilet için ilişkili nesneleri yükle
    private void loadTicketReferences(Ticket ticket) {
        // Tren bilgisini yükle
        TrainDAO trainDAO = new TrainDAO();
        Train train = trainDAO.getTrainById(ticket.getTrainId());
        ticket.setTrain(train);

        // Vagon bilgisini yükle
        WagonDAO wagonDAO = new WagonDAO();
        Wagon wagon = wagonDAO.getWagonById(ticket.getWagonId());
        ticket.setWagon(wagon);

        // Koltuk bilgisini yükle
        SeatDAO seatDAO = new SeatDAO();
        Seat seat = seatDAO.getSeatById(ticket.getSeatId());
        ticket.setSeat(seat);
    }
}