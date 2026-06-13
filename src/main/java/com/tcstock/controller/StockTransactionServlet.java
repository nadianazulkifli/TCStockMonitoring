/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.dao.ItemDAO;
import com.tcstock.dao.StockTransactionDAO;
import com.tcstock.model.Item;
import com.tcstock.model.StockTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/stock")
public class StockTransactionServlet extends HttpServlet {

    private final StockTransactionDAO stockTransactionDAO = new StockTransactionDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("role") != null;
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
            String type = request.getParameter("type");
            String dateFrom = request.getParameter("dateFrom");
            String dateTo = request.getParameter("dateTo");

            List<StockTransaction> transactions;

            boolean hasFilter =
                    (keyword != null && !keyword.trim().isEmpty()) ||
                    (type != null && !type.trim().isEmpty()) ||
                    (dateFrom != null && !dateFrom.trim().isEmpty()) ||
                    (dateTo != null && !dateTo.trim().isEmpty());

            if (hasFilter) {
                transactions = stockTransactionDAO.searchTransactions(
                        keyword == null ? "" : keyword.trim(),
                        type == null ? "" : type.trim(),
                        dateFrom == null ? "" : dateFrom.trim(),
                        dateTo == null ? "" : dateTo.trim()
                );
            } else {
                transactions = stockTransactionDAO.getAllTransactions();
            }

            request.setAttribute("transactions", transactions);
            request.setAttribute("keyword", keyword == null ? "" : keyword);
            request.setAttribute("type", type == null ? "" : type);
            request.setAttribute("dateFrom", dateFrom == null ? "" : dateFrom);
            request.setAttribute("dateTo", dateTo == null ? "" : dateTo);

            request.getRequestDispatcher("/stock/transactions.jsp").forward(request, response);

        } else if ("in".equals(action)) {
            List<Item> items = itemDAO.getAllItems();
            String selectedItemId = request.getParameter("itemId");

            request.setAttribute("items", items);
            request.setAttribute("selectedItemId", selectedItemId);

            request.getRequestDispatcher("/stock/stock-in.jsp").forward(request, response);

        } else if ("out".equals(action)) {
            List<Item> items = itemDAO.getAllItems();
            String selectedItemId = request.getParameter("itemId");

            request.setAttribute("items", items);
            request.setAttribute("selectedItemId", selectedItemId);

            request.getRequestDispatcher("/stock/stock-out.jsp").forward(request, response);

        } else {
            response.sendRedirect(request.getContextPath() + "/stock");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        HttpSession session = request.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        String action = request.getParameter("action");
        int itemId = Integer.parseInt(request.getParameter("itemId"));

        int qtyCtn = parseInt(request.getParameter("qtyCtn"));
        int qtyPck = parseInt(request.getParameter("qtyPck"));
        int qtyPcs = parseInt(request.getParameter("qtyPcs"));

        String transactionDate = request.getParameter("transactionDate");
        String referenceNo = request.getParameter("referenceNo");
        String remarks = request.getParameter("remarks");

        Item item = itemDAO.getItemById(itemId);

        if (item == null) {
            response.sendRedirect(request.getContextPath() + "/stock?error=Item not found");
            return;
        }

        int unitsPerCtn = item.getUnitsPerCtn();
        int unitsPerPck = item.getUnitsPerPck();

        int quantityBase =
                (qtyCtn * unitsPerCtn) +
                (qtyPck * unitsPerPck) +
                qtyPcs;

        String uom = item.getBaseUom();

        boolean success;

        if ("saveIn".equals(action)) {
            success = stockTransactionDAO.insertStockIn(
                    itemId,
                    userId,
                    qtyCtn,
                    qtyPck,
                    qtyPcs,
                    quantityBase,
                    uom,
                    referenceNo,
                    remarks,
                    transactionDate
            );

            if (success) {
                activityLogDAO.insertLog(
                        userId,
                        "CREATE",
                        "STOCK_IN",
                        itemId,
                        "Recorded stock in transaction dated " + transactionDate
                );

                response.sendRedirect(request.getContextPath()
                        + "/stock?success=Stock in saved successfully");

            } else {
                response.sendRedirect(request.getContextPath()
                        + "/stock?action=in&error=Failed to save stock in");
            }

        } else if ("saveOut".equals(action)) {
            success = stockTransactionDAO.insertStockOut(
                    itemId,
                    userId,
                    qtyCtn,
                    qtyPck,
                    qtyPcs,
                    quantityBase,
                    uom,
                    referenceNo,
                    remarks,
                    transactionDate
            );

            if (success) {
                activityLogDAO.insertLog(
                        userId,
                        "CREATE",
                        "STOCK_OUT",
                        itemId,
                        "Recorded stock out transaction dated " + transactionDate
                );

                response.sendRedirect(request.getContextPath()
                        + "/stock?success=Stock out saved successfully");

            } else {
                response.sendRedirect(request.getContextPath()
                        + "/stock?action=out&error=Insufficient stock or failed to save stock out");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/stock");
        }
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}