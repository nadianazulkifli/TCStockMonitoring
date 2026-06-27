<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.Category" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null || (!"admin".equalsIgnoreCase(role) && !"manager".equalsIgnoreCase(role))) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    Item item = (Item) request.getAttribute("item");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    boolean editMode = (item != null);
%>

<!DOCTYPE html>
<html>
<head>
    <title><%= editMode ? "Edit Item Info" : "Add New Item" %></title>
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
            <h1 class="page-title"><%= editMode ? "Edit Item Info" : "Add New Item" %></h1>
            <p class="page-subtitle">Maintain item master information only</p>
        </div>

        <div class="content-wrap">
            <div class="card">
                <div class="card-body p-4">
                    <form action="<%= request.getContextPath() %>/items" method="post">
                        <% if (editMode) { %>
                            <input type="hidden" name="id" value="<%= item.getId() %>">
                        <% } %>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Item Code</label>
                                <input type="text" name="itemCode" class="form-control"
                                       value="<%= editMode ? item.getItemCode() : "" %>" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Item Name</label>
                                <input type="text" name="itemName" class="form-control"
                                       value="<%= editMode ? item.getItemName() : "" %>" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Category</label>
                                <select name="categoryId" class="form-select" required>
                                    <option value="">-- Select Category --</option>

                                    <%
                                        if (categories != null) {
                                            for (Category category : categories) {
                                                boolean selected = editMode && item.getCategoryId() == category.getId();
                                    %>
                                        <option value="<%= category.getId() %>" <%= selected ? "selected" : "" %>>
                                            <%= category.getCategoryName() %>
                                        </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Base UOM</label>
                                <input type="text" name="baseUom" class="form-control"
                                       value="<%= editMode ? item.getBaseUom() : "" %>" required>
                            </div>

                            <div class="col-md-12 mb-3">
                                <label class="form-label">Pack Size Details</label>
                                <input type="text" name="packSizeDetails" class="form-control"
                                       value="<%= editMode ? item.getPackSizeDetails() : "" %>"
                                       placeholder="Example: 1CTN x 6PACK x 16PCS">
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Units Per CTN</label>
                                <input type="number" step="1" min="0" name="unitsPerCtn" class="form-control"
                                       value="<%= editMode ? item.getUnitsPerCtn() : 0 %>">
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Units Per PCK</label>
                                <input type="number" step="1" min="0" name="unitsPerPck" class="form-control"
                                       value="<%= editMode ? item.getUnitsPerPck() : 0 %>">
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Reorder Level</label>
                                <input type="number" step="1" min="0" name="reorderLevel" class="form-control"
                                       value="<%= editMode ? item.getReorderLevel() : 0 %>" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Status</label>
                                <select name="status" class="form-select" required>
                                    <option value="ACTIVE" <%= editMode && "ACTIVE".equalsIgnoreCase(item.getStatus()) ? "selected" : "" %>>
                                        ACTIVE
                                    </option>
                                    <option value="INACTIVE" <%= editMode && "INACTIVE".equalsIgnoreCase(item.getStatus()) ? "selected" : "" %>>
                                        INACTIVE
                                    </option>
                                </select>
                            </div>
                        </div>

                        <% if (editMode) { %>
                            <div class="card mb-3" style="background:#fff8f3;">
                                <div class="card-body">
                                    <h5 class="mb-2">Current Stock Information</h5>

                                    <p class="mb-2">
                                        <strong>Current Quantity:</strong>
                                        <%= item.getCurrentQuantity() %> <%= item.getBaseUom() %>
                                    </p>

                                    <p class="mb-3 text-muted">
                                        Stock quantity cannot be edited here. Please use Stock In or Stock Out
                                        so every stock movement is recorded in the transaction history.
                                    </p>

                                    <a href="<%= request.getContextPath() %>/stock?action=in&itemId=<%= item.getId() %>" class="btn btn-success me-2">
                                        Stock In
                                    </a>

                                    <a href="<%= request.getContextPath() %>/stock?action=out&itemId=<%= item.getId() %>" class="btn btn-warning text-dark">
                                        Stock Out
                                    </a>
                                </div>
                            </div>
                        <% } %>

                        <button type="submit" class="btn btn-primary">
                            <%= editMode ? "Update Item Info" : "Save Item" %>
                        </button>

                        <a href="<%= request.getContextPath() %>/items" class="btn btn-light">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>