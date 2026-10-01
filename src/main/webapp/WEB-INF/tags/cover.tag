<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="src" required="false" %>
<%@ attribute name="alt" required="false" %>
<%@ attribute name="css" required="false" %>
<c:choose>
    <c:when test="${empty src}"><div class="cover-placeholder ${css}">Chưa có ảnh bìa</div></c:when>
    <c:when test="${src.startsWith('http://') or src.startsWith('https://')}"><img class="${css}" src="<c:out value='${src}'/>" alt="<c:out value='${alt}'/>"></c:when>
    <c:otherwise><img class="${css}" src="${pageContext.request.contextPath}/assets/covers/<c:out value='${src}'/>" alt="<c:out value='${alt}'/>"></c:otherwise>
</c:choose>
