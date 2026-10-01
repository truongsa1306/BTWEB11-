<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đăng ký</title></head>
<body>
<div class="auth-box">
    <h1>Đăng ký tài khoản</h1>
    <p class="muted">Sau khi đăng ký, mã OTP sẽ được gửi qua email để kích hoạt tài khoản.</p>
    <form method="post" action="${ctx}/register" class="form" novalidate>
        <label>Email <input type="email" name="email" value="<c:out value='${param.email}'/>" maxlength="50"></label>
        <c:if test="${not empty errors.email}"><div class="field-error">${errors.email}</div></c:if>
        <label>Họ tên <input type="text" name="fullname" value="<c:out value='${param.fullname}'/>" maxlength="50"></label>
        <c:if test="${not empty errors.fullname}"><div class="field-error">${errors.fullname}</div></c:if>
        <label>Số điện thoại (không bắt buộc) <input type="text" name="phone" value="<c:out value='${param.phone}'/>" maxlength="10"></label>
        <c:if test="${not empty errors.phone}"><div class="field-error">${errors.phone}</div></c:if>
        <label>Mật khẩu <input type="password" name="password"></label>
        <c:if test="${not empty errors.password}"><div class="field-error">${errors.password}</div></c:if>
        <label>Nhập lại mật khẩu <input type="password" name="confirm"></label>
        <c:if test="${not empty errors.confirm}"><div class="field-error">${errors.confirm}</div></c:if>
        <button type="submit" class="btn">Đăng ký &amp; nhận OTP</button>
    </form>
    <p class="muted">Đã có tài khoản? <a href="${ctx}/login">Đăng nhập</a></p>
</div>
</body>
</html>
