<%@ page import="java.util.List" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="com.tcstock.model.Item" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<Item> items = (List<Item>) request.getAttribute("items");
    String selectedItemId = (String) request.getAttribute("selectedItemId");
    String error = request.getParameter("error");

    String selectedItemName = "";
    if (items != null && selectedItemId != null) {
        for (Item i : items) {
            if (selectedItemId.equals(String.valueOf(i.getId()))) {
                selectedItemName = i.getItemCode() + " - " + i.getItemName();
                break;
            }
        }
    }

    String today = LocalDate.now().toString();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Out</title>
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
            <h1 class="page-title">Stock Out</h1>
            <p class="page-subtitle">Reduce stock through a recorded transaction</p>
        </div>

        <div class="content-wrap">
            <% if (error != null) { %>
                <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <div class="card">
                <div class="card-body p-4">
                    <h3 class="section-title mb-3">Add Stock Out</h3>

                    <% if (selectedItemId != null && !selectedItemId.isEmpty()) { %>
                        <div class="alert alert-info">
                            <strong>Selected Item:</strong> <%= selectedItemName %>
                        </div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/stock" method="post">
                        <input type="hidden" name="action" value="saveOut">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Transaction Date</label>
                                <input type="date"
                                       name="transactionDate"
                                       class="form-control"
                                       value="<%= today %>"
                                       required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Reference No</label>
                                <input type="text"
                                       name="referenceNo"
                                       class="form-control"
                                       placeholder="Optional reference number">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Item</label>

                                <select name="itemId" id="itemId" class="form-select" required onchange="updateItemData()">
                                    <option value="">-- Select Item --</option>

                                    <%
                                        if (items != null) {
                                            for (Item item : items) {
                                    %>
                                        <option value="<%= item.getId() %>"
                                                data-uom="<%= item.getBaseUom() %>"
                                                data-ctn="<%= item.getUnitsPerCtn() %>"
                                                data-pck="<%= item.getUnitsPerPck() %>"
                                                <%= (selectedItemId != null && selectedItemId.equals(String.valueOf(item.getId()))) ? "selected" : "" %>>
                                            <%= item.getItemCode() %> - <%= item.getItemName() %>
                                            (Current: <%= item.getCurrentQuantity() %>)
                                        </option>
                                    <%
                                            }
                                        }
                                    %>
                                </select>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Base UOM</label>
                                <input type="text" name="uom" id="uom" class="form-control" readonly>
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Qty CTN</label>
                                <input type="number"
                                       step="1"
                                       min="0"
                                       name="qtyCtn"
                                       id="qtyCtn"
                                       class="form-control"
                                       value="0"
                                       oninput="calculateBaseQty()">
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Qty PCK</label>
                                <input type="number"
                                       step="1"
                                       min="0"
                                       name="qtyPck"
                                       id="qtyPck"
                                       class="form-control"
                                       value="0"
                                       oninput="calculateBaseQty()">
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">Qty PCS</label>
                                <input type="number"
                                       step="1"
                                       min="0"
                                       name="qtyPcs"
                                       id="qtyPcs"
                                       class="form-control"
                                       value="0"
                                       oninput="calculateBaseQty()">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Total Quantity (Base)</label>
                                <input type="number"
                                       step="1"
                                       min="0"
                                       name="quantityBase"
                                       id="quantityBase"
                                       class="form-control"
                                       value="0"
                                       readonly>
                            </div>

                            <div class="col-md-12 mb-3">
                                <label class="form-label">Remarks</label>
                                <textarea name="remarks"
                                          class="form-control"
                                          placeholder="Enter remarks if needed"></textarea>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary">Save Stock Out</button>
                        <a href="<%= request.getContextPath() %>/stock" class="btn btn-light">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function updateItemData() {
        const itemSelect = document.getElementById("itemId");
        const selected = itemSelect.options[itemSelect.selectedIndex];

        document.getElementById("uom").value = selected.getAttribute("data-uom") || "";
        calculateBaseQty();
    }

    function calculateBaseQty() {
        const itemSelect = document.getElementById("itemId");
        const selected = itemSelect.options[itemSelect.selectedIndex];

        const unitsPerCtn = parseInt(selected.getAttribute("data-ctn")) || 0;
        const unitsPerPck = parseInt(selected.getAttribute("data-pck")) || 0;

        const qtyCtn = parseInt(document.getElementById("qtyCtn").value) || 0;
        const qtyPck = parseInt(document.getElementById("qtyPck").value) || 0;
        const qtyPcs = parseInt(document.getElementById("qtyPcs").value) || 0;

        const total = (qtyCtn * unitsPerCtn) + (qtyPck * unitsPerPck) + qtyPcs;

        document.getElementById("quantityBase").value = total;
    }

    window.onload = function() {
        updateItemData();
    };
</script>
<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>