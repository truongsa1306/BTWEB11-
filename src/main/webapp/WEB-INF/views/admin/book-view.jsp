<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Xem sách #${book.bookid}</title></head>
<body>
<h1 class="page-title">Chi tiết sách #${book.bookid}</h1>
<table class="detail-table">
    <tr>
        <td class="detail-cover"><t:cover src="${book.coverImage}" alt="${book.title}" css="cover"/></td>
        <td class="detail-info">
            <div><span class="lbl">Tiêu đề:</span> <strong><c:out value="${book.title}"/></strong></div>
            <div><span class="lbl">Mã isbn:</span> ${book.isbn}</div>
            <div><span class="lbl">Tác giả:</span> <c:out value="${book.authorNames}"/></div>
            <div><span class="lbl">Publisher:</span> <c:out value="${book.publisher}"/></div>
            <div><span class="lbl">Publisher_date:</span> <fmt:formatDate value="${book.publishDate}" pattern="dd/MM/yyyy"/></div>
            <div><span class="lbl">Giá:</span> ${book.price}</div>
            <div><span class="lbl">Quantity:</span> ${book.quantity}</div>
            <div><span class="lbl">Cover image:</span> <c:out value="${book.coverImage}"/></div>
            <div><span class="lbl">Reviews:</span> ${book.reviewCount}</div>
            <p class="desc"><c:out value="${book.description}"/></p>
        </td>
    </tr>
</table>
<p>
    <a class="btn btn-warn" href="${ctx}/admin/books?action=edit&id=${book.bookid}">Sửa</a>
    <a class="btn btn-secondary" href="${ctx}/admin/books">&laquo; Danh sách</a>
</p>
</body>
</html>
