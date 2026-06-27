<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Stock Monitoring System</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">

      <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/bootstrap/css/bootstrap.min.css">
<link href="<%= request.getContextPath() %>/assets/css/theme.css" rel="stylesheet">
</head>
<body>

<div class="login-shell">
    <div class="card login-card">
        <div class="row g-0">
            <div class="col-lg-6">
                <div class="login-left" style='background:
    linear-gradient(rgba(80,20,15,.35), rgba(20,20,20,.45)),
    url("<%= request.getContextPath() %>/assets/images/login-bg.jpeg") center/cover no-repeat;'>
                    <div class="login-brand">
                        <div class="info-kicker">🍗 Texas Chicken Stock System</div>
                        <h1>Welcome Back!</h1>
                        <p>
                            Smart stock monitoring for Texas Chicken Petronas Wakaf Bharu.
                            Manage inventory, stock movement, alerts, counts, and reports in one place.
                        </p>
                    </div>

                    <div>
                        <p style="margin:0;font-size:12px;color:rgba(255,255,255,.75);">
                            © 2026 TC Stock Monitoring System
                        </p>
                    </div>
                </div>
            </div>

            <div class="col-lg-6">
                <div class="login-right">
                    <div class="login-form-box">
                        <h3>Sign In</h3>
                        <p class="sub">Please sign in to your account</p>

                        <%
                            String error = (String) request.getAttribute("error");
                            if (error != null) {
                        %>
                            <div class="alert alert-danger"><%= error %></div>
                        <%
                            }
                        %>

                        <form action="<%= request.getContextPath() %>/login" method="post">
                            <div class="mb-3">
                                <label class="form-label">Email</label>
                                <input type="email" name="email" class="form-control" placeholder="Enter your email" required>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Password</label>
                                <input type="password" name="password" class="form-control" placeholder="Enter your password" required>
                            </div>

                            <button type="submit" class="btn btn-primary w-100">Login</button>
                        </form>

                        <div class="mt-4 text-center" style="color:#9aa4b2;font-size:13px;">
                            Stock Monitoring System
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/assets/bootstrap/js/bootstrap.bundle.min.js"></script>

</body>
</html>