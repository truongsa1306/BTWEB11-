<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head><title><c:out value="${book.title}"/></title></head>
<body>
<p><a href="${ctx}/home">&laquo; Về trang chủ</a></p>
<table class="detail-table">
    <tr>
        <td class="detail-cover"><t:cover src="${book.coverImage}" alt="${book.title}" css="cover"/></td>
        <td class="detail-info">
            <div><span class="lbl">Tiêu đề:</span> <strong class="book-title"><c:out value="${book.title}"/></strong></div>
            <div><span class="lbl">Mã isbn:</span> ${book.isbn}</div>
            <div><span class="lbl">Tác giả:</span> <c:out value="${book.authorNames}"/></div>
            <div><span class="lbl">Publisher:</span> <c:out value="${book.publisher}"/></div>
            <div><span class="lbl">Publisher_date:</span> <fmt:formatDate value="${book.publishDate}" pattern="dd/MM/yyyy"/></div>
            <div><span class="lbl">Quantity:</span> ${book.quantity}</div>
            <div><span class="lbl">Reviews (${book.reviewCount})</span>
                <c:if test="${not empty book.avgRating}"> &middot; điểm TB <fmt:formatNumber value="${book.avgRating}" maxFractionDigits="1"/>/5</c:if></div>
            <c:if test="${not empty book.description}"><p class="desc"><c:out value="${book.description}"/></p></c:if>
            <c:if test="${empty sessionScope.user or not sessionScope.user.admin}">
                <div class="detail-actions"><t:add-to-cart bookId="${book.bookid}" stock="${book.quantity}" back="/book/detail?id=${book.bookid}" withQty="true"/></div>
            </c:if>
        </td>
    </tr>
</table>

<section class="reviews">
    <h2>Reviews</h2>
    <c:if test="${empty reviews}"><p class="muted">Chưa có review nào. Hãy là người đầu tiên!</p></c:if>
    <c:forEach items="${reviews}" var="r">
        <div class="review">
            <strong><c:out value="${r.userName}"/></strong> <t:stars value="${r.rating}"/>:
            <span><c:out value="${r.reviewText}"/></span>
        </div>
    </c:forEach>

    <h3>Thêm review</h3>
    <c:choose>
        <c:when test="${empty sessionScope.user}">
            <p class="muted">Bạn cần <a href="${ctx}/login">đăng nhập</a> để gửi review.</p>
        </c:when>
        <c:otherwise>
            <form method="post" action="${ctx}/book/detail" class="form review-form">
                <input type="hidden" name="id" value="${book.bookid}">
                <label>Đánh giá
                    <select name="rating">
                        <c:forEach begin="1" end="5" var="i"><option value="${6 - i}" ${(empty param.rating ? '5' : param.rating) == (6 - i) ? 'selected' : ''}>${6 - i} sao</option></c:forEach>
                    </select>
                </label>
                <c:if test="${not empty errors.rating}"><div class="field-error">${errors.rating}</div></c:if>
                <label>Nội dung review
                    <textarea name="review_text" rows="4" maxlength="2000" placeholder="Viết cảm nhận của bạn..."><c:out value="${param.review_text}"/></textarea>
                </label>
                <c:if test="${not empty errors.review_text}"><div class="field-error">${errors.review_text}</div></c:if>
                <button type="submit" class="btn">Submit</button>
                <small class="muted">Mỗi tài khoản chỉ có 1 review cho mỗi cuốn sách; gửi lại sẽ cập nhật review cũ.</small>
            </form>
        </c:otherwise>
    </c:choose>
</section>
</body>
</html>
