package com.trainticket.view.admin;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    public AdminLoginFrame() {
        initComponents();
    }

    private void initComponents() {
        // Frame ayarları
        setTitle("TCDD Bilet Sistemi - Admin Girişi");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Logo / Başlık
        JLabel titleLabel = new JLabel("TCDD Admin Paneli");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(139, 0, 0)); // Koyu kırmızı
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 20, 5);
        panel.add(titleLabel, gbc);

        // Kullanıcı adı etiketi
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(usernameLabel, gbc);

        // Kullanıcı adı alanı
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        // Şifre etiketi
        JLabel passwordLabel = new JLabel("Şifre:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);

        // Şifre alanı
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Giriş butonu
        loginButton = new JButton("Admin Girişi");
        loginButton.setBackground(new Color(139, 0, 0)); // Koyu kırmızı
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(loginButton, gbc);

        // Geri dön butonu
        backButton = new JButton("Normal Girişe Dön");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 20, 5);
        panel.add(backButton, gbc);

        // Panel'i frame'e ekle
        getContentPane().add(panel);

        // Olay dinleyicileri
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                // Normal login ekranını aç
                com.trainticket.view.LoginFrame loginFrame = new com.trainticket.view.LoginFrame();
                loginFrame.setVisible(true);
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kullanıcı adı ve şifre gereklidir!", "Hata", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Admin kontrolü (sadece "admin" kullanıcısına izin ver)
        if (!"admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "Sadece admin kullanıcıları giriş yapabilir!", "Yetkisiz Erişim", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Admin girişi başarılı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

            // Admin ana ekranını aç
            AdminMainFrame adminMainFrame = new AdminMainFrame(user);
            adminMainFrame.setVisible(true);

            // Bu girişi kapat
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Test için main metodu
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new AdminLoginFrame().setVisible(true);
        });
    }
}