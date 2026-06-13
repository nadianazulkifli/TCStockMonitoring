/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.tcstock.dao;

import com.tcstock.model.Item;
import com.tcstock.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT i.id, i.item_code, i.item_name, i.category_id, c.category_name, " +
                     "i.pack_size_details, i.base_uom, i.units_per_ctn, i.units_per_pck, " +
                     "i.reorder_level, i.current_quantity, i.status " +
                     "FROM items i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "WHERE i.status = 'ACTIVE' " +
                     "ORDER BY i.item_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                items.add(mapItemWithCategory(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public Item getItemById(int id) {
        Item item = null;

        String sql = "SELECT id, item_code, item_name, category_id, pack_size_details, " +
                     "base_uom, units_per_ctn, units_per_pck, reorder_level, current_quantity, status " +
                     "FROM items WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    item = mapItemWithoutCategory(rs);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public boolean insertItem(Item item) {
        String sql = "INSERT INTO items " +
                     "(item_code, item_name, category_id, pack_size_details, base_uom, " +
                     "units_per_ctn, units_per_pck, reorder_level, current_quantity, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getItemCode());
            ps.setString(2, item.getItemName());
            ps.setInt(3, item.getCategoryId());
            ps.setString(4, item.getPackSizeDetails());
            ps.setString(5, item.getBaseUom());
            ps.setDouble(6, item.getUnitsPerCtn());
            ps.setDouble(7, item.getUnitsPerPck());
            ps.setDouble(8, item.getReorderLevel());
            ps.setDouble(9, item.getCurrentQuantity());
            ps.setString(10, item.getStatus() == null ? "ACTIVE" : item.getStatus());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(Item item) {
        String sql = "UPDATE items SET item_code=?, item_name=?, category_id=?, pack_size_details=?, " +
                     "base_uom=?, units_per_ctn=?, units_per_pck=?, reorder_level=?, " +
                     "current_quantity=?, status=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, item.getItemCode());
            ps.setString(2, item.getItemName());
            ps.setInt(3, item.getCategoryId());
            ps.setString(4, item.getPackSizeDetails());
            ps.setString(5, item.getBaseUom());
            ps.setDouble(6, item.getUnitsPerCtn());
            ps.setDouble(7, item.getUnitsPerPck());
            ps.setDouble(8, item.getReorderLevel());
            ps.setDouble(9, item.getCurrentQuantity());
            ps.setString(10, item.getStatus() == null ? "ACTIVE" : item.getStatus());
            ps.setInt(11, item.getId());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Soft delete: item is not removed from database.
    // It only changes status from ACTIVE to INACTIVE.
    public boolean deleteItem(int id) {
        String sql = "UPDATE items SET status = 'INACTIVE' WHERE id = ? AND status = 'ACTIVE'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countAllActiveItems() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'ACTIVE'";

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

    public int countLowStockItems() {
        String sql = "SELECT COUNT(*) FROM items " +
                     "WHERE status = 'ACTIVE' AND current_quantity > 0 AND current_quantity <= reorder_level";

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

    public int countOutOfStockItems() {
        String sql = "SELECT COUNT(*) FROM items " +
                     "WHERE status = 'ACTIVE' AND current_quantity <= 0";

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

    public List<Item> getLowStockItems() {
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
                items.add(mapItemWithCategory(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Item> searchItems(String keyword) {
        List<Item> items = new ArrayList<>();

        String sql = "SELECT i.id, i.item_code, i.item_name, i.category_id, c.category_name, " +
                     "i.pack_size_details, i.base_uom, i.units_per_ctn, i.units_per_pck, " +
                     "i.reorder_level, i.current_quantity, i.status " +
                     "FROM items i " +
                     "JOIN categories c ON i.category_id = c.id " +
                     "WHERE i.status = 'ACTIVE' " +
                     "AND (i.item_code LIKE ? OR i.item_name LIKE ? OR c.category_name LIKE ?) " +
                     "ORDER BY i.item_name ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";
            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItemWithCategory(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public List<Item> searchLowStockItems(String keyword, String alertType) {
        List<Item> items = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT i.id, i.item_code, i.item_name, i.category_id, c.category_name, " +
                "i.pack_size_details, i.base_uom, i.units_per_ctn, i.units_per_pck, " +
                "i.reorder_level, i.current_quantity, i.status " +
                "FROM items i " +
                "JOIN categories c ON i.category_id = c.id " +
                "WHERE i.status = 'ACTIVE' " +
                "AND i.current_quantity <= i.reorder_level "
        );

        List<String> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.item_code LIKE ? OR i.item_name LIKE ? OR c.category_name LIKE ?) ");
            String searchValue = "%" + keyword.trim() + "%";
            params.add(searchValue);
            params.add(searchValue);
            params.add(searchValue);
        }

        if (alertType != null && !alertType.trim().isEmpty()) {
            if ("OUT".equalsIgnoreCase(alertType)) {
                sql.append("AND i.current_quantity <= 0 ");
            } else if ("LOW".equalsIgnoreCase(alertType)) {
                sql.append("AND i.current_quantity > 0 AND i.current_quantity <= i.reorder_level ");
            }
        }

        sql.append("ORDER BY i.current_quantity ASC, i.item_name ASC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItemWithCategory(rs));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    private Item mapItemWithCategory(ResultSet rs) throws Exception {
        Item item = mapItemWithoutCategory(rs);
        item.setCategoryName(rs.getString("category_name"));
        return item;
    }

    private Item mapItemWithoutCategory(ResultSet rs) throws Exception {
        Item item = new Item();
        item.setId(rs.getInt("id"));
        item.setItemCode(rs.getString("item_code"));
        item.setItemName(rs.getString("item_name"));
        item.setCategoryId(rs.getInt("category_id"));
        item.setPackSizeDetails(rs.getString("pack_size_details"));
        item.setBaseUom(rs.getString("base_uom"));
        item.setUnitsPerCtn(rs.getInt("units_per_ctn"));
        item.setUnitsPerPck(rs.getInt("units_per_pck"));
        item.setReorderLevel(rs.getInt("reorder_level"));
        item.setCurrentQuantity(rs.getInt("current_quantity"));
        item.setStatus(rs.getString("status"));
        return item;
    }
}