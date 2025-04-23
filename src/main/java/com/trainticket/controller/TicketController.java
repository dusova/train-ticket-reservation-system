package com.trainticket.controller;

import com.trainticket.dao.TicketDAO;
import com.trainticket.model.Ticket;
import com.trainticket.model.User;
import com.trainticket.model.Train;
import com.trainticket.model.Wagon;
import com.trainticket.model.Seat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketController {

    private TicketDAO ticketDAO;

    public TicketController() {
        this.ticketDAO = new TicketDAO();
    }

    // Yeni bilet oluştur
    public boolean createTicket(User user, Train train, Wagon wagon, Seat seat,
                                String passengerName, String passengerTcNo,
                                String passengerGender, double price, boolean isPaid) {

        if (user == null || train == null || wagon == null || seat == null ||
                passengerName == null || passengerName.isEmpty() ||
                passengerTcNo == null || passengerTcNo.isEmpty() ||
                passengerGender == null || passengerGender.isEmpty() ||
                price <= 0) {
            return false;
        }

        // TC Kimlik No formatını kontrol et
        if (passengerTcNo.length() != 11 || !passengerTcNo.matches("\\d+")) {
            return false;
        }

        // Cinsiyet kısıtlaması kontrolü
        if (!seat.canBeReservedBy(passengerGender)) {
            return false;
        }

        // Bilet oluştur
        Ticket ticket = new Ticket();
        ticket.setUserId(user.getId());
        ticket.setTrainId(train.getId());
        ticket.setWagonId(wagon.getId());
        ticket.setSeatId(seat.getId());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setPassengerName(passengerName);
        ticket.setPassengerTcNo(passengerTcNo);
        ticket.setPassengerGender(passengerGender);
        ticket.setPrice(price);
        ticket.setPaid(isPaid);

        // Referansları ayarla
        ticket.setTrain(train);
        ticket.setWagon(wagon);
        ticket.setSeat(seat);

        return ticketDAO.addTicket(ticket);
    }

    // Kullanıcının biletlerini getir
    public List<Ticket> getUserTickets(User user) {
        if (user == null || user.getId() <= 0) {
            return new ArrayList<>();
        }

        return ticketDAO.getTicketsByUserId(user.getId());
    }

    // TC Kimlik No ile biletleri getir
    public List<Ticket> getTicketsByTcNo(String tcNo) {
        if (tcNo == null || tcNo.isEmpty()) {
            return new ArrayList<>();
        }

        return ticketDAO.getTicketsByTcNo(tcNo);
    }

    // Bilet iptal et
    public boolean cancelTicket(int ticketId) {
        if (ticketId <= 0) {
            return false;
        }

        // Bileti kontrol et
        Ticket ticket = ticketDAO.getTicketById(ticketId);

        if (ticket == null) {
            return false;
        }

        // Yolculuk tarihini kontrol et (isteğe bağlı)
        if (ticket.getTrain() != null &&
                ticket.getTrain().getDepartureTime().isBefore(LocalDateTime.now())) {
            return false; // Kalkış saati geçmiş biletler iptal edilemez
        }

        return ticketDAO.cancelTicket(ticketId);
    }

    // Bilet ödeme durumunu güncelle
    public boolean updateTicketPaymentStatus(int ticketId, boolean isPaid) {
        if (ticketId <= 0) {
            return false;
        }

        return ticketDAO.updateTicketPaymentStatus(ticketId, isPaid);
    }

    // ID ile bilet getir
    public Ticket getTicketById(int id) {
        if (id <= 0) {
            return null;
        }

        return ticketDAO.getTicketById(id);
    }
}