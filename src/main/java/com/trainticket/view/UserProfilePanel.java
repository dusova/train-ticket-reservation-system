package com.trainticket.view;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserProfilePanel extends JPanel {

    private User currentUser;
    private UserDAO userDAO;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JComboBox<String> genderComboBox;
    private JTextField tcNoField;
    private JTextField phoneField;
    private JButton saveButton;

    public UserProfilePanel(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        initComponents();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Profil Bilgilerim");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kullanıcı Adı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);

        usernameField = new JTextField(20);
        usernameField.setText(currentUser.getUsername());
        usernameField.setEditable(false); // Kullanıcı adı değiştirilemez
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        // Şifre
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Şifre:"), gbc);

        passwordField = new JPasswordField(20);
        passwordField.setText(currentUser.getPassword());
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Şifre Tekrar
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Şifre Tekrar:"), gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setText(currentUser.getPassword());
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(confirmPasswordField, gbc);

        // Ad Soyad
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Ad Soyad:"), gbc);

        fullNameField = new JTextField(20);
        fullNameField.setText(currentUser.getFullName());
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(fullNameField, gbc);

        // E-posta
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("E-posta:"), gbc);

        emailField = new JTextField(20);
        emailField.setText(currentUser.getEmail());
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(emailField, gbc);

        // Cinsiyet
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Cinsiyet:"), gbc);

        String[] genders = {"Seçiniz...", "Erkek", "Kadın"};
        genderComboBox = new JComboBox<>(genders);
        if (currentUser.getGender() != null && !currentUser.getGender().isEmpty()) {
            genderComboBox.setSelectedItem(currentUser.getGender());
        }
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(genderComboBox, gbc);

        // TC Kimlik No
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("TC Kimlik No:"), gbc);

        tcNoField = new JTextField(20);
        tcNoField.setText(currentUser.getTcNo());
        tcNoField.setEditable(false); // TC Kimlik No değiştirilemez
        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(tcNoField, gbc);

        // Telefon
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Telefon:"), gbc);

        phoneField = new JTextField(20);
        phoneField.setText(currentUser.getPhoneNumber());
        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(phoneField, gbc);

        // Kaydet butonu
        saveButton = new JButton("Bilgilerimi Güncelle");
        saveButton.setBackground(new Color(0, 100, 0));
        saveButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(saveButton, gbc);

        // Panel ortala
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.add(formPanel);

        add(centerPanel, BorderLayout.CENTER);

        // Olay dinleyicileri
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        // Form değerlerini al
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();
        String phone = phoneField.getText().trim();

        // Validasyon
        if (password.isEmpty() || confirmPassword.isEmpty() || fullName.isEmpty() ||
                email.isEmpty() || gender.equals("Seçiniz...")) {

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

        // Kullanıcı bilgilerini güncelle
        currentUser.setPassword(password);
        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setGender(gender);
        currentUser.setPhoneNumber(phone);

        // Veritabanında güncelle
        boolean success = userDAO.updateUser(currentUser);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Profil bilgileriniz başarıyla güncellendi.",
                    "Güncelleme Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Profil bilgileriniz güncellenirken bir hata oluştu.",
                    "Güncelleme Hatası",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}