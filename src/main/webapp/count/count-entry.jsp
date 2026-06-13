<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.StockCountSession" %>
<%@ page import="com.tcstock.model.StockCountDetail" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");

    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    StockCountSession countSession = (StockCountSession) request.getAttribute("countSession");
    List<StockCountDetail> details = (List<StockCountDetail>) request.getAttribute("details");

    String success = request.getParameter("success");
    String error = request.getParameter("error");

    boolean isApproved = countSession != null && "APPROVED".equalsIgnoreCase(countSession.getStatus());
    boolean canApprove = "admin".equalsIgnoreCase(role) || "manager".equalsIgnoreCase(role);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Count Entry</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <link href="<%= request.getContextPath() %>/assets/css/theme.css" rel="stylesheet">
</head>

<body class="bg-light">

<nav class="navbar navbar-dark bg-success px-4">
    <span class="navbar-brand">Stock Count Entry</span>

    <div>
        <a href="<%= request.getContextPath() %>/stock-count" class="btn btn-warning btn-sm">
            Back to Sessions
        </a>

        <a href="<%= request.getContextPath() %>/logout" class="btn btn-light btn-sm">
            Logout
        </a>
    </div>
</nav>

<div class="container py-4">

    <% if (countSession != null) { %>
        <div class="card shadow-sm mb-3">
            <div class="card-body">
                <h4><%= countSession.getSessionName() %></h4>

                <p class="mb-1">
                    <strong>Count Type:</strong> <%= countSession.getCountType() %>
                </p>

                <p class="mb-1">
                    <strong>Count Date:</strong> <%= countSession.getCountDate() %>
                </p>

                <p class="mb-1">
                    <strong>Status:</strong>
                    <% if (isApproved) { %>
                        <span class="badge bg-success">APPROVED</span>
                    <% } else { %>
                        <span class="badge bg-warning text-dark"><%= countSession.getStatus() %></span>
                    <% } %>
                </p>

                <p class="mb-0">
                    <strong>Notes:</strong>
                    <%= countSession.getNotes() == null ? "" : countSession.getNotes() %>
                </p>
            </div>
        </div>
    <% } %>

    <% if (success != null) { %>
        <div class="alert alert-success"><%= success %></div>
    <% } %>

    <% if (error != null) { %>
        <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <% if (countSession != null && !isApproved && canApprove) { %>
        <form action="<%= request.getContextPath() %>/stock-count"
              method="post"
              class="mb-3"
              onsubmit="return confirm('Approve this stock count? This will update Item Master current quantity based on the counted quantity.');">

            <input type="hidden" name="action" value="approve">
            <input type="hidden" name="sessionId" value="<%= countSession.getId() %>">

            <button type="submit" class="btn btn-success">
                Approve & Update Item Master
            </button>
        </form>
    <% } %>

    <% if (isApproved) { %>
        <div class="alert alert-info">
            This stock count session has been approved. Count quantities can no longer be edited.
        </div>
    <% } %>

    <div class="card shadow-sm">
        <div class="card-body table-responsive">
            <table class="table table-bordered table-hover align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>No</th>
                        <th>Item Code</th>
                        <th>Item Name</th>
                        <th>System Qty</th>
                        <th>UOM</th>
                        <th>CTN</th>
                        <th>PCK</th>
                        <th>PCS</th>
                        <th>Counted Total</th>
                        <th>Variance</th>
                        <th>Remarks</th>
                        <th width="120">Action</th>
                    </tr>
                </thead>

                <tbody>
                <%
                    if (details != null && !details.isEmpty()) {
                        int no = 1;

                        for (StockCountDetail d : details) {
                            boolean varianceProblem = d.getVarianceQty() != 0;
                            String formId = "countForm" + d.getItemId();
                %>

                    <tr class="<%= varianceProblem ? "table-warning" : "" %>"
                        data-system-qty="<%= d.getCurrentQuantity() %>">

                        <td><%= no++ %></td>
                        <td><%= d.getItemCode() %></td>
                        <td><%= d.getItemName() %></td>
                        <td class="system-qty"><%= d.getCurrentQuantity() %></td>
                        <td><%= d.getBaseUom() %></td>

                        <td>
                            <input type="number"
                                   step="1"
                                   min="0"
                                   name="qtyCtn"
                                   form="<%= formId %>"
                                   class="form-control form-control-sm qty-ctn"
                                   value="<%= d.getQtyCtn() %>"
                                   data-ctn="<%= d.getUnitsPerCtn() %>"
                                   oninput="calculateRow(this)"
                                   <%= isApproved ? "disabled" : "" %>>
                        </td>

                        <td>
                            <input type="number"
                                   step="1"
                                   min="0"
                                   name="qtyPck"
                                   form="<%= formId %>"
                                   class="form-control form-control-sm qty-pck"
                                   value="<%= d.getQtyPck() %>"
                                   data-pck="<%= d.getUnitsPerPck() %>"
                                   oninput="calculateRow(this)"
                                   <%= isApproved ? "disabled" : "" %>>
                        </td>

                        <td>
                            <input type="number"
                                   step="1"
                                   min="0"
                                   name="qtyPcs"
                                   form="<%= formId %>"
                                   class="form-control form-control-sm qty-pcs"
                                   value="<%= d.getQtyPcs() %>"
                                   oninput="calculateRow(this)"
                                   <%= isApproved ? "disabled" : "" %>>
                        </td>

                        <td>
                            <input type="number"
                                   step="1"
                                   min="0"
                                   name="totalQuantityBase"
                                   form="<%= formId %>"
                                   class="form-control form-control-sm total-base"
                                   value="<%= d.getTotalQuantityBase() %>"
                                   readonly>
                        </td>

                        <td>
                            <span class="variance-badge badge <%= d.getVarianceQty() == 0 ? "bg-success" : "bg-warning text-dark" %>">
                                <%= d.getVarianceQty() %>
                            </span>
                        </td>

                        <td>
                            <input type="text"
                                   name="remarks"
                                   form="<%= formId %>"
                                   class="form-control form-control-sm"
                                   value="<%= d.getRemarks() == null ? "" : d.getRemarks() %>"
                                   <%= isApproved ? "disabled" : "" %>>
                        </td>

                        <td>
                            <% if (!isApproved) { %>
                                <form id="<%= formId %>" action="<%= request.getContextPath() %>/stock-count" method="post">
                                    <input type="hidden" name="action" value="saveDetail">
                                    <input type="hidden" name="sessionId" value="<%= d.getSessionId() %>">
                                    <input type="hidden" name="itemId" value="<%= d.getItemId() %>">

                                    <button type="submit" class="btn btn-primary btn-sm">
                                        Save
                                    </button>
                                </form>
                            <% } else { %>
                                <span class="text-muted">Locked</span>
                            <% } %>
                        </td>
                    </tr>

                <%
                        }
                    } else {
                %>

                    <tr>
                        <td colspan="12" class="text-center text-muted">
                            No items found for counting.
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

<script>
    function calculateRow(element) {
        const row = element.closest("tr");

        const qtyCtnInput = row.querySelector(".qty-ctn");
        const qtyPckInput = row.querySelector(".qty-pck");
        const qtyPcsInput = row.querySelector(".qty-pcs");
        const totalInput = row.querySelector(".total-base");
        const varianceBadge = row.querySelector(".variance-badge");

        const qtyCtn = parseInt(qtyCtnInput.value) || 0;
        const qtyPck = parseInt(qtyPckInput.value) || 0;
        const qtyPcs = parseInt(qtyPcsInput.value) || 0;

        const unitsPerCtn = parseInt(qtyCtnInput.getAttribute("data-ctn")) || 0;
        const unitsPerPck = parseInt(qtyPckInput.getAttribute("data-pck")) || 0;

        const systemQty = parseInt(row.getAttribute("data-system-qty")) || 0;

        const total = (qtyCtn * unitsPerCtn) + (qtyPck * unitsPerPck) + qtyPcs;
        const variance = total - systemQty;

        totalInput.value = total;

        varianceBadge.textContent = variance;

        if (variance === 0) {
            varianceBadge.className = "variance-badge badge bg-success";
        } else {
            varianceBadge.className = "variance-badge badge bg-warning text-dark";
        }
    }
</script>

</body>
</html>