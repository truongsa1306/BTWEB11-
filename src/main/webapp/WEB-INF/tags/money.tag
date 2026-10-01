<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<fmt:setLocale value="en_US" scope="page"/>$<fmt:formatNumber value="${value}" minFractionDigits="2" maxFractionDigits="2"/>
