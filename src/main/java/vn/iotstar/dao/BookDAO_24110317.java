package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.Book_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang books va book_author. */
public class BookDAO_24110317 {
    private static final String SELECT_BOOK =
            "SELECT b.bookid, b.isbn, b.title, b.publisher, b.price, b.description, b.publish_date, "
            + "b.cover_image, b.quantity, "
            + "(SELECT STRING_AGG(a.author_name, ', ') WITHIN GROUP (ORDER BY a.author_name) FROM book_author ba "
            + "   JOIN author a ON a.author_id = ba.author_id WHERE ba.bookid = b.bookid) AS author_names, "
            + "(SELECT COUNT(*) FROM rating r WHERE r.bookid = b.bookid) AS review_count, "
            + "(SELECT AVG(CAST(r.rating AS FLOAT)) FROM rating r WHERE r.bookid = b.bookid) AS avg_rating "
            + "FROM books b ";

    private Book_24110317 map(ResultSet rs) throws SQLException {
        Book_24110317 b = new Book_24110317();
        b.setBookid(rs.getInt("bookid"));
        int isbn = rs.getInt("isbn");
        b.setIsbn(rs.wasNull() ? null : isbn);
        b.setTitle(rs.getString("title"));
        b.setPublisher(rs.getString("publisher"));
        b.setPrice(rs.getBigDecimal("price"));
        b.setDescription(rs.getString("description"));
        b.setPublishDate(rs.getDate("publish_date"));
        b.setCoverImage(rs.getString("cover_image"));
        int qty = rs.getInt("quantity");
        b.setQuantity(rs.wasNull() ? null : qty);
        b.setAuthorNames(rs.getString("author_names"));
        b.setReviewCount(rs.getInt("review_count"));
        double avg = rs.getDouble("avg_rating");
        b.setAvgRating(rs.wasNull() ? null : avg);
        return b;
    }

    /** Sach cua 1 tac gia, phan trang (offset = (page-1)*limit). */
    public List<Book_24110317> findByAuthor(int authorId, int offset, int limit) {
        String sql = SELECT_BOOK + "JOIN book_author x ON x.bookid = b.bookid WHERE x.author_id = ? "
                + "ORDER BY b.title, b.bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, authorId, offset, limit);
    }

    /** Tat ca sach, phan trang (dung cho CRUD admin va trang San pham). */
    public List<Book_24110317> findPage(int offset, int limit) {
        String sql = SELECT_BOOK + "ORDER BY b.bookid OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        return queryList(sql, offset, limit);
    }

    public int count() {
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT COUNT(*) FROM books");
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi dem sach", e);
        }
    }

    public Book_24110317 findById(int id) {
        try (Connection c = DBConnection_24110317.getConnection()) {
            Book_24110317 b;
            try (PreparedStatement ps = c.prepareStatement(SELECT_BOOK + "WHERE b.bookid = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    b = map(rs);
                }
            }
            try (PreparedStatement ps = c.prepareStatement("SELECT author_id FROM book_author WHERE bookid = ?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        b.getAuthorIds().add(rs.getInt(1));
                    }
                }
            }
            return b;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi tim sach theo id", e);
        }
    }

    /** ISBN da ton tai o sach khac (excludeBookId = 0 khi them moi). */
    public boolean existsIsbn(int isbn, int excludeBookId) {
        String sql = "SELECT 1 FROM books WHERE isbn = ? AND bookid <> ?";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, isbn);
            ps.setInt(2, excludeBookId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi kiem tra ISBN", e);
        }
    }

    /** Them sach + lien ket tac gia trong 1 transaction. @return bookid tu tang. */
    public int insert(Book_24110317 b, List<Integer> authorIds) {
        String sql = "INSERT INTO books (isbn, title, publisher, price, description, publish_date, cover_image, quantity) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bind(ps, b);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        id = keys.getInt(1);
                    }
                }
                insertAuthors(c, id, authorIds);
                c.commit();
                return id;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi them sach", e);
        }
    }

    /** Cap nhat sach + thay the danh sach tac gia trong 1 transaction. */
    public boolean update(Book_24110317 b, List<Integer> authorIds) {
        String sql = "UPDATE books SET isbn = ?, title = ?, publisher = ?, price = ?, description = ?, "
                + "publish_date = ?, cover_image = ?, quantity = ? WHERE bookid = ?";
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try {
                int rows;
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    bind(ps, b);
                    ps.setInt(9, b.getBookid());
                    rows = ps.executeUpdate();
                }
                if (rows == 0) {
                    c.rollback();
                    return false;
                }
                try (PreparedStatement del = c.prepareStatement("DELETE FROM book_author WHERE bookid = ?")) {
                    del.setInt(1, b.getBookid());
                    del.executeUpdate();
                }
                insertAuthors(c, b.getBookid(), authorIds);
                c.commit();
                return true;
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi cap nhat sach", e);
        }
    }

    /** Xoa sach (book_author va rating bi xoa theo nho ON DELETE CASCADE). */
    public boolean delete(int id) {
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE bookid = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi xoa sach", e);
        }
    }

    private List<Book_24110317> queryList(String sql, int... params) {
        List<Book_24110317> list = new ArrayList<>();
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi truy van sach", e);
        }
    }

    private void bind(PreparedStatement ps, Book_24110317 b) throws SQLException {
        if (b.getIsbn() == null) {
            ps.setNull(1, Types.INTEGER);
        } else {
            ps.setInt(1, b.getIsbn());
        }
        ps.setString(2, b.getTitle());
        ps.setString(3, b.getPublisher());
        ps.setBigDecimal(4, b.getPrice());
        ps.setString(5, b.getDescription());
        ps.setDate(6, b.getPublishDate());
        ps.setString(7, b.getCoverImage());
        if (b.getQuantity() == null) {
            ps.setNull(8, Types.INTEGER);
        } else {
            ps.setInt(8, b.getQuantity());
        }
    }

    private void insertAuthors(Connection c, int bookId, List<Integer> authorIds) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO book_author (bookid, author_id) VALUES (?, ?)")) {
            for (Integer authorId : authorIds) {
                ps.setInt(1, bookId);
                ps.setInt(2, authorId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}
