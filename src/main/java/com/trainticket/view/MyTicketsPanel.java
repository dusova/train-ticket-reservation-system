package com.trainticket.view;

import com.trainticket.dao.TicketDAO;
import com.trainticket.model.Ticket;
import com.trainticket.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MyTicketsPanel extends JPanel {

    private User currentUser;
    private JTable ticketsTable;
    private DefaultTableModel tableModel;
    private List<Ticket> tickets;

    public MyTicketsPanel(User user) {
        this.currentUser = user;
        initComponents();
        loadTickets();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Biletlerim");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Tablo
        String[] columnNames = {"Bilet No", "Tren", "Güzergah", "Tarih", "Vagon/Koltuk", "Ücret", "Ödeme Durumu"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketsTable = new JTable(tableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.setRowHeight(25);

        // Çift tıklama ile detay görüntüleme
        ticketsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewTicketDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Yenile");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTickets();
            }
        });
        buttonPanel.add(refreshButton);

        JButton viewButton = new JButton("Detaylar");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewTicketDetails();
            }
        });
        buttonPanel.add(viewButton);

        JButton cancelButton = new JButton("Bileti İptal Et");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelTicket();
            }
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTickets() {
        // Mevcut tablo içeriğini temizle
        tableModel.setRowCount(0);

        // Kullanıcının biletlerini getir
        TicketDAO ticketDAO = new TicketDAO();
        tickets = ticketDAO.getTicketsByUserId(currentUser.getId());

        if (tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Henüz satın alınmış biletiniz bulunmamaktadır.",
                    "Bilet Bulunamadı",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Biletleri tabloya ekle
        for (Ticket ticket : tickets) {
            String trainInfo = "";
            String routeInfo = "";
            String dateInfo = "";
            String wagonSeatInfo = "";

            if (ticket.getTrain() != null) {
                trainInfo = ticket.getTrain().getTrainNumber() + " - " + ticket.getTrain().getTrainName();
                routeInfo = ticket.getTrain().getDepartureStation() + " → " + ticket.getTrain().getArrivalStation();
                dateInfo = ticket.getTrain().getFormattedDepartureTime();
            }

            if (ticket.getWagon() != null && ticket.getSeat() != null) {
                wagonSeatInfo = "Vagon: " + ticket.getWagon().getWagonNumber() +
                        ", Koltuk: " + ticket.getSeat().getSeatNumber();
            }

            Object[] row = {
                    ticket.getId(),
                    trainInfo,
                    routeInfo,
                    dateInfo,
                    wagonSeatInfo,
                    String.format("%.2f TL", ticket.getPrice()),
                    ticket.isPaid() ? "Ödenmiş" : "Ödenmemiş"
            };

            tableModel.addRow(row);
        }
    }

    private void viewTicketDetails() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tickets.size()) {
            Ticket selectedTicket = tickets.get(selectedRow);

            JDialog ticketDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bilet Detayları", true);
            ticketDialog.setSize(400, 400);
            ticketDialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JTextArea ticketDetails = new JTextArea(selectedTicket.getSummary());
            ticketDetails.setEditable(false);
            ticketDetails.setFont(new Font("Monospaced", Font.PLAIN, 14));
            ticketDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(ticketDetails);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();

            JButton printButton = new JButton("Bileti Yazdır");
            printButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ticketDetails.print();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ticketDialog,
                                "Yazdırma sırasında bir hata oluştu: " + ex.getMessage(),
                                "Yazdırma Hatası",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            buttonPanel.add(printButton);

            JButton closeButton = new JButton("Kapat");
            closeButton.addActionListener(e -> ticketDialog.dispose());
            buttonPanel.add(closeButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            ticketDialog.add(panel);
            ticketDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lütfen bir bilet seçin!",
                    "Bilet Seçilmedi",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cancelTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tickets.size()) {
            Ticket selectedTicket = tickets.get(selectedRow);

            // Bilet iptal edilebilir mi kontrol et (örn. yolculuk tarihi geçmemiş olmalı)
            boolean canCancel = true;
            String cantCancelReason = null;

            if (selectedTicket.getTrain() != null) {
                if (selectedTicket.getTrain().getDepartureTime().isBefore(java.time.LocalDateTime.now())) {
                    canCancel = false;
                    cantCancelReason = "Kalkış tarihi geçmiş biletler iptal edilemez.";
                }
            }

            if (!canCancel) {
                JOptionPane.showMessageDialog(this,
                        cantCancelReason,
                        "İptal Edilemez",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bilet No: " + selectedTicket.getId() + " numaralı bileti iptal etmek istediğinizden emin misiniz?",
                    "Bilet İptal Onayı",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Bileti iptal et
                TicketDAO ticketDAO = new TicketDAO();
                boolean success = ticketDAO.cancelTicket(selectedTicket.getId());

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Bilet başarıyla iptal edildi. İade işlemi 3-5 iş günü içinde gerçekleştirilecektir.",
                            "İptal Başarılı",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Bilet listesini yenile
                    loadTickets();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Bilet iptal edilirken bir hata oluştu.",
                            "İptal Hatası",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Lütfen iptal etmek istediğiniz bileti seçin!",
                    "Bilet Seçilmedi",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}