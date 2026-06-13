package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.dao.CategoryDAO;
import com.tcstock.dao.ItemDAO;
import com.tcstock.model.Category;
import com.tcstock.model.Item;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/items")
public class ItemServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");
        return role != null && (
                "admin".equalsIgnoreCase(role) ||
                "manager".equalsIgnoreCase(role)
        );
    }

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");
        return "admin".equalsIgnoreCase(role);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.equals("list")) {
            String keyword = request.getParameter("keyword");
            List<Item> items;

            if (keyword != null && !keyword.trim().isEmpty()) {
                items = itemDAO.searchItems(keyword.trim());
                request.setAttribute("keyword", keyword.trim());
            } else {
                items = itemDAO.getAllItems();
            }

            request.setAttribute("items", items);
            request.getRequestDispatcher("/inventory/items.jsp").forward(request, response);

        } else if (action.equals("new")) {
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/inventory/item-form.jsp").forward(request, response);

        } else if (action.equals("edit")) {
            int id = Integer.parseInt(request.getParameter("id"));
            Item item = itemDAO.getItemById(id);
            List<Category> categories = categoryDAO.getAllCategories();

            request.setAttribute("item", item);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("/inventory/item-form.jsp").forward(request, response);

        } else if (action.equals("delete")) {
            processDelete(request, response);

        } else {
            response.sendRedirect(request.getContextPath() + "/items");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("delete".equalsIgnoreCase(action)) {
            processDelete(request, response);
            return;
        }

        String idParam = request.getParameter("id");
        String itemCode = request.getParameter("itemCode");
        String itemName = request.getParameter("itemName");
        int categoryId = Integer.parseInt(request.getParameter("categoryId"));
        String packSizeDetails = request.getParameter("packSizeDetails");
        String baseUom = request.getParameter("baseUom");

        int unitsPerCtn = parseInt(request.getParameter("unitsPerCtn"));
        int unitsPerPck = parseInt(request.getParameter("unitsPerPck"));
        int reorderLevel = parseInt(request.getParameter("reorderLevel"));

        String status = request.getParameter("status");

        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE";
        }

        Item item = new Item();
        item.setItemCode(itemCode);
        item.setItemName(itemName);
        item.setCategoryId(categoryId);
        item.setPackSizeDetails(packSizeDetails);
        item.setBaseUom(baseUom);
        item.setUnitsPerCtn(unitsPerCtn);
        item.setUnitsPerPck(unitsPerPck);
        item.setReorderLevel(reorderLevel);
        item.setStatus(status);

        HttpSession session = request.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        if (idParam == null || idParam.isEmpty()) {
            item.setCurrentQuantity(0);

            boolean success = itemDAO.insertItem(item);
            if (success) {
                activityLogDAO.insertLog(userId, "CREATE", "ITEM", null,
                        "Created new item: " + item.getItemName());
            }

        } else {
            int itemId = Integer.parseInt(idParam);
            item.setId(itemId);

            Item existingItem = itemDAO.getItemById(itemId);
            if (existingItem != null) {
                item.setCurrentQuantity(existingItem.getCurrentQuantity());
            } else {
                item.setCurrentQuantity(0);
            }

            boolean success = itemDAO.updateItem(item);
            if (success) {
                activityLogDAO.insertLog(userId, "UPDATE", "ITEM", item.getId(),
                        "Updated item info: " + item.getItemName());
            }
        }

        response.sendRedirect(request.getContextPath() + "/items");
    }

    private void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        if (!isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/items");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        boolean deleted = itemDAO.deleteItem(id);

        HttpSession session = request.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        if (deleted) {
            activityLogDAO.insertLog(userId, "SOFT_DELETE", "ITEM", id,
                    "Soft deleted item by changing status to INACTIVE");
        }

        response.sendRedirect(request.getContextPath() + "/items");
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}