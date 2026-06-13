<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null || (!"admin".equalsIgnoreCase(role) && !"manager".equalsIgnoreCase(role))) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<Item> items = (List<Item>) request.getAttribute("items");
    Object keywordObj = request.getAttribute("keyword");
    String keyword = keywordObj != null ? keywordObj.toString() : "";
%>

<!DOCTYPE html>
<html>
<head>
    <title>Item Master</title>
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
            <h1 class="page-title">Item Master</h1>
            <p class="page-subtitle">
                Edit item information here. Use Stock In and Stock Out to change quantity through transaction records.
            </p>
        </div>

        <div class="content-wrap">
            <div class="page-box">
                <div class="section-head">
                    <div>
                        <h3 class="section-title">Item Master List</h3>
                        <p class="section-desc">Create, review, and maintain item master data</p>
                    </div>

                    <a href="<%= request.getContextPath() %>/items?action=new" class="btn btn-primary">
                        + Add Item
                    </a>
                </div>

                <form action="<%= request.getContextPath() %>/items" method="get" class="row g-3 mt-3">
                    <div class="col-md-8">
                        <input type="text"
                               name="keyword"
                               class="form-control"
                               placeholder="Search by item code, name, or category"
                               value="<%= keyword %>">
                    </div>

                    <div class="col-md-4 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">Search</button>
                        <a href="<%= request.getContextPath() %>/items" class="btn btn-light">Reset</a>
                    </div>
                </form>
            </div>

            <div class="card mt-4">
                <div class="card-body">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>Category</th>
                                <th>Base UOM</th>
                                <th>Current Qty</th>
                                <th>Reorder Level</th>
                                <th>Status</th>
                                <th width="340">Actions</th>
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (items != null && !items.isEmpty()) {
                                int no = 1;

                                for (Item item : items) {
                                    boolean outOfStock = item.getCurrentQuantity() <= 0;
                                    boolean lowStock = item.getCurrentQuantity() > 0 &&
                                                       item.getCurrentQuantity() <= item.getReorderLevel();
                        %>

                            <tr class="<%= outOfStock ? "table-danger" : (lowStock ? "table-warning" : "") %>">
                                <td><%= no++ %></td>
                                <td><%= item.getItemCode() %></td>
                                <td><%= item.getItemName() %></td>
                                <td><%= item.getCategoryName() %></td>
                                <td><%= item.getBaseUom() %></td>
                                <td><%= item.getCurrentQuantity() %></td>
                                <td><%= item.getReorderLevel() %></td>

                                <td>
                                    <%
                                        if (outOfStock) {
                                    %>
                                        <span class="badge bg-danger">OUT</span>
                                    <%
                                        } else if (lowStock) {
                                    %>
                                        <span class="badge bg-warning text-dark">LOW</span>
                                    <%
                                        } else {
                                    %>
                                        <span class="badge bg-success">OK</span>
                                    <%
                                        }
                                    %>
                                </td>

                                <td>
                                    <div class="d-flex flex-wrap gap-2">
                                        <a href="<%= request.getContextPath() %>/items?action=edit&id=<%= item.getId() %>"
                                           class="btn btn-sm btn-primary">
                                            Edit Info
                                        </a>

                                        <a href="<%= request.getContextPath() %>/stock?action=in&itemId=<%= item.getId() %>"
                                           class="btn btn-sm btn-success">
                                            Stock In
                                        </a>

                                        <a href="<%= request.getContextPath() %>/stock?action=out&itemId=<%= item.getId() %>"
                                           class="btn btn-sm btn-warning text-dark">
                                            Stock Out
                                        </a>

                                        <% if ("admin".equalsIgnoreCase(role)) { %>
                                            <form action="<%= request.getContextPath() %>/items"
                                                  method="post"
                                                  class="d-inline"
                                                  onsubmit="return confirm('Are you sure you want to delete this item? This item will be marked as INACTIVE and will not be permanently removed.');">

                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="<%= item.getId() %>">

                                                <button type="submit" class="btn btn-sm btn-danger">
                                                    Delete
                                                </button>
                                            </form>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>

                        <%
                                }
                            } else {
                        %>

                            <tr>
                                <td colspan="9" class="text-center text-muted">
                                    No items found.
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

</body>
</html>