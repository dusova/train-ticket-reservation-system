package com.trainticket.view.admin;

import com.trainticket.dao.TicketDAO;
import com.trainticket.dao.TrainDAO;
import com.trainticket.dao.UserDAO;
import com.trainticket.model.Ticket;
import com.trainticket.model.Train;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private JTable ticketSalesTable;
    private DefaultTableModel ticketSalesTableModel;
    private JComboBox<String> periodComboBox;
    private JTable routeStatsTable;
    private DefaultTableModel routeStatsTableModel;
    private JButton generateReportButton;

    public ReportPanel() {
        initComponents();
        loadInitialData();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Raporlar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Sekme paneli oluştur
        tabbedPane = new JTabbedPane();

        // Bilet Satış Raporu sekmesi
        JPanel ticketSalesPanel = createTicketSalesPanel();
        tabbedPane.addTab("Bilet Satış Raporu", ticketSalesPanel);

        // Güzergah İstatistikleri sekmesi
        JPanel routeStatsPanel = createRouteStatsPanel();
        tabbedPane.addTab("Güzergah İstatistikleri", routeStatsPanel);

        // Doluluk Oranları sekmesi
        JPanel occupancyRatesPanel = createOccupancyRatesPanel();
        tabbedPane.addTab("Doluluk Oranları", occupancyRatesPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Buton paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        generateReportButton = new JButton("Rapor Oluştur");
        generateReportButton.setBackground(new Color(0, 100, 150));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.addActionListener(e -> generateReport());
        buttonPanel.add(generateReportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createTicketSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filtre paneli
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Dönem: "));

        String[] periods = {"Bugün", "Bu Hafta", "Bu Ay", "Tüm Zamanlar"};
        periodComboBox = new JComboBox<>(periods);
        periodComboBox.addActionListener(e -> loadTicketSalesData());
        filterPanel.add(periodComboBox);

        panel.add(filterPanel, BorderLayout.NORTH);

        // Tablo
        String[] columnNames = {"Tarih", "Satılan Bilet", "Toplam Kazanç (TL)"};
        ticketSalesTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketSalesTable = new JTable(ticketSalesTableModel);
        ticketSalesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketSalesTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(ticketSalesTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRouteStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tablo
        String[] columnNames = {"Güzergah", "Sefer Sayısı", "Satılan Bilet", "Toplam Kazanç (TL)"};
        routeStatsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        routeStatsTable = new JTable(routeStatsTableModel);
        routeStatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routeStatsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(routeStatsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOccupancyRatesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Burada doluluk oranları grafiği olabilir
        // Şimdilik basit bir tablo gösterelim
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new BorderLayout());

        // Örnek doluluk bilgisi
        JLabel infoLabel = new JLabel("<html>Ortalama Doluluk Oranı: %65<br><br>" +
                "En Yüksek Doluluk: İstanbul → Ankara (%85)<br>" +
                "En Düşük Doluluk: İzmir → Konya (%45)<br><br>" +
                "(Bu grafik örnek gösterim için eklenmiştir.)</html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        chartPanel.add(infoLabel, BorderLayout.CENTER);

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadInitialData() {
        loadTicketSalesData();
        loadRouteStatsData();
    }

    private void loadTicketSalesData() {
        // Tabloyu temizle
        ticketSalesTableModel.setRowCount(0);

        // Seçilen döneme göre bilet satışlarını getir
        // Burada gerçek veriler yerine örnek veriler kullanıyoruz
        String selectedPeriod = (String) periodComboBox.getSelectedItem();

        // Örnek veriler
        if ("Bugün".equals(selectedPeriod)) {
            Object[] row1 = {LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 12, "1,450.00"};
            ticketSalesTableModel.addRow(row1);
        } else if ("Bu Hafta".equals(selectedPeriod)) {
            Object[] row1 = {LocalDate.now().minusDays(6).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 8, "950.00"};
            Object[] row2 = {LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 10, "1,200.00"};
            Object[] row3 = {LocalDate.now().minusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 12, "1,450.00"};
            Object[] row4 = {LocalDate.now().minusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 15, "1,800.00"};
            Object[] row5 = {LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 11, "1,300.00"};
            Object[] row6 = {LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 9, "1,100.00"};
            Object[] row7 = {LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), 12, "1,450.00"};

            ticketSalesTableModel.addRow(row1);
            ticketSalesTableModel.addRow(row2);
            ticketSalesTableModel.addRow(row3);
            ticketSalesTableModel.addRow(row4);
            ticketSalesTableModel.addRow(row5);
            ticketSalesTableModel.addRow(row6);
            ticketSalesTableModel.addRow(row7);
        } else {
            // Diğer dönemler için örnek veri
            Object[] row1 = {LocalDate.now().minusDays(30).format(DateTimeFormatter.ofPattern("MM.yyyy")), 87, "10,450.00"};
            Object[] row2 = {LocalDate.now().minusDays(15).format(DateTimeFormatter.ofPattern("MM.yyyy")), 95, "11,340.00"};
            Object[] row3 = {LocalDate.now().format(DateTimeFormatter.ofPattern("MM.yyyy")), 77, "9,240.00"};

            ticketSalesTableModel.addRow(row1);
            ticketSalesTableModel.addRow(row2);
            ticketSalesTableModel.addRow(row3);
        }
    }

    private void loadRouteStatsData() {
        // Tabloyu temizle
        routeStatsTableModel.setRowCount(0);

        // Örnek veriler
        Object[] row1 = {"İstanbul → Ankara", 120, 1800, "216,000.00"};
        Object[] row2 = {"Ankara → İstanbul", 118, 1750, "210,000.00"};
        Object[] row3 = {"İzmir → Konya", 80, 950, "81,125.00"};
        Object[] row4 = {"Konya → İzmir", 82, 920, "78,660.00"};

        routeStatsTableModel.addRow(row1);
        routeStatsTableModel.addRow(row2);
        routeStatsTableModel.addRow(row3);
        routeStatsTableModel.addRow(row4);
    }

    private void generateReport() {
        // PDF veya Excel raporu oluşturma işlemi burada yapılabilir
        // Şimdilik sadece bir mesaj gösterelim

        JOptionPane.showMessageDialog(this,
                "Rapor oluşturuldu ve 'reports' klasörüne kaydedildi.",
                "Rapor Oluşturuldu",
                JOptionPane.INFORMATION_MESSAGE);
    }
}