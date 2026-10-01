package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.service.BookService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Cau 3: trang Home - sach theo tung tac gia, 3 sach/trang. */
@WebServlet(name = "HomeController", urlPatterns = {"/home"})
public class HomeController_24110317 extends HttpServlet {
    private final BookService_24110317 bookService = new BookService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int page = WebUtil_24110317.parseInt(req.getParameter("page"), 1);
        req.setAttribute("homePage", bookService.getHomePage(page));
        req.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(req, resp);
    }
}
