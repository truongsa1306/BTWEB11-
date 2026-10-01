<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Giỏ hàng</title></head>
<body>
<h1 class="page-title">Giỏ hàng <small>(${totalQuantity} cuốn)</small></h1>

<c:choose>
    <c:when test="${empty items}">
        <div class="empty-box">
            <p>Giỏ hàng của bạn đang trống.</p>
            <a class="btn" href="${ctx}/products">Tiếp tục mua sắm</a>
            <a class="btn btn-secondary" href="${ctx}/orders">Xem đơn hàng của tôi</a>
        </div>
    </c:when>
    <c:otherwise>
        <c:if test="${hasProblem}">
            <div class="alert alert-warning">Một số sách hết hàng hoặc vượt số lượng cho phép (dòng nền đỏ). Hãy sửa số lượng hoặc xóa chúng trước khi thanh toán.</div>
        </c:if>
        <div class="table-wrap">
            <table class="data-table cart-table">
                <thead>
                <tr><th>Sách</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach items="${items}" var="it">
                    <tr class="${it.valid ? '' : 'row-invalid'}">
                        <td>
                            <div class="cart-book">
                                <a href="${ctx}/book/detail?id=${it.bookid}"><t:cover src="${it.coverImage}" alt="${it.title}" css="thumb"/></a>
                                <div>
                                    <a class="book-title" href="${ctx}/book/detail?id=${it.bookid}"><c:out value="${it.title}"/></a>
                                    <div class="muted">Kho còn ${it.stock}</div>
                                </div>
                            </div>
                        </td>
                        <td><t:money value="${it.price}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${it.outOfStock}"><span class="badge badge-out">Hết hàng</span></c:when>
                                <c:otherwise>
                                    <form method="post" action="${ctx}/cart" class="qty-form">
                                        <input type="hidden" name="action" value="update">
                                        <input type="hidden" name="bookid" value="${it.bookid}">
                                        <button type="button" class="qty-btn" data-delta="-1" aria-label="Giảm">&minus;</button>
                                        <input type="number" name="quantity" value="${it.quantity}" min="1" max="${it.maxAllowed}" class="qty-input" required>
                                        <button type="button" class="qty-btn" data-delta="1" aria-label="Tăng">+</button>
                                        <button type="submit" class="btn btn-sm btn-secondary">Cập nhật</button>
                                    </form>
                                    <small class="${it.overLimit ? 'field-error' : 'muted'}">Tối đa ${it.maxAllowed} cuốn<c:if test="${it.overLimit}"> &ndash; vui lòng giảm số lượng</c:if></small>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><strong><t:money value="${it.lineTotal}"/></strong></td>
                        <td class="actions">
                            <form method="post" action="${ctx}/cart" class="inline-form" data-confirm="Xóa &quot;${fn:escapeXml(it.title)}&quot; khỏi giỏ hàng?">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="bookid" value="${it.bookid}">
                                <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="cart-footer">
            <form method="post" action="${ctx}/cart" class="inline-form" data-confirm="Xóa toàn bộ giỏ hàng?">
                <input type="hidden" name="action" value="clear">
                <button type="submit" class="btn btn-link">Xóa toàn bộ giỏ hàng</button>
            </form>
            <div class="cart-summary">
                <div>Tổng cộng (${totalQuantity} cuốn): <strong class="grand-total"><t:money value="${total}"/></strong></div>
                <div class="muted">Thanh toán khi nhận hàng (COD)</div>
                <div class="form-actions">
                    <a class="btn btn-secondary" href="${ctx}/products">Tiếp tục mua sắm</a>
                    <c:choose>
                        <c:when test="${hasProblem}"><span class="btn btn-disabled" title="Hãy sửa các dòng nền đỏ trước">Thanh toán</span></c:when>
                        <c:otherwise><a class="btn" href="${ctx}/checkout">Thanh toán</a></c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>
</body>
</html>
