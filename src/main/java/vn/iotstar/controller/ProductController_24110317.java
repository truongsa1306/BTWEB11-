package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.service.BookService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Menu "San pham": danh sach phang tat ca sach (luoi the), co phan trang. */
@WebServlet(name = "ProductController", urlPatterns = {"/products"})
public class ProductController_24110317 extends HttpServlet {
    private final BookService_24110317 bookService = new BookService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = WebUtil_24110317.parseInt(req.getParameter("page"), 1);
        req.setAttribute("result", bookService.getBooksPage(page, BookService_24110317.PRODUCT_PAGE_SIZE));
        req.getRequestDispatcher("/WEB-INF/views/products.jsp").forward(req, resp);
    }
}
