package com.trainticket.view;

import com.trainticket.dao.WagonDAO;
import com.trainticket.dao.SeatDAO;
import com.trainticket.model.Train;
import com.trainticket.model.User;
import com.trainticket.model.Wagon;
import com.trainticket.model.Seat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SeatSelectionPanel extends JPanel {

    private User currentUser;
    private Train selectedTrain;
    private MainFrame parentFrame;

    private JComboBox<Wagon> wagonComboBox;
    private JPanel seatPanel;
    private JButton continueButton;
    private JButton backButton;

    private Wagon selectedWagon;
    private Seat selectedSeat;

    public SeatSelectionPanel(User user, Train train, MainFrame parent) {
        this.currentUser = user;
        this.selectedTrain = train;
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Koltuk Seçimi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Tren bilgisi
        JPanel trainInfoPanel = new JPanel();
        trainInfoPanel.setLayout(new GridLayout(4, 2, 10, 5));
        trainInfoPanel.setBorder(BorderFactory.createTitledBorder("Tren Bilgileri"));

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        trainInfoPanel.add(new JLabel("Tren No:"));
        trainInfoPanel.add(new JLabel(selectedTrain.getTrainNumber() + " - " + selectedTrain.getTrainName()));

        trainInfoPanel.add(new JLabel("Güzergah:"));
        trainInfoPanel.add(new JLabel(selectedTrain.getDepartureStation() + " → " + selectedTrain.getArrivalStation()));

        trainInfoPanel.add(new JLabel("Kalkış:"));
        trainInfoPanel.add(new JLabel(selectedTrain.getDepartureTime().format(dateTimeFormatter)));

        trainInfoPanel.add(new JLabel("Varış:"));
        trainInfoPanel.add(new JLabel(selectedTrain.getArrivalTime().format(dateTimeFormatter)));

        mainPanel.add(trainInfoPanel, BorderLayout.NORTH);

        // Vagon seçimi
        JPanel wagonSelectionPanel = new JPanel();
        wagonSelectionPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        wagonSelectionPanel.setBorder(BorderFactory.createTitledBorder("Vagon Seçimi"));

        wagonSelectionPanel.add(new JLabel("Vagon:"));

        wagonComboBox = new JComboBox<>();

        // ÖNEMLİ: ActionListener'ı şimdi eklemiyoruz, vagonları yükledikten sonra ekleyeceğiz
        wagonSelectionPanel.add(wagonComboBox);

        mainPanel.add(wagonSelectionPanel, BorderLayout.CENTER);

        // Koltuk paneli - BURADA ÖNEMLİ DEĞİŞİKLİK YAPILDI
        seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(0, 5, 10, 10)); // Dinamik satır sayısı, 5 sütun
        seatPanel.setBorder(BorderFactory.createTitledBorder("Koltuk Seçimi"));

        JScrollPane seatScrollPane = new JScrollPane(seatPanel);
        seatScrollPane.setPreferredSize(new Dimension(500, 300));

        mainPanel.add(seatScrollPane, BorderLayout.SOUTH);

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

        continueButton = new JButton("Devam Et");
        continueButton.setEnabled(false);
        continueButton.setBackground(new Color(0, 100, 0));
        continueButton.setForeground(Color.WHITE);
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                continueToPassengerInfo();
            }
        });
        buttonPanel.add(continueButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // ÖNEMLİ: Vagonları yükle ve sonra ActionListener'ı ekle
        loadWagons();

        // Şimdi ActionListener'ı ekleyebiliriz
        wagonComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateSeatPanel();
            }
        });
    }

    private void loadWagons() {
        WagonDAO wagonDAO = new WagonDAO();
        List<Wagon> wagons = wagonDAO.getWagonsByTrainId(selectedTrain.getId());

        wagonComboBox.removeAllItems();

        if (wagons.isEmpty()) {
            // Eğer veritabanında vagon yoksa, örnek vagonlar oluştur
            Wagon wagon1 = new Wagon(1, selectedTrain.getId(), 1, "Ekonomi", 40);
            Wagon wagon2 = new Wagon(2, selectedTrain.getId(), 2, "Business", 20);

            wagonDAO.addWagon(wagon1);
            wagonDAO.addWagon(wagon2);

            wagons = wagonDAO.getWagonsByTrainId(selectedTrain.getId());
        }

        for (Wagon wagon : wagons) {
            wagonComboBox.addItem(wagon);
        }

        if (!wagons.isEmpty()) {
            selectedWagon = wagons.get(0);
            updateSeatPanel();
        }
    }

    private void updateSeatPanel() {
        seatPanel.removeAll();

        selectedWagon = (Wagon) wagonComboBox.getSelectedItem();
        if (selectedWagon == null) {
            seatPanel.revalidate();
            seatPanel.repaint();
            return;
        }

        // Vagon koltuklarını yükle
        SeatDAO seatDAO = new SeatDAO();
        List<Seat> seats = seatDAO.getSeatsByWagonId(selectedWagon.getId());

        // Koltuklarını oluştur
        for (Seat seat : seats) {
            JToggleButton seatButton = new JToggleButton(String.valueOf(seat.getSeatNumber()));
            seatButton.setPreferredSize(new Dimension(50, 50));

            // Rezerve edilmiş koltukları kontrol et
            if (seat.isReserved()) {
                seatButton.setEnabled(false);
                seatButton.setBackground(Color.RED);
                seatButton.setToolTipText("Rezerve - " + seat.getReservedByGender());
            } else {
                // Boş koltuk
                seatButton.setBackground(Color.GREEN);
                seatButton.setToolTipText("Boş koltuk");

                // Koltuk seçme olayı
                final Seat currentSeat = seat;
                seatButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JToggleButton button = (JToggleButton) e.getSource();

                        // Diğer tüm seçili butonları temizle
                        for (Component c : seatPanel.getComponents()) {
                            if (c instanceof JToggleButton && c != button) {
                                ((JToggleButton) c).setSelected(false);
                            }
                        }

                        // Seçili koltuğu ayarla
                        if (button.isSelected()) {
                            selectedSeat = currentSeat;
                            continueButton.setEnabled(true);
                        } else {
                            selectedSeat = null;
                            continueButton.setEnabled(false);
                        }
                    }
                });
            }

            seatPanel.add(seatButton);
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void goBack() {
        // Tren arama ekranına geri dön
        SearchTrainsPanel searchPanel = new SearchTrainsPanel(currentUser, parentFrame);
        parentFrame.showPanel(searchPanel, "search");
    }

    private void continueToPassengerInfo() {
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen bir koltuk seçin!",
                    "Uyarı",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Yolcu bilgileri ve ödeme ekranına geç
        PassengerInfoPanel passengerPanel = new PassengerInfoPanel(
                currentUser, selectedTrain, selectedWagon, selectedSeat, parentFrame);
        parentFrame.showPanel(passengerPanel, "passengerInfo");
    }
}