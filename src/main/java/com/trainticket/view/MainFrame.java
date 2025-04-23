package com.trainticket.view;

import com.trainticket.model.User;
import com.trainticket.model.Ticket;
import com.trainticket.dao.TicketDAO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {

    private User currentUser;
    private JPanel mainPanel;
    private JPanel welcomePanel;
    private JPanel contentPanel;

    private JButton searchTrainsButton;
    private JButton myTicketsButton;
    private JButton userProfileButton;
    private JButton logoutButton;

    public MainFrame(User user) {
        this.currentUser = user;
        initComponents();
        showWelcomePanel();
    }

    private void initComponents() {
        // Frame ayarları
        setTitle("TCDD Bilet Sistemi - Ana Sayfa");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Ana panel
        mainPanel = new JPanel(new BorderLayout());

        // Üst panel (menü)
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // İçerik paneli
        contentPanel = new JPanel(new CardLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Hoş geldiniz paneli
        welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");

        // Frame'e ana paneli ekle
        getContentPane().add(mainPanel);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(139, 0, 0)); // Koyu kırmızı
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // TCDD Logo/Başlık
        JLabel titleLabel = new JLabel("TCDD Bilet Sistemi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Butonlar
        searchTrainsButton = new JButton("Bilet Ara");
        searchTrainsButton.setBackground(new Color(220, 220, 220));
        panel.add(searchTrainsButton);

        myTicketsButton = new JButton("Biletlerim");
        myTicketsButton.setBackground(new Color(220, 220, 220));
        panel.add(myTicketsButton);

        userProfileButton = new JButton("Profilim");
        userProfileButton.setBackground(new Color(220, 220, 220));
        panel.add(userProfileButton);

        logoutButton = new JButton("Çıkış");
        logoutButton.setBackground(new Color(220, 220, 220));
        panel.add(logoutButton);

        // Olay dinleyicileri
        searchTrainsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSearchTrainsPanel();
            }
        });

        myTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyTicketsPanel();
            }
        });

        userProfileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserProfilePanel();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });

        return panel;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Hoş geldiniz mesajı
        JPanel welcomeMessagePanel = new JPanel();
        welcomeMessagePanel.setLayout(new BoxLayout(welcomeMessagePanel, BoxLayout.Y_AXIS));
        welcomeMessagePanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        JLabel welcomeLabel = new JLabel("Hoş Geldiniz, " + currentUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeMessagePanel.add(welcomeLabel);

        JLabel subLabel = new JLabel("TCDD Bilet Sistemi ile yolculuğunuzu planlamaya başlayın.");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        welcomeMessagePanel.add(subLabel);

        // Hızlı erişim butonları
        JPanel quickAccessPanel = new JPanel();
        quickAccessPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton quickSearchButton = new JButton("Bilet Ara");
        quickSearchButton.setPreferredSize(new Dimension(150, 100));
        quickSearchButton.setFont(new Font("Arial", Font.BOLD, 14));
        quickSearchButton.addActionListener(e -> showSearchTrainsPanel());
        quickAccessPanel.add(quickSearchButton);

        JButton quickMyTicketsButton = new JButton("Biletlerim");
        quickMyTicketsButton.setPreferredSize(new Dimension(150, 100));
        quickMyTicketsButton.setFont(new Font("Arial", Font.BOLD, 14));
        quickMyTicketsButton.addActionListener(e -> showMyTicketsPanel());
        quickAccessPanel.add(quickMyTicketsButton);

        welcomeMessagePanel.add(quickAccessPanel);

        // Son biletler
        JPanel recentTicketsPanel = new JPanel();
        recentTicketsPanel.setLayout(new BoxLayout(recentTicketsPanel, BoxLayout.Y_AXIS));
        recentTicketsPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel recentTicketsLabel = new JLabel("Son Biletleriniz");
        recentTicketsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        recentTicketsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        recentTicketsPanel.add(recentTicketsLabel);

        TicketDAO ticketDAO = new TicketDAO();
        List<Ticket> recentTickets = ticketDAO.getTicketsByUserId(currentUser.getId());

        if (recentTickets.isEmpty()) {
            JLabel noTicketsLabel = new JLabel("Henüz bilet satın almadınız.");
            noTicketsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noTicketsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            recentTicketsPanel.add(noTicketsLabel);
        } else {
            JPanel ticketListPanel = new JPanel();
            ticketListPanel.setLayout(new BoxLayout(ticketListPanel, BoxLayout.Y_AXIS));

            // Son 3 bileti göster
            int count = Math.min(recentTickets.size(), 3);
            for (int i = 0; i < count; i++) {
                Ticket ticket = recentTickets.get(i);

                JPanel ticketPanel = new JPanel();
                ticketPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                ticketPanel.setBorder(BorderFactory.createEtchedBorder());

                JLabel ticketLabel = new JLabel();
                if (ticket.getTrain() != null) {
                    ticketLabel.setText(ticket.getTrain().getDepartureStation() + " → " +
                            ticket.getTrain().getArrivalStation() + " | " +
                            ticket.getTrain().getFormattedDepartureTime());
                } else {
                    ticketLabel.setText("Bilet #" + ticket.getId());
                }

                ticketPanel.add(ticketLabel);

                JButton viewButton = new JButton("Görüntüle");
                int finalI = i;
                viewButton.addActionListener(e -> showTicketDetails(recentTickets.get(finalI)));
                ticketPanel.add(viewButton);

                ticketListPanel.add(ticketPanel);
            }

            JScrollPane scrollPane = new JScrollPane(ticketListPanel);
            scrollPane.setPreferredSize(new Dimension(400, 150));
            recentTicketsPanel.add(scrollPane);
        }

        // Panel'leri ana panel'e ekle
        panel.add(welcomeMessagePanel, BorderLayout.NORTH);
        panel.add(recentTicketsPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showSearchTrainsPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Tren arama panelini oluştur
        SearchTrainsPanel searchPanel = new SearchTrainsPanel(currentUser, this);
        contentPanel.add(searchPanel, "search");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "search");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyTicketsPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Biletlerim panelini oluştur
        MyTicketsPanel ticketsPanel = new MyTicketsPanel(currentUser);
        contentPanel.add(ticketsPanel, "tickets");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "tickets");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserProfilePanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Profil panelini oluştur
        UserProfilePanel profilePanel = new UserProfilePanel(currentUser);
        contentPanel.add(profilePanel, "profile");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "profile");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showWelcomePanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Hoş geldiniz panelini yeniden oluştur (son biletleri güncellemek için)
        welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "welcome");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showTicketDetails(Ticket ticket) {
        JDialog ticketDialog = new JDialog(this, "Bilet Detayları", true);
        ticketDialog.setSize(400, 400);
        ticketDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea ticketDetails = new JTextArea(ticket.getSummary());
        ticketDetails.setEditable(false);
        ticketDetails.setFont(new Font("Monospaced", Font.PLAIN, 14));
        ticketDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(ticketDetails);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Kapat");
        closeButton.addActionListener(e -> ticketDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        ticketDialog.add(panel);
        ticketDialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istediğinizden emin misiniz?",
                "Çıkış",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Login ekranına dön
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    // Diğer panel'leri MainFrame'e eklemek için yardımcı metot
    public void showPanel(JPanel panel, String name) {
        contentPanel.removeAll();
        contentPanel.add(panel, name);
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, name);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // Ana sayfaya dönmek için
    public void showHome() {
        showWelcomePanel();
    }
}