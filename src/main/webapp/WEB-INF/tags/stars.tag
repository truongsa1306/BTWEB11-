<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="value" type="java.lang.Integer" required="false" %>
<c:if test="${not empty value}"><span class="stars" title="${value}/5"><c:forEach begin="1" end="5" var="i">${i <= value ? '★' : '☆'}</c:forEach></span></c:if>
