package com.trainticket.view.admin;

import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminMainFrame extends JFrame {

    private User adminUser;
    private JPanel mainPanel;
    private JPanel contentPanel;

    private JButton trainsButton;
    private JButton wagonsButton;
    private JButton usersButton;
    private JButton reportsButton;
    private JButton logoutButton;

    public AdminMainFrame(User user) {
        this.adminUser = user;
        initComponents();
    }

    private void initComponents() {
        // Frame ayarları
        setTitle("TCDD Yönetim Paneli");
        setSize(900, 700);
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
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");

        // Frame'e ana paneli ekle
        getContentPane().add(mainPanel);

        // İlk olarak hoş geldiniz panelini göster
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "welcome");
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(139, 0, 0)); // Koyu kırmızı
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        // TCDD Admin Panel Başlık
        JLabel titleLabel = new JLabel("TCDD Yönetim Paneli");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        // Butonlar
        trainsButton = new JButton("Tren Seferleri");
        trainsButton.setBackground(new Color(220, 220, 220));
        panel.add(trainsButton);

        wagonsButton = new JButton("Vagonlar");
        wagonsButton.setBackground(new Color(220, 220, 220));
        panel.add(wagonsButton);

        usersButton = new JButton("Kullanıcılar");
        usersButton.setBackground(new Color(220, 220, 220));
        panel.add(usersButton);

        reportsButton = new JButton("Raporlar");
        reportsButton.setBackground(new Color(220, 220, 220));
        panel.add(reportsButton);

        logoutButton = new JButton("Çıkış");
        logoutButton.setBackground(new Color(220, 220, 220));
        panel.add(logoutButton);

        // Olay dinleyicileri
        trainsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showTrainManagementPanel();
            }
        });

        wagonsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWagonManagementPanel();
            }
        });

        usersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showUserManagementPanel();
            }
        });

        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReportsPanel();
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

        JLabel welcomeLabel = new JLabel("Hoş Geldiniz, " + adminUser.getFullName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeMessagePanel.add(welcomeLabel);

        JLabel subLabel = new JLabel("TCDD Yönetim Paneline hoş geldiniz. Lütfen bir işlem seçin.");
        subLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));
        welcomeMessagePanel.add(subLabel);

        // Hızlı erişim butonları
        JPanel quickAccessPanel = new JPanel();
        quickAccessPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

        JButton trainManagementButton = new JButton("Tren Seferleri Yönetimi");
        trainManagementButton.setPreferredSize(new Dimension(200, 100));
        trainManagementButton.setFont(new Font("Arial", Font.BOLD, 14));
        trainManagementButton.addActionListener(e -> showTrainManagementPanel());
        quickAccessPanel.add(trainManagementButton);

        JButton userManagementButton = new JButton("Kullanıcı Yönetimi");
        userManagementButton.setPreferredSize(new Dimension(200, 100));
        userManagementButton.setFont(new Font("Arial", Font.BOLD, 14));
        userManagementButton.addActionListener(e -> showUserManagementPanel());
        quickAccessPanel.add(userManagementButton);

        welcomeMessagePanel.add(quickAccessPanel);

        // İstatistikler (örnek)
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Sistem İstatistikleri"));

        JLabel usersLabel = new JLabel("Toplam Kullanıcı: " + getNumberOfUsers());
        usersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(usersLabel);

        JLabel trainsLabel = new JLabel("Toplam Sefer: " + getNumberOfTrains());
        trainsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(trainsLabel);

        JLabel ticketsLabel = new JLabel("Toplam Satılan Bilet: " + getNumberOfTickets());
        ticketsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statsPanel.add(ticketsLabel);

        JPanel statsWrapperPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsWrapperPanel.add(statsPanel);

        panel.add(welcomeMessagePanel, BorderLayout.NORTH);
        panel.add(statsWrapperPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showTrainManagementPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Tren yönetim panelini oluştur
        TrainManagementPanel trainPanel = new TrainManagementPanel();
        contentPanel.add(trainPanel, "trains");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "trains");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showWagonManagementPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Vagon yönetim panelini oluştur
        WagonManagementPanel wagonPanel = new WagonManagementPanel();
        contentPanel.add(wagonPanel, "wagons");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "wagons");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserManagementPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Kullanıcı yönetim panelini oluştur
        UserManagementPanel userPanel = new UserManagementPanel();
        contentPanel.add(userPanel, "users");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "users");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReportsPanel() {
        // Mevcut içeriği temizle
        contentPanel.removeAll();

        // Rapor panelini oluştur
        ReportPanel reportPanel = new ReportPanel();
        contentPanel.add(reportPanel, "reports");

        // Görünümü güncelle
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "reports");
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istediğinizden emin misiniz?",
                "Çıkış",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Admin login ekranına dön
            AdminLoginFrame loginFrame = new AdminLoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    // İstatistik metodları (gerçek uygulamada DAO'lardan çekilecek)
    private int getNumberOfUsers() {
        // Örnek değer
        return 15;
    }

    private int getNumberOfTrains() {
        // Örnek değer
        return 30;
    }

    private int getNumberOfTickets() {
        // Örnek değer
        return 120;
    }
}