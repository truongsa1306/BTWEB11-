<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title>Trang chủ</title></head>
<body>
<h1 class="page-title">Sách theo tác giả</h1>
<c:choose>
    <c:when test="${empty homePage.author}">
        <p class="empty">Chưa có sách nào trong cơ sở dữ liệu.</p>
    </c:when>
    <c:otherwise>
        <c:set var="books" value="${homePage.books}"/>
        <c:set var="missing" value="${3 - fn:length(books)}"/>
        <table class="book-table">
            <tr><th colspan="3" class="author-row">Tác giả : <c:out value="${homePage.author.authorName}"/></th></tr>
            <tr class="cover-row">
                <c:forEach items="${books}" var="b">
                    <td><a href="${ctx}/book/detail?id=${b.bookid}"><t:cover src="${b.coverImage}" alt="${b.title}" css="cover"/></a></td>
                </c:forEach>
                <c:forEach begin="1" end="${missing}"><td></td></c:forEach>
            </tr>
            <tr class="info-row">
                <c:forEach items="${books}" var="b">
                    <td>
                        <div><span class="lbl">Tiêu đề:</span> <a class="book-title" href="${ctx}/book/detail?id=${b.bookid}"><c:out value="${b.title}"/></a></div>
                        <div><span class="lbl">Mã isbn:</span> ${b.isbn}</div>
                        <div><span class="lbl">Tác giả:</span> <c:out value="${b.authorNames}"/></div>
                        <div><span class="lbl">Publisher:</span> <c:out value="${b.publisher}"/></div>
                        <div><span class="lbl">Publisher_date:</span> <fmt:formatDate value="${b.publishDate}" pattern="dd/MM/yyyy"/></div>
                        <div><span class="lbl">Quantity:</span> ${b.quantity}</div>
                        <div><span class="lbl">Review (${b.reviewCount})</span></div>
                        <c:if test="${empty sessionScope.user or not sessionScope.user.admin}">
                            <div class="home-cart"><t:add-to-cart bookId="${b.bookid}" stock="${b.quantity}" back="/home?page=${homePage.page}"/></div>
                        </c:if>
                    </td>
                </c:forEach>
                <c:forEach begin="1" end="${missing}"><td></td></c:forEach>
            </tr>
            <tr><td colspan="3" class="pager-row"><t:pagination current="${homePage.page}" total="${homePage.totalPages}" url="${ctx}/home?page="/></td></tr>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
