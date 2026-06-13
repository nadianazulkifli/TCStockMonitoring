<%
    String sidebarRole = (String) session.getAttribute("role");
    String fullName = (String) session.getAttribute("fullName");
    String contextPath = request.getContextPath();

    String servletPath = request.getServletPath();
    String uri = request.getRequestURI();

    boolean dashboardActive =
            uri.endsWith("/admin/dashboard.jsp") ||
            uri.endsWith("/manager/dashboard.jsp") ||
            uri.endsWith("/worker/dashboard.jsp");

    boolean profileActive = "/profile".equals(servletPath);
    boolean usersActive = "/users".equals(servletPath);
    boolean itemsActive = "/items".equals(servletPath);
    boolean stockActive = "/stock".equals(servletPath);
    boolean alertsActive = "/alerts".equals(servletPath);
    boolean countActive = "/stock-count".equals(servletPath);
    boolean reportsActive = "/reports".equals(servletPath);
    boolean logsActive = "/activity-logs".equals(servletPath);

    String initials = "U";
    if (fullName != null && !fullName.trim().isEmpty()) {
        initials = fullName.substring(0, 1).toUpperCase();
    }
%>

<div class="sidebar">
    <div class="brand-card">
        <div class="brand-row">
            <img src="<%= request.getContextPath() %>/assets/images/login-bg.jpeg"
                 alt="TC Stock Logo"
                 style="width:42px;height:42px;border-radius:10px;object-fit:cover;background:#fff;padding:4px;">

            <div>
                <p class="brand-title">TC STOCK</p>
                <p class="brand-subtitle">Texas Chicken Inventory</p>
            </div>
        </div>

        <div class="user-mini">
            <div class="user-avatar"><%= initials %></div>
            <div>
                <p class="name"><%= fullName == null ? "User" : fullName %></p>
                <p class="role"><%= sidebarRole == null ? "" : sidebarRole %></p>
            </div>
        </div>
    </div>

    <div class="nav-title">Main Menu</div>

   <a class="<%= dashboardActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/<%= sidebarRole %>/dashboard.jsp">
    <span class="nav-icon"><i class="bi bi-speedometer2"></i></span>
    <span>Dashboard</span>
</a>

<a class="<%= profileActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/profile">
    <span class="nav-icon"><i class="bi bi-person-circle"></i></span>
    <span>My Profile</span>
</a>

<% if ("admin".equalsIgnoreCase(sidebarRole)) { %>
<a class="<%= usersActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/users">
    <span class="nav-icon"><i class="bi bi-people"></i></span>
    <span>User Management</span>
</a>
<% } %>

<% if ("admin".equalsIgnoreCase(sidebarRole) || "manager".equalsIgnoreCase(sidebarRole)) { %>
<a class="<%= itemsActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/items">
    <span class="nav-icon"><i class="bi bi-box-seam"></i></span>
    <span>Item Master</span>
</a>
<% } %>

<a class="<%= stockActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/stock">
    <span class="nav-icon"><i class="bi bi-arrow-left-right"></i></span>
    <span>Stock Transactions</span>
</a>

<a class="<%= alertsActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/alerts">
    <span class="nav-icon"><i class="bi bi-exclamation-triangle"></i></span>
    <span>Low Stock Alerts</span>
</a>

<a class="<%= countActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/stock-count">
    <span class="nav-icon"><i class="bi bi-clipboard-check"></i></span>
    <span>Stock Count</span>
</a>

<% if ("admin".equalsIgnoreCase(sidebarRole) || "manager".equalsIgnoreCase(sidebarRole)) { %>
<a class="<%= reportsActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/reports">
    <span class="nav-icon"><i class="bi bi-bar-chart"></i></span>
    <span>Reports</span>
</a>

<a class="<%= logsActive ? "nav-link active-link" : "nav-link" %>"
   href="<%= contextPath %>/activity-logs">
    <span class="nav-icon"><i class="bi bi-journal-text"></i></span>
    <span>Activity Logs</span>
</a>
<% } %>
    <div class="logout-wrap">
        <a class="btn-logout" href="<%= contextPath %>/logout">Logout</a>
    </div>
</div>