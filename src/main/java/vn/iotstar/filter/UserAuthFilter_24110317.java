package vn.iotstar.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.model.User_24110317;
import vn.iotstar.util.WebUtil_24110317;

/**
 * Bao ve /cart, /checkout, /orders (vai tro USER):
 * chua dang nhap -> /login (nho trang de quay lai); ADMIN -> ve trang quan tri.
 */
public class UserAuthFilter_24110317 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User_24110317 user = WebUtil_24110317.currentUser(req);

        if (user == null) {
            String back;
            if ("GET".equalsIgnoreCase(req.getMethod())) {
                back = WebUtil_24110317.currentPath(req);
            } else { // POST (vd them vao gio) khong the GET lai -> quay ve trang nguoi dung dang xem, hoac /cart
                String b = req.getParameter("back");
                back = WebUtil_24110317.isSafeReturnUrl(b) ? b : "/cart";
            }
            req.getSession(true).setAttribute(WebUtil_24110317.SESSION_RETURN_URL, back);
            WebUtil_24110317.flash(req, "warning", "Vui lòng đăng nhập để sử dụng giỏ hàng và đơn hàng.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (user.isAdmin()) {
            WebUtil_24110317.flash(req, "warning", "Tài khoản quản trị không dùng giỏ hàng / đơn hàng của người dùng.");
            resp.sendRedirect(req.getContextPath() + "/admin/books");
            return;
        }
        resp.setHeader("Cache-Control", "no-store"); // khong cho xem lai sau khi dang xuat
        chain.doFilter(request, response);
    }
}
