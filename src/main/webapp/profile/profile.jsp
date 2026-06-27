<%@ page import="com.tcstock.model.User" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String pageRole = (String) session.getAttribute("role");
    if (pageRole == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    User profileUser = (User) request.getAttribute("profileUser");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
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
            <h1 class="page-title">My Profile</h1>
            <p class="page-subtitle">View your account details and update your password</p>
        </div>

        <div class="content-wrap">
            <div class="row g-4">
                <div class="col-md-5">
                    <div class="card">
                        <div class="card-body p-4">
                            <h3 class="section-title mb-3">Profile Information</h3>

                            <% if (profileUser != null) { %>
                                <div class="mb-3">
                                    <label class="form-label">Full Name</label>
                                    <div><%= profileUser.getFullName() %></div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Email</label>
                                    <div><%= profileUser.getEmail() %></div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Role</label>
                                    <div><%= profileUser.getRoleName() %></div>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Status</label>
                                    <div>
                                        <span class="badge <%= "ACTIVE".equalsIgnoreCase(profileUser.getStatus()) ? "bg-success" : "bg-secondary" %>">
                                            <%= profileUser.getStatus() %>
                                        </span>
                                    </div>
                                </div>

                                <div class="mb-0">
                                    <label class="form-label">Created At</label>
                                    <div><%= profileUser.getCreatedAt() %></div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <div class="col-md-7">
                    <div class="card">
                        <div class="card-body p-4">
                            <h3 class="section-title mb-3">Change Password</h3>

                            <% if (error != null) { %>
                                <div class="alert alert-danger"><%= error %></div>
                            <% } %>

                            <% if (success != null) { %>
                                <div class="alert alert-success"><%= success %></div>
                            <% } %>

                            <form action="<%= request.getContextPath() %>/profile" method="post">
                                <div class="mb-3">
                                    <label class="form-label">Current Password</label>
                                    <input type="password" name="currentPassword" class="form-control" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">New Password</label>
                                    <input type="password" name="newPassword" class="form-control" required>
                                </div>

                                <div class="mb-3">
                                    <label class="form-label">Confirm New Password</label>
                                    <input type="password" name="confirmPassword" class="form-control" required>
                                </div>

                                <button type="submit" class="btn btn-primary">Update Password</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>