/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.StockCountDetail;
import com.tcstock.model.StockCountSession;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockCountDAO {

    public List<StockCountSession> getAllSessions() {
        List<StockCountSession> sessions = new ArrayList<>();

        String sql = "SELECT scs.id, scs.session_name, scs.count_type, scs.count_date, " +
                     "scs.status, scs.created_by, u.full_name, scs.notes, scs.created_at " +
                     "FROM stock_count_sessions scs " +
                     "JOIN users u ON scs.created_by = u.id " +
                     "ORDER BY scs.count_date DESC, scs.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StockCountSession session = mapSession(rs);
                sessions.add(session);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    public boolean insertSession(StockCountSession session) {
        String sql = "INSERT INTO stock_count_sessions " +
                     "(session_name, count_type, count_date, status, created_by, notes) " +
                     "VALUES (?, ?, ?, 'DRAFT', ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, session.getSessionName());
            ps.setString(2, session.getCountType());
            ps.setString(3, session.getCountDate());
            ps.setInt(4, session.getCreatedBy());
            ps.setString(5, session.getNotes());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public StockCountSession getSessionById(int id) {
        StockCountSession session = null;

        String sql = "SELECT scs.id, scs.session_name, scs.count_type, scs.count_date, " +
                     "scs.status, scs.created_by, u.full_name, scs.notes, scs.created_at " +
                     "FROM stock_count_sessions scs " +
                     "JOIN users u ON scs.created_by = u.id " +
                     "WHERE scs.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    session = mapSession(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return session;
    }

    public List<StockCountDetail> getCountEntryItems(int sessionId) {
        List<StockCountDetail> list = new ArrayList<>();

        String sql = "SELECT i.id AS item_id, i.item_code, i.item_name, i.base_uom, " +
                     "i.current_quantity, " +
                     "COALESCE(i.units_per_ctn, 0) AS units_per_ctn, " +
                     "COALESCE(i.units_per_pck, 0) AS units_per_pck, " +
                     "COALESCE(scd.id, 0) AS detail_id, " +
                     "COALESCE(scd.qty_ctn, 0) AS qty_ctn, " +
                     "COALESCE(scd.qty_pck, 0) AS qty_pck, " +
                     "COALESCE(scd.qty_pcs, 0) AS qty_pcs, " +
                     "COALESCE(scd.total_quantity_base, 0) AS total_quantity_base, " +
                     "COALESCE(scd.variance_qty, 0) AS variance_qty, " +
                     "COALESCE(scd.remarks, '') AS remarks " +
                     "FROM items i " +
                     "LEFT JOIN stock_count_details scd " +
                     "ON scd.item_id = i.id AND scd.session_id = ? " +
                     "WHERE i.status = 'ACTIVE' " +
                     "ORDER BY i.item_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sessionId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockCountDetail detail = new StockCountDetail();

                    detail.setId(rs.getInt("detail_id"));
                    detail.setSessionId(sessionId);
                    detail.setItemId(rs.getInt("item_id"));
                    detail.setItemCode(rs.getString("item_code"));
                    detail.setItemName(rs.getString("item_name"));
                    detail.setBaseUom(rs.getString("base_uom"));

                    detail.setCurrentQuantity(rs.getInt("current_quantity"));
                    detail.setUnitsPerCtn(rs.getInt("units_per_ctn"));
                    detail.setUnitsPerPck(rs.getInt("units_per_pck"));

                    detail.setQtyCtn(rs.getInt("qty_ctn"));
                    detail.setQtyPck(rs.getInt("qty_pck"));
                    detail.setQtyPcs(rs.getInt("qty_pcs"));
                    detail.setTotalQuantityBase(rs.getInt("total_quantity_base"));
                    detail.setVarianceQty(rs.getInt("variance_qty"));
                    detail.setRemarks(rs.getString("remarks"));

                    list.add(detail);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean saveCountDetail(int sessionId, int itemId, int qtyCtn, int qtyPck,
                                   int qtyPcs, int totalQuantityBase, String countedUom,
                                   String remarks) {

        String getCurrentQtySql = "SELECT current_quantity FROM items WHERE id = ?";

        String upsertSql = "INSERT INTO stock_count_details " +
                           "(session_id, item_id, qty_ctn, qty_pck, qty_pcs, " +
                           "total_quantity_base, counted_uom, variance_qty, remarks) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE " +
                           "qty_ctn = VALUES(qty_ctn), " +
                           "qty_pck = VALUES(qty_pck), " +
                           "qty_pcs = VALUES(qty_pcs), " +
                           "total_quantity_base = VALUES(total_quantity_base), " +
                           "counted_uom = VALUES(counted_uom), " +
                           "variance_qty = VALUES(variance_qty), " +
                           "remarks = VALUES(remarks)";

        try (Connection conn = DBConnection.getConnection()) {
            int currentQty = 0;

            try (PreparedStatement psCurrent = conn.prepareStatement(getCurrentQtySql)) {
                psCurrent.setInt(1, itemId);

                try (ResultSet rs = psCurrent.executeQuery()) {
                    if (rs.next()) {
                        currentQty = rs.getInt("current_quantity");
                    } else {
                        return false;
                    }
                }
            }

            int variance = totalQuantityBase - currentQty;

            try (PreparedStatement ps = conn.prepareStatement(upsertSql)) {
                ps.setInt(1, sessionId);
                ps.setInt(2, itemId);
                ps.setInt(3, qtyCtn);
                ps.setInt(4, qtyPck);
                ps.setInt(5, qtyPcs);
                ps.setInt(6, totalQuantityBase);
                ps.setString(7, countedUom);
                ps.setInt(8, variance);
                ps.setString(9, remarks);

                return ps.executeUpdate() > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean approveSession(int sessionId, int approvedBy) {
        String getDetailsSql = "SELECT item_id, total_quantity_base, variance_qty, counted_uom " +
                               "FROM stock_count_details " +
                               "WHERE session_id = ?";

        String updateItemSql = "UPDATE items " +
                               "SET current_quantity = ? " +
                               "WHERE id = ?";

        String insertTransactionSql = "INSERT INTO stock_transactions " +
                "(item_id, user_id, transaction_type, qty_ctn, qty_pck, qty_pcs, " +
                "quantity_base, uom, reference_no, remarks) " +
                "VALUES (?, ?, 'COUNT_UPDATE', 0, 0, 0, ?, ?, ?, ?)";

        String updateSessionSql = "UPDATE stock_count_sessions " +
                                  "SET status = 'APPROVED', approved_by = ?, updated_at = CURRENT_TIMESTAMP " +
                                  "WHERE id = ? AND status <> 'APPROVED'";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psDetails = conn.prepareStatement(getDetailsSql);
                 PreparedStatement psUpdateItem = conn.prepareStatement(updateItemSql);
                 PreparedStatement psTransaction = conn.prepareStatement(insertTransactionSql);
                 PreparedStatement psUpdateSession = conn.prepareStatement(updateSessionSql)) {

                psDetails.setInt(1, sessionId);

                try (ResultSet rs = psDetails.executeQuery()) {
                    while (rs.next()) {
                        int itemId = rs.getInt("item_id");
                        int countedQty = rs.getInt("total_quantity_base");
                        int varianceQty = rs.getInt("variance_qty");
                        String uom = rs.getString("counted_uom");

                        psUpdateItem.setInt(1, countedQty);
                        psUpdateItem.setInt(2, itemId);
                        psUpdateItem.executeUpdate();

                        if (varianceQty != 0) {
                            psTransaction.setInt(1, itemId);
                            psTransaction.setInt(2, approvedBy);
                            psTransaction.setInt(3, varianceQty);
                            psTransaction.setString(4, uom);
                            psTransaction.setString(5, "COUNT-" + sessionId);
                            psTransaction.setString(6, "Stock count approved. Item Master quantity updated.");
                            psTransaction.executeUpdate();
                        }
                    }
                }

                psUpdateSession.setInt(1, approvedBy);
                psUpdateSession.setInt(2, sessionId);
                psUpdateSession.executeUpdate();

                conn.commit();
                return true;

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<StockCountSession> searchSessions(String keyword, String countType, String status) {
        List<StockCountSession> sessions = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT scs.id, scs.session_name, scs.count_type, scs.count_date, " +
                "scs.status, scs.created_by, u.full_name, scs.notes, scs.created_at " +
                "FROM stock_count_sessions scs " +
                "JOIN users u ON scs.created_by = u.id " +
                "WHERE 1=1 "
        );

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND scs.session_name LIKE ? ");
            params.add("%" + keyword + "%");
        }

        if (countType != null && !countType.isEmpty()) {
            sql.append("AND scs.count_type = ? ");
            params.add(countType);
        }

        if (status != null && !status.isEmpty()) {
            sql.append("AND scs.status LIKE ? ");
            params.add("%" + status + "%");
        }

        sql.append("ORDER BY scs.count_date DESC, scs.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockCountSession session = mapSession(rs);
                    sessions.add(session);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sessions;
    }

    private StockCountSession mapSession(ResultSet rs) throws Exception {
        StockCountSession session = new StockCountSession();

        session.setId(rs.getInt("id"));
        session.setSessionName(rs.getString("session_name"));
        session.setCountType(rs.getString("count_type"));
        session.setCountDate(rs.getString("count_date"));
        session.setStatus(rs.getString("status"));
        session.setCreatedBy(rs.getInt("created_by"));
        session.setCreatedByName(rs.getString("full_name"));
        session.setNotes(rs.getString("notes"));
        session.setCreatedAt(rs.getString("created_at"));

        return session;
    }
}