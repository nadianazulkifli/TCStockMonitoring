/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;



import com.tcstock.util.DBConnection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.tcstock.dao.ActivityLogDAO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        String sql = "SELECT u.id, u.full_name, u.email, r.role_name " +
                     "FROM users u " +
                     "JOIN roles r ON u.role_id = r.id " +
                     "WHERE u.email = ? AND u.password = ? AND u.status = 'ACTIVE'";
        
        
        
        

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = request.getSession();
                     int userId = rs.getInt("id");
                    session.setAttribute("userId", rs.getInt("id"));
                    session.setAttribute("fullName", rs.getString("full_name"));
                    session.setAttribute("email", rs.getString("email"));
                    session.setAttribute("role", rs.getString("role_name"));
                    
                    activityLogDAO.insertLog(userId, "LOGIN", "AUTH", null, "User logged into the system");
                   

                    String role = rs.getString("role_name");

                    if ("admin".equalsIgnoreCase(role)) {
                        response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                    } else if ("manager".equalsIgnoreCase(role)) {
                        response.sendRedirect(request.getContextPath() + "/manager/dashboard.jsp");
                    } else if ("worker".equalsIgnoreCase(role)) {
                        response.sendRedirect(request.getContextPath() + "/worker/dashboard.jsp");
                    } else {
                        session.invalidate();
                        request.setAttribute("error", "Role is not valid.");
                        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
                    }

                } else {
                    request.setAttribute("error", "Invalid email or password.");
                    request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
                }
            }

        } catch (Exception e) {
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
        }
    }
}