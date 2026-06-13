/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.tcstock.controller;

import com.tcstock.dao.ActivityLogDAO;
import com.tcstock.dao.RoleDAO;
import com.tcstock.dao.UserDAO;
import com.tcstock.model.Role;
import com.tcstock.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final ActivityLogDAO activityLogDAO = new ActivityLogDAO();

    private boolean isAuthorized(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String role = (String) session.getAttribute("role");
        return role != null && "admin".equalsIgnoreCase(role);
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
            String roleFilter = request.getParameter("role");
            String statusFilter = request.getParameter("status");

            List<User> users;
            boolean hasFilter =
                    (keyword != null && !keyword.trim().isEmpty()) ||
                    (roleFilter != null && !roleFilter.trim().isEmpty()) ||
                    (statusFilter != null && !statusFilter.trim().isEmpty());

            if (hasFilter) {
                users = userDAO.searchUsers(
                        keyword == null ? "" : keyword.trim(),
                        roleFilter == null ? "" : roleFilter.trim(),
                        statusFilter == null ? "" : statusFilter.trim()
                );
            } else {
                users = userDAO.getAllUsers();
            }

            request.setAttribute("users", users);
            request.setAttribute("keyword", keyword == null ? "" : keyword);
            request.setAttribute("roleFilter", roleFilter == null ? "" : roleFilter);
            request.setAttribute("statusFilter", statusFilter == null ? "" : statusFilter);

            request.getRequestDispatcher("/users/users.jsp").forward(request, response);

        } else if ("new".equals(action)) {
            List<Role> roles = roleDAO.getAllRoles();
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/users/user-form.jsp").forward(request, response);

        } else if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            User userData = userDAO.getUserById(id);
            List<Role> roles = roleDAO.getAllRoles();

            request.setAttribute("userData", userData);
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("/users/user-form.jsp").forward(request, response);
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
        int adminUserId = (Integer) session.getAttribute("userId");

        String idParam = request.getParameter("id");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        int roleId = Integer.parseInt(request.getParameter("roleId"));
        String status = request.getParameter("status");

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoleId(roleId);
        user.setStatus(status);

        if (idParam == null || idParam.isEmpty()) {
            if (userDAO.emailExists(email)) {
                request.setAttribute("error", "Email already exists.");
                request.setAttribute("roles", roleDAO.getAllRoles());
                request.setAttribute("userData", user);
                request.getRequestDispatcher("/users/user-form.jsp").forward(request, response);
                return;
            }

            boolean success = userDAO.insertUser(user);
            if (success) {
                activityLogDAO.insertLog(adminUserId, "CREATE", "USER", null,
                        "Created new user: " + fullName);
            }

        } else {
            int userId = Integer.parseInt(idParam);
            user.setId(userId);

            if (userDAO.emailExistsForOtherUser(email, userId)) {
                request.setAttribute("error", "Email already exists.");
                request.setAttribute("roles", roleDAO.getAllRoles());
                request.setAttribute("userData", user);
                request.getRequestDispatcher("/users/user-form.jsp").forward(request, response);
                return;
            }

            boolean success;
            if (password != null && !password.trim().isEmpty()) {
                success = userDAO.updateUserWithPassword(user);
            } else {
                success = userDAO.updateUser(user);
            }

            if (success) {
                activityLogDAO.insertLog(adminUserId, "UPDATE", "USER", userId,
                        "Updated user: " + fullName);
            }
        }

        response.sendRedirect(request.getContextPath() + "/users");
    }
}