<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/> | Quản trị BookStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <sitemesh:write property="head"/>
</head>
<body class="theme-admin">
<%@ include file="/WEB-INF/decorators/_header.jspf" %>
<div class="admin-bar">
    <div class="container">
        <strong>⚙ Khu vực quản trị</strong>
        <a href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a>
        <a href="${pageContext.request.contextPath}/admin/books?action=new">+ Thêm sách</a>
    </div>
</div>
<main class="container content">
    <%@ include file="/WEB-INF/decorators/_flash.jspf" %>
    <sitemesh:write property="body"/>
</main>
<%@ include file="/WEB-INF/decorators/_footer.jspf" %>
<script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
</body>
</html>
