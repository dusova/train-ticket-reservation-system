package com.trainticket.controller;

import com.trainticket.dao.UserDAO;
import com.trainticket.model.User;

public class UserController {

    private UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    // Kullanıcı girişi
    public User login(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return null;
        }

        return userDAO.validateUser(username, password);
    }

    // Kullanıcı kaydı
    public boolean register(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty() ||
                user.getFullName() == null || user.getFullName().isEmpty() ||
                user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getGender() == null || user.getGender().isEmpty() ||
                user.getTcNo() == null || user.getTcNo().isEmpty()) {
            return false;
        }

        // TC Kimlik No formatını kontrol et
        if (user.getTcNo().length() != 11 || !user.getTcNo().matches("\\d+")) {
            return false;
        }

        // TC No zaten kayıtlı mı kontrol et
        if (userDAO.findUserByTcNo(user.getTcNo()) != null) {
            return false;
        }

        return userDAO.addUser(user);
    }

    // Profil güncelleme
    public boolean updateProfile(User user) {
        if (user == null || user.getId() <= 0) {
            return false;
        }

        return userDAO.updateUser(user);
    }

    // TC No ile kullanıcı bulma
    public User findUserByTcNo(String tcNo) {
        if (tcNo == null || tcNo.isEmpty()) {
            return null;
        }

        return userDAO.findUserByTcNo(tcNo);
    }

    // ID ile kullanıcı bulma
    public User getUserById(int id) {
        if (id <= 0) {
            return null;
        }

        return userDAO.getUserById(id);
    }
}