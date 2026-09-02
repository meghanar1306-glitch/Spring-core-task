<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Leave Requests - Employee Leave Management Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<div class="top-bar">
    <a href="${pageContext.request.contextPath}/dashboard">🏠 Dashboard</a>
    <a href="${pageContext.request.contextPath}/leave/apply">📝 Apply Leave</a>
    <a href="${pageContext.request.contextPath}/leave/history">📋 My Leave Requests</a>
    <a href="${pageContext.request.contextPath}/logout" style="margin-left:auto;">🚪 Logout</a>
</div>

<div class="container" style="max-width: 900px;">
    <h1>📋 My Leave Requests</h1>

    <c:choose>
        <c:when test="${empty leaveRequests}">
            <p>You have not submitted any leave requests yet.</p>
        </c:when>
        <c:otherwise>
            <table>
                <tr>
                    <th>Request ID</th>
                    <th>Leave Type</th>
                    <th>From</th>
                    <th>To</th>
                    <th>Days</th>
                    <th>Reason</th>
                    <th>Status</th>
                    <th>Submitted On</th>
                </tr>
                <c:forEach var="request" items="${leaveRequests}">
                    <tr>
                        <td>${request.requestId}</td>
                        <td>${request.leaveType}</td>
                        <td>${request.fromDate}</td>
                        <td>${request.toDate}</td>
                        <td>${request.numberOfDays}</td>
                        <td>${request.reason}</td>
                        <td>
                            <c:choose>
                                <c:when test="${request.status == 'PENDING'}">
                                    <span class="status-pending">${request.status}</span>
                                </c:when>
                                <c:when test="${request.status == 'APPROVED'}">
                                    <span class="status-approved">${request.status}</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-rejected">${request.status}</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>${request.createdDate}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<footer>
    Employee Leave Management Portal &middot; v1.0.0
</footer>

</body>
</html>
