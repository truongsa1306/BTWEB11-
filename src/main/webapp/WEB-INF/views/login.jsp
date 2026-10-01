<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đăng nhập</title></head>
<body>
<div class="auth-box">
    <h1>Đăng nhập</h1>
    <c:if test="${not empty info}"><div class="alert alert-info"><c:out value="${info}"/></div></c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/>
            <c:if test="${notActivated}"> <a href="${ctx}/verify-otp?email=<c:out value='${email}'/>">Nhập mã OTP</a></c:if>
        </div>
    </c:if>
    <form method="post" action="${ctx}/login" class="form">
        <label>Email <input type="email" name="email" value="<c:out value='${email}'/>" maxlength="50" required autofocus></label>
        <label>Mật khẩu <input type="password" name="password" required></label>
        <button type="submit" class="btn">Đăng nhập</button>
    </form>
    <p class="muted">Chưa có tài khoản? <a href="${ctx}/register">Đăng ký</a></p>
</div>
</body>
</html>
