<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="bookId" required="true" type="java.lang.Integer" %>
<%@ attribute name="stock" required="false" type="java.lang.Integer" description="Ton kho (books.quantity)" %>
<%@ attribute name="back" required="true" description="Duong dan (context-relative) de quay lai sau khi them" %>
<%@ attribute name="withQty" required="false" type="java.lang.Boolean" description="true: hien o nhap so luong" %>
<c:choose>
    <c:when test="${empty stock or stock le 0}"><span class="badge badge-out">Hết hàng</span></c:when>
    <c:otherwise>
        <c:set var="maxQty" value="${stock lt applicationScope.cartMaxPerItem ? stock : applicationScope.cartMaxPerItem}"/>
        <form method="post" action="${pageContext.request.contextPath}/cart" class="add-cart-form">
            <input type="hidden" name="action" value="add">
            <input type="hidden" name="bookid" value="${bookId}">
            <input type="hidden" name="back" value="<c:out value='${back}'/>">
            <c:choose>
                <c:when test="${withQty}">
                    <label class="qty-label">Số lượng
                        <input type="number" name="quantity" value="1" min="1" max="${maxQty}" class="qty-input" required>
                    </label>
                </c:when>
                <c:otherwise><input type="hidden" name="quantity" value="1"></c:otherwise>
            </c:choose>
            <button type="submit" class="btn btn-sm">🛒 Thêm vào giỏ</button>
        </form>
    </c:otherwise>
</c:choose>
