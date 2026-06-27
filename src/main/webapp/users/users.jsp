<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.User" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String pageRole = (String) session.getAttribute("role");
    if (pageRole == null || !"admin".equalsIgnoreCase(pageRole)) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<User> users = (List<User>) request.getAttribute("users");
%>

<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
   <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap/css/bootstrap.min.css">
<link href="<%= request.getContextPath() %>/assets/css/theme.css" rel="stylesheet">
</head>
<body>

<div class="main-layout">
    <%@ include file="/includes/sidebar.jsp" %>

    <div class="content-area">
        <div class="topbar">
            <h1 class="page-title">User Management</h1>
            <p class="page-subtitle">Manage admin, manager, and worker accounts</p>
        </div>

        <div class="content-wrap">
            <div class="page-box">
    <div class="section-head">
        <div>
            <h3 class="section-title">System Users</h3>
            <p class="section-desc">Create and maintain authorized staff accounts</p>
        </div>
        <a href="<%= request.getContextPath() %>/users?action=new" class="btn btn-primary">+ Add User</a>
    </div>

    <form action="<%= request.getContextPath() %>/users" method="get" class="row g-3">
        <div class="col-md-5">
            <input type="text"
                   name="keyword"
                   class="form-control"
                   placeholder="Search by full name or email"
                   value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
        </div>

        <div class="col-md-3">
            <select name="role" class="form-select">
                <option value="">All Roles</option>
                <option value="admin" <%= "admin".equals(request.getAttribute("roleFilter")) ? "selected" : "" %>>Admin</option>
                <option value="manager" <%= "manager".equals(request.getAttribute("roleFilter")) ? "selected" : "" %>>Manager</option>
                <option value="worker" <%= "worker".equals(request.getAttribute("roleFilter")) ? "selected" : "" %>>Worker</option>
            </select>
        </div>

        <div class="col-md-2">
            <select name="status" class="form-select">
                <option value="">All Status</option>
                <option value="ACTIVE" <%= "ACTIVE".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>ACTIVE</option>
                <option value="INACTIVE" <%= "INACTIVE".equals(request.getAttribute("statusFilter")) ? "selected" : "" %>>INACTIVE</option>
            </select>
        </div>

        <div class="col-md-2 d-flex gap-2">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="<%= request.getContextPath() %>/users" class="btn btn-light">Reset</a>
        </div>
    </form>
</div>

            <div class="card mt-4">
                <div class="card-body">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Created At</th>
                                <th width="120">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (users != null && !users.isEmpty()) {
                                int no = 1;
                                for (User user : users) {
                        %>
                            <tr>
                                <td><%= no++ %></td>
                                <td><%= user.getFullName() %></td>
                                <td><%= user.getEmail() %></td>
                                <td><%= user.getRoleName() %></td>
                                <td>
                                    <span class="badge <%= "ACTIVE".equalsIgnoreCase(user.getStatus()) ? "bg-success" : "bg-secondary" %>">
                                        <%= user.getStatus() %>
                                    </span>
                                </td>
                                <td><%= user.getCreatedAt() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/users?action=edit&id=<%= user.getId() %>" class="btn btn-sm btn-primary">
                                        Edit
                                    </a>
                                </td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="7" class="text-center text-muted">No users found.</td>
                            </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>