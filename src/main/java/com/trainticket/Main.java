package com.trainticket;

import com.trainticket.view.LoginFrame;
import com.trainticket.util.DatabaseUtil;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Veritabanı bağlantısını kur
        DatabaseUtil.initializeDatabase();

        // JVM kapatılırken veritabanı bağlantısını kapat
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DatabaseUtil.closeConnection();
        }));

        // Swing UI thread'inde çalıştır
        SwingUtilities.invokeLater(() -> {
            try {
                // Windows görünümünü uygula
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Login ekranını göster
            new LoginFrame().setVisible(true);
        });
    }
}