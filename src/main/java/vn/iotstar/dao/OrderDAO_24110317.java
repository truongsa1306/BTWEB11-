package vn.iotstar.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.model.Order_24110317;
import vn.iotstar.model.OrderItem_24110317;
import vn.iotstar.model.OrderStatus_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang orders va order_items. */
public class OrderDAO_24110317 {
    private final CartDAO_24110317 cartDAO = new CartDAO_24110317();

    private static final String SELECT_ORDER =
            "SELECT o.order_id, o.userid, o.receiver_name, o.phone, o.address, o.note, o.payment_method, "
            + "o.total_amount, o.status, o.created_at, o.updated_at, "
            + "(SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi WHERE oi.order_id = o.order_id) AS item_count, "
            + "(SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.order_id) AS line_count, "
            + "(SELECT TOP 1 oi.title FROM order_items oi WHERE oi.order_id = o.order_id ORDER BY oi.order_item_id) AS first_title "
            + "FROM orders o ";

    private Order_24110317 map(ResultSet rs) throws SQLException {
        Order_24110317 o = new Order_24110317();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("userid"));
        o.setReceiverName(rs.getString("receiver_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setNote(rs.getString("note"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(OrderStatus_24110317.fromCode(rs.getString("status")));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        o.setItemCount(rs.getInt("item_count"));
        o.setLineCount(rs.getInt("line_count"));
        o.setFirstTitle(rs.getString("first_title"));
        return o;
    }

    /**
     * Dat hang COD tu gio hang trong 1 transaction:
     * doc gio -> tao orders -> tao order_items (luu ten + gia luc dat) -> tru kho nguyen tu -> xoa gio.
     * Het hang o buoc tru kho (nguoi khac vua mua) -> rollback toan bo va nem BusinessException.
     * @return order_id vua tao
     */
    public int createFromCart(int userId, String receiverName, String phone, String address, String note) {
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                List<CartItem_24110317> items = cartDAO.findByUser(c, userId);
                if (items.isEmpty()) {
                    throw new BusinessException_24110317("Giỏ hàng đang trống.");
                }
                BigDecimal total = BigDecimal.ZERO;
                for (CartItem_24110317 it : items) {
                    total = total.add(it.getLineTotal());
                }

                int orderId;
                String insOrder = "INSERT INTO orders (userid, receiver_name, phone, address, note, payment_method, total_amount, status) "
                        + "VALUES (?, ?, ?, ?, ?, 'COD', ?, 'NEW')";
                try (PreparedStatement ps = c.prepareStatement(insOrder, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, receiverName);
                    ps.setString(3, phone);
                    ps.setString(4, address);
                    ps.setString(5, note);
                    ps.setBigDecimal(6, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getInt(1);
                    }
                }

                String insItem = "INSERT INTO order_items (order_id, bookid, title, unit_price, quantity) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(insItem)) {
                    for (CartItem_24110317 it : items) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, it.getBookid());
                        ps.setString(3, it.getTitle());
                        ps.setBigDecimal(4, it.getPrice());
                        ps.setInt(5, it.getQuantity());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                // Tru kho co dieu kien (quantity >= can mua) -> khong bao gio am kho du co nhieu nguoi cung dat.
                String dec = "UPDATE books SET quantity = quantity - ? WHERE bookid = ? AND quantity >= ?";
                try (PreparedStatement ps = c.prepareStatement(dec)) {
                    for (CartItem_24110317 it : items) {
                        ps.setInt(1, it.getQuantity());
                        ps.setInt(2, it.getBookid());
                        ps.setInt(3, it.getQuantity());
                        if (ps.executeUpdate() == 0) {
                            throw new BusinessException_24110317("Sách \"" + it.getTitle()
                                    + "\" không còn đủ hàng. Vui lòng kiểm tra lại giỏ hàng.");
                        }
                    }
                }

                try (PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE userid = ?")) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                }
                c.commit();
                return orderId;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi dat hang", e);
        }
    }

    /** Lich su don cua user, loc theo trang thai (status = null -> tat ca), moi nhat truoc. */
    public List<Order_24110317> findByUser(int userId, OrderStatus_24110317 status, int offset, int limit) {
        StringBuilder sql = new StringBuilder(SELECT_ORDER).append("WHERE o.userid = ? ");
        if (status != null) {
            sql.append("AND o.status = ? ");
        }
        sql.append("ORDER BY o.created_at DESC, o.order_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        List<Order_24110317> list = new ArrayList<>();
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            int i = 1;
            ps.setInt(i++, userId);
            if (status != null) {
                ps.setString(i++, status.getCode());
            }
            ps.setInt(i++, offset);
            ps.setInt(i, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi lay lich su don hang", e);
        }
    }

    public int countByUser(int userId, OrderStatus_24110317 status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE userid = ?" + (status != null ? " AND status = ?" : "");
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            if (status != null) {
                ps.setString(2, status.getCode());
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi dem don hang", e);
        }
    }

    /** So don theo tung trang thai (key = ma trang thai) de hien tren cac tab loc. */
    public Map<String, Integer> countGroupedByStatus(int userId) {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) FROM orders WHERE userid = ? GROUP BY status";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderStatus_24110317 s = OrderStatus_24110317.fromCode(rs.getString(1));
                    if (s != null) { // chuan hoa chu hoa/thuong neu sua tay trong DB
                        map.merge(s.getCode(), rs.getInt(2), Integer::sum);
                    }
                }
            }
            return map;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi thong ke don hang", e);
        }
    }

    /** Chi tiet 1 don (kem cac dong san pham). Chi tra ve neu don thuoc ve user (chong xem don nguoi khac). */
    public Order_24110317 findByIdForUser(int orderId, int userId) {
        try (Connection c = DBConnection_24110317.getConnection()) {
            Order_24110317 o;
            try (PreparedStatement ps = c.prepareStatement(SELECT_ORDER + "WHERE o.order_id = ? AND o.userid = ?")) {
                ps.setInt(1, orderId);
                ps.setInt(2, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    o = map(rs);
                }
            }
            String sql = "SELECT oi.order_item_id, oi.order_id, oi.bookid, oi.title, oi.unit_price, oi.quantity, b.cover_image "
                    + "FROM order_items oi LEFT JOIN books b ON b.bookid = oi.bookid "
                    + "WHERE oi.order_id = ? ORDER BY oi.order_item_id";
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        OrderItem_24110317 it = new OrderItem_24110317();
                        it.setId(rs.getInt("order_item_id"));
                        it.setOrderId(rs.getInt("order_id"));
                        int bookid = rs.getInt("bookid");
                        it.setBookid(rs.wasNull() ? null : bookid);
                        it.setTitle(rs.getString("title"));
                        it.setUnitPrice(rs.getBigDecimal("unit_price"));
                        it.setQuantity(rs.getInt("quantity"));
                        it.setCoverImage(rs.getString("cover_image"));
                        o.getItems().add(it);
                    }
                }
            }
            return o;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi lay chi tiet don hang", e);
        }
    }

    /** Khach huy don: chi khi don dang NEW. Hoan lai ton kho trong cung transaction. @return true neu da huy. */
    public boolean cancelNewOrder(int orderId, int userId) {
        String upd = "UPDATE orders SET status = 'CANCELLED', updated_at = GETDATE() "
                + "WHERE order_id = ? AND userid = ? AND status = 'NEW'";
        String restock = "UPDATE b SET b.quantity = b.quantity + oi.quantity "
                + "FROM books b JOIN order_items oi ON oi.bookid = b.bookid WHERE oi.order_id = ?";
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, userId);
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    c.rollback();
                    return false;
                }
                try (PreparedStatement ps = c.prepareStatement(restock)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi huy don hang", e);
        }
    }
}
