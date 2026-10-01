package vn.iotstar.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/** Gui email OTP qua SMTP (Jakarta Mail). Cau hinh trong mail.properties. */
public final class MailUtil_24110317 {
    private static final Properties CFG = new Properties();

    static {
        try (InputStream in = MailUtil_24110317.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (in != null) {
                CFG.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private MailUtil_24110317() {
    }

    private static String cfg(String env, String key) {
        String v = System.getenv(env);
        return (v != null && !v.isBlank()) ? v : CFG.getProperty(key, "");
    }

    /** @return true neu gui mail thanh cong. */
    public static boolean sendOtp(String to, String fullname, String otp, int minutes) {
        if (Boolean.parseBoolean(CFG.getProperty("mail.log-otp", "false"))) {
            System.out.println("[OTP] " + to + " -> " + otp + " (hieu luc " + minutes + " phut)");
        }
        String host = cfg("MAIL_HOST", "mail.host");
        String user = cfg("MAIL_USER", "mail.user");
        String pass = cfg("MAIL_PASSWORD", "mail.password");
        if (host.isBlank()) {
            return false;
        }
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", cfg("MAIL_PORT", "mail.port").isBlank() ? "587" : cfg("MAIL_PORT", "mail.port"));
        props.put("mail.smtp.starttls.enable", cfg("MAIL_STARTTLS", "mail.starttls").isBlank() ? "true" : cfg("MAIL_STARTTLS", "mail.starttls"));
        props.put("mail.smtp.connectiontimeout", "8000");
        props.put("mail.smtp.timeout", "8000");
        props.put("mail.smtp.writetimeout", "8000");
        boolean auth = !user.isBlank();
        props.put("mail.smtp.auth", String.valueOf(auth));
        try {
            Session session = auth
                    ? Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(user, pass);
                        }
                    })
                    : Session.getInstance(props);
            MimeMessage msg = new MimeMessage(session);
            String from = user.isBlank() ? "no-reply@bookstore.local" : user;
            msg.setFrom(new InternetAddress(from, CFG.getProperty("mail.from.name", "BookStore"), "UTF-8"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            msg.setSubject("Mã OTP kích hoạt tài khoản BookStore", "UTF-8");
            msg.setText("Xin chào " + (fullname == null ? "" : fullname) + ",\n\n"
                    + "Mã OTP kích hoạt tài khoản của bạn là: " + otp + "\n"
                    + "Mã có hiệu lực trong " + minutes + " phút. Vui lòng không chia sẻ mã này cho bất kỳ ai.\n\n"
                    + "BookStore", "UTF-8");
            Transport.send(msg);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("[MAIL] Gui OTP that bai: " + e.getMessage());
            return false;
        }
    }
}
