<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.Role" %>
<%@ page import="com.tcstock.model.User" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String pageRole = (String) session.getAttribute("role");
    if (pageRole == null || !"admin".equalsIgnoreCase(pageRole)) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    User userData = (User) request.getAttribute("userData");
    List<Role> roles = (List<Role>) request.getAttribute("roles");
    String error = (String) request.getAttribute("error");

    boolean editMode = (userData != null && userData.getId() > 0);
%>

<!DOCTYPE html>
<html>
<head>
    <title><%= editMode ? "Edit User" : "Add User" %></title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
<link href="<%= request.getContextPath() %>/assets/css/theme.css" rel="stylesheet">
</head>
<body>

<div class="main-layout">
    <%@ include file="/includes/sidebar.jsp" %>

    <div class="content-area">
        <div class="topbar">
            <h1 class="page-title"><%= editMode ? "Edit User" : "Add User" %></h1>
            <p class="page-subtitle">Create or update authorized system accounts</p>
        </div>

        <div class="content-wrap">
            <div class="card">
                <div class="card-body p-4">
                    <% if (error != null) { %>
                        <div class="alert alert-danger"><%= error %></div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/users" method="post">
                        <% if (editMode) { %>
                            <input type="hidden" name="id" value="<%= userData.getId() %>">
                        <% } %>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Full Name</label>
                                <input type="text" name="fullName" class="form-control"
                                       value="<%= editMode ? userData.getFullName() : "" %>" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control"
                                       value="<%= editMode ? userData.getEmail() : "" %>" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    Password <%= editMode ? "(leave blank if no change)" : "" %>
                                </label>
                                <input type="password" name="password" class="form-control"
                                       <%= editMode ? "" : "required" %>>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Role</label>
                                <select name="roleId" class="form-select" required>
                                    <option value="">-- Select Role --</option>
                                    <%
                                        if (roles != null) {
                                            for (Role roleObj : roles) {
                                                boolean selected = editMode && userData.getRoleId() == roleObj.getId();
                                    %>
                                        <option value="<%= roleObj.getId() %>" <%= selected ? "selected" : "" %>>
                                            <%= roleObj.getRoleName() %>
                                        </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Status</label>
                                <select name="status" class="form-select" required>
                                    <option value="ACTIVE" <%= editMode && "ACTIVE".equalsIgnoreCase(userData.getStatus()) ? "selected" : "" %>>
                                        ACTIVE
                                    </option>
                                    <option value="INACTIVE" <%= editMode && "INACTIVE".equalsIgnoreCase(userData.getStatus()) ? "selected" : "" %>>
                                        INACTIVE
                                    </option>
                                </select>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary">
                            <%= editMode ? "Update User" : "Save User" %>
                        </button>
                        <a href="<%= request.getContextPath() %>/users" class="btn btn-light">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>