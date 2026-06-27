<%@ page import="java.util.List" %>
<%@ page import="com.tcstock.model.StockTransaction" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    List<StockTransaction> transactions =
            (List<StockTransaction>) request.getAttribute("transactions");

    String success = request.getParameter("success");
    String error = request.getParameter("error");

    String keyword = request.getAttribute("keyword") != null
            ? request.getAttribute("keyword").toString()
            : "";

    String type = request.getAttribute("type") != null
            ? request.getAttribute("type").toString()
            : "";

    String dateFrom = request.getAttribute("dateFrom") != null
            ? request.getAttribute("dateFrom").toString()
            : "";

    String dateTo = request.getAttribute("dateTo") != null
            ? request.getAttribute("dateTo").toString()
            : "";
%>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Transactions</title>
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
            <h1 class="page-title">Stock Transactions</h1>
            <p class="page-subtitle">All stock movement records are tracked here</p>
        </div>

        <div class="content-wrap">

            <% if (success != null) { %>
                <div class="alert alert-success"><%= success %></div>
            <% } %>

            <% if (error != null) { %>
                <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <div class="page-box">
                <div class="section-head">
                    <div>
                        <h3 class="section-title">Transaction History</h3>
                        <p class="section-desc">
                            Use Stock In and Stock Out to update quantity through proper transaction flow
                        </p>
                    </div>

                    <div class="quick-actions">
                        <a href="<%= request.getContextPath() %>/stock?action=in" class="btn btn-primary">
                            Stock In
                        </a>
                        <a href="<%= request.getContextPath() %>/stock?action=out" class="btn btn-dark">
                            Stock Out
                        </a>
                    </div>
                </div>

                <form action="<%= request.getContextPath() %>/stock" method="get" class="row g-3 mt-3">
                    <input type="hidden" name="action" value="list">

                    <div class="col-md-4">
                        <input type="text"
                               name="keyword"
                               class="form-control"
                               placeholder="Search item code, item name, ref no, or user"
                               value="<%= keyword %>">
                    </div>

                    <div class="col-md-2">
                        <select name="type" class="form-select">
                            <option value="">All Types</option>
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

                    <div class="col-md-2">
                        <input type="date"
                               name="dateFrom"
                               class="form-control"
                               value="<%= dateFrom %>">
                    </div>

                    <div class="col-md-2">
                        <input type="date"
                               name="dateTo"
                               class="form-control"
                               value="<%= dateTo %>">
                    </div>

                    <div class="col-md-2 d-flex gap-2">
                        <button type="submit" class="btn btn-primary">
                            Filter
                        </button>

                        <a href="<%= request.getContextPath() %>/stock" class="btn btn-light">
                            Reset
                        </a>
                    </div>
                </form>
            </div>

            <div class="card mt-4">
                <div class="card-body table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>No</th>
                                <th>Date Time</th>
                                <th>Type</th>
                                <th>Item Code</th>
                                <th>Item Name</th>
                                <th>Qty CTN</th>
                                <th>Qty PCK</th>
                                <th>Qty PCS</th>
                                <th>Qty Base</th>
                                <th>UOM</th>
                                <th>Reference No</th>
                                <th>Remarks</th>
                                <th>By User</th>
                            </tr>
                        </thead>

                        <tbody>
                        <%
                            if (transactions != null && !transactions.isEmpty()) {
                                int no = 1;

                                for (StockTransaction st : transactions) {
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
                                    No transaction records found.
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