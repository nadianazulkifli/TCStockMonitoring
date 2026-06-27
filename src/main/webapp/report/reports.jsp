<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page import="com.tcstock.model.StockTransaction" %>
<%@ page import="com.tcstock.model.StockCountSummary" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null || (!"admin".equalsIgnoreCase(role) && !"manager".equalsIgnoreCase(role))) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<Item> inventoryReport = (List<Item>) request.getAttribute("inventoryReport");
    List<Item> lowStockReport = (List<Item>) request.getAttribute("lowStockReport");
    List<StockTransaction> transactionReport = (List<StockTransaction>) request.getAttribute("transactionReport");
    List<StockCountSummary> stockCountSummary = (List<StockCountSummary>) request.getAttribute("stockCountSummary");

    Integer totalItems = (Integer) request.getAttribute("totalItems");
    Integer lowStockCount = (Integer) request.getAttribute("lowStockCount");
    Integer outOfStockCount = (Integer) request.getAttribute("outOfStockCount");
    Integer todayTransactions = (Integer) request.getAttribute("todayTransactions");

    String dateFrom = (String) request.getAttribute("dateFrom");
    String dateTo = (String) request.getAttribute("dateTo");
    String type = (String) request.getAttribute("type");

    if (dateFrom == null) {
        dateFrom = "";
    }

    if (dateTo == null) {
        dateTo = "";
    }

    if (type == null) {
        type = "";
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Reports</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

     <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap/css/bootstrap.min.css">
    <link href="<%= request.getContextPath() %>/assets/css/theme.css" rel="stylesheet">

    <style>
        @media print {
            .no-print {
                display: none !important;
            }

            body {
                background: white !important;
            }

            .card {
                box-shadow: none !important;
                border: 1px solid #ddd !important;
            }
        }
    </style>
</head>

<body>

<div class="main-layout">
    <%@ include file="/includes/sidebar.jsp" %>

    <div class="content-area">
        <div class="topbar no-print">
            <h1 class="page-title">Reports</h1>
            <p class="page-subtitle">
                View inventory, low stock, stock movement, and stock count reports
            </p>
        </div>

        <div class="content-wrap">

            <div class="no-print mb-3">
                <button onclick="window.print()" class="btn btn-dark">
                    Print Report
                </button>
            </div>

            <div class="row g-4">
                <div class="col-md-3">
                    <div class="stat-card stat-red">
                        <div class="stat-label">Total Items</div>
                        <h3 class="stat-number"><%= totalItems == null ? 0 : totalItems %></h3>
                        <p class="stat-note">Active inventory records</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-gold">
                        <div class="stat-label">Low Stock</div>
                        <h3 class="stat-number"><%= lowStockCount == null ? 0 : lowStockCount %></h3>
                        <p class="stat-note">Below reorder level</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-red">
                        <div class="stat-label">Out of Stock</div>
                        <h3 class="stat-number"><%= outOfStockCount == null ? 0 : outOfStockCount %></h3>
                        <p class="stat-note">Need immediate restock</p>
                    </div>
                </div>

                <div class="col-md-3">
                    <div class="stat-card stat-green">
                        <div class="stat-label">Today Transactions</div>
                        <h3 class="stat-number"><%= todayTransactions == null ? 0 : todayTransactions %></h3>
                        <p class="stat-note">Stock in / out activity</p>
                    </div>
                </div>
            </div>

            <div class="card mt-4 no-print">
                <div class="card-body p-4">
                    <h3 class="section-title mb-3">Stock Movement Filter</h3>

                    <form action="<%= request.getContextPath() %>/reports" method="get">
                        <div class="row">
                            <div class="col-md-3 mb-3">
                                <label class="form-label">Date From</label>
                                <input type="date"
                                       name="dateFrom"
                                       class="form-control"
                                       value="<%= dateFrom %>">
                            </div>

                            <div class="col-md-3 mb-3">
                                <label class="form-label">Date To</label>
                                <input type="date"
                                       name="dateTo"
                                       class="form-control"
                                       value="<%= dateTo %>">
                            </div>

                            <div class="col-md-3 mb-3">
                                <label class="form-label">Transaction Type</label>
                                <select name="type" class="form-select">
                                    <option value="">ALL</option>
                                    <option value="IN" <%= "IN".equalsIgnoreCase(type) ? "selected" : "" %>>
                                        IN
                                    </option>
                                    <option value="OUT" <%= "OUT".equalsIgnoreCase(type) ? "selected" : "" %>>
                                        OUT
                                    </option>
                                    <option value="ADJUSTMENT" <%= "ADJUSTMENT".equalsIgnoreCase(type) ? "selected" : "" %>>
                                        ADJUSTMENT
                                    </option>
                                    <option value="COUNT_UPDATE" <%= "COUNT_UPDATE".equalsIgnoreCase(type) ? "selected" : "" %>>
                                        COUNT UPDATE
                                    </option>
                                </select>
                            </div>

                            <div class="col-md-3 mb-3 d-flex align-items-end">
                                <button type="submit" class="btn btn-primary me-2">
                                    Filter
                                </button>

                                <a href="<%= request.getContextPath() %>/reports" class="btn btn-light">
                                    Reset
                                </a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>

            <% if (!dateFrom.isEmpty() || !dateTo.isEmpty() || !type.isEmpty()) { %>
                <div class="alert alert-info mt-4">
                    <strong>Current Filter:</strong>
                    Date From: <%= dateFrom.isEmpty() ? "All" : dateFrom %>,
                    Date To: <%= dateTo.isEmpty() ? "All" : dateTo %>,
                    Type: <%= type.isEmpty() ? "ALL" : type %>
                </div>
            <% } %>

            <div class="card mt-4">
                <div class="card-body table-responsive">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Inventory Report</h3>
                            <p class="section-desc">Active items and current stock status</p>
                        </div>
                    </div>

                    <table class="table table-hover align-middle">
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
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (inventoryReport != null && !inventoryReport.isEmpty()) {
                                int no = 1;

                                for (Item item : inventoryReport) {
                        %>

                            <tr>
                                <td><%= no++ %></td>
                                <td><%= item.getItemCode() %></td>
                                <td><%= item.getItemName() %></td>
                                <td><%= item.getCategoryName() %></td>
                                <td><%= item.getCurrentQuantity() %></td>
                                <td><%= item.getReorderLevel() %></td>
                                <td><%= item.getBaseUom() %></td>
                                <td>
                                    <span class="badge bg-success">
                                        <%= item.getStatus() %>
                                    </span>
                                </td>
                            </tr>

                        <%
                                }
                            } else {
                        %>

                            <tr>
                                <td colspan="8" class="text-center text-muted">
                                    No inventory data found.
                                </td>
                            </tr>

                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-body table-responsive">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Low Stock Report</h3>
                            <p class="section-desc">Items below reorder level or out of stock</p>
                        </div>
                    </div>

                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>Category</th>
                                <th>Current Qty</th>
                                <th>Reorder Level</th>
                                <th>UOM</th>
                                <th>Alert</th>
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (lowStockReport != null && !lowStockReport.isEmpty()) {
                                int no = 1;

                                for (Item item : lowStockReport) {
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
                            </tr>

                        <%
                                }
                            } else {
                        %>

                            <tr>
                                <td colspan="8" class="text-center text-muted">
                                    No low stock data found.
                                </td>
                            </tr>

                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-body table-responsive">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Stock Movement Report</h3>
                            <p class="section-desc">
                                Stock In, Stock Out, and count update transactions based on selected date
                            </p>
                        </div>
                    </div>

                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Transaction Date</th>
                                <th>Type</th>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>CTN</th>
                                <th>PCK</th>
                                <th>PCS</th>
                                <th>Qty Base</th>
                                <th>UOM</th>
                                <th>Reference No</th>
                                <th>Remarks</th>
                                <th>By User</th>
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (transactionReport != null && !transactionReport.isEmpty()) {
                                int no = 1;

                                for (StockTransaction st : transactionReport) {
                                    String badgeClass = "bg-secondary";

                                    if ("IN".equalsIgnoreCase(st.getTransactionType())) {
                                        badgeClass = "bg-success";
                                    } else if ("OUT".equalsIgnoreCase(st.getTransactionType())) {
                                        badgeClass = "bg-danger";
                                    } else if ("ADJUSTMENT".equalsIgnoreCase(st.getTransactionType())) {
                                        badgeClass = "bg-warning text-dark";
                                    } else if ("COUNT_UPDATE".equalsIgnoreCase(st.getTransactionType())) {
                                        badgeClass = "bg-info text-dark";
                                    }
                        %>

                            <tr>
                                <td><%= no++ %></td>

                                <td><%= st.getTransactionDatetime() %></td>

                                <td>
                                    <span class="badge <%= badgeClass %>">
                                        <%= st.getTransactionType() %>
                                    </span>
                                </td>

                                <td><%= st.getItemCode() %></td>
                                <td><%= st.getItemName() %></td>
                                <td><%= st.getQtyCtn() %></td>
                                <td><%= st.getQtyPck() %></td>
                                <td><%= st.getQtyPcs() %></td>
                                <td><%= st.getQuantityBase() %></td>
                                <td><%= st.getUom() %></td>
                                <td><%= st.getReferenceNo() == null ? "" : st.getReferenceNo() %></td>
                                <td><%= st.getRemarks() == null ? "" : st.getRemarks() %></td>
                                <td><%= st.getUserName() %></td>
                            </tr>

                        <%
                                }
                            } else {
                        %>

                            <tr>
                                <td colspan="13" class="text-center text-muted">
                                    No transaction data found.
                                </td>
                            </tr>

                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
            </div>

            <div class="card mt-4">
                <div class="card-body table-responsive">
                    <div class="section-head">
                        <div>
                            <h3 class="section-title">Stock Count Summary Report</h3>
                            <p class="section-desc">Summary of stock count sessions and variance</p>
                        </div>
                    </div>

                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Session Name</th>
                                <th>Count Type</th>
                                <th>Count Date</th>
                                <th>Status</th>
                                <th>Total Items Counted</th>
                                <th>Variance Items</th>
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (stockCountSummary != null && !stockCountSummary.isEmpty()) {
                                int no = 1;

                                for (StockCountSummary s : stockCountSummary) {
                        %>

                            <tr>
                                <td><%= no++ %></td>
                                <td><%= s.getSessionName() %></td>
                                <td><%= s.getCountType() %></td>
                                <td><%= s.getCountDate() %></td>
                                <td>
                                    <span class="badge <%= "APPROVED".equalsIgnoreCase(s.getStatus()) ? "bg-success" : "bg-warning text-dark" %>">
                                        <%= s.getStatus() %>
                                    </span>
                                </td>
                                <td><%= s.getTotalItemsCounted() %></td>
                                <td>
                                    <span class="badge <%= s.getVarianceItems() > 0 ? "bg-warning text-dark" : "bg-success" %>">
                                        <%= s.getVarianceItems() %>
                                    </span>
                                </td>
                            </tr>

                        <%
                                }
                            } else {
                        %>

                            <tr>
                                <td colspan="7" class="text-center text-muted">
                                    No stock count summary found.
                                </td>
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