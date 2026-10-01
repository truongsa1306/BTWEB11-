<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Sản phẩm</title></head>
<body>
<h1 class="page-title">Tất cả sản phẩm <small>(${result.totalItems} cuốn)</small></h1>
<c:if test="${empty result.items}"><p class="empty">Chưa có sách nào.</p></c:if>
<div class="card-grid">
    <c:forEach items="${result.items}" var="b">
        <article class="card">
            <a href="${ctx}/book/detail?id=${b.bookid}"><t:cover src="${b.coverImage}" alt="${b.title}" css="cover"/></a>
            <h3><a href="${ctx}/book/detail?id=${b.bookid}"><c:out value="${b.title}"/></a></h3>
            <p class="muted"><c:out value="${b.authorNames}"/></p>
            <p class="price">$${b.price} <span class="muted">· còn ${b.quantity}</span></p>
            <c:if test="${empty sessionScope.user or not sessionScope.user.admin}">
                <t:add-to-cart bookId="${b.bookid}" stock="${b.quantity}" back="/products?page=${result.page}"/>
            </c:if>
        </article>
    </c:forEach>
</div>
<t:pagination current="${result.page}" total="${result.totalPages}" url="${ctx}/products?page="/>
</body>
</html>
