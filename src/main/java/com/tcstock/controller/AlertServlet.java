/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ItemDAO;
import com.tcstock.model.Item;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/alerts")
public class AlertServlet extends HttpServlet {

    private final ItemDAO itemDAO = new ItemDAO();

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

        String keyword = request.getParameter("keyword");
        String alertType = request.getParameter("alertType");

        List<Item> lowStockItems;
        boolean hasFilter =
                (keyword != null && !keyword.trim().isEmpty()) ||
                (alertType != null && !alertType.trim().isEmpty());

        if (hasFilter) {
            lowStockItems = itemDAO.searchLowStockItems(
                    keyword == null ? "" : keyword.trim(),
                    alertType == null ? "" : alertType.trim()
            );
        } else {
            lowStockItems = itemDAO.getLowStockItems();
        }

        request.setAttribute("lowStockItems", lowStockItems);
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("alertType", alertType == null ? "" : alertType);

        request.getRequestDispatcher("/alerts/low-stock.jsp").forward(request, response);
    }
}