package com.trainticket.view;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;
import com.trainticket.view.admin.AdminLoginFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        // Frame ayarları
        setTitle("TCDD Bilet Sistemi - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Logo / Başlık
        JLabel titleLabel = new JLabel("TCDD Bilet Sistemi");
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
        loginButton = new JButton("Giriş Yap");
        loginButton.setBackground(new Color(139, 0, 0)); // Koyu kırmızı
        loginButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(loginButton, gbc);

        // Kayıt butonu
        registerButton = new JButton("Kayıt Ol");
        registerButton.setBackground(new Color(0, 100, 0)); // Koyu yeşil
        registerButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 5, 5, 5); // Insets'i değiştirdik
        panel.add(registerButton, gbc);

        // Admin giriş butonu - YENİ EKLENEN BUTON
        JButton adminButton = new JButton("Admin Girişi");
        adminButton.setBackground(new Color(100, 100, 100)); // Gri
        adminButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5; // Register butonunun altına konumlandırdık
        gbc.insets = new Insets(5, 5, 20, 5);
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new com.trainticket.view.admin.AdminLoginFrame().setVisible(true);
            }
        });
        panel.add(adminButton, gbc);

        // Panel'i frame'e ekle
        getContentPane().add(panel);

        // Olay dinleyicileri
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRegisterFrame();
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

        UserDAO userDAO = new UserDAO();
        User user = userDAO.validateUser(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Giriş başarılı!", "Bilgi", JOptionPane.INFORMATION_MESSAGE);

            // Ana ekranı aç
            MainFrame mainFrame = new MainFrame(user);
            mainFrame.setVisible(true);

            // Giriş ekranını kapat
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Geçersiz kullanıcı adı veya şifre!", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegisterFrame() {
        RegisterFrame registerFrame = new RegisterFrame(this);
        registerFrame.setVisible(true);
        this.setVisible(false);
    }
}