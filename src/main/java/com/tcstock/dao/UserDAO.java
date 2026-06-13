/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.User;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public List<User> searchUsers(String keyword, String roleFilter, String statusFilter) {
    List<User> users = new ArrayList<>();

    StringBuilder sql = new StringBuilder(
            "SELECT u.id, u.full_name, u.email, u.role_id, r.role_name, u.status, u.created_at " +
            "FROM users u " +
            "JOIN roles r ON u.role_id = r.id " +
            "WHERE 1=1 "
    );

    List<String> params = new ArrayList<>();

    if (keyword != null && !keyword.isEmpty()) {
        sql.append("AND (u.full_name LIKE ? OR u.email LIKE ?) ");
        String searchValue = "%" + keyword + "%";
        params.add(searchValue);
        params.add(searchValue);
    }

    if (roleFilter != null && !roleFilter.isEmpty()) {
        sql.append("AND LOWER(r.role_name) = ? ");
        params.add(roleFilter.toLowerCase());
    }

    if (statusFilter != null && !statusFilter.isEmpty()) {
        sql.append("AND u.status = ? ");
        params.add(statusFilter);
    }

    sql.append("ORDER BY u.full_name ASC");

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql.toString())) {

        for (int i = 0; i < params.size(); i++) {
            ps.setString(i + 1, params.get(i));
        }

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                users.add(user);
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return users;
}

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        String sql = "SELECT u.id, u.full_name, u.email, u.password, u.role_id, r.role_name, u.status, u.created_at " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "ORDER BY u.id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User getUserById(int id) {
        User user = null;

        String sql = "SELECT u.id, u.full_name, u.email, u.password, u.role_id, r.role_name, u.status, u.created_at " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    user.setStatus(rs.getString("status"));
                    user.setCreatedAt(rs.getString("created_at"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (full_name, email, password, role_id, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRoleId());
            ps.setString(5, user.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, role_id = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getRoleId());
            ps.setString(4, user.getStatus());
            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserWithPassword(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, password = ?, role_id = ?, status = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setInt(4, user.getRoleId());
            ps.setString(5, user.getStatus());
            ps.setInt(6, user.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean emailExistsForOtherUser(String email, int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id <> ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public User getUserByEmail(String email) {
    User user = null;

    String sql = "SELECT u.id, u.full_name, u.email, u.password, u.role_id, r.role_name, u.status, u.created_at " +
                 "FROM users u " +
                 "JOIN roles r ON u.role_id = r.id " +
                 "WHERE u.email = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getString("created_at"));
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return user;
}

public boolean updateOwnPassword(int userId, String newPassword) {
    String sql = "UPDATE users SET password = ? WHERE id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, newPassword);
        ps.setInt(2, userId);

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
}