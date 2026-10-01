package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.Review_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang rating (review cua user cho sach). */
public class ReviewDAO_24110317 {

    private Review_24110317 map(ResultSet rs) throws SQLException {
        Review_24110317 r = new Review_24110317();
        r.setUserid(rs.getInt("userid"));
        r.setBookid(rs.getInt("bookid"));
        int rating = rs.getInt("rating");
        r.setRating(rs.wasNull() ? null : rating);
        r.setReviewText(rs.getString("review_text"));
        String name = rs.getString("fullname");
        r.setUserName(name == null || name.isBlank() ? rs.getString("email") : name);
        return r;
    }

    public List<Review_24110317> findByBook(int bookId) {
        String sql = "SELECT r.userid, r.bookid, r.rating, r.review_text, u.fullname, u.email "
                + "FROM rating r JOIN users u ON u.id = r.userid WHERE r.bookid = ? ORDER BY r.userid DESC";
        List<Review_24110317> list = new ArrayList<>();
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi lay review", e);
        }
    }

    /** Khoa chinh (userid, bookid): moi user 1 review/sach -> gui lai se cap nhat review cu (UPDATE truoc, khong co dong nao thi INSERT). */
    public void save(int userId, int bookId, Integer rating, String text) {
        String upd = "UPDATE rating SET rating = ?, review_text = ? WHERE userid = ? AND bookid = ?";
        String ins = "INSERT INTO rating (userid, bookid, rating, review_text) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = c.prepareStatement(upd)) {
                    setRating(ps, 1, rating);
                    ps.setString(2, text);
                    ps.setInt(3, userId);
                    ps.setInt(4, bookId);
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    try (PreparedStatement ps = c.prepareStatement(ins)) {
                        ps.setInt(1, userId);
                        ps.setInt(2, bookId);
                        setRating(ps, 3, rating);
                        ps.setString(4, text);
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi luu review", e);
        }
    }

    private static void setRating(PreparedStatement ps, int idx, Integer rating) throws SQLException {
        if (rating == null) {
            ps.setNull(idx, Types.TINYINT);
        } else {
            ps.setInt(idx, rating);
        }
    }
}
