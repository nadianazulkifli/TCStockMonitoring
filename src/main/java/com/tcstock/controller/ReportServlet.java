/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ItemDAO;
import com.tcstock.dao.ReportDAO;
import com.tcstock.dao.StockTransactionDAO;
import com.tcstock.model.Item;
import com.tcstock.model.StockCountSummary;
import com.tcstock.model.StockTransaction;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final StockTransactionDAO stockTransactionDAO = new StockTransactionDAO();

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
        String type = request.getParameter("type");

        List<Item> inventoryReport = reportDAO.getInventoryReport();
        List<Item> lowStockReport = reportDAO.getLowStockReport();
        List<StockTransaction> transactionReport = reportDAO.getTransactionReport(dateFrom, dateTo, type);
        List<StockCountSummary> stockCountSummary = reportDAO.getStockCountSummaryReport();

        request.setAttribute("inventoryReport", inventoryReport);
        request.setAttribute("lowStockReport", lowStockReport);
        request.setAttribute("transactionReport", transactionReport);
        request.setAttribute("stockCountSummary", stockCountSummary);

        request.setAttribute("totalItems", itemDAO.countAllActiveItems());
        request.setAttribute("lowStockCount", itemDAO.countLowStockItems());
        request.setAttribute("outOfStockCount", itemDAO.countOutOfStockItems());
        request.setAttribute("todayTransactions", stockTransactionDAO.countTransactionsToday());

        request.setAttribute("dateFrom", dateFrom);
        request.setAttribute("dateTo", dateTo);
        request.setAttribute("type", type);

        request.getRequestDispatcher("/report/reports.jsp").forward(request, response);
    }
}