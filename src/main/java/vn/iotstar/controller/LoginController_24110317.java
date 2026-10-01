package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.service.CartService_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.service.UserService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Cau 2: dang nhap. USER -> /home, ADMIN -> /admin/books, that bai -> quay lai /login. */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController_24110317 extends HttpServlet {
    private final UserService_24110317 userService = new UserService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        if (user != null) { // da dang nhap
            resp.sendRedirect(req.getContextPath() + (user.isAdmin() ? "/admin/books" : "/home"));
            return;
        }
        if ("1".equals(req.getParameter("logout"))) {
            req.setAttribute("info", "Bạn đã đăng xuất.");
        }
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            User_24110317 user = userService.login(email, req.getParameter("password"));

            HttpSession session = req.getSession(true);
            String returnUrl = (String) session.getAttribute(WebUtil_24110317.SESSION_RETURN_URL);
            session.removeAttribute(WebUtil_24110317.SESSION_RETURN_URL);
            req.changeSessionId(); // chong session fixation
            session.setAttribute(WebUtil_24110317.SESSION_USER, user);
            session.setMaxInactiveInterval(30 * 60);
            if (!user.isAdmin()) { // so cuon trong gio hien o header
                int cartCount = 0;
                try {
                    cartCount = new CartService_24110317().countItems(user);
                } catch (DataAccessException_24110317 e) {
                    log("Khong dem duoc gio hang (da chay database_cart_orders.sql chua?)", e);
                }
                session.setAttribute(WebUtil_24110317.SESSION_CART_COUNT, cartCount);
            }

            String target;
            if (user.isAdmin()) {
                target = WebUtil_24110317.isSafeReturnUrl(returnUrl) && returnUrl.startsWith("/admin") ? returnUrl : "/admin/books";
            } else {
                target = WebUtil_24110317.isSafeReturnUrl(returnUrl) && !returnUrl.startsWith("/admin") ? returnUrl : "/home";
            }
            resp.sendRedirect(req.getContextPath() + target);
        } catch (BusinessException_24110317 e) { // that bai -> quay lai trang dang nhap
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email);
            req.setAttribute("notActivated", UserService_24110317.CODE_NOT_ACTIVATED.equals(e.getCode()));
            req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
