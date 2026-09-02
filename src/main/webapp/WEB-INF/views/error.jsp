<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Something Went Wrong</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>

<div class="container" style="max-width: 480px; margin-top: 90px; text-align:center;">
    <div style="font-size:48px;">😕</div>
    <h1 style="padding-bottom:0;">Something Went Wrong</h1>
    <p class="error-message" style="text-align:left;">
        Sorry, an unexpected error occurred while processing your request.
        Please try again, or contact support if the problem continues.
    </p>
    <p><a href="${pageContext.request.contextPath}/login">← Return to Login</a></p>
</div>

</body>
</html>
