package vn.iotstar.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import vn.iotstar.dao.OtpDAO_24110317;
import vn.iotstar.dao.UserDAO_24110317;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.OtpToken_24110317;
import vn.iotstar.model.User_24110317;
import vn.iotstar.util.MailUtil_24110317;
import vn.iotstar.util.PasswordUtil_24110317;

/** Nghiep vu tai khoan: dang ky, OTP, dang nhap. */
public class UserService_24110317 {
    public static final int OTP_MINUTES = 5;
    private static final int OTP_MAX_ATTEMPTS = 5;
    public static final String CODE_NOT_ACTIVATED = "NOT_ACTIVATED";
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserDAO_24110317 userDAO = new UserDAO_24110317();
    private final OtpDAO_24110317 otpDAO = new OtpDAO_24110317();

    /**
     * Dang ky: validate -> luu user (chua kich hoat) -> tao OTP -> gui mail.
     * @return true neu gui mail thanh cong (false: OTP van duoc luu, in ra console).
     */
    public boolean register(String email, String fullname, String phone, String password, String confirm) {
        Map<String, String> err = new LinkedHashMap<>();
        email = email == null ? "" : email.trim().toLowerCase();
        fullname = fullname == null ? "" : fullname.trim();
        phone = phone == null ? "" : phone.trim();
        password = password == null ? "" : password;

        if (email.isEmpty()) {
            err.put("email", "Vui lòng nhập email.");
        } else if (email.length() > 50 || !EMAIL.matcher(email).matches()) {
            err.put("email", "Email không hợp lệ (tối đa 50 ký tự).");
        }
        if (fullname.isEmpty()) {
            err.put("fullname", "Vui lòng nhập họ tên.");
        } else if (fullname.length() > 50) {
            err.put("fullname", "Họ tên tối đa 50 ký tự.");
        }
        Integer phoneVal = null;
        if (!phone.isEmpty()) {
            if (!phone.matches("\\d{9,10}") || Long.parseLong(phone) > Integer.MAX_VALUE) {
                err.put("phone", "Số điện thoại phải gồm 9-10 chữ số.");
            } else {
                phoneVal = (int) Long.parseLong(phone);
            }
        }
        if (password.length() < 6) {
            err.put("password", "Mật khẩu tối thiểu 6 ký tự.");
        }
        if (!password.equals(confirm)) {
            err.put("confirm", "Xác nhận mật khẩu không khớp.");
        }

        User_24110317 existing = err.containsKey("email") ? null : userDAO.findByEmail(email);
        if (existing != null && existing.isActive()) {
            err.put("email", "Email này đã được đăng ký.");
        }
        if (!err.isEmpty()) {
            throw new ValidationException_24110317(err);
        }

        String hash = PasswordUtil_24110317.hash(password);
        int userId;
        if (existing != null) { // da dang ky nhung chua kich hoat -> cap nhat & gui lai OTP
            userDAO.updatePending(existing.getId(), fullname, phoneVal, hash);
            userId = existing.getId();
        } else {
            User_24110317 u = new User_24110317();
            u.setEmail(email);
            u.setFullname(fullname);
            u.setPhone(phoneVal);
            u.setPasswd(hash);
            userId = userDAO.insert(u);
        }
        return issueOtp(userId, email, fullname);
    }

    /** Gui lai OTP cho tai khoan chua kich hoat. */
    public boolean resendOtp(String email) {
        User_24110317 u = findPending(email);
        return issueOtp(u.getId(), u.getEmail(), u.getFullname());
    }

    /** Kiem tra OTP va kich hoat tai khoan. */
    public void verifyOtp(String email, String code) {
        User_24110317 u = findPending(email);
        code = code == null ? "" : code.trim();
        if (!code.matches("\\d{6}")) {
            throw new BusinessException_24110317("Mã OTP gồm 6 chữ số.");
        }
        OtpToken_24110317 otp = otpDAO.findLatestUnused(u.getId());
        if (otp == null) {
            throw new BusinessException_24110317("Không có mã OTP hợp lệ. Vui lòng bấm \"Gửi lại OTP\".");
        }
        if (otp.isExpired()) {
            throw new BusinessException_24110317("Mã OTP đã hết hạn. Vui lòng bấm \"Gửi lại OTP\".");
        }
        if (otp.getAttempts() >= OTP_MAX_ATTEMPTS) {
            throw new BusinessException_24110317("Bạn đã nhập sai quá nhiều lần. Vui lòng bấm \"Gửi lại OTP\".");
        }
        if (!otp.getCode().equals(code)) {
            otpDAO.incrementAttempts(otp.getId());
            throw new BusinessException_24110317("Mã OTP không đúng.");
        }
        otpDAO.markUsed(otp.getId());
        userDAO.activate(u.getId());
    }

    /** Dang nhap. @throws BusinessException_24110317 neu sai thong tin / chua kich hoat. */
    public User_24110317 login(String email, String password) {
        email = email == null ? "" : email.trim().toLowerCase();
        if (email.isEmpty() || password == null || password.isEmpty()) {
            throw new BusinessException_24110317("Vui lòng nhập email và mật khẩu.");
        }
        User_24110317 u = userDAO.findByEmail(email);
        if (u == null || !PasswordUtil_24110317.verify(password, u.getPasswd())) {
            throw new BusinessException_24110317("Email hoặc mật khẩu không đúng.");
        }
        if (!u.isActive()) {
            throw new BusinessException_24110317("Tài khoản chưa được kích hoạt. Vui lòng xác thực OTP.", CODE_NOT_ACTIVATED);
        }
        userDAO.updateLastLogin(u.getId());
        u.setPasswd(null); // khong giu hash trong session
        return u;
    }

    private User_24110317 findPending(String email) {
        email = email == null ? "" : email.trim().toLowerCase();
        User_24110317 u = email.isEmpty() ? null : userDAO.findByEmail(email);
        if (u == null) {
            throw new BusinessException_24110317("Không tìm thấy tài khoản với email này.");
        }
        if (u.isActive()) {
            throw new BusinessException_24110317("Tài khoản đã được kích hoạt, bạn có thể đăng nhập.");
        }
        return u;
    }

    private boolean issueOtp(int userId, String email, String fullname) {
        String code = String.format("%06d", RANDOM.nextInt(1_000_000));
        long exp = System.currentTimeMillis() + OTP_MINUTES * 60_000L;
        otpDAO.create(userId, code, new Timestamp(exp));
        return MailUtil_24110317.sendOtp(email, fullname, code, OTP_MINUTES);
    }
}
