package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.Author_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang author. */
public class AuthorDAO_24110317 {

    private Author_24110317 map(ResultSet rs) throws SQLException {
        Author_24110317 a = new Author_24110317();
        a.setAuthorId(rs.getInt("author_id"));
        a.setAuthorName(rs.getString("author_name"));
        a.setDateOfBirth(rs.getDate("date_of_birth"));
        return a;
    }

    public List<Author_24110317> findAll() {
        return query("SELECT author_id, author_name, date_of_birth FROM author ORDER BY author_name, author_id", false);
    }

    /** Cac tac gia co it nhat 1 sach, kem so sach (bookCount). */
    public List<Author_24110317> findAllWithBooks() {
        String sql = "SELECT a.author_id, a.author_name, a.date_of_birth, COUNT(ba.bookid) AS cnt "
                + "FROM author a JOIN book_author ba ON ba.author_id = a.author_id "
                + "GROUP BY a.author_id, a.author_name, a.date_of_birth "
                + "ORDER BY a.author_name, a.author_id";
        return query(sql, true);
    }

    private List<Author_24110317> query(String sql, boolean withCount) {
        List<Author_24110317> list = new ArrayList<>();
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Author_24110317 a = map(rs);
                if (withCount) {
                    a.setBookCount(rs.getInt("cnt"));
                }
                list.add(a);
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi truy van author", e);
        }
    }
}
