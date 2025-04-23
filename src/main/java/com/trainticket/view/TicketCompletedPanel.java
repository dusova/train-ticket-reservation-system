package com.trainticket.view;

import com.trainticket.model.Ticket;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;

public class TicketCompletedPanel extends JPanel {

    private User currentUser;
    private Ticket ticket;
    private MainFrame parentFrame;

    private JTextArea ticketDetails;
    private JButton printButton;
    private JButton homeButton;

    public TicketCompletedPanel(User user, Ticket ticket, MainFrame parent) {
        this.currentUser = user;
        this.ticket = ticket;
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel successIcon = new JLabel("✓", SwingConstants.CENTER);
        successIcon.setFont(new Font("Arial", Font.BOLD, 50));
        successIcon.setForeground(new Color(0, 150, 0));
        headerPanel.add(successIcon, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("İşlem Tamamlandı!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JLabel subtitleLabel = new JLabel("Biletiniz başarıyla oluşturuldu.", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Bilet detayları
        JPanel ticketPanel = new JPanel(new BorderLayout());
        ticketPanel.setBorder(BorderFactory.createTitledBorder("Bilet Bilgileri"));

        ticketDetails = new JTextArea(15, 40);
        ticketDetails.setEditable(false);
        ticketDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ticketDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bilet bilgilerini formatla
        StringBuilder sb = new StringBuilder();
        sb.append("----------------- TCDD BİLET -----------------\n\n");
        sb.append("Bilet No: ").append(ticket.getId()).append("\n");
        sb.append("Yolcu: ").append(ticket.getPassengerName()).append("\n");
        sb.append("TC No: ").append(ticket.getPassengerTcNo()).append("\n\n");

        if (ticket.getTrain() != null) {
            sb.append("Tren: ").append(ticket.getTrain().getTrainNumber())
                    .append(" - ").append(ticket.getTrain().getTrainName()).append("\n");
            sb.append("Güzergah: ").append(ticket.getTrain().getDepartureStation())
                    .append(" → ").append(ticket.getTrain().getArrivalStation()).append("\n");
            sb.append("Kalkış: ").append(ticket.getTrain().getFormattedDepartureTime()).append("\n");
            sb.append("Varış: ").append(ticket.getTrain().getFormattedArrivalTime()).append("\n\n");
        }

        if (ticket.getWagon() != null) {
            sb.append("Vagon: ").append(ticket.getWagon().getWagonNumber())
                    .append(" (").append(ticket.getWagon().getWagonType()).append(")\n");
        }

        if (ticket.getSeat() != null) {
            sb.append("Koltuk: ").append(ticket.getSeat().getSeatNumber()).append("\n\n");
        }

        sb.append("Ücret: ").append(String.format("%.2f TL", ticket.getPrice())).append("\n");
        sb.append("Ödeme Durumu: ").append(ticket.isPaid() ? "Ödenmiş" : "Ödenmemiş").append("\n\n");
        sb.append("Tarih: ").append(ticket.getPurchaseDate().format(
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))).append("\n");
        sb.append("\n-----------------------------------------------\n");
        sb.append("TCDD yolculuğunuzda iyi eğlenceler diler.");

        ticketDetails.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(ticketDetails);
        ticketPanel.add(scrollPane, BorderLayout.CENTER);

        add(ticketPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        printButton = new JButton("Bileti Yazdır");
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        printButton.setIcon(UIManager.getIcon("FileView.printIcon"));
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printTicket();
            }
        });
        buttonPanel.add(printButton);

        homeButton = new JButton("Ana Sayfaya Dön");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
        });
        buttonPanel.add(homeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void printTicket() {
        try {
            ticketDetails.print();
            JOptionPane.showMessageDialog(this,
                    "Bilet yazdırma işlemi başlatıldı.",
                    "Yazdırma",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(this,
                    "Yazdırma sırasında bir hata oluştu: " + e.getMessage(),
                    "Yazdırma Hatası",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goHome() {
        // Ana sayfaya dön
        parentFrame.showHome();
    }
}