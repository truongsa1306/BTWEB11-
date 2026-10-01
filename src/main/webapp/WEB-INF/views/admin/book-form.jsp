<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="isEdit" value="${not empty form.bookid and form.bookid != '0'}"/>
<html>
<head><title>${isEdit ? 'Sửa sách' : 'Thêm sách'}</title></head>
<body>
<h1 class="page-title">${isEdit ? 'Cập nhật sách' : 'Thêm sách mới'}</h1>
<form method="post" action="${ctx}/admin/books" class="form form-wide" novalidate>
    <input type="hidden" name="action" value="save">
    <input type="hidden" name="bookid" value="${isEdit ? form.bookid : 0}">

    <label>Tiêu đề <input type="text" name="title" maxlength="200" value="<c:out value='${form.title}'/>"></label>
    <c:if test="${not empty errors.title}"><div class="field-error">${errors.title}</div></c:if>

    <label>Mã ISBN (số nguyên) <input type="text" name="isbn" inputmode="numeric" maxlength="10" value="<c:out value='${form.isbn}'/>"></label>
    <c:if test="${not empty errors.isbn}"><div class="field-error">${errors.isbn}</div></c:if>

    <fieldset class="author-select">
        <legend>Tác giả (chọn 1 hoặc nhiều)</legend>
        <c:forEach items="${authors}" var="a">
            <label class="check"><input type="checkbox" name="authorIds" value="${a.authorId}" ${selectedAuthorIds.contains(a.authorId) ? 'checked' : ''}> <c:out value="${a.authorName}"/></label>
        </c:forEach>
    </fieldset>
    <c:if test="${not empty errors.authorIds}"><div class="field-error">${errors.authorIds}</div></c:if>

    <label>Publisher <input type="text" name="publisher" maxlength="100" value="<c:out value='${form.publisher}'/>"></label>
    <c:if test="${not empty errors.publisher}"><div class="field-error">${errors.publisher}</div></c:if>

    <div class="row-3">
        <div>
            <label>Publisher_date <input type="date" name="publishDate" value="<c:out value='${form.publishDate}'/>"></label>
            <c:if test="${not empty errors.publishDate}"><div class="field-error">${errors.publishDate}</div></c:if>
        </div>
        <div>
            <label>Giá (0 - 9999.99) <input type="text" name="price" inputmode="decimal" value="<c:out value='${form.price}'/>"></label>
            <c:if test="${not empty errors.price}"><div class="field-error">${errors.price}</div></c:if>
        </div>
        <div>
            <label>Quantity <input type="text" name="quantity" inputmode="numeric" value="<c:out value='${form.quantity}'/>"></label>
            <c:if test="${not empty errors.quantity}"><div class="field-error">${errors.quantity}</div></c:if>
        </div>
    </div>

    <label>Cover image (tên file trong <code>assets/covers</code> hoặc URL http/https)
        <input type="text" name="coverImage" id="coverImage" maxlength="100" value="<c:out value='${form.coverImage}'/>">
    </label>
    <c:if test="${not empty errors.coverImage}"><div class="field-error">${errors.coverImage}</div></c:if>
    <div class="cover-preview" id="coverPreview" data-base="${ctx}/assets/covers/"></div>

    <label>Mô tả <textarea name="description" rows="5"><c:out value="${form.description}"/></textarea></label>

    <div class="form-actions">
        <button type="submit" class="btn">${isEdit ? 'Lưu thay đổi' : 'Thêm sách'}</button>
        <a class="btn btn-secondary" href="${ctx}/admin/books">Hủy</a>
    </div>
</form>
</body>
</html>
