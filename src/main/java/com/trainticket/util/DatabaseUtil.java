package com.trainticket.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {
    private static final String DB_URL = "jdbc:sqlite:trainticket.db";
    private static Connection connection = null;

    // Veritabanı bağlantısını başlat
    public static void initializeDatabase() {
        try {
            // SQLite JDBC sürücüsünü yükle
            Class.forName("org.sqlite.JDBC");

            // Bağlantıyı oluştur
            getConnection();
            System.out.println("Veritabanı bağlantısı başarılı.");

            // Gerekli tabloları oluştur
            createTables();

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC sürücüsü bulunamadı: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Veritabanı bağlantısını getir (bağlantı yoksa yeni bağlantı oluştur)
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                // Auto-commit'i açık tut
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    // Veritabanı bağlantısını kapat - uygulamayı kapatırken çağrılmalı
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Veritabanı bağlantısı kapatıldı.");
            } catch (SQLException e) {
                System.err.println("Bağlantı kapatma hatası: " + e.getMessage());
            }
        }
    }

    // Tabloları oluştur
    private static void createTables() {
        try (Statement statement = getConnection().createStatement()) {
            // Kullanıcılar tablosu
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL," +
                            "fullName TEXT NOT NULL," +
                            "email TEXT NOT NULL," +
                            "gender TEXT NOT NULL," +
                            "tcNo TEXT NOT NULL UNIQUE," +
                            "phoneNumber TEXT," +
                            "registrationDate TEXT DEFAULT CURRENT_TIMESTAMP" +
                            ");"
            );

            // Tren seferleri tablosu
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS trains (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "trainNumber TEXT NOT NULL," +
                            "trainName TEXT NOT NULL," +
                            "departureStation TEXT NOT NULL," +
                            "arrivalStation TEXT NOT NULL," +
                            "departureTime TEXT NOT NULL," +
                            "arrivalTime TEXT NOT NULL," +
                            "price REAL NOT NULL" +
                            ");"
            );

            // Vagonlar tablosu
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS wagons (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "trainId INTEGER NOT NULL," +
                            "wagonNumber INTEGER NOT NULL," +
                            "wagonType TEXT NOT NULL," +
                            "totalSeats INTEGER NOT NULL," +
                            "FOREIGN KEY (trainId) REFERENCES trains(id)" +
                            ");"
            );

            // Koltuklar tablosu
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS seats (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "wagonId INTEGER NOT NULL," +
                            "seatNumber INTEGER NOT NULL," +
                            "isReserved INTEGER DEFAULT 0," +
                            "reservedByGender TEXT," +
                            "FOREIGN KEY (wagonId) REFERENCES wagons(id)" +
                            ");"
            );

            // Biletler tablosu
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS tickets (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "userId INTEGER NOT NULL," +
                            "trainId INTEGER NOT NULL," +
                            "wagonId INTEGER NOT NULL," +
                            "seatId INTEGER NOT NULL," +
                            "purchaseDate TEXT DEFAULT CURRENT_TIMESTAMP," +
                            "passengerName TEXT NOT NULL," +
                            "passengerTcNo TEXT NOT NULL," +
                            "passengerGender TEXT NOT NULL," +
                            "price REAL NOT NULL," +
                            "isPaid INTEGER DEFAULT 0," +
                            "FOREIGN KEY (userId) REFERENCES users(id)," +
                            "FOREIGN KEY (trainId) REFERENCES trains(id)," +
                            "FOREIGN KEY (wagonId) REFERENCES wagons(id)," +
                            "FOREIGN KEY (seatId) REFERENCES seats(id)" +
                            ");"
            );

            System.out.println("Veritabanı tabloları başarıyla oluşturuldu.");

            // Örnek veri ekleme
            insertSampleData(statement);

        } catch (SQLException e) {
            System.err.println("Tablo oluşturma hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Örnek verileri ekle
    private static void insertSampleData(Statement statement) throws SQLException {
        // Örnek admin kullanıcısı
        statement.execute(
                "INSERT OR IGNORE INTO users (username, password, fullName, email, gender, tcNo, phoneNumber) " +
                        "VALUES ('admin', 'admin123', 'Admin User', 'admin@trainticket.com', 'Erkek', '11111111111', '5551112233');"
        );

        // Örnek tren seferleri
        statement.execute(
                "INSERT OR IGNORE INTO trains (trainNumber, trainName, departureStation, arrivalStation, departureTime, arrivalTime, price) " +
                        "VALUES ('YHT1', 'Yüksek Hızlı Tren', 'Ankara', 'İstanbul', '2025-04-24 08:00', '2025-04-24 12:30', 120.50);"
        );

        statement.execute(
                "INSERT OR IGNORE INTO trains (trainNumber, trainName, departureStation, arrivalStation, departureTime, arrivalTime, price) " +
                        "VALUES ('YHT2', 'Yüksek Hızlı Tren', 'İstanbul', 'Ankara', '2025-04-25 14:00', '2025-04-25 18:30', 120.50);"
        );

        statement.execute(
                "INSERT OR IGNORE INTO trains (trainNumber, trainName, departureStation, arrivalStation, departureTime, arrivalTime, price) " +
                        "VALUES ('ANK1', 'Anadolu Ekspresi', 'İzmir', 'Konya', '2025-04-26 09:15', '2025-04-26 17:45', 85.75);"
        );

        // Her tren için vagonlar ve koltuklar ekleme işlemi eklenebilir
        System.out.println("Örnek veriler başarıyla eklendi.");
    }
}