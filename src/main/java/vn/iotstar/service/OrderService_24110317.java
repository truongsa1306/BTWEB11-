package vn.iotstar.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.iotstar.dao.CartDAO_24110317;
import vn.iotstar.dao.OrderDAO_24110317;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.model.Order_24110317;
import vn.iotstar.model.OrderStatus_24110317;
import vn.iotstar.model.PageResult_24110317;
import vn.iotstar.model.User_24110317;

/** Nghiep vu don hang: dat hang COD, lich su (loc theo trang thai), chi tiet, huy don moi. */
public class OrderService_24110317 {
    public static final int HISTORY_PAGE_SIZE = 5;

    private final OrderDAO_24110317 orderDAO = new OrderDAO_24110317();
    private final CartDAO_24110317 cartDAO = new CartDAO_24110317();

    /**
     * Validate thong tin giao hang roi dat hang COD tu gio hang.
     * @return order_id
     * @throws ValidationException_24110317 sai thong tin giao hang (field -> message)
     * @throws BusinessException_24110317   gio trong / het hang / vuot gioi han
     */
    public int placeCodOrder(User_24110317 user, String name, String phone, String address, String note) {
        name = trim(name);
        phone = trim(phone).replaceAll("[\\s.\\-]", "");
        address = trim(address);
        note = trim(note);

        Map<String, String> err = new LinkedHashMap<>();
        if (name.isEmpty()) err.put("receiverName", "Vui lòng nhập họ tên người nhận.");
        else if (name.length() > 100) err.put("receiverName", "Họ tên tối đa 100 ký tự.");

        if (phone.isEmpty()) err.put("phone", "Vui lòng nhập số điện thoại.");
        else if (!phone.matches("0\\d{9,10}")) err.put("phone", "Số điện thoại phải bắt đầu bằng 0 và gồm 10-11 chữ số.");

        if (address.isEmpty()) err.put("address", "Vui lòng nhập địa chỉ giao hàng.");
        else if (address.length() < 10) err.put("address", "Địa chỉ quá ngắn (nhập số nhà, đường, phường/xã, quận/huyện, tỉnh/thành).");
        else if (address.length() > 255) err.put("address", "Địa chỉ tối đa 255 ký tự.");

        if (note.length() > 255) err.put("note", "Ghi chú tối đa 255 ký tự.");
        if (!err.isEmpty()) {
            throw new ValidationException_24110317(err);
        }

        // Kiem tra gio hang truoc de bao loi than thien (DAO van kiem tra lai nguyen tu khi tru kho)
        List<CartItem_24110317> items = cartDAO.findByUser(user.getId());
        if (items.isEmpty()) {
            throw new BusinessException_24110317("Giỏ hàng đang trống.");
        }
        for (CartItem_24110317 it : items) {
            int limit = Math.max(0, Math.min(it.getStock(), CartService_24110317.MAX_PER_ITEM));
            if (it.getQuantity() > limit) {
                throw new BusinessException_24110317(limit == 0
                        ? "Sách \"" + it.getTitle() + "\" đã hết hàng. Vui lòng xóa khỏi giỏ."
                        : "Sách \"" + it.getTitle() + "\" chỉ có thể đặt tối đa " + limit + " cuốn. Vui lòng sửa số lượng.");
            }
        }
        return orderDAO.createFromCart(user.getId(), name, phone, address, note.isEmpty() ? null : note);
    }

    /** Lich su don (phan trang) loc theo trang thai (status = null -> tat ca). */
    public PageResult_24110317<Order_24110317> getHistory(User_24110317 user, OrderStatus_24110317 status, int page) {
        int total = orderDAO.countByUser(user.getId(), status);
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) HISTORY_PAGE_SIZE));
        page = Math.max(1, Math.min(page, totalPages));
        List<Order_24110317> items = orderDAO.findByUser(user.getId(), status, (page - 1) * HISTORY_PAGE_SIZE, HISTORY_PAGE_SIZE);
        return new PageResult_24110317<>(items, page, HISTORY_PAGE_SIZE, total);
    }

    public Map<String, Integer> getStatusCounts(User_24110317 user) {
        return orderDAO.countGroupedByStatus(user.getId());
    }

    public Order_24110317 getDetail(User_24110317 user, int orderId) {
        return orderId <= 0 ? null : orderDAO.findByIdForUser(orderId, user.getId());
    }

    public void cancel(User_24110317 user, int orderId) {
        if (!orderDAO.cancelNewOrder(orderId, user.getId())) {
            throw new BusinessException_24110317("Chỉ có thể hủy đơn hàng ở trạng thái \"Đơn hàng mới\".");
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
