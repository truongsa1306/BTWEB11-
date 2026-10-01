<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="current" type="java.lang.Integer" required="true" %>
<%@ attribute name="total" type="java.lang.Integer" required="true" %>
<%@ attribute name="url" required="true" description="Tien to URL ket thuc bang page= (vd: /KTQT/home?page=)" %>
<c:if test="${total > 0}">
    <%-- Cua so toi da 10 so trang quanh trang hien tai --%>
    <c:set var="wEnd" value="${current + 4 > total ? total : current + 4}"/>
    <c:set var="wStart" value="${wEnd - 9 < 1 ? 1 : wEnd - 9}"/>
    <c:set var="wEnd" value="${wStart + 9 > total ? total : wStart + 9}"/>
    <nav class="pagination" aria-label="Phân trang">
        <c:choose>
            <c:when test="${current > 1}"><a href="<c:out value='${url}${current - 1}'/>">&laquo; Trang trước</a></c:when>
            <c:otherwise><span class="disabled">&laquo; Trang trước</span></c:otherwise>
        </c:choose>
        <span class="sep">&ndash;</span>
        <c:forEach begin="${wStart}" end="${wEnd}" var="i">
            <c:choose>
                <c:when test="${i == current}"><span class="current">${i}</span></c:when>
                <c:otherwise><a href="<c:out value='${url}${i}'/>">${i}</a></c:otherwise>
            </c:choose>
        </c:forEach>
        <span class="sep">&ndash;</span>
        <c:choose>
            <c:when test="${current < total}"><a href="<c:out value='${url}${current + 1}'/>">Trang sau &raquo;</a></c:when>
            <c:otherwise><span class="disabled">Trang sau &raquo;</span></c:otherwise>
        </c:choose>
    </nav>
</c:if>
