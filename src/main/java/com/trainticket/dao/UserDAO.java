package com.trainticket.dao;

import com.trainticket.model.User;
import com.trainticket.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Kullanıcı ekleme
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password, fullName, email, gender, tcNo, phoneNumber) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getGender());
            pstmt.setString(6, user.getTcNo());
            pstmt.setString(7, user.getPhoneNumber());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı ekleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Bağlantıyı kapatmıyoruz, sadece statement'ı kapatıyoruz
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Kullanıcı girişini doğrula
    public User validateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setTcNo(rs.getString("tcNo"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı doğrulama hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null; // Eşleşen kullanıcı bulunamadı
    }

    // TC Kimlik Numarası ile kullanıcı ara
    public User findUserByTcNo(String tcNo) {
        String query = "SELECT * FROM users WHERE tcNo = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, tcNo);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setTcNo(rs.getString("tcNo"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("TC No ile kullanıcı arama hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null; // Eşleşen kullanıcı bulunamadı
    }

    // Kullanıcı ID'si ile kullanıcı ara
    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setTcNo(rs.getString("tcNo"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                return user;
            }

        } catch (SQLException e) {
            System.err.println("ID ile kullanıcı arama hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null; // Eşleşen kullanıcı bulunamadı
    }

    // Kullanıcı bilgilerini güncelle
    public boolean updateUser(User user) {
        String query = "UPDATE users SET username = ?, password = ?, fullName = ?, email = ?, gender = ?, " +
                "tcNo = ?, phoneNumber = ? WHERE id = ?";

        PreparedStatement pstmt = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getGender());
            pstmt.setString(6, user.getTcNo());
            pstmt.setString(7, user.getPhoneNumber());
            pstmt.setInt(8, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Tüm kullanıcıları listele
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        Statement stmt = null;
        ResultSet rs = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("fullName"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));
                user.setTcNo(rs.getString("tcNo"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                users.add(user);
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return users;
    }

    // Kullanıcı sil
    public boolean deleteUser(int id) {
        String query = "DELETE FROM users WHERE id = ?";

        PreparedStatement pstmt = null;

        try {
            Connection conn = DatabaseUtil.getConnection();
            pstmt = conn.prepareStatement(query);

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Kullanıcı silme hatası: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}