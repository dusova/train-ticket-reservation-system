package com.trainticket.view.admin;

import com.trainticket.dao.TrainDAO;
import com.trainticket.model.Train;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrainManagementPanel extends JPanel {

    private JTable trainsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;

    private List<Train> trains;

    public TrainManagementPanel() {
        initComponents();
        loadTrains();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Tren Seferleri Yönetimi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = new JButton("Yeni Sefer Ekle");
        addButton.setBackground(new Color(0, 150, 0));
        addButton.setForeground(Color.WHITE);
        buttonPanel.add(addButton);

        editButton = new JButton("Sefer Düzenle");
        buttonPanel.add(editButton);

        deleteButton = new JButton("Sefer Sil");
        deleteButton.setBackground(new Color(150, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Yenile");
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Tablo
        String[] columnNames = {"ID", "Tren No", "Tren Adı", "Kalkış", "Varış", "Kalkış Zamanı", "Varış Zamanı", "Ücret"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trainsTable = new JTable(tableModel);
        trainsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(trainsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Olay dinleyicileri
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTrain();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editTrain();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTrain();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTrains();
            }
        });
    }

    private void loadTrains() {
        TrainDAO trainDAO = new TrainDAO();
        trains = trainDAO.getAllTrains();

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Trenleri tabloya ekle
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (Train train : trains) {
            Object[] row = {
                    train.getId(),
                    train.getTrainNumber(),
                    train.getTrainName(),
                    train.getDepartureStation(),
                    train.getArrivalStation(),
                    train.getDepartureTime().format(formatter),
                    train.getArrivalTime().format(formatter),
                    String.format("%.2f TL", train.getPrice())
            };

            tableModel.addRow(row);
        }
    }

    private void addTrain() {
        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Tren Seferi Ekle", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tren No
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tren No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField trainNumberField = new JTextField(15);
        formPanel.add(trainNumberField, gbc);

        // Tren Adı
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tren Adı:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField trainNameField = new JTextField(15);
        formPanel.add(trainNameField, gbc);

        // Kalkış İstasyonu
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Kalkış:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JComboBox<String> departureStationComboBox = new JComboBox<>();
        populateStations(departureStationComboBox);
        formPanel.add(departureStationComboBox, gbc);

        // Varış İstasyonu
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Varış:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JComboBox<String> arrivalStationComboBox = new JComboBox<>();
        populateStations(arrivalStationComboBox);
        formPanel.add(arrivalStationComboBox, gbc);

        // Kalkış Tarihi
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Kalkış Tarihi (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField departureDateField = new JTextField(15);
        formPanel.add(departureDateField, gbc);

        // Varış Tarihi
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Varış Tarihi (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField arrivalDateField = new JTextField(15);
        formPanel.add(arrivalDateField, gbc);

        // Ücret
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Ücret (TL):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField priceField = new JTextField(15);
        formPanel.add(priceField, gbc);

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
            if (trainNumberField.getText().isEmpty() || trainNameField.getText().isEmpty() ||
                    departureStationComboBox.getSelectedIndex() == 0 || arrivalStationComboBox.getSelectedIndex() == 0 ||
                    departureDateField.getText().isEmpty() || arrivalDateField.getText().isEmpty() ||
                    priceField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm alanları doldurun!");
                return;
            }

            try {
                // Tarihleri parse et
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime departureTime = LocalDateTime.parse(departureDateField.getText(), dateFormatter);
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalDateField.getText(), dateFormatter);

                // Ücret validasyonu
                double price = Double.parseDouble(priceField.getText());

                // Yeni tren oluştur
                Train newTrain = new Train();
                newTrain.setTrainNumber(trainNumberField.getText());
                newTrain.setTrainName(trainNameField.getText());
                newTrain.setDepartureStation((String) departureStationComboBox.getSelectedItem());
                newTrain.setArrivalStation((String) arrivalStationComboBox.getSelectedItem());
                newTrain.setDepartureTime(departureTime);
                newTrain.setArrivalTime(arrivalTime);
                newTrain.setPrice(price);

                // Veritabanına ekle
                TrainDAO trainDAO = new TrainDAO();
                boolean success = trainDAO.addTrain(newTrain);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Tren seferi başarıyla eklendi.");
                    dialog.dispose();
                    loadTrains(); // Tabloyu yenile
                } else {
                    JOptionPane.showMessageDialog(dialog, "Tren seferi eklenirken bir hata oluştu.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Geçersiz tarih formatı veya ücret bilgisi! \nTarih formatı: yyyy-MM-dd HH:mm \nÜcret: Sayısal değer");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editTrain() {
        int selectedRow = trainsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek istediğiniz tren seferini seçin.");
            return;
        }

        // Seçilen treni al
        int trainId = (int) tableModel.getValueAt(selectedRow, 0);
        final Train[] selectedTrainArray = new Train[1]; // Array içinde tutarak lambda için final olmasını sağlıyoruz

        for (Train train : trains) {
            if (train.getId() == trainId) {
                selectedTrainArray[0] = train;
                break;
            }
        }

        if (selectedTrainArray[0] == null) {
            JOptionPane.showMessageDialog(this, "Seçilen tren seferi bulunamadı.");
            return;
        }

        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tren Seferi Düzenle", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tren No
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tren No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField trainNumberField = new JTextField(15);
        trainNumberField.setText(selectedTrainArray[0].getTrainNumber());
        formPanel.add(trainNumberField, gbc);

        // Tren Adı
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Tren Adı:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField trainNameField = new JTextField(15);
        trainNameField.setText(selectedTrainArray[0].getTrainName());
        formPanel.add(trainNameField, gbc);

        // Kalkış İstasyonu
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Kalkış:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JComboBox<String> departureStationComboBox = new JComboBox<>();
        populateStations(departureStationComboBox);
        departureStationComboBox.setSelectedItem(selectedTrainArray[0].getDepartureStation());
        formPanel.add(departureStationComboBox, gbc);

        // Varış İstasyonu
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Varış:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JComboBox<String> arrivalStationComboBox = new JComboBox<>();
        populateStations(arrivalStationComboBox);
        arrivalStationComboBox.setSelectedItem(selectedTrainArray[0].getArrivalStation());
        formPanel.add(arrivalStationComboBox, gbc);

        // Kalkış Tarihi
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Kalkış Tarihi (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField departureDateField = new JTextField(15);
        departureDateField.setText(selectedTrainArray[0].getDepartureTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        formPanel.add(departureDateField, gbc);

        // Varış Tarihi
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Varış Tarihi (yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField arrivalDateField = new JTextField(15);
        arrivalDateField.setText(selectedTrainArray[0].getArrivalTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        formPanel.add(arrivalDateField, gbc);

        // Ücret
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Ücret (TL):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField priceField = new JTextField(15);
        priceField.setText(String.valueOf(selectedTrainArray[0].getPrice()));
        formPanel.add(priceField, gbc);

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
            if (trainNumberField.getText().isEmpty() || trainNameField.getText().isEmpty() ||
                    departureStationComboBox.getSelectedIndex() == 0 || arrivalStationComboBox.getSelectedIndex() == 0 ||
                    departureDateField.getText().isEmpty() || arrivalDateField.getText().isEmpty() ||
                    priceField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm alanları doldurun!");
                return;
            }

            try {
                // Tarihleri parse et
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime departureTime = LocalDateTime.parse(departureDateField.getText(), dateFormatter);
                LocalDateTime arrivalTime = LocalDateTime.parse(arrivalDateField.getText(), dateFormatter);

                // Ücret validasyonu
                double price = Double.parseDouble(priceField.getText());

                // Treni güncelle
                selectedTrainArray[0].setTrainNumber(trainNumberField.getText());
                selectedTrainArray[0].setTrainName(trainNameField.getText());
                selectedTrainArray[0].setDepartureStation((String) departureStationComboBox.getSelectedItem());
                selectedTrainArray[0].setArrivalStation((String) arrivalStationComboBox.getSelectedItem());
                selectedTrainArray[0].setDepartureTime(departureTime);
                selectedTrainArray[0].setArrivalTime(arrivalTime);
                selectedTrainArray[0].setPrice(price);

                // Veritabanında güncelle
                TrainDAO trainDAO = new TrainDAO();
                boolean success = trainDAO.updateTrain(selectedTrainArray[0]);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Tren seferi başarıyla güncellendi.");
                    dialog.dispose();
                    loadTrains(); // Tabloyu yenile
                } else {
                    JOptionPane.showMessageDialog(dialog, "Tren seferi güncellenirken bir hata oluştu.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Geçersiz tarih formatı veya ücret bilgisi! \nTarih formatı: yyyy-MM-dd HH:mm \nÜcret: Sayısal değer");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteTrain() {
        int selectedRow = trainsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz tren seferini seçin.");
            return;
        }

        // Seçilen trenin ID'sini al
        int trainId = (int) tableModel.getValueAt(selectedRow, 0);

        // Onay iste
        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçilen tren seferini silmek istediğinizden emin misiniz?",
                "Sefer Sil",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            TrainDAO trainDAO = new TrainDAO();
            boolean success = trainDAO.deleteTrain(trainId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Tren seferi başarıyla silindi.");
                loadTrains(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(this, "Tren seferini silerken bir hata oluştu.");
            }
        }
    }

    private void populateStations(JComboBox<String> comboBox) {
        TrainDAO trainDAO = new TrainDAO();
        List<String> stations = trainDAO.getAllStations();

        comboBox.addItem("Seçiniz...");
        for (String station : stations) {
            comboBox.addItem(station);
        }
    }
}