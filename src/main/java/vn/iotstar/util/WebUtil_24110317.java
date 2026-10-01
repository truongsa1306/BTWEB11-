package vn.iotstar.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.model.User_24110317;

/** Cac ham tien ich cho tang Presentation. */
public final class WebUtil_24110317 {
    public static final String SESSION_USER = "user";
    public static final String SESSION_RETURN_URL = "returnUrl";
    public static final String SESSION_CART_COUNT = "cartCount";

    private WebUtil_24110317() {
    }

    public static User_24110317 currentUser(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (User_24110317) s.getAttribute(SESSION_USER);
    }

    /** Luu tong so cuon trong gio vao session de header hien thi (khong truy van DB moi trang). */
    public static void setCartCount(HttpServletRequest req, int count) {
        req.getSession(true).setAttribute(SESSION_CART_COUNT, count);
    }

    public static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }

    /** Thong bao 1 lan (hien o decorator roi xoa). type: success | error | info | warning. */
    public static void flash(HttpServletRequest req, String type, String message) {
        HttpSession s = req.getSession(true);
        s.setAttribute("flashType", type);
        s.setAttribute("flash", message);
    }

    /** Duong dan (context-relative) cua request hien tai, dung de quay lai sau khi dang nhap. */
    public static String currentPath(HttpServletRequest req) {
        String q = req.getQueryString();
        return req.getServletPath() + (q == null ? "" : "?" + q);
    }

    public static boolean isSafeReturnUrl(String url) {
        return url != null && url.startsWith("/") && !url.startsWith("//") && !url.contains("://");
    }
}
