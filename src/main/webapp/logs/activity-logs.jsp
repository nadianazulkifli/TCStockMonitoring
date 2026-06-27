<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.ActivityLog" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null || (!"admin".equalsIgnoreCase(role) && !"manager".equalsIgnoreCase(role))) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<ActivityLog> logs = (List<ActivityLog>) request.getAttribute("logs");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Activity Logs</title>
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
            <h1 class="page-title">Activity Logs</h1>
            <p class="page-subtitle">Track user activities and system actions</p>
        </div>

        <div class="content-wrap">
            <div class="page-box">
    <div class="section-head">
        <div>
            <h3 class="section-title">System Activity History</h3>
            <p class="section-desc">All important user actions are recorded here for audit trail</p>
        </div>
    </div>

    <form action="<%= request.getContextPath() %>/activity-logs" method="get" class="row g-3">
        <div class="col-md-4">
            <input type="text"
                   name="keyword"
                   class="form-control"
                   placeholder="Search user, description, or record ID"
                   value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
        </div>

        <div class="col-md-2">
            <input type="text"
                   name="actionFilter"
                   class="form-control"
                   placeholder="Action"
                   value="<%= request.getAttribute("actionFilter") != null ? request.getAttribute("actionFilter") : "" %>">
        </div>

        <div class="col-md-2">
            <input type="text"
                   name="moduleFilter"
                   class="form-control"
                   placeholder="Module"
                   value="<%= request.getAttribute("moduleFilter") != null ? request.getAttribute("moduleFilter") : "" %>">
        </div>

        <div class="col-md-2">
            <input type="date"
                   name="dateFrom"
                   class="form-control"
                   value="<%= request.getAttribute("dateFrom") != null ? request.getAttribute("dateFrom") : "" %>">
        </div>

        <div class="col-md-2">
            <input type="date"
                   name="dateTo"
                   class="form-control"
                   value="<%= request.getAttribute("dateTo") != null ? request.getAttribute("dateTo") : "" %>">
        </div>

        <div class="col-md-12 d-flex gap-2">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="<%= request.getContextPath() %>/activity-logs" class="btn btn-light">Reset</a>
        </div>
    </form>
</div>
            <div class="card mt-4">
                <div class="card-body">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Date Time</th>
                                <th>User</th>
                                <th>Action</th>
                                <th>Module</th>
                                <th>Record ID</th>
                                <th>Description</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (logs != null && !logs.isEmpty()) {
                                int no = 1;
                                for (ActivityLog log : logs) {
                        %>
                            <tr>
                                <td><%= no++ %></td>
                                <td><%= log.getLoggedAt() %></td>
                                <td><%= log.getUserName() %></td>
                                <td>
                                    <span class="badge bg-primary"><%= log.getAction() %></span>
                                </td>
                                <td><%= log.getModuleName() %></td>
                                <td><%= log.getRecordId() == null ? "" : log.getRecordId() %></td>
                                <td><%= log.getDescription() == null ? "" : log.getDescription() %></td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="7" class="text-center text-muted">No activity logs found.</td>
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