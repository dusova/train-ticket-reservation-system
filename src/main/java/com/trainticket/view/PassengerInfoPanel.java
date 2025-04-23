package com.trainticket.view;

import com.trainticket.dao.TicketDAO;
import com.trainticket.dao.UserDAO;
import com.trainticket.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class PassengerInfoPanel extends JPanel {

    private User currentUser;
    private Train selectedTrain;
    private Wagon selectedWagon;
    private Seat selectedSeat;
    private MainFrame parentFrame;

    private JTextField nameField;
    private JTextField tcNoField;
    private JComboBox<String> genderComboBox;
    private JLabel priceLabel;
    private JButton confirmButton;
    private JButton backButton;

    public PassengerInfoPanel(User user, Train train, Wagon wagon, Seat seat, MainFrame parent) {
        this.currentUser = user;
        this.selectedTrain = train;
        this.selectedWagon = wagon;
        this.selectedSeat = seat;
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Yolcu Bilgileri");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Tren ve Koltuk Bilgisi
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Seçilen Bilet"));

        infoPanel.add(new JLabel("Tren:"));
        infoPanel.add(new JLabel(selectedTrain.getTrainNumber() + " - " + selectedTrain.getTrainName()));

        infoPanel.add(new JLabel("Güzergah:"));
        infoPanel.add(new JLabel(selectedTrain.getDepartureStation() + " → " + selectedTrain.getArrivalStation()));

        infoPanel.add(new JLabel("Tarih:"));
        infoPanel.add(new JLabel(selectedTrain.getFormattedDepartureTime()));

        infoPanel.add(new JLabel("Vagon:"));
        infoPanel.add(new JLabel(selectedWagon.getWagonNumber() + " - " + selectedWagon.getWagonType()));

        infoPanel.add(new JLabel("Koltuk:"));
        infoPanel.add(new JLabel(String.valueOf(selectedSeat.getSeatNumber())));

        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Yolcu bilgileri formu
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Yolcu Bilgileri"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ad Soyad
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Ad Soyad:"), gbc);

        nameField = new JTextField(20);
        nameField.setText(currentUser.getFullName());
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(nameField, gbc);

        // TC Kimlik No
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("TC Kimlik No:"), gbc);

        tcNoField = new JTextField(20);
        tcNoField.setText(currentUser.getTcNo());
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(tcNoField, gbc);

        // Cinsiyet
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Cinsiyet:"), gbc);

        String[] genders = {"Seçiniz...", "Erkek", "Kadın"};
        genderComboBox = new JComboBox<>(genders);
        if (currentUser.getGender() != null && !currentUser.getGender().isEmpty()) {
            genderComboBox.setSelectedItem(currentUser.getGender());
        }
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(genderComboBox, gbc);

        // Ücret
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Bilet Ücreti:"), gbc);

        double price = selectedTrain.getPrice();
        if (selectedWagon.getWagonType().equals("Business")) {
            price *= 1.5; // Business vagon için %50 ek ücret
        }

        priceLabel = new JLabel(String.format("%.2f TL", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(priceLabel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Panel'i ana panel'e ekle
        add(mainPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        backButton = new JButton("Geri");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        buttonPanel.add(backButton);

        confirmButton = new JButton("Ödemeye Geç");
        confirmButton.setBackground(new Color(0, 100, 0));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processTicket();
            }
        });
        buttonPanel.add(confirmButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void goBack() {
        // Koltuk seçimi ekranına geri dön
        SeatSelectionPanel seatPanel = new SeatSelectionPanel(currentUser, selectedTrain, parentFrame);
        parentFrame.showPanel(seatPanel, "seatSelection");
    }

    private void processTicket() {
        // Form validasyonu
        String name = nameField.getText().trim();
        String tcNo = tcNoField.getText().trim();
        String gender = (String) genderComboBox.getSelectedItem();

        if (name.isEmpty() || tcNo.isEmpty() || gender.equals("Seçiniz...")) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen tüm alanları doldurun!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tcNo.length() != 11 || !tcNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "TC Kimlik No 11 haneli sayısal bir değer olmalıdır!",
                    "Hatalı TC No",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cinsiyet kısıtlamasını kontrol et
        if (!selectedSeat.canBeReservedBy(gender)) {
            JOptionPane.showMessageDialog(this,
                    "Seçtiğiniz koltuk, cinsiyet kısıtlaması nedeniyle rezerve edilemez!",
                    "Cinsiyet Kısıtlaması",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Bilet oluştur
        Ticket ticket = new Ticket();
        ticket.setUserId(currentUser.getId());
        ticket.setTrainId(selectedTrain.getId());
        ticket.setWagonId(selectedWagon.getId());
        ticket.setSeatId(selectedSeat.getId());
        ticket.setPurchaseDate(LocalDateTime.now());
        ticket.setPassengerName(name);
        ticket.setPassengerTcNo(tcNo);
        ticket.setPassengerGender(gender);

        // Ücreti hesapla
        double price = selectedTrain.getPrice();
        if (selectedWagon.getWagonType().equals("Business")) {
            price *= 1.5; // Business vagon için %50 ek ücret
        }
        ticket.setPrice(price);

        // Başlangıçta ödenmemiş olarak ayarla
        ticket.setPaid(false);

        // Referansları ayarla
        ticket.setTrain(selectedTrain);
        ticket.setWagon(selectedWagon);
        ticket.setSeat(selectedSeat);

        // Ödeme ekranını aç
        PaymentPanel paymentPanel = new PaymentPanel(currentUser, ticket, parentFrame);
        parentFrame.showPanel(paymentPanel, "payment");
    }
}