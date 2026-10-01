package vn.iotstar.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import vn.iotstar.exception.DataAccessException_24110317;
import vn.iotstar.model.OtpToken_24110317;
import vn.iotstar.util.DBConnection_24110317;

/** Truy van bang otp_codes. */
public class OtpDAO_24110317 {

    /** Vo hieu hoa OTP cu chua dung roi tao OTP moi (1 transaction). */
    public void create(int userId, String code, Timestamp expiresAt) {
        try (Connection c = DBConnection_24110317.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement inv = c.prepareStatement("UPDATE otp_codes SET used = 1 WHERE user_id = ? AND used = 0");
                 PreparedStatement ins = c.prepareStatement(
                         "INSERT INTO otp_codes (user_id, otp_code, expires_at, used, attempts) VALUES (?, ?, ?, 0, 0)")) {
                inv.setInt(1, userId);
                inv.executeUpdate();
                ins.setInt(1, userId);
                ins.setString(2, code);
                ins.setTimestamp(3, expiresAt);
                ins.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi tao OTP", e);
        }
    }

    /** OTP moi nhat chua su dung cua user (hoac null). */
    public OtpToken_24110317 findLatestUnused(int userId) {
        String sql = "SELECT TOP 1 id, user_id, otp_code, expires_at, used, attempts FROM otp_codes "
                + "WHERE user_id = ? AND used = 0 ORDER BY id DESC";
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                OtpToken_24110317 o = new OtpToken_24110317();
                o.setId(rs.getInt("id"));
                o.setUserId(rs.getInt("user_id"));
                o.setCode(rs.getString("otp_code"));
                o.setExpiresAt(rs.getTimestamp("expires_at"));
                o.setUsed(rs.getBoolean("used"));
                o.setAttempts(rs.getInt("attempts"));
                return o;
            }
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi tim OTP", e);
        }
    }

    public void incrementAttempts(int otpId) {
        exec("UPDATE otp_codes SET attempts = attempts + 1 WHERE id = ?", otpId);
    }

    public void markUsed(int otpId) {
        exec("UPDATE otp_codes SET used = 1 WHERE id = ?", otpId);
    }

    private void exec(String sql, int id) {
        try (Connection c = DBConnection_24110317.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException_24110317("Loi cap nhat OTP", e);
        }
    }
}
