package vn.iotstar.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.service.UserService_24110317;
import vn.iotstar.util.WebUtil_24110317;

/** Cau 2: dang ky -> gui OTP qua mail -> chuyen sang /verify-otp. */
@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController_24110317 extends HttpServlet {
    private final UserService_24110317 userService = new UserService_24110317();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        try {
            boolean mailSent = userService.register(email, req.getParameter("fullname"), req.getParameter("phone"),
                    req.getParameter("password"), req.getParameter("confirm"));
            req.getSession(true).setAttribute("otpEmail", email.trim().toLowerCase());
            if (mailSent) {
                WebUtil_24110317.flash(req, "success", "Đã gửi mã OTP tới " + email.trim() + ". Vui lòng kiểm tra email.");
            } else {
                WebUtil_24110317.flash(req, "warning", "Không gửi được email (kiểm tra cấu hình SMTP trong mail.properties). "
                        + "Mã OTP đã được ghi ra console của server để test.");
            }
            resp.sendRedirect(req.getContextPath() + "/verify-otp");
        } catch (ValidationException_24110317 e) {
            req.setAttribute("errors", e.getErrors());
            req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
        }
    }
}
