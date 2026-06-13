/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.model.ActivityLog;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/activity-logs")
public class ActivityLogServlet extends HttpServlet {

    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");
        return role != null &&
                ("admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String keyword = request.getParameter("keyword");
        String actionFilter = request.getParameter("actionFilter");
        String moduleFilter = request.getParameter("moduleFilter");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");

        boolean hasFilter =
                (keyword != null && !keyword.trim().isEmpty()) ||
                (actionFilter != null && !actionFilter.trim().isEmpty()) ||
                (moduleFilter != null && !moduleFilter.trim().isEmpty()) ||
                (dateFrom != null && !dateFrom.trim().isEmpty()) ||
                (dateTo != null && !dateTo.trim().isEmpty());

        List<ActivityLog> logs;
        if (hasFilter) {
            logs = activityLogDAO.searchLogs(
                    keyword == null ? "" : keyword.trim(),
                    actionFilter == null ? "" : actionFilter.trim(),
                    moduleFilter == null ? "" : moduleFilter.trim(),
                    dateFrom == null ? "" : dateFrom.trim(),
                    dateTo == null ? "" : dateTo.trim()
            );
        } else {
            logs = activityLogDAO.getAllLogs();
        }

        request.setAttribute("logs", logs);
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.setAttribute("actionFilter", actionFilter == null ? "" : actionFilter);
        request.setAttribute("moduleFilter", moduleFilter == null ? "" : moduleFilter);
        request.setAttribute("dateFrom", dateFrom == null ? "" : dateFrom);
        request.setAttribute("dateTo", dateTo == null ? "" : dateTo);

        request.getRequestDispatcher("/logs/activity-logs.jsp").forward(request, response);
    }
}