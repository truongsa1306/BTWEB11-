<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đơn hàng ${order.code}</title></head>
<body>
<p><a href="${ctx}/orders">&laquo; Lịch sử đặt hàng</a></p>
<h1 class="page-title">Đơn hàng ${order.code} <t:status-badge status="${order.status}"/></h1>

<c:choose>
    <c:when test="${order.status.onTrack}">
        <ol class="steps">
            <c:forEach items="${track}" var="s" varStatus="vs">
                <li class="${vs.index lt order.status.step ? 'done' : (vs.index == order.status.step ? 'current' : '')}">
                    <span class="dot">${vs.index + 1}</span><span class="step-label"><c:out value="${s.label}"/></span>
                </li>
            </c:forEach>
        </ol>
    </c:when>
    <c:when test="${order.status.code == 'CANCELLED'}"><div class="alert alert-error">Đơn hàng này đã bị hủy.</div></c:when>
    <c:otherwise><div class="alert alert-warning">Đơn hàng này đã được hoàn trả.</div></c:otherwise>
</c:choose>

<section class="panel">
    <h2>Thông tin nhận hàng</h2>
    <div class="info-grid">
        <div><span class="lbl">Người nhận:</span> <c:out value="${order.receiverName}"/></div>
        <div><span class="lbl">Điện thoại:</span> <c:out value="${order.phone}"/></div>
        <div class="span-2"><span class="lbl">Địa chỉ:</span> <c:out value="${order.address}"/></div>
        <c:if test="${not empty order.note}"><div class="span-2"><span class="lbl">Ghi chú:</span> <c:out value="${order.note}"/></div></c:if>
        <div><span class="lbl">Thanh toán:</span> Thanh toán khi nhận hàng (<c:out value="${order.paymentMethod}"/>)</div>
        <div><span class="lbl">Ngày đặt:</span> <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
        <div><span class="lbl">Cập nhật lần cuối:</span> <fmt:formatDate value="${order.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></div>
    </div>
</section>

<section class="panel">
    <h2>Sản phẩm (${order.itemCount} cuốn)</h2>
    <div class="table-wrap">
        <table class="data-table">
            <thead><tr><th>Sách</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr></thead>
            <tbody>
            <c:forEach items="${order.items}" var="it">
                <tr>
                    <td>
                        <div class="cart-book">
                            <t:cover src="${it.coverImage}" alt="${it.title}" css="thumb"/>
                            <c:choose>
                                <c:when test="${not empty it.bookid}"><a class="book-title" href="${ctx}/book/detail?id=${it.bookid}"><c:out value="${it.title}"/></a></c:when>
                                <c:otherwise><span class="book-title"><c:out value="${it.title}"/></span> <span class="muted">(sách đã ngừng bán)</span></c:otherwise>
                            </c:choose>
                        </div>
                    </td>
                    <td><t:money value="${it.unitPrice}"/></td>
                    <td>${it.quantity}</td>
                    <td><t:money value="${it.lineTotal}"/></td>
                </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr><td colspan="3" class="right">Tổng thanh toán</td><td><strong class="grand-total"><t:money value="${order.totalAmount}"/></strong></td></tr>
            </tfoot>
        </table>
    </div>
</section>

<div class="form-actions">
    <a class="btn btn-secondary" href="${ctx}/orders">&laquo; Quay lại danh sách</a>
    <c:if test="${order.cancellable}">
        <form method="post" action="${ctx}/orders/cancel" class="inline-form" data-confirm="Bạn chắc chắn muốn hủy đơn ${order.code}?">
            <input type="hidden" name="id" value="${order.orderId}">
            <button type="submit" class="btn btn-danger">Hủy đơn hàng</button>
        </form>
    </c:if>
</div>
</body>
</html>
