package com.trainticket.view.admin;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JButton searchButton;

    private List<User> users;

    public UserManagementPanel() {
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Kullanıcı Yönetimi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Arama paneli
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Kullanıcı Ara: "));

        searchField = new JTextField(20);
        searchPanel.add(searchField);

        searchButton = new JButton("Ara");
        searchButton.addActionListener(e -> searchUsers());
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.SOUTH);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = new JButton("Yeni Kullanıcı Ekle");
        addButton.setBackground(new Color(0, 150, 0));
        addButton.setForeground(Color.WHITE);
        buttonPanel.add(addButton);

        editButton = new JButton("Kullanıcı Düzenle");
        buttonPanel.add(editButton);

        deleteButton = new JButton("Kullanıcı Sil");
        deleteButton.setBackground(new Color(150, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Yenile");
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Tablo
        String[] columnNames = {"ID", "Kullanıcı Adı", "Tam Ad", "E-posta", "Cinsiyet", "TC No", "Telefon"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Olay dinleyicileri
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });
    }

    private void loadUsers() {
        UserDAO userDAO = new UserDAO();
        users = userDAO.getAllUsers();

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Kullanıcıları tabloya ekle
        for (User user : users) {
            Object[] row = {
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getGender(),
                    user.getTcNo(),
                    user.getPhoneNumber()
            };

            tableModel.addRow(row);
        }
    }

    private void searchUsers() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            // Arama metni boşsa tüm kullanıcıları göster
            loadUsers();
            return;
        }

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Kullanıcıları ara
        for (User user : users) {
            if (user.getUsername().toLowerCase().contains(searchText) ||
                    user.getFullName().toLowerCase().contains(searchText) ||
                    user.getEmail().toLowerCase().contains(searchText) ||
                    user.getTcNo().contains(searchText)) {

                Object[] row = {
                        user.getId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getGender(),
                        user.getTcNo(),
                        user.getPhoneNumber()
                };

                tableModel.addRow(row);
            }
        }
    }

    private void addUser() {
        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Kullanıcı Ekle", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Kullanıcı Adı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        // Şifre
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Şifre:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        // Tam Ad
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tam Ad:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField fullNameField = new JTextField(15);
        formPanel.add(fullNameField, gbc);

        // E-posta
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("E-posta:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField emailField = new JTextField(15);
        formPanel.add(emailField, gbc);

        // Cinsiyet
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cinsiyet:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        String[] genders = {"Seçiniz...", "Erkek", "Kadın"};
        JComboBox<String> genderComboBox = new JComboBox<>(genders);
        formPanel.add(genderComboBox, gbc);

        // TC No
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("TC Kimlik No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField tcNoField = new JTextField(15);
        formPanel.add(tcNoField, gbc);

        // Telefon
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Telefon:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField phoneField = new JTextField(15);
        formPanel.add(phoneField, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buton panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Kaydet");
        saveButton.setBackground(new Color(0, 150, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            // Form validasyonu
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String tcNo = tcNoField.getText().trim();
            String phone = phoneField.getText().trim();

            if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() ||
                    email.isEmpty() || gender.equals("Seçiniz...") || tcNo.isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm zorunlu alanları doldurun!");
                return;
            }

            if (tcNo.length() != 11 || !tcNo.matches("\\d+")) {
                JOptionPane.showMessageDialog(dialog, "TC Kimlik No 11 haneli sayısal bir değer olmalıdır!");
                return;
            }

            // Yeni kullanıcı oluştur
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFullName(fullName);
            newUser.setEmail(email);
            newUser.setGender(gender);
            newUser.setTcNo(tcNo);
            newUser.setPhoneNumber(phone);

            // Veritabanına ekle
            UserDAO userDAO = new UserDAO();

            // TC No zaten kayıtlı mı kontrol et
            if (userDAO.findUserByTcNo(tcNo) != null) {
                JOptionPane.showMessageDialog(dialog, "Bu TC Kimlik Numarası zaten kayıtlı!");
                return;
            }

            boolean success = userDAO.addUser(newUser);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Kullanıcı başarıyla eklendi.");
                dialog.dispose();
                loadUsers(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(dialog, "Kullanıcı eklenirken bir hata oluştu. Kullanıcı adı zaten kullanılıyor olabilir.");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek istediğiniz kullanıcıyı seçin.");
            return;
        }

        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        final User[] selectedUserArray = new User[1]; // Array içinde tutarak lambda için final olmasını sağlıyoruz

        for (User user : users) {
            if (user.getId() == userId) {
                selectedUserArray[0] = user;
                break;
            }
        }

        if (selectedUserArray[0] == null) {
            JOptionPane.showMessageDialog(this, "Seçilen kullanıcı bulunamadı.");
            return;
        }

        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Kullanıcı Düzenle", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Kullanıcı Adı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField usernameField = new JTextField(15);
        usernameField.setText(selectedUserArray[0].getUsername());
        usernameField.setEditable(false); // Kullanıcı adı değiştirilemez
        formPanel.add(usernameField, gbc);

        // Şifre
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Şifre:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setText(selectedUserArray[0].getPassword());
        formPanel.add(passwordField, gbc);

        // Tam Ad
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Tam Ad:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField fullNameField = new JTextField(15);
        fullNameField.setText(selectedUserArray[0].getFullName());
        formPanel.add(fullNameField, gbc);

        // E-posta
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("E-posta:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField emailField = new JTextField(15);
        emailField.setText(selectedUserArray[0].getEmail());
        formPanel.add(emailField, gbc);

        // Cinsiyet
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cinsiyet:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        String[] genders = {"Seçiniz...", "Erkek", "Kadın"};
        JComboBox<String> genderComboBox = new JComboBox<>(genders);
        genderComboBox.setSelectedItem(selectedUserArray[0].getGender());
        formPanel.add(genderComboBox, gbc);

        // TC No
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("TC Kimlik No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField tcNoField = new JTextField(15);
        tcNoField.setText(selectedUserArray[0].getTcNo());
        tcNoField.setEditable(false); // TC No değiştirilemez
        formPanel.add(tcNoField, gbc);

        // Telefon
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Telefon:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField phoneField = new JTextField(15);
        phoneField.setText(selectedUserArray[0].getPhoneNumber());
        formPanel.add(phoneField, gbc);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buton panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("İptal");
        cancelButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Güncelle");
        saveButton.setBackground(new Color(0, 150, 0));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            // Form validasyonu
            String password = new String(passwordField.getPassword());
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String gender = (String) genderComboBox.getSelectedItem();
            String phone = phoneField.getText().trim();

            if (password.isEmpty() || fullName.isEmpty() ||
                    email.isEmpty() || gender.equals("Seçiniz...")) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm zorunlu alanları doldurun!");
                return;
            }

            // Kullanıcıyı güncelle
            selectedUserArray[0].setPassword(password);
            selectedUserArray[0].setFullName(fullName);
            selectedUserArray[0].setEmail(email);
            selectedUserArray[0].setGender(gender);
            selectedUserArray[0].setPhoneNumber(phone);

            // Veritabanında güncelle
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.updateUser(selectedUserArray[0]);

            if (success) {
                JOptionPane.showMessageDialog(dialog, "Kullanıcı başarıyla güncellendi.");
                dialog.dispose();
                loadUsers(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(dialog, "Kullanıcı güncellenirken bir hata oluştu.");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz kullanıcıyı seçin.");
            return;
        }

        // Seçilen kullanıcının ID'sini al
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);

        // Admin kullanıcısı silinemez
        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "Admin kullanıcısı silinemez!");
            return;
        }

        // Onay iste
        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçilen kullanıcıyı silmek istediğinizden emin misiniz?",
                "Kullanıcı Sil",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            UserDAO userDAO = new UserDAO();
            boolean success = userDAO.deleteUser(userId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Kullanıcı başarıyla silindi.");
                loadUsers(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(this, "Kullanıcı silinirken bir hata oluştu.");
            }
        }
    }
}