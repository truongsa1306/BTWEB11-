package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.Book_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.service.BookService_24110317;
import vn.iotstar.service.ReviewService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Cau 4: chi tiet sach (GET) va them review (POST) tai /book/detail?id=... */
@WebServlet(name = "BookDetailController", urlPatterns = {"/book/detail"})
public class BookDetailController_24110317 extends HttpServlet {
    private final BookService_24110317 bookService = new BookService_24110317();
    private final ReviewService_24110317 reviewService = new ReviewService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Book_24110317 book = bookService.getById(WebUtil_24110317.parseInt(req.getParameter("id"), 0));
        if (book == null) {
            notFound(req, resp);
            return;
        }
        show(req, resp, book);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = WebUtil_24110317.parseInt(req.getParameter("id"), 0);
        Book_24110317 book = bookService.getById(id);
        if (book == null) {
            notFound(req, resp);
            return;
        }
        User_24110317 user = WebUtil_24110317.currentUser(req);
        if (user == null) { // can dang nhap de review
            req.getSession(true).setAttribute(WebUtil_24110317.SESSION_RETURN_URL, "/book/detail?id=" + id);
            WebUtil_24110317.flash(req, "warning", "Vui lòng đăng nhập để gửi review.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        try {
            reviewService.addReview(user, id, req.getParameter("rating"), req.getParameter("review_text"));
            WebUtil_24110317.flash(req, "success", "Đã gửi review của bạn.");
            resp.sendRedirect(req.getContextPath() + "/book/detail?id=" + id); // PRG: quay lai trang detail
        } catch (ValidationException_24110317 e) {
            req.setAttribute("errors", e.getErrors());
            show(req, resp, book);
        } catch (BusinessException_24110317 e) {
            WebUtil_24110317.flash(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/book/detail?id=" + id);
        }
    }

    private void show(HttpServletRequest req, HttpServletResponse resp, Book_24110317 book)
            throws ServletException, IOException {
        req.setAttribute("book", book);
        req.setAttribute("reviews", reviewService.getReviews(book.getBookid()));
        req.getRequestDispatcher("/WEB-INF/views/book-detail.jsp").forward(req, resp);
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.setAttribute("errorTitle", "404 - Không tìm thấy sách");
        req.setAttribute("errorMessage", "Cuốn sách bạn yêu cầu không tồn tại hoặc đã bị xóa.");
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
    }
}
