package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.Book_24110317;
import vn.iotstar.service.BookService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/**
 * Cau 6: CRUD Books (chi ADMIN - duoc AuthFilter bao ve).
 *  GET  /admin/books?action=list|new|edit|view  (mac dinh list, co phan trang)
 *  POST /admin/books  action=save (them/sua) | delete
 */
@WebServlet(name = "AdminBookController", urlPatterns = {"/admin", "/admin/books"})
public class AdminBookController_24110317 extends HttpServlet {
    private static final String VIEW = "/WEB-INF/views/admin/";
    private final BookService_24110317 bookService = new BookService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }
        switch (action) {
            case "new":
                showForm(req, resp, new HashMap<>(defaults()), List.of());
                break;
            case "edit":
            case "view": {
                Book_24110317 book = bookService.getById(WebUtil_24110317.parseInt(req.getParameter("id"), 0));
                if (book == null) {
                    WebUtil_24110317.flash(req, "error", "Sách không tồn tại.");
                    resp.sendRedirect(req.getContextPath() + "/admin/books");
                    return;
                }
                if ("view".equals(action)) {
                    req.setAttribute("book", book);
                    req.getRequestDispatcher(VIEW + "book-view.jsp").forward(req, resp);
                } else {
                    showForm(req, resp, formFromBook(book), book.getAuthorIds());
                }
                break;
            }
            default:
                req.setAttribute("result", bookService.getBooksPage(
                        WebUtil_24110317.parseInt(req.getParameter("page"), 1), BookService_24110317.ADMIN_PAGE_SIZE));
                req.getRequestDispatcher(VIEW + "book-list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        String ctx = req.getContextPath();
        try {
            if ("delete".equals(action)) {
                bookService.delete(WebUtil_24110317.parseInt(req.getParameter("id"), 0));
                WebUtil_24110317.flash(req, "success", "Đã xóa sách.");
                resp.sendRedirect(ctx + "/admin/books?page=" + WebUtil_24110317.parseInt(req.getParameter("page"), 1));
                return;
            }
            // action = save
            Book_24110317 book = bookService.parseAndValidate(req.getParameterMap());
            if (book.getBookid() > 0) {
                bookService.update(book);
                WebUtil_24110317.flash(req, "success", "Đã cập nhật sách.");
            } else {
                bookService.create(book);
                WebUtil_24110317.flash(req, "success", "Đã thêm sách mới.");
            }
            resp.sendRedirect(ctx + "/admin/books");
        } catch (ValidationException_24110317 e) {
            req.setAttribute("errors", e.getErrors());
            showForm(req, resp, formFromParams(req), BookService_24110317.parseAuthorIds(req.getParameterValues("authorIds")));
        } catch (BusinessException_24110317 e) {
            WebUtil_24110317.flash(req, "error", e.getMessage());
            resp.sendRedirect(ctx + "/admin/books");
        }
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, Map<String, String> form, List<Integer> selected)
            throws ServletException, IOException {
        req.setAttribute("form", form);
        req.setAttribute("selectedAuthorIds", selected);
        req.setAttribute("authors", bookService.getAllAuthors());
        req.getRequestDispatcher(VIEW + "book-form.jsp").forward(req, resp);
    }

    private Map<String, String> defaults() {
        Map<String, String> m = new HashMap<>();
        m.put("quantity", "0");
        m.put("price", "0.00");
        return m;
    }

    private Map<String, String> formFromBook(Book_24110317 b) {
        Map<String, String> m = new HashMap<>();
        m.put("bookid", String.valueOf(b.getBookid()));
        m.put("isbn", b.getIsbn() == null ? "" : String.valueOf(b.getIsbn()));
        m.put("title", nz(b.getTitle()));
        m.put("publisher", nz(b.getPublisher()));
        m.put("price", b.getPrice() == null ? "" : b.getPrice().toPlainString());
        m.put("description", nz(b.getDescription()));
        m.put("publishDate", b.getPublishDate() == null ? "" : b.getPublishDate().toString());
        m.put("coverImage", nz(b.getCoverImage()));
        m.put("quantity", b.getQuantity() == null ? "" : String.valueOf(b.getQuantity()));
        return m;
    }

    private Map<String, String> formFromParams(HttpServletRequest req) {
        Map<String, String> m = new HashMap<>();
        for (String k : new String[]{"bookid", "isbn", "title", "publisher", "price", "description", "publishDate", "coverImage", "quantity"}) {
            m.put(k, nz(req.getParameter(k)));
        }
        return m;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
