<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="status" required="true" type="vn.iotstar.model.OrderStatus_24110317" %>
<c:if test="${not empty status}"><span class="badge badge-${status.cssClass}"><c:out value="${status.label}"/></span></c:if>
