package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang cart_items (gio hang luu trong DB theo user). */
public class CartDAO_24110317 {

    /** Cac dong gio hang cua user, kem thong tin sach va ton kho (theo thu tu them vao gio). */
    public List<CartItem_24110317> findByUser(int userId) {
        try (Connection c = DBConnection_24110317.getConnection()) {
            return findByUser(c, userId);
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi lay gio hang", e);
        }
    }

    /** Dung chung 1 ket noi (de OrderDAO doc gio hang ngay trong transaction dat hang). */
    List<CartItem_24110317> findByUser(Connection c, int userId) throws SQLException {
        String sql = "SELECT b.bookid, b.title, b.cover_image, b.price, b.quantity AS stock, ci.quantity AS qty "
                + "FROM cart_items ci JOIN books b ON b.bookid = ci.bookid "
                + "WHERE ci.userid = ? ORDER BY ci.added_at, b.bookid";
        List<CartItem_24110317> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem_24110317 it = new CartItem_24110317();
                    it.setBookid(rs.getInt("bookid"));
                    it.setTitle(rs.getString("title"));
                    it.setCoverImage(rs.getString("cover_image"));
                    it.setPrice(rs.getBigDecimal("price"));
                    it.setStock(rs.getInt("stock")); // NULL -> 0
                    it.setQuantity(rs.getInt("qty"));
                    list.add(it);
                }
            }
        }
        return list;
    }

    /** So luong cua 1 sach trong gio (0 neu chua co). */
    public int getQuantity(int userId, int bookId) {
        String sql = "SELECT quantity FROM cart_items WHERE userid = ? AND bookid = ?";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi doc so luong gio hang", e);
        }
    }

    /** Dat so luong cho 1 sach trong gio: UPDATE truoc, khong co dong nao thi INSERT (cung transaction). */
    public void setQuantity(int userId, int bookId, int quantity) {
        String upd = "UPDATE cart_items SET quantity = ? WHERE userid = ? AND bookid = ?";
        String ins = "INSERT INTO cart_items (userid, bookid, quantity) VALUES (?, ?, ?)";
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    ps.setInt(1, quantity);
                    ps.setInt(2, userId);
                    ps.setInt(3, bookId);
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    try (PreparedStatement ps = c.prepareStatement(ins)) {
                        ps.setInt(1, userId);
                        ps.setInt(2, bookId);
                        ps.setInt(3, quantity);
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi luu gio hang", e);
        }
    }

    public boolean remove(int userId, int bookId) {
        return execute("DELETE FROM cart_items WHERE userid = ? AND bookid = ?", userId, bookId) > 0;
    }

    public void clear(int userId) {
        execute("DELETE FROM cart_items WHERE userid = ?", userId);
    }

    /** Tong so cuon trong gio (hien o header). */
    public int countItems(int userId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE userid = ?";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi dem gio hang", e);
        }
    }

    private int execute(String sql, int... params) {
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi cap nhat gio hang", e);
        }
    }
}
