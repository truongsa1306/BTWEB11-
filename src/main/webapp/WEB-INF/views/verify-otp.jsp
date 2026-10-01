<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Xác thực OTP</title></head>
<body>
<div class="auth-box">
    <h1>Xác thực OTP</h1>
    <p class="muted">Nhập mã gồm 6 chữ số đã gửi tới email của bạn (hiệu lực ${otpMinutes} phút).</p>
    <c:if test="${not empty error}"><div class="alert alert-error"><c:out value="${error}"/></div></c:if>
    <form method="post" action="${ctx}/verify-otp" class="form">
        <input type="hidden" name="action" value="verify">
        <label>Email <input type="email" name="email" value="<c:out value='${email}'/>" maxlength="50" required></label>
        <label>Mã OTP <input type="text" name="otp" inputmode="numeric" pattern="\d{6}" maxlength="6" class="otp-input" required autofocus></label>
        <button type="submit" class="btn">Kích hoạt tài khoản</button>
    </form>
    <form method="post" action="${ctx}/verify-otp" class="inline-form">
        <input type="hidden" name="action" value="resend">
        <input type="hidden" name="email" value="<c:out value='${email}'/>">
        <button type="submit" class="btn btn-link">Gửi lại OTP</button>
    </form>
</div>
</body>
</html>
