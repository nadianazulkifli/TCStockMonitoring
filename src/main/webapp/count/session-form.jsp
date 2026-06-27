<%@ page contentType="text/html" pageEncoding="UTF-8" %>

<%
    String role = (String) session.getAttribute("role");
    if (role == null) {
        response.sendRedirect(request.getContextPath() + "/auth/login.jsp");
        return;
    }

    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html>
<head>
    <title>New Stock Count Session</title>
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
            <h1 class="page-title">New Stock Count Session</h1>
            <p class="page-subtitle">Create a new session for physical stock counting</p>
        </div>

        <div class="content-wrap">
            <% if (error != null) { %>
                <div class="alert alert-danger"><%= error %></div>
            <% } %>

            <div class="card">
                <div class="card-body p-4">
                    <h3 class="section-title mb-3">Session Information</h3>

                    <form action="<%= request.getContextPath() %>/stock-count" method="post">
                        <input type="hidden" name="action" value="saveSession">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Session Name</label>
                                <input type="text" name="sessionName" class="form-control"
                                       placeholder="Enter session name" required>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Count Type</label>
                                <select name="countType" class="form-select" required>
                                    <option value="">-- Select Count Type --</option>
                                    <option value="WEEKLY">WEEKLY</option>
                                    <option value="MONTHLY">MONTHLY</option>
                                    <option value="MONTH_END">MONTH_END</option>
                                    <option value="OTHER">OTHER</option>
                                </select>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Count Date</label>
                                <input type="date" name="countDate" class="form-control" required>
                            </div>

                            <div class="col-md-12 mb-3">
                                <label class="form-label">Notes</label>
                                <textarea name="notes" class="form-control"
                                          placeholder="Enter notes if needed"></textarea>
                            </div>
                        </div>

                        <button type="submit" class="btn btn-primary">Save Session</button>
                        <a href="<%= request.getContextPath() %>/stock-count" class="btn btn-light">Cancel</a>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>