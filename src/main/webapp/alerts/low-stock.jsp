<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<Item> lowStockItems = (List<Item>) request.getAttribute("lowStockItems");

    int totalAlerts = 0;
    int outOfStockCount = 0;
    int lowOnlyCount = 0;

    if (lowStockItems != null) {
        totalAlerts = lowStockItems.size();
        for (Item item : lowStockItems) {
            if (item.getCurrentQuantity() <= 0) {
                outOfStockCount++;
            } else {
                lowOnlyCount++;
            }
        }
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Low Stock Alerts</title>
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
            <h1 class="page-title">Low Stock Alerts</h1>
            <p class="page-subtitle">Monitor low stock and out of stock items</p>
        </div>

        <div class="content-wrap">
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="stat-card stat-gold">
                        <div class="stat-label">Low Stock</div>
                        <h3 class="stat-number"><%= lowOnlyCount %></h3>
                        <p class="stat-note">Below reorder level</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="stat-card stat-red">
                        <div class="stat-label">Out of Stock</div>
                        <h3 class="stat-number"><%= outOfStockCount %></h3>
                        <p class="stat-note">Urgent replenishment needed</p>
                    </div>
                </div>

                <div class="col-md-4">
                    <div class="stat-card stat-blue">
                        <div class="stat-label">Total Alerts</div>
                        <h3 class="stat-number"><%= totalAlerts %></h3>
                        <p class="stat-note">Items requiring attention</p>
                    </div>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-body">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Alert Items</h3>
                            <p class="section-desc">Items that need action from stock replenishment or review</p>
                        </div>
                    </div>

                    <form action="<%= request.getContextPath() %>/alerts" method="get" class="row g-3 mb-4">
                        <div class="col-md-6">
                            <input type="text"
                                   name="keyword"
                                   class="form-control"
                                   placeholder="Search by item code, item name, or category"
                                   value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>">
                        </div>

                        <div class="col-md-3">
                            <select name="alertType" class="form-select">
                                <option value="">All Alerts</option>
                                <option value="LOW" <%= "LOW".equals(request.getAttribute("alertType")) ? "selected" : "" %>>LOW</option>
                                <option value="OUT" <%= "OUT".equals(request.getAttribute("alertType")) ? "selected" : "" %>>OUT</option>
                            </select>
                        </div>

                        <div class="col-md-3 d-flex gap-2">
                            <button type="submit" class="btn btn-primary">Search</button>
                            <a href="<%= request.getContextPath() %>/alerts" class="btn btn-light">Reset</a>
                        </div>
                    </form>

                    <table class="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>Category</th>
                                <th>Current Qty</th>
                                <th>Reorder Level</th>
                                <th>UOM</th>
                                <th>Status</th>
                                <th width="180">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (lowStockItems != null && !lowStockItems.isEmpty()) {
                                int no = 1;
                                for (Item item : lowStockItems) {
                                    boolean isOutOfStock = item.getCurrentQuantity() <= 0;
                        %>
                            <tr class="<%= isOutOfStock ? "table-danger" : "table-warning" %>">
                                <td><%= no++ %></td>
                                <td><%= item.getItemCode() %></td>
                                <td><%= item.getItemName() %></td>
                                <td><%= item.getCategoryName() %></td>
                                <td><%= item.getCurrentQuantity() %></td>
                                <td><%= item.getReorderLevel() %></td>
                                <td><%= item.getBaseUom() %></td>
                                <td>
                                    <span class="badge <%= isOutOfStock ? "bg-danger" : "bg-warning text-dark" %>">
                                        <%= isOutOfStock ? "OUT" : "LOW" %>
                                    </span>
                                </td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/stock?action=in&itemId=<%= item.getId() %>" class="btn btn-sm btn-primary">
                                        Stock In
                                    </a>
                                </td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="9" class="text-center text-muted">No low stock alerts found.</td>
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

</body>
</html>