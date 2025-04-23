package com.trainticket.view;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<String> genderComboBox;
    private JTextField tcNoField;
    private JTextField phoneField;
    private JButton registerButton;
    private JButton backButton;

    private JFrame parentFrame;

    public RegisterFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initComponents();
    }

    private void initComponents() {
        // Frame ayarları
        setTitle("TCDD Bilet Sistemi - Kayıt Ol");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Başlık
        JLabel titleLabel = new JLabel("Yeni Kullanıcı Kaydı");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 100, 0)); // Koyu yeşil
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 20, 5);
        panel.add(titleLabel, gbc);

        // Kullanıcı adı
        JLabel usernameLabel = new JLabel("Kullanıcı Adı:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);

        // Şifre
        JLabel passwordLabel = new JLabel("Şifre:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);

        // Şifre Tekrar
        JLabel confirmPasswordLabel = new JLabel("Şifre Tekrar:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(confirmPasswordField, gbc);

        // Ad Soyad
        JLabel fullNameLabel = new JLabel("Ad Soyad:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(fullNameLabel, gbc);

        fullNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(fullNameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("E-posta:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);

        // Cinsiyet
        JLabel genderLabel = new JLabel("Cinsiyet:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(genderLabel, gbc);

        String[] genders = {"Seçiniz...", "Erkek", "Kadın"};
        genderComboBox = new JComboBox<>(genders);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(genderComboBox, gbc);

        // TC No
        JLabel tcNoLabel = new JLabel("TC Kimlik No:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(tcNoLabel, gbc);

        tcNoField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(tcNoField, gbc);

        // Telefon
        JLabel phoneLabel = new JLabel("Telefon:");
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(phoneLabel, gbc);

        phoneField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(phoneField, gbc);

        // Kayıt Ol butonu
        registerButton = new JButton("Kayıt Ol");
        registerButton.setBackground(new Color(0, 100, 0));
        registerButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        panel.add(registerButton, gbc);

        // Geri Dön butonu
        backButton = new JButton("Giriş Ekranına Dön");
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.insets = new Insets(5, 5, 20, 5);
        panel.add(backButton, gbc);

        // Panel'i frame'e ekle
        getContentPane().add(panel);

        // Olay dinleyicileri
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBackToLogin();
            }
        });
    }

    private void register() {
        // Form verilerini al
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String tcNo = tcNoField.getText().trim();
        String phone = phoneField.getText().trim();

        // Validasyon
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                fullName.isEmpty() || email.isEmpty() || gender.equals("Seçiniz...") ||
                tcNo.isEmpty()) {

            JOptionPane.showMessageDialog(this,
                    "Lütfen tüm zorunlu alanları doldurun!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Şifreler eşleşmiyor!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (tcNo.length() != 11 || !tcNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "TC Kimlik No 11 haneli sayısal bir değer olmalıdır!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kullanıcı oluştur
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setGender(gender);
        user.setTcNo(tcNo);
        user.setPhoneNumber(phone);

        // Veritabanına kaydet
        UserDAO userDAO = new UserDAO();

        // TC No zaten kayıtlı mı kontrol et
        if (userDAO.findUserByTcNo(tcNo) != null) {
            JOptionPane.showMessageDialog(this,
                    "Bu TC Kimlik Numarası zaten kayıtlı!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userDAO.addUser(user);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Kayıt başarıyla tamamlandı! Giriş yapabilirsiniz.",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
            goBackToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Kayıt sırasında bir hata oluştu! Kullanıcı adı zaten kullanılıyor olabilir.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToLogin() {
        parentFrame.setVisible(true);
        this.dispose();
    }
}