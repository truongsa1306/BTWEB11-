package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang users bang JDBC + PreparedStatement. */
public class UserDAO_24110317 {
    private static final String COLS =
            "id, email, fullname, phone, passwd, signup_date, last_login, is_admin, is_active";

    private User_24110317 map(ResultSet rs) throws SQLException {
        User_24110317 u = new User_24110317();
        u.setId(rs.getInt("id"));
        u.setEmail(rs.getString("email"));
        u.setFullname(rs.getString("fullname"));
        int phone = rs.getInt("phone");
        u.setPhone(rs.wasNull() ? null : phone);
        u.setPasswd(rs.getString("passwd"));
        u.setSignupDate(rs.getTimestamp("signup_date"));
        u.setLastLogin(rs.getTimestamp("last_login"));
        u.setAdmin(rs.getBoolean("is_admin"));
        u.setActive(rs.getBoolean("is_active"));
        return u;
    }

    public User_24110317 findByEmail(String email) {
        String sql = "SELECT " + COLS + " FROM users WHERE email = ?";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi tim user theo email", e);
        }
    }

    public User_24110317 findById(int id) {
        String sql = "SELECT " + COLS + " FROM users WHERE id = ?";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi tim user theo id", e);
        }
    }

    /** Them user moi (chua kich hoat, role USER). @return id tu tang. */
    public int insert(User_24110317 u) {
        String sql = "INSERT INTO users (email, fullname, phone, passwd, signup_date, is_admin, is_active) "
                + "VALUES (?, ?, ?, ?, GETDATE(), 0, 0)";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getFullname());
            setNullableInt(ps, 3, u.getPhone());
            ps.setString(4, u.getPasswd());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi them user", e);
        }
    }

    /** Cap nhat thong tin cua tai khoan dang cho kich hoat (dang ky lai cung email). */
    public void updatePending(int id, String fullname, Integer phone, String passwd) {
        String sql = "UPDATE users SET fullname = ?, phone = ?, passwd = ? WHERE id = ? AND is_active = 0";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullname);
            setNullableInt(ps, 2, phone);
            ps.setString(3, passwd);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi cap nhat user cho kich hoat", e);
        }
    }

    public void activate(int id) {
        exec("UPDATE users SET is_active = 1 WHERE id = ?", id, "Loi kich hoat user");
    }

    public void updateLastLogin(int id) {
        exec("UPDATE users SET last_login = GETDATE() WHERE id = ?", id, "Loi cap nhat last_login");
    }

    private void exec(String sql, int id, String msg) {
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException_24110317(msg, e);
        }
    }

    private static void setNullableInt(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) {
            ps.setNull(idx, Types.INTEGER);
        } else {
            ps.setInt(idx, v);
        }
    }
}
