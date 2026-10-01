<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Quản lý sách</title></head>
<body>
<div class="toolbar">
    <h1 class="page-title">Quản lý sách <small>(${result.totalItems} cuốn)</small></h1>
    <a class="btn" href="${ctx}/admin/books?action=new">+ Thêm sách</a>
</div>
<div class="table-wrap">
<table class="data-table">
    <thead>
    <tr><th>ID</th><th>Bìa</th><th>Tiêu đề</th><th>ISBN</th><th>Tác giả</th><th>Publisher</th><th>Giá</th><th>SL</th><th>Thao tác</th></tr>
    </thead>
    <tbody>
    <c:forEach items="${result.items}" var="b">
        <tr>
            <td>${b.bookid}</td>
            <td><t:cover src="${b.coverImage}" alt="${b.title}" css="thumb"/></td>
            <td><c:out value="${b.title}"/></td>
            <td>${b.isbn}</td>
            <td><c:out value="${b.authorNames}"/></td>
            <td><c:out value="${b.publisher}"/></td>
            <td>${b.price}</td>
            <td>${b.quantity}</td>
            <td class="actions">
                <a class="btn btn-sm" href="${ctx}/admin/books?action=view&id=${b.bookid}">Xem</a>
                <a class="btn btn-sm btn-warn" href="${ctx}/admin/books?action=edit&id=${b.bookid}">Sửa</a>
                <form method="post" action="${ctx}/admin/books" class="inline-form" data-confirm="Xóa sách &quot;<c:out value='${b.title}'/>&quot;?">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" value="${b.bookid}">
                    <input type="hidden" name="page" value="${result.page}">
                    <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                </form>
            </td>
        </tr>
    </c:forEach>
    <c:if test="${empty result.items}"><tr><td colspan="9" class="empty">Chưa có sách nào.</td></tr></c:if>
    </tbody>
</table>
</div>
<t:pagination current="${result.page}" total="${result.totalPages}" url="${ctx}/admin/books?page="/>
</body>
</html>
