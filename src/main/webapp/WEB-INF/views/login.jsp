<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login - Employee Leave Management Portal</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<div class="login-page">
    <div class="container login-card">
        <div class="brand-icon">🗓️</div>
        <h1>Employee Leave Management Portal</h1>
        <h2>Sign in to your account</h2>

        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <label for="employeeId">Employee ID</label>
            <input type="text" id="employeeId" name="employeeId" required autofocus>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <input type="submit" value="Login">
        </form>
    </div>
</div>

<footer>
    Employee Leave Management Portal &middot; v1.0.0
    &middot; Support: hr-support@example.com
</footer>

</body>
</html>
