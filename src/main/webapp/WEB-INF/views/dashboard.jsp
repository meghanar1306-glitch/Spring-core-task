<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard - Employee Leave Management Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<div class="top-bar">
    <a href="${pageContext.request.contextPath}/dashboard">🏠 Dashboard</a>
    <a href="${pageContext.request.contextPath}/leave/apply">📝 Apply Leave</a>
    <a href="${pageContext.request.contextPath}/leave/history">📋 My Leave Requests</a>
    <a href="${pageContext.request.contextPath}/logout" style="margin-left:auto;">🚪 Logout</a>
</div>

<div class="container">
    <h1>👋 Welcome, ${employee.name}</h1>

    <p>
        <strong>Employee ID:</strong> ${employee.employeeId}<br>
        <strong>Department:</strong> ${employee.department}<br>
        <strong>Dashboard view preference (from cookie):</strong> ${viewPreference}
    </p>

    <div class="summary-cards">
        <div class="card">
            <div class="value">${employee.leaveBalance}</div>
            <div>Leave Balance (days)</div>
        </div>
        <div class="card">
            <div class="value">${pendingCount}</div>
            <div>Pending Requests</div>
        </div>
        <div class="card">
            <div class="value">${approvedCount}</div>
            <div>Approved Requests</div>
        </div>
    </div>

    <c:if test="${viewPreference == 'detailed'}">
        <h2>🔗 Quick Links</h2>
        <ul>
            <li><a href="${pageContext.request.contextPath}/leave/apply">Apply for a new leave</a></li>
            <li><a href="${pageContext.request.contextPath}/leave/history">View my leave request history</a></li>
        </ul>
    </c:if>

    <h2>⚙️ Dashboard View Preference</h2>
    <p>Choose how much detail the dashboard shows (saved in a cookie for next time):</p>
    <form action="${pageContext.request.contextPath}/preferences/dashboard-view" method="post">
        <select name="view">
            <option value="detailed" ${viewPreference == 'detailed' ? 'selected' : ''}>Detailed</option>
            <option value="compact" ${viewPreference == 'compact' ? 'selected' : ''}>Compact</option>
        </select>
        <input type="submit" value="Save Preference">
    </form>
</div>

<footer>
    Employee Leave Management Portal &middot; v1.0.0
</footer>

</body>
</html>
