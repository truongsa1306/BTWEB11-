<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Thanh toán</title></head>
<body>
<h1 class="page-title">Thanh toán đơn hàng</h1>
<div class="checkout-grid">
    <form method="post" action="${ctx}/checkout" class="form form-wide" data-once="1">
        <h2>Thông tin giao hàng</h2>
        <label>Họ tên người nhận
            <input type="text" name="receiverName" maxlength="100" value="<c:out value='${form_receiverName}'/>" required>
        </label>
        <c:if test="${not empty errors.receiverName}"><div class="field-error"><c:out value="${errors.receiverName}"/></div></c:if>

        <label>Số điện thoại
            <input type="text" name="phone" maxlength="15" inputmode="tel" placeholder="0901234567" value="<c:out value='${form_phone}'/>" required>
        </label>
        <c:if test="${not empty errors.phone}"><div class="field-error"><c:out value="${errors.phone}"/></div></c:if>

        <label>Địa chỉ giao hàng
            <textarea name="address" rows="3" maxlength="255" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành" required><c:out value="${form_address}"/></textarea>
        </label>
        <c:if test="${not empty errors.address}"><div class="field-error"><c:out value="${errors.address}"/></div></c:if>

        <label>Ghi chú (không bắt buộc)
            <textarea name="note" rows="2" maxlength="255"><c:out value="${form_note}"/></textarea>
        </label>
        <c:if test="${not empty errors.note}"><div class="field-error"><c:out value="${errors.note}"/></div></c:if>

        <h2>Phương thức thanh toán</h2>
        <div class="pay-box">
            <label class="check"><input type="radio" name="payment" value="COD" checked> 💵 Thanh toán khi nhận hàng (COD)</label>
            <div class="muted">Bạn trả tiền mặt cho nhân viên giao hàng khi nhận sách.</div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn">Đặt hàng</button>
            <a class="btn btn-secondary" href="${ctx}/cart">&laquo; Quay lại giỏ hàng</a>
        </div>
    </form>

    <aside class="summary-box">
        <h2>Đơn hàng (${totalQuantity} cuốn)</h2>
        <ul class="summary-list">
            <c:forEach items="${items}" var="it">
                <li>
                    <span><c:out value="${it.title}"/> <span class="muted">&times; ${it.quantity}</span></span>
                    <strong><t:money value="${it.lineTotal}"/></strong>
                </li>
            </c:forEach>
        </ul>
        <div class="summary-total"><span>Phí vận chuyển</span><span>Miễn phí</span></div>
        <div class="summary-total grand"><span>Tổng thanh toán</span><strong><t:money value="${total}"/></strong></div>
    </aside>
</div>
</body>
</html>
