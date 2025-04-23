package com.trainticket.view;

import com.trainticket.dao.TicketDAO;
import com.trainticket.model.Ticket;
import com.trainticket.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class PaymentPanel extends JPanel {

    private User currentUser;
    private Ticket ticket;
    private MainFrame parentFrame;

    private JTextField cardNumberField;
    private JTextField cardHolderField;
    private JComboBox<String> expiryMonthComboBox;
    private JComboBox<String> expiryYearComboBox;
    private JTextField cvvField;
    private JButton payButton;
    private JButton cancelButton;

    // Ödeme işleminin tamamlanıp tamamlanmadığını izlemek için flag
    private boolean paymentCompleted = false;

    public PaymentPanel(User user, Ticket ticket, MainFrame parent) {
        this.currentUser = user;
        this.ticket = ticket;
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {
        // Panel ayarları
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık
        JLabel titleLabel = new JLabel("Ödeme Bilgileri");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Ana panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Bilet özeti
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Bilet Özeti"));

        JTextArea summaryArea = new JTextArea(10, 30);
        summaryArea.setEditable(false);
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Bilet özetini oluştur
        String summary = ticket.getSummary();
        summaryArea.setText(summary);

        JScrollPane summaryScrollPane = new JScrollPane(summaryArea);
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Ödeme formu
        JPanel paymentFormPanel = new JPanel();
        paymentFormPanel.setLayout(new GridBagLayout());
        paymentFormPanel.setBorder(BorderFactory.createTitledBorder("Kredi Kartı Bilgileri"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Kart Numarası
        gbc.gridx = 0;
        gbc.gridy = 0;
        paymentFormPanel.add(new JLabel("Kart Numarası:"), gbc);

        cardNumberField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        paymentFormPanel.add(cardNumberField, gbc);

        // Kart Sahibi
        gbc.gridx = 0;
        gbc.gridy = 1;
        paymentFormPanel.add(new JLabel("Kart Sahibi:"), gbc);

        cardHolderField = new JTextField(20);
        cardHolderField.setText(currentUser.getFullName());
        gbc.gridx = 1;
        gbc.gridy = 1;
        paymentFormPanel.add(cardHolderField, gbc);

        // Son Kullanma Tarihi
        gbc.gridx = 0;
        gbc.gridy = 2;
        paymentFormPanel.add(new JLabel("Son Kullanma Tarihi:"), gbc);

        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        expiryMonthComboBox = new JComboBox<>(months);
        expiryPanel.add(expiryMonthComboBox);

        expiryPanel.add(new JLabel("/"));

        String[] years = new String[10];
        int currentYear = java.time.LocalDate.now().getYear() % 100; // Son 2 hane
        for (int i = 0; i < 10; i++) {
            years[i] = String.format("%02d", currentYear + i);
        }
        expiryYearComboBox = new JComboBox<>(years);
        expiryPanel.add(expiryYearComboBox);

        gbc.gridx = 1;
        gbc.gridy = 2;
        paymentFormPanel.add(expiryPanel, gbc);

        // CVV
        gbc.gridx = 0;
        gbc.gridy = 3;
        paymentFormPanel.add(new JLabel("CVV:"), gbc);

        cvvField = new JTextField(3);
        gbc.gridx = 1;
        gbc.gridy = 3;
        paymentFormPanel.add(cvvField, gbc);

        mainPanel.add(paymentFormPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        cancelButton = new JButton("İptal");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelPayment();
            }
        });
        buttonPanel.add(cancelButton);

        payButton = new JButton("Ödemeyi Tamamla");
        payButton.setBackground(new Color(0, 100, 0));
        payButton.setForeground(Color.WHITE);
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processPayment();
            }
        });
        buttonPanel.add(payButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void cancelPayment() {
        // Ödeme zaten tamamlandıysa işlemi engelle
        if (paymentCompleted) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Ödemeyi iptal etmek istiyor musunuz? Bilet satın alınmayacaktır.",
                "Ödeme İptali",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Ana sayfaya dön
            parentFrame.showHome();
        }
    }

    private void processPayment() {
        // Ödeme zaten tamamlandıysa işlemi engelle
        if (paymentCompleted) {
            return;
        }

        // Form validasyonu
        String cardNumber = cardNumberField.getText().trim();
        String cardHolder = cardHolderField.getText().trim();
        String cvv = cvvField.getText().trim();

        if (cardNumber.isEmpty() || cardHolder.isEmpty() || cvv.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen tüm kart bilgilerini doldurun!",
                    "Eksik Bilgi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!cardNumber.matches("\\d{16}")) {
            JOptionPane.showMessageDialog(this,
                    "Kart numarası 16 haneli ve sayısal olmalıdır!",
                    "Hatalı Kart Numarası",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!cvv.matches("\\d{3}")) {
            JOptionPane.showMessageDialog(this,
                    "CVV 3 haneli ve sayısal olmalıdır!",
                    "Hatalı CVV",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Butonu devre dışı bırak
        payButton.setEnabled(false);
        payButton.setText("İşleniyor...");
        cancelButton.setEnabled(false);

        // Payment timer
        final Timer timer = new Timer(2000, null);
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                completePayment();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void completePayment() {
        // Ödeme bayrağını ayarla
        paymentCompleted = true;

        // Bileti veritabanına kaydet
        TicketDAO ticketDAO = new TicketDAO();

        // Ödendi olarak işaretle
        ticket.setPaid(true);

        boolean success = ticketDAO.addTicket(ticket);

        if (success) {
            // İşlem başarılı olursa, bilet tamamlandı ekranını göster
            JOptionPane.showMessageDialog(this,
                    "Ödeme başarıyla tamamlandı! Biletiniz oluşturuldu.",
                    "Ödeme Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);

            // Bilet tamamlandı ekranını göster
            TicketCompletedPanel completedPanel = new TicketCompletedPanel(currentUser, ticket, parentFrame);
            parentFrame.showPanel(completedPanel, "completed");
        } else {
            // İşlem başarısız olursa, hata mesajı göster ve ana sayfaya dön
            JOptionPane.showMessageDialog(this,
                    "Ödeme işlemi başarısız oldu. Lütfen daha sonra tekrar deneyin.",
                    "Ödeme Hatası",
                    JOptionPane.ERROR_MESSAGE);

            // Ana sayfaya dön
            parentFrame.showHome();
        }
    }
}