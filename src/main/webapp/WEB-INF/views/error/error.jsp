<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Lỗi</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
<div class="auth-box">
    <h1><c:out value="${empty errorTitle ? 'Đã xảy ra lỗi' : errorTitle}"/></h1>
    <p><c:out value="${empty errorMessage ? 'Hệ thống gặp sự cố hoặc không tìm thấy trang yêu cầu. Vui lòng thử lại sau.' : errorMessage}"/></p>
    <p><a href="${pageContext.request.contextPath}/home">&laquo; Về trang chủ</a></p>
</div>
</body>
</html>
