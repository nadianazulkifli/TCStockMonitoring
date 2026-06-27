<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.StockCountSession" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<StockCountSession> sessions = (List<StockCountSession>) request.getAttribute("sessions");
    String success = request.getParameter("success");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Count Sessions</title>
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
            <h1 class="page-title">Stock Count Sessions</h1>
            <p class="page-subtitle">Manage stock count sessions and physical inventory checks</p>
        </div>

        <div class="content-wrap">
            <div class="page-box">
                
    <div class="section-head">
        <div>
            <h3 class="section-title">Session List</h3>
            <p class="section-desc">Create and manage stock count sessions for periodic verification</p>
        </div>
        <a href="<%= request.getContextPath() %>/stock-count?action=new" class="btn btn-primary">+ New Session</a>
    </div>

    <form action="<%= request.getContextPath() %>/stock-count" method="get" class="row g-3">
        <input type="hidden" name="action" value="list">

        <div class="col-md-4">
            <input type="text"
                   name="keyword"
                   class="form-control"
                   placeholder="Search by session name"
                   value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
        </div>

        <div class="col-md-3">
            <select name="countType" class="form-select">
                <option value="">All Count Types</option>
                <option value="WEEKLY" <%= "WEEKLY".equals(request.getAttribute("countType")) ? "selected" : "" %>>WEEKLY</option>
                <option value="MONTHLY" <%= "MONTHLY".equals(request.getAttribute("countType")) ? "selected" : "" %>>MONTHLY</option>
                <option value="MONTH_END" <%= "MONTH_END".equals(request.getAttribute("countType")) ? "selected" : "" %>>MONTH_END</option>
                <option value="OTHER" <%= "OTHER".equals(request.getAttribute("countType")) ? "selected" : "" %>>OTHER</option>
            </select>
        </div>

        <div class="col-md-3">
            <input type="text"
                   name="status"
                   class="form-control"
                   placeholder="Status"
                   value="<%= request.getAttribute("status") != null ? request.getAttribute("status") : "" %>">
        </div>

        <div class="col-md-2 d-flex gap-2">
            <button type="submit" class="btn btn-primary">Search</button>
            <a href="<%= request.getContextPath() %>/stock-count" class="btn btn-light">Reset</a>
        </div>
    </form>
</div>

            <% if (success != null) { %>
                <div class="alert alert-success mt-3"><%= success %></div>
            <% } %>

            <div class="card mt-4">
                <div class="card-body">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Session Name</th>
                                <th>Count Type</th>
                                <th>Count Date</th>
                                <th>Status</th>
                                <th>Created By</th>
                                <th>Notes</th>
                                <th width="150">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (sessions != null && !sessions.isEmpty()) {
                                int no = 1;
                                for (StockCountSession s : sessions) {
                        %>
                            <tr>
                                <td><%= no++ %></td>
                                <td><%= s.getSessionName() %></td>
                                <td><%= s.getCountType() %></td>
                                <td><%= s.getCountDate() %></td>
                                <td>
                                    <span class="badge bg-info text-dark"><%= s.getStatus() %></span>
                                </td>
                                <td><%= s.getCreatedByName() %></td>
                                <td><%= s.getNotes() == null ? "" : s.getNotes() %></td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/stock-count?action=entry&id=<%= s.getId() %>" class="btn btn-sm btn-primary">
                                        Enter Count
                                    </a>
                                </td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="8" class="text-center text-muted">No stock count sessions found.</td>
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