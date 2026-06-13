/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.StockTransaction;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class StockTransactionDAO {

    public List<StockTransaction> getAllTransactions() {
        List<StockTransaction> list = new ArrayList<>();

        String sql = "SELECT st.id, st.item_id, i.item_code, i.item_name, st.user_id, u.full_name, " +
                     "st.transaction_type, st.qty_ctn, st.qty_pck, st.qty_pcs, st.quantity_base, " +
                     "st.uom, st.reference_no, st.remarks, st.transaction_datetime " +
                     "FROM stock_transactions st " +
                     "JOIN items i ON st.item_id = i.id " +
                     "JOIN users u ON st.user_id = u.id " +
                     "ORDER BY st.transaction_datetime DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapTransaction(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<StockTransaction> searchTransactions(String keyword, String type, String dateFrom, String dateTo) {
        List<StockTransaction> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT st.id, st.item_id, i.item_code, i.item_name, st.user_id, u.full_name, " +
                "st.transaction_type, st.qty_ctn, st.qty_pck, st.qty_pcs, st.quantity_base, " +
                "st.uom, st.reference_no, st.remarks, st.transaction_datetime " +
                "FROM stock_transactions st " +
                "JOIN items i ON st.item_id = i.id " +
                "JOIN users u ON st.user_id = u.id " +
                "WHERE 1=1 "
        );

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (i.item_code LIKE ? OR i.item_name LIKE ? OR st.reference_no LIKE ? OR u.full_name LIKE ?) ");
            String searchValue = "%" + keyword + "%";
            params.add(searchValue);
            params.add(searchValue);
            params.add(searchValue);
            params.add(searchValue);
        }

        if (type != null && !type.isEmpty()) {
            sql.append("AND st.transaction_type = ? ");
            params.add(type);
        }

        if (dateFrom != null && !dateFrom.isEmpty()) {
            sql.append("AND DATE(st.transaction_datetime) >= ? ");
            params.add(dateFrom);
        }

        if (dateTo != null && !dateTo.isEmpty()) {
            sql.append("AND DATE(st.transaction_datetime) <= ? ");
            params.add(dateTo);
        }

        sql.append("ORDER BY st.transaction_datetime DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTransaction(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insertStockIn(int itemId, int userId, int qtyCtn, int qtyPck, int qtyPcs,
                                 int quantityBase, String uom, String referenceNo,
                                 String remarks, String transactionDate) {

        String transactionDateTime = formatTransactionDateTime(transactionDate);

        String insertTransaction = "INSERT INTO stock_transactions " +
                "(item_id, user_id, transaction_type, qty_ctn, qty_pck, qty_pcs, quantity_base, " +
                "uom, reference_no, remarks, transaction_datetime) " +
                "VALUES (?, ?, 'IN', ?, ?, ?, ?, ?, ?, ?, ?)";

        String updateItem = "UPDATE items SET current_quantity = current_quantity + ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(insertTransaction);
                 PreparedStatement ps2 = conn.prepareStatement(updateItem)) {

                ps1.setInt(1, itemId);
                ps1.setInt(2, userId);
                ps1.setInt(3, qtyCtn);
                ps1.setInt(4, qtyPck);
                ps1.setInt(5, qtyPcs);
                ps1.setInt(6, quantityBase);
                ps1.setString(7, uom);
                ps1.setString(8, referenceNo);
                ps1.setString(9, remarks);
                ps1.setString(10, transactionDateTime);
                ps1.executeUpdate();

                ps2.setInt(1, quantityBase);
                ps2.setInt(2, itemId);
                ps2.executeUpdate();

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

    public boolean insertStockOut(int itemId, int userId, int qtyCtn, int qtyPck, int qtyPcs,
                                  int quantityBase, String uom, String referenceNo,
                                  String remarks, String transactionDate) {

        String transactionDateTime = formatTransactionDateTime(transactionDate);

        String checkQty = "SELECT current_quantity FROM items WHERE id = ?";

        String insertTransaction = "INSERT INTO stock_transactions " +
                "(item_id, user_id, transaction_type, qty_ctn, qty_pck, qty_pcs, quantity_base, " +
                "uom, reference_no, remarks, transaction_datetime) " +
                "VALUES (?, ?, 'OUT', ?, ?, ?, ?, ?, ?, ?, ?)";

        String updateItem = "UPDATE items SET current_quantity = current_quantity - ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCheck = conn.prepareStatement(checkQty);
                 PreparedStatement ps1 = conn.prepareStatement(insertTransaction);
                 PreparedStatement ps2 = conn.prepareStatement(updateItem)) {

                psCheck.setInt(1, itemId);

                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        int currentQty = rs.getInt("current_quantity");

                        if (currentQty < quantityBase) {
                            conn.rollback();
                            return false;
                        }
                    } else {
                        conn.rollback();
                        return false;
                    }
                }

                ps1.setInt(1, itemId);
                ps1.setInt(2, userId);
                ps1.setInt(3, qtyCtn);
                ps1.setInt(4, qtyPck);
                ps1.setInt(5, qtyPcs);
                ps1.setInt(6, quantityBase);
                ps1.setString(7, uom);
                ps1.setString(8, referenceNo);
                ps1.setString(9, remarks);
                ps1.setString(10, transactionDateTime);
                ps1.executeUpdate();

                ps2.setInt(1, quantityBase);
                ps2.setInt(2, itemId);
                ps2.executeUpdate();

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

    public int countTransactionsToday() {
        String sql = "SELECT COUNT(*) FROM stock_transactions WHERE DATE(transaction_datetime) = CURDATE()";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private StockTransaction mapTransaction(ResultSet rs) throws Exception {
        StockTransaction st = new StockTransaction();

        st.setId(rs.getInt("id"));
        st.setItemId(rs.getInt("item_id"));
        st.setItemCode(rs.getString("item_code"));
        st.setItemName(rs.getString("item_name"));
        st.setUserId(rs.getInt("user_id"));
        st.setUserName(rs.getString("full_name"));
        st.setTransactionType(rs.getString("transaction_type"));

        st.setQtyCtn(rs.getInt("qty_ctn"));
        st.setQtyPck(rs.getInt("qty_pck"));
        st.setQtyPcs(rs.getInt("qty_pcs"));
        st.setQuantityBase(rs.getInt("quantity_base"));

        st.setUom(rs.getString("uom"));
        st.setReferenceNo(rs.getString("reference_no"));
        st.setRemarks(rs.getString("remarks"));
        st.setTransactionDatetime(rs.getString("transaction_datetime"));

        return st;
    }

    private String formatTransactionDateTime(String transactionDate) {
        if (transactionDate == null || transactionDate.trim().isEmpty()) {
            return null;
        }

        return transactionDate.trim() + " 00:00:00";
    }
}