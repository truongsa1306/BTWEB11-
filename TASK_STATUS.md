# TASK_STATUS
Test = kiểm thử thủ công bằng curl trên Tomcat 10.1 + MariaDB (bản Sitemesh/JSTL build từ source). `mvn clean package` CHƯA chạy được trong sandbox (chặn Maven Central, x-deny-reason: host_not_allowed) -> cần chạy trên máy thật.
[FINAL AUDIT - Claude 2] Đã đọc toàn bộ 8 tài liệu handoff + scan 32/32 file .java bắt buộc (đủ hậu tố _24110317, không trùng lặp). Đã fix _footer.jspf (họ tên). Đã review thủ công logic Controller/Service/DAO cho Câu 1,2,3,4,6 - khớp REQUIREMENTS.md. database.sql xác nhận thuần MySQL 8.x, KHÔNG chuyển sang SQL Server. `mvn clean package` vẫn không chạy được trong sandbox này (cùng lý do chặn mạng như trước) - cần xác nhận trên máy thật.
| Requirement | Status | File liên quan | Test |
|---|---|---|---|
| Maven Project | DONE (chưa chạy mvn) | pom.xml | javac OK |
| 3 Layer | DONE | controller/service/dao | OK |
| Sitemesh | DONE | SiteMeshFilter_24110317, decorators/ | user/admin decorator OK |
| User/Admin | DONE | AuthFilter_24110317 | user->/admin bị chặn, admin vào được |
| Register | DONE | RegisterController, UserService | validate + tạo user OK |
| OTP | DONE | VerifyOtpController, MailUtil | mail SMTP + kích hoạt OK, OTP sai bị từ chối |
| Login | DONE | LoginController | USER->/home, ADMIN->/admin/books, sai->/login |
| Logout | DONE | LogoutController | session hủy, /admin bị chuyển hướng |
| Home Books | DONE | home.jsp | OK |
| Pagination 3/page | DONE | BookService.getHomePage | 10 trang, 3 sách/trang |
| Book Detail | DONE | book-detail.jsp | OK |
| Review | DONE | BookDetailController | thêm review, review rỗng bị chặn |
| CRUD Books | DONE | AdminBookController, admin/*.jsp | create/edit/delete + validate OK |
| CRUD Pagination | DONE | book-list.jsp | 5/trang, clamp trang OK |

[Claude 3 - Takeover audit] Đọc đủ 8 handoff + pom/db.properties/web.xml/database.sql + DAO/Service/Filter/Login/header/footer. Không sửa source. ZIP2 không có -> không migration. Build/test chưa chạy được (không mvn/javac/mạng).
[Claude 3] Migration SQL Server đã sửa xong source; build/test runtime chưa chạy (sandbox không có mvn/SQL Server).
