<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Đơn hàng của tôi</title></head>
<body>
<h1 class="page-title">Lịch sử đặt hàng <small>(${result.totalItems} đơn<c:if test="${not empty currentStatus}"> &ndash; <c:out value="${currentStatus.label}"/></c:if>)</small></h1>

<nav class="status-tabs" aria-label="Lọc theo trạng thái">
    <a href="${ctx}/orders" class="${empty currentStatus ? 'active' : ''}">Tất cả <span class="count">${allCount}</span></a>
    <c:forEach items="${statuses}" var="s">
        <a href="${ctx}/orders?status=${s.code}" class="${currentStatus == s ? 'active' : ''}">
            <c:out value="${s.label}"/> <span class="count">${empty counts[s.code] ? 0 : counts[s.code]}</span>
        </a>
    </c:forEach>
</nav>

<c:if test="${empty result.items}">
    <p class="empty">
        <c:choose>
            <c:when test="${empty currentStatus}">Bạn chưa có đơn hàng nào. <a href="${ctx}/products">Mua sắm ngay</a></c:when>
            <c:otherwise>Không có đơn hàng nào ở trạng thái "<c:out value="${currentStatus.label}"/>".</c:otherwise>
        </c:choose>
    </p>
</c:if>

<c:forEach items="${result.items}" var="o">
    <article class="order-card">
        <header>
            <div><strong>${o.code}</strong> <span class="muted">&middot; <fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span></div>
            <t:status-badge status="${o.status}"/>
        </header>
        <div class="order-body">
            <div>
                <c:out value="${o.firstTitle}"/>
                <c:if test="${o.lineCount gt 1}"><span class="muted"> và ${o.lineCount - 1} sản phẩm khác</span></c:if>
                <small class="muted">(${o.itemCount} cuốn)</small>
            </div>
            <div>Tổng tiền: <strong><t:money value="${o.totalAmount}"/></strong> <span class="muted">&middot; COD</span></div>
        </div>
        <footer>
            <span class="muted">Cập nhật: <fmt:formatDate value="${o.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></span>
            <a class="btn btn-sm" href="${ctx}/orders/detail?id=${o.orderId}">Xem chi tiết</a>
        </footer>
    </article>
</c:forEach>

<t:pagination current="${result.page}" total="${result.totalPages}" url="${ctx}/orders?status=${empty currentStatus ? 'ALL' : currentStatus.code}&page="/>
</body>
</html>
