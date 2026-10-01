package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.service.CartService_24110317;
import vn.iotstar.service.OrderService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Thanh toan don hang bang COD: GET /checkout (form giao hang), POST /checkout (dat hang). */
@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController_24110317 extends HttpServlet {
    private final CartService_24110317 cartService = new CartService_24110317();
    private final OrderService_24110317 orderService = new OrderService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        if (!prepare(req, resp, user)) {
            return;
        }
        // Dien san thong tin tu tai khoan
        req.setAttribute("form_receiverName", user.getDisplayName().equals(user.getEmail()) ? "" : user.getDisplayName());
        req.setAttribute("form_phone", user.getPhone() == null ? "" : "0" + user.getPhone());
        req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        try {
            int orderId = orderService.placeCodOrder(user, req.getParameter("receiverName"),
                    req.getParameter("phone"), req.getParameter("address"), req.getParameter("note"));
            WebUtil_24110317.setCartCount(req, cartService.countItems(user));
            WebUtil_24110317.flash(req, "success", "Đặt hàng thành công! Bạn thanh toán tiền mặt (COD) khi nhận hàng.");
            resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + orderId); // PRG
        } catch (ValidationException_24110317 e) { // sai thong tin giao hang -> hien lai form
            if (!prepare(req, resp, user)) {
                return;
            }
            req.setAttribute("errors", e.getErrors());
            req.setAttribute("form_receiverName", req.getParameter("receiverName"));
            req.setAttribute("form_phone", req.getParameter("phone"));
            req.setAttribute("form_address", req.getParameter("address"));
            req.setAttribute("form_note", req.getParameter("note"));
            req.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(req, resp);
        } catch (BusinessException_24110317 e) { // gio trong / het hang -> ve gio hang sua
            WebUtil_24110317.flash(req, "error", e.getMessage());
            WebUtil_24110317.setCartCount(req, cartService.countItems(user));
            resp.sendRedirect(req.getContextPath() + "/cart");
        }
    }

    /** Nap gio hang vao request; gio trong hoac co sach khong hop le -> chuyen ve /cart. @return true neu co the tiep tuc. */
    private boolean prepare(HttpServletRequest req, HttpServletResponse resp, User_24110317 user) throws IOException {
        List<CartItem_24110317> items = cartService.getItems(user);
        if (items.isEmpty()) {
            WebUtil_24110317.flash(req, "warning", "Giỏ hàng đang trống, hãy chọn sách trước khi thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return false;
        }
        if (CartService_24110317.hasProblem(items)) {
            WebUtil_24110317.flash(req, "error", "Giỏ hàng có sách hết hàng hoặc vượt số lượng cho phép. Vui lòng điều chỉnh trước khi thanh toán.");
            resp.sendRedirect(req.getContextPath() + "/cart");
            return false;
        }
        req.setAttribute("items", items);
        req.setAttribute("total", CartService_24110317.total(items));
        req.setAttribute("totalQuantity", CartService_24110317.totalQuantity(items));
        return true;
    }
}
