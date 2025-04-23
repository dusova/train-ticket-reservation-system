package com.trainticket.view.admin;

import com.trainticket.dao.TrainDAO;
import com.trainticket.dao.WagonDAO;
import com.trainticket.model.Train;
import com.trainticket.model.Wagon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class WagonManagementPanel extends JPanel {

    private JTable wagonsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JComboBox<Train> trainFilterComboBox;

    private List<Wagon> wagons;

    public WagonManagementPanel() {
        initComponents();
        loadWagons();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Vagon Yönetimi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Trene Göre Filtrele: "));

        trainFilterComboBox = new JComboBox<>();
        populateTrainComboBox();
        trainFilterComboBox.addActionListener(e -> filterWagonsByTrain());
        filterPanel.add(trainFilterComboBox);

        add(filterPanel, BorderLayout.SOUTH);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = new JButton("Yeni Vagon Ekle");
        addButton.setBackground(new Color(0, 150, 0));
        addButton.setForeground(Color.WHITE);
        buttonPanel.add(addButton);

        editButton = new JButton("Vagon Düzenle");
        buttonPanel.add(editButton);

        deleteButton = new JButton("Vagon Sil");
        deleteButton.setBackground(new Color(150, 0, 0));
        deleteButton.setForeground(Color.WHITE);
        buttonPanel.add(deleteButton);

        refreshButton = new JButton("Yenile");
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.NORTH);

        // Tablo
        String[] columnNames = {"ID", "Tren ID", "Tren", "Vagon No", "Vagon Tipi", "Toplam Koltuk"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        wagonsTable = new JTable(tableModel);
        wagonsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        wagonsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(wagonsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Olay dinleyicileri
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addWagon();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editWagon();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteWagon();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadWagons();
            }
        });
    }

    private void loadWagons() {
        // Tüm trenleri getir
        TrainDAO trainDAO = new TrainDAO();
        List<Train> trains = trainDAO.getAllTrains();

        // Tüm vagonları getir
        List<Wagon> allWagons = new WagonDAO().getAllWagons();
        this.wagons = allWagons;

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Vagonları tabloya ekle
        for (Wagon wagon : wagons) {
            // Tren bilgisini bul
            Train train = null;
            for (Train t : trains) {
                if (t.getId() == wagon.getTrainId()) {
                    train = t;
                    break;
                }
            }

            String trainInfo = train != null ?
                    train.getTrainNumber() + " - " + train.getTrainName() :
                    "Bilinmeyen Tren";

            Object[] row = {
                    wagon.getId(),
                    wagon.getTrainId(),
                    trainInfo,
                    wagon.getWagonNumber(),
                    wagon.getWagonType(),
                    wagon.getTotalSeats()
            };

            tableModel.addRow(row);
        }
    }

    private void filterWagonsByTrain() {
        // Seçilen treni al
        Train selectedTrain = (Train) trainFilterComboBox.getSelectedItem();

        if (selectedTrain == null) {
            // Filtre yok, tüm vagonları göster
            loadWagons();
            return;
        }

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Seçilen trenin vagonlarını getir
        WagonDAO wagonDAO = new WagonDAO();
        List<Wagon> filteredWagons = wagonDAO.getWagonsByTrainId(selectedTrain.getId());

        // Vagonları tabloya ekle
        for (Wagon wagon : filteredWagons) {
            Object[] row = {
                    wagon.getId(),
                    wagon.getTrainId(),
                    selectedTrain.getTrainNumber() + " - " + selectedTrain.getTrainName(),
                    wagon.getWagonNumber(),
                    wagon.getWagonType(),
                    wagon.getTotalSeats()
            };

            tableModel.addRow(row);
        }
    }

    private void populateTrainComboBox() {
        TrainDAO trainDAO = new TrainDAO();
        List<Train> trains = trainDAO.getAllTrains();

        trainFilterComboBox.removeAllItems();
        trainFilterComboBox.addItem(null); // "Tümü" seçeneği

        for (Train train : trains) {
            trainFilterComboBox.addItem(train);
        }
    }

    private void addWagon() {
        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Yeni Vagon Ekle", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tren Seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tren:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JComboBox<Train> trainComboBox = new JComboBox<>();
        populateTrainComboBoxForForm(trainComboBox);
        formPanel.add(trainComboBox, gbc);

        // Vagon No
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Vagon No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField wagonNumberField = new JTextField(10);
        formPanel.add(wagonNumberField, gbc);

        // Vagon Tipi
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Vagon Tipi:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] wagonTypes = {"Ekonomi", "Business", "Yataklı", "Yemekli"};
        JComboBox<String> wagonTypeComboBox = new JComboBox<>(wagonTypes);
        formPanel.add(wagonTypeComboBox, gbc);

        // Toplam Koltuk
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Toplam Koltuk:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField totalSeatsField = new JTextField(10);
        formPanel.add(totalSeatsField, gbc);

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
            if (trainComboBox.getSelectedItem() == null ||
                    wagonNumberField.getText().isEmpty() ||
                    totalSeatsField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm alanları doldurun!");
                return;
            }

            try {
                // Vagon No validasyonu
                int wagonNumber = Integer.parseInt(wagonNumberField.getText());

                // Koltuk sayısı validasyonu
                int totalSeats = Integer.parseInt(totalSeatsField.getText());

                if (wagonNumber <= 0 || totalSeats <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Vagon no ve koltuk sayısı pozitif olmalıdır!");
                    return;
                }

                // Yeni vagon oluştur
                Train selectedTrain = (Train) trainComboBox.getSelectedItem();

                Wagon newWagon = new Wagon();
                newWagon.setTrainId(selectedTrain.getId());
                newWagon.setWagonNumber(wagonNumber);
                newWagon.setWagonType((String) wagonTypeComboBox.getSelectedItem());
                newWagon.setTotalSeats(totalSeats);

                // Veritabanına ekle
                WagonDAO wagonDAO = new WagonDAO();
                boolean success = wagonDAO.addWagon(newWagon);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Vagon başarıyla eklendi.");
                    dialog.dispose();
                    loadWagons(); // Tabloyu yenile
                } else {
                    JOptionPane.showMessageDialog(dialog, "Vagon eklenirken bir hata oluştu.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vagon no ve koltuk sayısı sayısal değer olmalıdır!");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editWagon() {
        int selectedRow = wagonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen düzenlemek istediğiniz vagonu seçin.");
            return;
        }

        // Seçilen vagonu al
        int wagonId = (int) tableModel.getValueAt(selectedRow, 0);
        final Wagon[] selectedWagonArray = new Wagon[1];


        for (Wagon wagon : wagons) {
            if (wagon.getId() == wagonId) {
                selectedWagonArray[0] = wagon;
                break;
            }
        }

        if (selectedWagonArray[0] == null) {
            JOptionPane.showMessageDialog(this, "Seçilen vagon bulunamadı.");
            return;
        }

        // Diyalog oluştur
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Vagon Düzenle", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Tren Seçimi
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Tren:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JComboBox<Train> trainComboBox = new JComboBox<>();
        populateTrainComboBoxForForm(trainComboBox);

        // Mevcut treni seç
        for (int i = 0; i < trainComboBox.getItemCount(); i++) {
            Train train = trainComboBox.getItemAt(i);
            if (train != null && train.getId() == selectedWagonArray[0].getTrainId()) {
                trainComboBox.setSelectedIndex(i);
                break;
            }
        }

        formPanel.add(trainComboBox, gbc);

        // Vagon No
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Vagon No:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField wagonNumberField = new JTextField(10);
        wagonNumberField.setText(String.valueOf(selectedWagonArray[0].getWagonNumber()));
        formPanel.add(wagonNumberField, gbc);

        // Vagon Tipi
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Vagon Tipi:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] wagonTypes = {"Ekonomi", "Business", "Yataklı", "Yemekli"};
        JComboBox<String> wagonTypeComboBox = new JComboBox<>(wagonTypes);

        // Mevcut vagon tipini seç
        for (int i = 0; i < wagonTypes.length; i++) {
            if (wagonTypes[i].equals(selectedWagonArray[0].getWagonType())) {
                wagonTypeComboBox.setSelectedIndex(i);
                break;
            }
        }

        formPanel.add(wagonTypeComboBox, gbc);

        // Toplam Koltuk
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Toplam Koltuk:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField totalSeatsField = new JTextField(10);
        totalSeatsField.setText(String.valueOf(selectedWagonArray[0].getTotalSeats()));
        formPanel.add(totalSeatsField, gbc);

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
            if (trainComboBox.getSelectedItem() == null ||
                    wagonNumberField.getText().isEmpty() ||
                    totalSeatsField.getText().isEmpty()) {

                JOptionPane.showMessageDialog(dialog, "Lütfen tüm alanları doldurun!");
                return;
            }

            try {
                // Vagon No validasyonu
                int wagonNumber = Integer.parseInt(wagonNumberField.getText());

                // Koltuk sayısı validasyonu
                int totalSeats = Integer.parseInt(totalSeatsField.getText());

                if (wagonNumber <= 0 || totalSeats <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Vagon no ve koltuk sayısı pozitif olmalıdır!");
                    return;
                }

                // Vagon tipini al
                String wagonType = (String) wagonTypeComboBox.getSelectedItem();

                // Vagonu güncelle
                Train selectedTrain = (Train) trainComboBox.getSelectedItem();

                selectedWagonArray[0].setTrainId(selectedTrain.getId());
                selectedWagonArray[0].setWagonNumber(wagonNumber);
                selectedWagonArray[0].setWagonType(wagonType);
                selectedWagonArray[0].setTotalSeats(totalSeats);

                // Veritabanında güncelle
                WagonDAO wagonDAO = new WagonDAO();
                boolean success = wagonDAO.updateWagon(selectedWagonArray[0]);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Vagon başarıyla güncellendi.");
                    dialog.dispose();
                    loadWagons(); // Tabloyu yenile
                } else {
                    JOptionPane.showMessageDialog(dialog, "Vagon güncellenirken bir hata oluştu.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Vagon no ve koltuk sayısı sayısal değer olmalıdır!");
            }
        });
        buttonPanel.add(saveButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteWagon() {
        int selectedRow = wagonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Lütfen silmek istediğiniz vagonu seçin.");
            return;
        }

        // Seçilen vagonun ID'sini al
        int wagonId = (int) tableModel.getValueAt(selectedRow, 0);

        // Onay iste
        int confirm = JOptionPane.showConfirmDialog(this,
                "Seçilen vagonu silmek istediğinizden emin misiniz?",
                "Vagon Sil",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            WagonDAO wagonDAO = new WagonDAO();
            boolean success = wagonDAO.deleteWagon(wagonId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Vagon başarıyla silindi.");
                loadWagons(); // Tabloyu yenile
            } else {
                JOptionPane.showMessageDialog(this, "Vagonu silerken bir hata oluştu.");
            }
        }
    }

    private void populateTrainComboBoxForForm(JComboBox<Train> comboBox) {
        TrainDAO trainDAO = new TrainDAO();
        List<Train> trains = trainDAO.getAllTrains();

        comboBox.removeAllItems();

        for (Train train : trains) {
            comboBox.addItem(train);
        }
    }
}