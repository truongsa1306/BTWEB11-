package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.service.CartService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/**
 * Gio hang (vai tro USER, da duoc UserAuthFilter bao ve).
 * GET /cart: xem gio. POST /cart voi action = add | update | remove | clear (xong redirect - PRG).
 */
@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController_24110317 extends HttpServlet {
    private final CartService_24110317 cartService = new CartService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        List<CartItem_24110317> items = cartService.getItems(user);
        req.setAttribute("items", items);
        req.setAttribute("total", CartService_24110317.total(items));
        req.setAttribute("totalQuantity", CartService_24110317.totalQuantity(items));
        req.setAttribute("hasProblem", CartService_24110317.hasProblem(items));
        WebUtil_24110317.setCartCount(req, CartService_24110317.totalQuantity(items)); // dong bo so tren header
        req.getRequestDispatcher("/WEB-INF/views/cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        String action = req.getParameter("action");
        int bookId = WebUtil_24110317.parseInt(req.getParameter("bookid"), 0);
        String back = req.getParameter("back");
        String target = "/cart";
        try {
            if ("add".equals(action)) {
                cartService.add(user, bookId, req.getParameter("quantity"));
                WebUtil_24110317.flash(req, "success", "Đã thêm sách vào giỏ hàng.");
                if (WebUtil_24110317.isSafeReturnUrl(back)) {
                    target = back; // quay lai trang dang xem (chi tiet / san pham / trang chu)
                }
            } else if ("update".equals(action)) {
                cartService.updateQuantity(user, bookId, req.getParameter("quantity"));
                WebUtil_24110317.flash(req, "success", "Đã cập nhật số lượng.");
            } else if ("remove".equals(action)) {
                cartService.remove(user, bookId);
                WebUtil_24110317.flash(req, "success", "Đã xóa sách khỏi giỏ hàng.");
            } else if ("clear".equals(action)) {
                cartService.clear(user);
                WebUtil_24110317.flash(req, "success", "Đã xóa toàn bộ giỏ hàng.");
            } else {
                WebUtil_24110317.flash(req, "error", "Thao tác không hợp lệ.");
            }
        } catch (BusinessException_24110317 e) {
            WebUtil_24110317.flash(req, "error", e.getMessage());
            if ("add".equals(action) && WebUtil_24110317.isSafeReturnUrl(back)) {
                target = back;
            }
        }
        WebUtil_24110317.setCartCount(req, cartService.countItems(user));
        resp.sendRedirect(req.getContextPath() + target);
    }
}
