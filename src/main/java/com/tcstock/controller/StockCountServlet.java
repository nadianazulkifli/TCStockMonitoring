/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.dao.ItemDAO;
import com.tcstock.dao.StockCountDAO;
import com.tcstock.model.Item;
import com.tcstock.model.StockCountDetail;
import com.tcstock.model.StockCountSession;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/stock-count")
public class StockCountServlet extends HttpServlet {

    private final StockCountDAO stockCountDAO = new StockCountDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();
    private final ItemDAO itemDAO = new ItemDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("role") != null;
    }

    private boolean canApprove(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");

        return "admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || "list".equals(action)) {
            String keyword = request.getParameter("keyword");
            String countType = request.getParameter("countType");
            String status = request.getParameter("status");

            boolean hasFilter =
                    (keyword != null && !keyword.trim().isEmpty()) ||
                    (countType != null && !countType.trim().isEmpty()) ||
                    (status != null && !status.trim().isEmpty());

            List<StockCountSession> sessions;

            if (hasFilter) {
                sessions = stockCountDAO.searchSessions(
                        keyword == null ? "" : keyword.trim(),
                        countType == null ? "" : countType.trim(),
                        status == null ? "" : status.trim()
                );
            } else {
                sessions = stockCountDAO.getAllSessions();
            }

            request.setAttribute("sessions", sessions);
            request.setAttribute("keyword", keyword == null ? "" : keyword);
            request.setAttribute("countType", countType == null ? "" : countType);
            request.setAttribute("status", status == null ? "" : status);

            request.getRequestDispatcher("/count/sessions.jsp").forward(request, response);

        } else if ("new".equals(action)) {
            request.getRequestDispatcher("/count/session-form.jsp").forward(request, response);

        } else if ("entry".equals(action)) {
            int sessionId = Integer.parseInt(request.getParameter("id"));

            StockCountSession countSession = stockCountDAO.getSessionById(sessionId);
            List<StockCountDetail> details = stockCountDAO.getCountEntryItems(sessionId);

            request.setAttribute("countSession", countSession);
            request.setAttribute("details", details);

            request.getRequestDispatcher("/count/count-entry.jsp").forward(request, response);

        } else {
            response.sendRedirect(request.getContextPath() + "/stock-count");
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

        if ("saveSession".equals(action)) {
            StockCountSession countSession = new StockCountSession();

            countSession.setSessionName(request.getParameter("sessionName"));
            countSession.setCountType(request.getParameter("countType"));
            countSession.setCountDate(request.getParameter("countDate"));
            countSession.setCreatedBy(userId);
            countSession.setNotes(request.getParameter("notes"));

            boolean success = stockCountDAO.insertSession(countSession);

            if (success) {
                activityLogDAO.insertLog(
                        userId,
                        "CREATE",
                        "STOCK_COUNT_SESSION",
                        null,
                        "Created stock count session: " + countSession.getSessionName()
                );

                response.sendRedirect(request.getContextPath()
                        + "/stock-count?success=Session created successfully");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/stock-count?action=new&error=Failed to create session");
            }

        } else if ("saveDetail".equals(action)) {
            int sessionId = Integer.parseInt(request.getParameter("sessionId"));
            int itemId = Integer.parseInt(request.getParameter("itemId"));

            int qtyCtn = parseInt(request.getParameter("qtyCtn"));
            int qtyPck = parseInt(request.getParameter("qtyPck"));
            int qtyPcs = parseInt(request.getParameter("qtyPcs"));

            String remarks = request.getParameter("remarks");

            Item item = itemDAO.getItemById(itemId);

            if (item == null) {
                response.sendRedirect(request.getContextPath()
                        + "/stock-count?action=entry&id=" + sessionId + "&error=Item not found");
                return;
            }

            int totalQuantityBase =
                    (qtyCtn * item.getUnitsPerCtn()) +
                    (qtyPck * item.getUnitsPerPck()) +
                    qtyPcs;

            String countedUom = item.getBaseUom();

            boolean success = stockCountDAO.saveCountDetail(
                    sessionId,
                    itemId,
                    qtyCtn,
                    qtyPck,
                    qtyPcs,
                    totalQuantityBase,
                    countedUom,
                    remarks
            );

            if (success) {
                activityLogDAO.insertLog(
                        userId,
                        "UPDATE",
                        "STOCK_COUNT_DETAIL",
                        itemId,
                        "Saved stock count detail"
                );

                response.sendRedirect(request.getContextPath()
                        + "/stock-count?action=entry&id=" + sessionId + "&success=Count saved successfully");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/stock-count?action=entry&id=" + sessionId + "&error=Failed to save count");
            }

        } else if ("approve".equals(action)) {
            if (!canApprove(request)) {
                response.sendRedirect(request.getContextPath()
                        + "/stock-count?error=Only admin or manager can approve stock count");
                return;
            }

            int sessionId = Integer.parseInt(request.getParameter("sessionId"));

            boolean success = stockCountDAO.approveSession(sessionId, userId);

            if (success) {
                activityLogDAO.insertLog(
                        userId,
                        "APPROVE",
                        "STOCK_COUNT_SESSION",
                        sessionId,
                        "Approved stock count session and updated Item Master quantity"
                );

                response.sendRedirect(request.getContextPath()
                        + "/stock-count?success=Stock count approved and Item Master updated");
            } else {
                response.sendRedirect(request.getContextPath()
                        + "/stock-count?action=entry&id=" + sessionId + "&error=Failed to approve stock count");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/stock-count");
        }
    }

    private int parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}