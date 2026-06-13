/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.Item;
import com.tcstock.model.StockCountSummary;
import com.tcstock.model.StockTransaction;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {

    public List<Item> getInventoryReport() {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT i.id, i.item_code, i.item_name, i.category_id, c.category_name, " +
                     "i.pack_size_details, i.base_uom, i.units_per_ctn, i.units_per_pck, " +
                     "i.reorder_level, i.current_quantity, i.status " +
                     "FROM items i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "WHERE i.status = 'ACTIVE' " +
                     "ORDER BY c.category_name ASC, i.item_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItem(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Item> getLowStockReport() {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT i.id, i.item_code, i.item_name, i.category_id, c.category_name, " +
                     "i.pack_size_details, i.base_uom, i.units_per_ctn, i.units_per_pck, " +
                     "i.reorder_level, i.current_quantity, i.status " +
                     "FROM items i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "WHERE i.status = 'ACTIVE' " +
                     "AND i.current_quantity <= i.reorder_level " +
                     "ORDER BY i.current_quantity ASC, i.item_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItem(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<StockTransaction> getTransactionReport(String dateFrom, String dateTo, String type) {
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

        if (dateFrom != null && !dateFrom.trim().isEmpty()) {
            sql.append("AND DATE(st.transaction_datetime) >= ? ");
            params.add(dateFrom.trim());
        }

        if (dateTo != null && !dateTo.trim().isEmpty()) {
            sql.append("AND DATE(st.transaction_datetime) <= ? ");
            params.add(dateTo.trim());
        }

        if (type != null && !type.trim().isEmpty()) {
            sql.append("AND st.transaction_type = ? ");
            params.add(type.trim());
        }

        sql.append("ORDER BY st.transaction_datetime DESC, st.id DESC");

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

    public List<StockCountSummary> getStockCountSummaryReport() {
        List<StockCountSummary> list = new ArrayList<>();

        String sql = "SELECT scs.id, scs.session_name, scs.count_type, scs.count_date, scs.status, " +
                     "COUNT(scd.id) AS total_items_counted, " +
                     "SUM(CASE WHEN scd.variance_qty <> 0 THEN 1 ELSE 0 END) AS variance_items " +
                     "FROM stock_count_sessions scs " +
                     "LEFT JOIN stock_count_details scd ON scs.id = scd.session_id " +
                     "GROUP BY scs.id, scs.session_name, scs.count_type, scs.count_date, scs.status " +
                     "ORDER BY scs.count_date DESC, scs.id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StockCountSummary summary = new StockCountSummary();

                summary.setSessionId(rs.getInt("id"));
                summary.setSessionName(rs.getString("session_name"));
                summary.setCountType(rs.getString("count_type"));
                summary.setCountDate(rs.getString("count_date"));
                summary.setStatus(rs.getString("status"));
                summary.setTotalItemsCounted(rs.getInt("total_items_counted"));
                summary.setVarianceItems(rs.getInt("variance_items"));

                list.add(summary);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private Item mapItem(ResultSet rs) throws Exception {
        Item item = new Item();

        item.setId(rs.getInt("id"));
        item.setItemCode(rs.getString("item_code"));
        item.setItemName(rs.getString("item_name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setCategoryName(rs.getString("category_name"));
        item.setPackSizeDetails(rs.getString("pack_size_details"));
        item.setBaseUom(rs.getString("base_uom"));
        item.setUnitsPerCtn(rs.getInt("units_per_ctn"));
        item.setUnitsPerPck(rs.getInt("units_per_pck"));
        item.setReorderLevel(rs.getInt("reorder_level"));
        item.setCurrentQuantity(rs.getInt("current_quantity"));
        item.setStatus(rs.getString("status"));

        return item;
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
}