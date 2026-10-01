package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.service.UserService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Cau 2: nhap OTP de kich hoat tai khoan (action=verify) hoac gui lai OTP (action=resend). */
@WebServlet(name = "VerifyOtpController", urlPatterns = {"/verify-otp"})
public class VerifyOtpController_24110317 extends HttpServlet {
    private final UserService_24110317 userService = new UserService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null || email.isBlank()) {
            HttpSession s = req.getSession(false);
            email = s == null ? "" : (String) s.getAttribute("otpEmail");
        }
        req.setAttribute("email", email == null ? "" : email);
        req.setAttribute("otpMinutes", UserService_24110317.OTP_MINUTES);
        req.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String action = req.getParameter("action");
        try {
            if ("resend".equals(action)) {
                boolean sent = userService.resendOtp(email);
                WebUtil_24110317.flash(req, sent ? "success" : "warning", sent
                        ? "Đã gửi lại mã OTP. Vui lòng kiểm tra email."
                        : "Không gửi được email. Mã OTP mới đã được ghi ra console của server.");
                req.getSession(true).setAttribute("otpEmail", email.trim().toLowerCase());
                resp.sendRedirect(req.getContextPath() + "/verify-otp");
                return;
            }
            userService.verifyOtp(email, req.getParameter("otp"));
            HttpSession s = req.getSession(false);
            if (s != null) {
                s.removeAttribute("otpEmail");
            }
            WebUtil_24110317.flash(req, "success", "Kích hoạt tài khoản thành công. Bạn có thể đăng nhập.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (BusinessException_24110317 e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("email", email == null ? "" : email);
            req.setAttribute("otpMinutes", UserService_24110317.OTP_MINUTES);
            req.getRequestDispatcher("/WEB-INF/views/verify-otp.jsp").forward(req, resp);
        }
    }
}
