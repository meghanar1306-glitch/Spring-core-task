<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Apply Leave - Employee Leave Management Portal</title>
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
    <h1>📝 Apply for Leave</h1>

    <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
    </c:if>
    <c:if test="${not empty successMessage}">
        <div class="success-message">${successMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/leave/apply" method="post">

        <label for="leaveType">Leave Type</label>
        <select id="leaveType" name="leaveType" required>
            <c:forEach var="type" items="${leaveTypes}">
                <option value="${type}">${type}</option>
            </c:forEach>
        </select>

        <label for="fromDate">From Date</label>
        <input type="date" id="fromDate" name="fromDate" required>

        <label for="toDate">To Date</label>
        <input type="date" id="toDate" name="toDate" required>

        <label for="reason">Reason</label>
        <textarea id="reason" name="reason" rows="4" required></textarea>

        <input type="submit" value="Submit Leave Request">
    </form>
</div>

<footer>
    Employee Leave Management Portal &middot; v1.0.0
</footer>

</body>
</html>
