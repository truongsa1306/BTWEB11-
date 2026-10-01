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

/** Bao ve /admin/*: chua dang nhap -> /login; khong phai ADMIN -> ve trang chu + thong bao. */
public class AuthFilter_24110317 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        User_24110317 user = WebUtil_24110317.currentUser(req);

        if (user == null) {
            req.getSession(true).setAttribute(WebUtil_24110317.SESSION_RETURN_URL, WebUtil_24110317.currentPath(req));
            WebUtil_24110317.flash(req, "warning", "Vui lòng đăng nhập để tiếp tục.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (!user.isAdmin()) {
            WebUtil_24110317.flash(req, "error", "Bạn không có quyền truy cập trang quản trị.");
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }
        // Trang quan tri khong duoc cache (tranh xem lai sau khi dang xuat)
        resp.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}
