/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.dao.UserDAO;
import com.tcstock.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthorized(request)) {
            response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
            return;
        }

        HttpSession session = request.getSession(false);
        int userId = (Integer) session.getAttribute("userId");

        User user = userDAO.getUserById(userId);
        request.setAttribute("profileUser", user);
        request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
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

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        User user = userDAO.getUserById(userId);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/profile?error=User not found");
            return;
        }

        if (currentPassword == null || !currentPassword.equals(user.getPassword())) {
            request.setAttribute("error", "Current password is incorrect.");
            request.setAttribute("profileUser", user);
            request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "New password cannot be empty.");
            request.setAttribute("profileUser", user);
            request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirm password do not match.");
            request.setAttribute("profileUser", user);
            request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
            return;
        }

        boolean success = userDAO.updateOwnPassword(userId, newPassword);

        if (success) {
            activityLogDAO.insertLog(userId, "UPDATE", "PROFILE", userId, "Updated own password");
            request.setAttribute("success", "Password updated successfully.");
        } else {
            request.setAttribute("error", "Failed to update password.");
        }

        User updatedUser = userDAO.getUserById(userId);
        request.setAttribute("profileUser", updatedUser);
        request.getRequestDispatcher("/profile/profile.jsp").forward(request, response);
    }
}