package com.trainticket.view;

import com.trainticket.dao.TrainDAO;
import com.trainticket.model.Train;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SearchTrainsPanel extends JPanel {

    private User currentUser;
    private MainFrame parentFrame;

    private JComboBox<String> fromStationComboBox;
    private JComboBox<String> toStationComboBox;
    // JDateChooser yerine JComboBox'lar kullanıyoruz
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Integer> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JButton searchButton;
    private JTable trainsTable;
    private DefaultTableModel tableModel;

    private List<Train> searchResults;

    public SearchTrainsPanel(User user, MainFrame parent) {
        this.currentUser = user;
        this.parentFrame = parent;
        initComponents();

        // Sayfa yüklendiğinde tüm seferleri listele
        loadAllTrains();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Tren Bileti Ara");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Arama formu
        JPanel searchFormPanel = new JPanel();
        searchFormPanel.setLayout(new GridBagLayout());
        searchFormPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nereden
        JLabel fromLabel = new JLabel("Nereden:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        searchFormPanel.add(fromLabel, gbc);

        fromStationComboBox = new JComboBox<>();
        populateStations(fromStationComboBox);
        gbc.gridx = 1;
        gbc.gridy = 0;
        searchFormPanel.add(fromStationComboBox, gbc);

        // Nereye
        JLabel toLabel = new JLabel("Nereye:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        searchFormPanel.add(toLabel, gbc);

        toStationComboBox = new JComboBox<>();
        populateStations(toStationComboBox);
        gbc.gridx = 1;
        gbc.gridy = 1;
        searchFormPanel.add(toStationComboBox, gbc);

        // Tarih - JDateChooser yerine basit JComboBox'lar kullanıyoruz
        JLabel dateLabel = new JLabel("Tarih:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        searchFormPanel.add(dateLabel, gbc);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        // Gün için ComboBox
        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) {
            dayComboBox.addItem(i);
        }
        dayComboBox.setSelectedItem(LocalDate.now().getDayOfMonth());
        datePanel.add(dayComboBox);

        datePanel.add(new JLabel("/"));

        // Ay için ComboBox
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            monthComboBox.addItem(i);
        }
        monthComboBox.setSelectedItem(LocalDate.now().getMonthValue());
        datePanel.add(monthComboBox);

        datePanel.add(new JLabel("/"));

        // Yıl için ComboBox
        yearComboBox = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear; i <= currentYear + 1; i++) {
            yearComboBox.addItem(i);
        }
        yearComboBox.setSelectedItem(currentYear);
        datePanel.add(yearComboBox);

        gbc.gridx = 1;
        gbc.gridy = 2;
        searchFormPanel.add(datePanel, gbc);

        // Arama butonu
        searchButton = new JButton("Tren Ara");
        searchButton.setBackground(new Color(139, 0, 0));
        searchButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        searchFormPanel.add(searchButton, gbc);

        add(searchFormPanel, BorderLayout.CENTER);

        // Sonuçlar tablosu
        String[] columnNames = {"Tren No", "Tren Adı", "Kalkış", "Varış", "Kalkış Saati", "Varış Saati", "Fiyat"};
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
        scrollPane.setPreferredSize(new Dimension(700, 300));
        add(scrollPane, BorderLayout.SOUTH);

        // Olayları ekle
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTrains();
            }
        });

        trainsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectTrain();
                }
            }
        });
    }

    private void populateStations(JComboBox<String> comboBox) {
        TrainDAO trainDAO = new TrainDAO();
        List<String> stations = trainDAO.getAllStations();

        comboBox.addItem("Seçiniz...");
        for (String station : stations) {
            comboBox.addItem(station);
        }
    }

    // Tüm trenleri yükle
    private void loadAllTrains() {
        TrainDAO trainDAO = new TrainDAO();
        searchResults = trainDAO.getAllTrains();

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Sonuçları tabloya ekle
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Train train : searchResults) {
            String departureDateTime = train.getDepartureTime().format(dateFormatter) + " " +
                    train.getDepartureTime().format(timeFormatter);
            String arrivalDateTime = train.getArrivalTime().format(dateFormatter) + " " +
                    train.getArrivalTime().format(timeFormatter);

            Object[] row = {
                    train.getTrainNumber(),
                    train.getTrainName(),
                    train.getDepartureStation(),
                    train.getArrivalStation(),
                    departureDateTime,
                    arrivalDateTime,
                    String.format("%.2f TL", train.getPrice())
            };

            tableModel.addRow(row);
        }

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seferler bulunamadı. Lütfen yöneticinizle iletişime geçin.",
                    "Sefer Bulunamadı",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void searchTrains() {
        // Seçimleri kontrol et
        String fromStation = (String) fromStationComboBox.getSelectedItem();
        String toStation = (String) toStationComboBox.getSelectedItem();

        // ComboBox'lardan tarih alınıyor
        int day = (Integer) dayComboBox.getSelectedItem();
        int month = (Integer) monthComboBox.getSelectedItem();
        int year = (Integer) yearComboBox.getSelectedItem();

        // Eğer kalkış ve varış seçilmemişse tüm trenleri göster
        if (fromStation.equals("Seçiniz...") || toStation.equals("Seçiniz...")) {
            loadAllTrains();
            return;
        }

        if (fromStation.equals(toStation)) {
            JOptionPane.showMessageDialog(this,
                    "Kalkış ve varış istasyonları aynı olamaz!",
                    "Hatalı Seçim",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // LocalDate oluştur
        LocalDate localDate;
        try {
            localDate = LocalDate.of(year, month, day);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Geçersiz tarih! Lütfen geçerli bir tarih seçin.",
                    "Tarih Hatası",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Seçilen günün başlangıcı
        LocalDateTime searchDate = LocalDateTime.of(localDate, LocalTime.of(0, 0));

        // Trenleri ara
        TrainDAO trainDAO = new TrainDAO();
        searchResults = trainDAO.getTrainsByRoute(fromStation, toStation);

        // Tarihine göre filtrele
        searchResults.removeIf(train -> !train.getDepartureTime().toLocalDate().equals(localDate));

        // Tabloyu temizle
        tableModel.setRowCount(0);

        // Sonuçları tabloya ekle
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        for (Train train : searchResults) {
            String departureDateTime = train.getDepartureTime().format(dateFormatter) + " " +
                    train.getDepartureTime().format(timeFormatter);
            String arrivalDateTime = train.getArrivalTime().format(dateFormatter) + " " +
                    train.getArrivalTime().format(timeFormatter);

            Object[] row = {
                    train.getTrainNumber(),
                    train.getTrainName(),
                    train.getDepartureStation(),
                    train.getArrivalStation(),
                    departureDateTime,
                    arrivalDateTime,
                    String.format("%.2f TL", train.getPrice())
            };

            tableModel.addRow(row);
        }

        if (searchResults.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seçilen kriterlere uygun tren bulunamadı.",
                    "Sonuç Yok",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void selectTrain() {
        int selectedRow = trainsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < searchResults.size()) {
            Train selectedTrain = searchResults.get(selectedRow);

            // Koltuk seçim ekranını aç
            SeatSelectionPanel seatPanel = new SeatSelectionPanel(currentUser, selectedTrain, parentFrame);
            parentFrame.showPanel(seatPanel, "seatSelection");
        }
    }
}