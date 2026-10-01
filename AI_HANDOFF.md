# AI_HANDOFF
CURRENT_PHASE: Claude 3 - MIGRATION MySQL -> SQL Server ĐÃ VIẾT XONG, CHƯA CHẠY THỬ (chờ mvn + SQL Server thật).
COMPLETED: Maven config, DB script, JDBC, DAO, Service, Controller, JSP, Sitemesh, Auth+OTP, Home/Detail/Review, Admin CRUD, validation. Đối chiếu toàn bộ 32 class/interface bắt buộc (_24110317 suffix) - ĐỦ, không thiếu, không trùng.
FIXED: _footer.jspf - thay placeholder "[Điền họ tên sinh viên]" bằng "Nguyễn Văn Trường Sa" (footer nay hiển thị đúng Họ tên/MSSV/Mã đề).
IN_PROGRESS: -
NEXT: chạy `mvn clean package` trên máy thật có Internet (sandbox audit này bị chặn Maven Central - xem BUILD_STATUS); test đăng nhập/OTP/CRUD trên MySQL thật; cấu hình SMTP thật trong mail.properties nếu cần gửi mail thật (hiện dùng mail.log-otp=true để in OTP ra console, phù hợp lúc chưa có credential thật).
BUILD_STATUS: NOT_TESTED (môi trường sandbox audit chặn repo.maven.apache.org - x-deny-reason: host_not_allowed, không phải lỗi source). Đã review thủ công toàn bộ 32 file .java: brace/paren cân bằng, import/package đúng, logic controller->service->dao->jdbc nhất quán, không phát hiện lỗi cú pháp hoặc logic rõ ràng. Cần chạy `mvn clean package` thật trên máy có mạng để xác nhận PASS/FAIL cuối cùng.
MYSQL_STATUS: PASS (cấu hình) - db.properties/DBConnection_24110317 dùng đúng com.mysql.cj.jdbc.Driver, jdbc:mysql://localhost:3306/BookStore..., root/root; database.sql là cú pháp MySQL 8.x thuần (InnoDB, AUTO_INCREMENT, GROUP_CONCAT), không có cú pháp SQL Server. Chưa kết nối MySQL thật trong sandbox này để test runtime (không có MySQL server chạy sẵn).
SMTP_STATUS: NOT_CONFIGURED (như cũ) - mail.properties vẫn là placeholder (your-email@gmail.com/your-app-password), mail.log-otp=true nên OTP vẫn hoạt động qua console. Không tự ý điền credential thật vào source theo đúng chỉ dẫn.
KNOWN: chưa có CSRF token (ghi nhận, không tự triển khai theo yêu cầu); Sitemesh/JSTL trước đó test bằng bản build từ source trên môi trường khác.

--- CLAUDE 3 (takeover audit) ---
ZIP2: KHÔNG được cung cấp (chỉ có KTQT_final.zip + Đề_thi PDF).
DATABASE_STATUS: giữ MySQL (BookStore, root/root, connector-j 8.4.0). KHÔNG migration. SQL Server sa/123 chỉ là thông tin môi trường.
DB_EVIDENCE_TỪ_ĐỀ: các ảnh trong đề chụp từ SSMS (SQL Server, tên bảng dbo.books/dbo.users..., kiểu text/tinyint/bit/decimal(6,2)). Đề KHÔNG ghi chữ "phải dùng SQL Server". Cần sinh viên xác nhận DB phòng thi/giảng viên yêu cầu.
MYSQL_ONLY_SQL: BookDAO (GROUP_CONCAT ... SEPARATOR, LIMIT ? OFFSET ? x2), OtpDAO (ORDER BY id DESC LIMIT 1), UserDAO (NOW() x2), database.sql (AUTO_INCREMENT, ENGINE, utf8mb4, b'0', COMMENT, ON DELETE CASCADE ok). Generated keys (RETURN_GENERATED_KEYS) dùng ở BookDAO.insert, UserDAO.insert - cần test lại nếu đổi DB.
BUILD_STATUS: NOT_TESTED (sandbox Claude 3 cũng không có mvn/javac/mạng). Không kết luận lỗi source.
TEST_STATUS: chưa chạy runtime.
KNOWN_ISSUES: (1) _header.jspf in ${sessionScope.user.role} không qua c:out (giá trị enum, an toàn, chỉ lệch convention); (2) Đề Câu 2 đọc theo nghĩa đen "ngược lại quay lại đăng nhập" có thể nghĩa ADMIN cũng về /login - project cho ADMIN -> /admin/books (giữ nguyên theo chỉ dẫn); (3) Home: 1 tác giả/trang + tối đa 3 sách (giả định trong OPEN_QUESTIONS).
NEXT: sinh viên xác nhận DB -> nếu SQL Server: migration theo kế hoạch (pom driver mssql-jdbc, db.properties, DBConnection, BookDAO/OtpDAO/UserDAO, database.sql T-SQL, IDENTITY, OFFSET/FETCH, STRING_AGG, GETDATE(), BIT). Sau đó mvn clean package + test.

--- CLAUDE 3 (migration SQL Server, theo xác nhận của sinh viên: "db sẽ là sqlserver") ---
DATABASE_STATUS: SQL Server (BookStore, sa/123, mssql-jdbc 12.6.1.jre11). CHƯA test runtime.
COMPLETED: pom.xml (mssql-jdbc), db.properties, database.sql viết lại T-SQL (IDENTITY, IDENTITY_INSERT, N'' literal, BIT 0/1, GETDATE, filtered unique index isbn, DROP TABLE IF EXISTS), BookDAO (STRING_AGG WITHIN GROUP, OFFSET ? ROWS FETCH NEXT ? ROWS ONLY - THỨ TỰ THAM SỐ offset,limit; AVG(CAST(rating AS FLOAT)) vì AVG(tinyint) trả int), OtpDAO (TOP 1), UserDAO (GETDATE x2), ReviewDAO (bỏ ON DUPLICATE KEY UPDATE -> UPDATE rồi INSERT trong transaction). DBConnection không cần sửa (đọc driver từ db.properties).
ASSUMPTIONS: databaseName=BookStore (đề chỉ hiện "...Store"), instance mặc định cổng 1433, SQL Server >= 2017.
NEXT: (1) chạy database.sql trong SSMS; (2) bật TCP/IP + Mixed Mode nếu kết nối lỗi; (3) mvn clean package; (4) test: đăng ký/OTP, login, Home phân trang (offset/limit), chi tiết sách + review (thêm mới & gửi lại), CRUD (insert lấy generated key, update, delete cascade).
