package vn.iotstar.model;

import java.sql.Timestamp;

/** Bang otp_codes - ma OTP kich hoat tai khoan. */
public class OtpToken_24110317 {
    private int id;
    private int userId;
    private String code;
    private Timestamp expiresAt;
    private boolean used;
    private int attempts;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Timestamp getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Timestamp expiresAt) { this.expiresAt = expiresAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isExpired() { return expiresAt.before(new Timestamp(System.currentTimeMillis())); }
}
