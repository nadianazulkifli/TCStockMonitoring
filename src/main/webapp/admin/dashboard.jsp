<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.dao.ItemDAO" %>
<%@ page import="com.tcstock.dao.StockTransactionDAO" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null || !"admin".equalsIgnoreCase(role)) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    ItemDAO itemDAO = new ItemDAO();
    StockTransactionDAO stockDAO = new StockTransactionDAO();

    int totalItems = itemDAO.countAllActiveItems();
    int lowStockCount = itemDAO.countLowStockItems();
    int outOfStockCount = itemDAO.countOutOfStockItems();
    int todayTransactions = stockDAO.countTransactionsToday();

    List<Item> lowStockItems = itemDAO.getLowStockItems();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
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
            <h1 class="page-title">Dashboard</h1>
            <p class="page-subtitle">Welcome back, <%= session.getAttribute("fullName") %> 👋</p>
        </div>

        <div class="content-wrap">
            <div class="row g-4">
                <div class="col-md-3">
                    <div class="stat-card stat-red">
                        <div class="stat-label">Total Items</div>
                        <h3 class="stat-number"><%= totalItems %></h3>
                        <p class="stat-note">Active items in system</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-gold">
                        <div class="stat-label">Low Stock Items</div>
                        <h3 class="stat-number"><%= lowStockCount %></h3>
                        <p class="stat-note">Need reorder</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-red">
                        <div class="stat-label">Out of Stock</div>
                        <h3 class="stat-number"><%= outOfStockCount %></h3>
                        <p class="stat-note">Currently unavailable</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-green">
                        <div class="stat-label">Today Transactions</div>
                        <h3 class="stat-number"><%= todayTransactions %></h3>
                        <p class="stat-note">Stock in / out</p>
                    </div>
                </div>
            </div>

            <div class="page-box mt-4">
                <div class="section-head">
                    <div>
                        <h3 class="section-title">Quick Actions</h3>
                        
                    </div>

                    <div class="quick-actions">
                        <a href="<%= request.getContextPath() %>/users" class="btn btn-dark">Users</a>
                        <a href="<%= request.getContextPath() %>/items" class="btn btn-primary">Item Master</a>
                        <a href="<%= request.getContextPath() %>/stock" class="btn btn-dark">Transactions</a>
                        <a href="<%= request.getContextPath() %>/reports" class="btn btn-primary">Reports</a>
                    </div>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-body">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Low Stock Preview</h3>
                            <p class="section-desc">Latest items that need attention</p>
                        </div>

                        <a href="<%= request.getContextPath() %>/alerts" class="btn btn-primary btn-sm">View All Alerts</a>
                    </div>

                    <table class="table">
                        <thead>
                            <tr>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>Current Qty</th>
                                <th>Reorder Level</th>
                                <th>UOM</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                        <%
                            if (lowStockItems != null && !lowStockItems.isEmpty()) {
                                int count = 0;
                                for (Item item : lowStockItems) {
                                    if (count == 5) break;
                                    boolean isOutOfStock = item.getCurrentQuantity() <= 0;
                                    count++;
                        %>
                            <tr>
                                <td><%= item.getItemCode() %></td>
                                <td><%= item.getItemName() %></td>
                                <td><%= item.getCurrentQuantity() %></td>
                                <td><%= item.getReorderLevel() %></td>
                                <td><%= item.getBaseUom() %></td>
                                <td>
                                    <span class="badge <%= isOutOfStock ? "bg-danger" : "bg-warning text-dark" %>">
                                        <%= isOutOfStock ? "OUT" : "LOW" %>
                                    </span>
                                </td>
                            </tr>
                        <%
                                }
                            } else {
                        %>
                            <tr>
                                <td colspan="6" class="text-center text-muted">No low stock items found.</td>
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