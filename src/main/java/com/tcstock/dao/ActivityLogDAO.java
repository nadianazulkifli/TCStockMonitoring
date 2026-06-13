/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.ActivityLog;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    public void insertLog(int userId, String action, String moduleName, Integer recordId, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action, module_name, record_id, description) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, moduleName);

            if (recordId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, recordId);
            }

            ps.setString(5, description);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ActivityLog> getAllLogs() {
        List<ActivityLog> logs = new ArrayList<>();

        String sql = "SELECT al.id, al.user_id, u.full_name, al.action, al.module_name, " +
                     "al.record_id, al.description, al.logged_at " +
                     "FROM activity_logs al " +
                     "JOIN users u ON al.user_id = u.id " +
                     "ORDER BY al.logged_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setUserId(rs.getInt("user_id"));
                log.setUserName(rs.getString("full_name"));
                log.setAction(rs.getString("action"));
                log.setModuleName(rs.getString("module_name"));
                int recordId = rs.getInt("record_id");
                log.setRecordId(rs.wasNull() ? null : recordId);
                log.setDescription(rs.getString("description"));
                log.setLoggedAt(rs.getString("logged_at"));
                logs.add(log);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }

    public List<ActivityLog> searchLogs(String keyword, String actionFilter, String moduleFilter, String dateFrom, String dateTo) {
        List<ActivityLog> logs = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT al.id, al.user_id, u.full_name, al.action, al.module_name, " +
                "al.record_id, al.description, al.logged_at " +
                "FROM activity_logs al " +
                "JOIN users u ON al.user_id = u.id " +
                "WHERE 1=1 "
        );

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (u.full_name LIKE ? OR al.description LIKE ? OR CAST(al.record_id AS CHAR) LIKE ?) ");
            String searchValue = "%" + keyword + "%";
            params.add(searchValue);
            params.add(searchValue);
            params.add(searchValue);
        }

        if (actionFilter != null && !actionFilter.isEmpty()) {
            sql.append("AND al.action LIKE ? ");
            params.add("%" + actionFilter + "%");
        }

        if (moduleFilter != null && !moduleFilter.isEmpty()) {
            sql.append("AND al.module_name LIKE ? ");
            params.add("%" + moduleFilter + "%");
        }

        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append("AND DATE(al.logged_at) >= ? ");
            params.add(dateFrom);
        }

        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append("AND DATE(al.logged_at) <= ? ");
            params.add(dateTo);
        }

        sql.append("ORDER BY al.logged_at DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivityLog log = new ActivityLog();
                    log.setId(rs.getInt("id"));
                    log.setUserId(rs.getInt("user_id"));
                    log.setUserName(rs.getString("full_name"));
                    log.setAction(rs.getString("action"));
                    log.setModuleName(rs.getString("module_name"));
                    int recordId = rs.getInt("record_id");
                    log.setRecordId(rs.wasNull() ? null : recordId);
                    log.setDescription(rs.getString("description"));
                    log.setLoggedAt(rs.getString("logged_at"));
                    logs.add(log);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return logs;
    }
}