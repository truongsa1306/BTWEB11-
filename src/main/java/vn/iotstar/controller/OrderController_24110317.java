package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.model.Order_24110317;
import vn.iotstar.model.OrderStatus_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.service.OrderService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/**
 * Don hang cua toi:
 *  GET  /orders?status=&page=  lich su don, loc theo trang thai
 *  GET  /orders/detail?id=     chi tiet 1 don (tien trinh theo trang thai)
 *  POST /orders/cancel         huy don moi (chi trang thai NEW)
 */
@WebServlet(name = "OrderController", urlPatterns = {"/orders", "/orders/detail", "/orders/cancel"})
public class OrderController_24110317 extends HttpServlet {
    private final OrderService_24110317 orderService = new OrderService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User_24110317 user = WebUtil_24110317.currentUser(req);
        if ("/orders/detail".equals(req.getServletPath())) {
            showDetail(req, resp, user);
        } else if ("/orders/cancel".equals(req.getServletPath())) {
            resp.sendRedirect(req.getContextPath() + "/orders"); // huy chi nhan POST
        } else {
            showHistory(req, resp, user);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!"/orders/cancel".equals(req.getServletPath())) {
            resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        User_24110317 user = WebUtil_24110317.currentUser(req);
        int id = WebUtil_24110317.parseInt(req.getParameter("id"), 0);
        try {
            if (orderService.getDetail(user, id) == null) {
                notFound(req, resp);
                return;
            }
            orderService.cancel(user, id);
            WebUtil_24110317.flash(req, "success", "Đã hủy đơn hàng.");
        } catch (BusinessException_24110317 e) {
            WebUtil_24110317.flash(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/orders/detail?id=" + id);
    }

    private void showHistory(HttpServletRequest req, HttpServletResponse resp, User_24110317 user)
            throws ServletException, IOException {
        // status khong hop le / rong / ALL -> hien tat ca
        OrderStatus_24110317 status = OrderStatus_24110317.fromCode(req.getParameter("status"));
        int page = WebUtil_24110317.parseInt(req.getParameter("page"), 1);

        Map<String, Integer> counts = orderService.getStatusCounts(user);
        int all = 0;
        for (int n : counts.values()) {
            all += n;
        }
        req.setAttribute("result", orderService.getHistory(user, status, page));
        req.setAttribute("statuses", OrderStatus_24110317.values());
        req.setAttribute("counts", counts);
        req.setAttribute("allCount", all);
        req.setAttribute("currentStatus", status); // null = tat ca
        req.getRequestDispatcher("/WEB-INF/views/orders.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, User_24110317 user)
            throws ServletException, IOException {
        Order_24110317 order = orderService.getDetail(user, WebUtil_24110317.parseInt(req.getParameter("id"), 0));
        if (order == null) {
            notFound(req, resp);
            return;
        }
        req.setAttribute("order", order);
        req.setAttribute("track", OrderStatus_24110317.track());
        req.getRequestDispatcher("/WEB-INF/views/order-detail.jsp").forward(req, resp);
    }

    private void notFound(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.setAttribute("errorTitle", "404 - Không tìm thấy đơn hàng");
        req.setAttribute("errorMessage", "Đơn hàng không tồn tại hoặc không thuộc tài khoản của bạn.");
        req.getRequestDispatcher("/WEB-INF/views/error/error.jsp").forward(req, resp);
    }
}
