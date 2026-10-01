# CHANGELOG_AI
- Xóa Main.java mẫu (Java 25 instance main, không dùng).
- Sửa SiteMeshFilter: decorator path tương đối vì Sitemesh tự thêm prefix /WEB-INF/decorators/.
- Thêm jsp-property-group UTF-8 vào web.xml (jspf bị lỗi encoding tiếng Việt).
- [Claude 2 - Final Audit] Sửa WEB-INF/decorators/_footer.jspf: thay placeholder "[Điền họ tên sinh viên]" bằng "Nguyễn Văn Trường Sa" theo yêu cầu đề 02/MSSV 24110317.
- [Claude 2 - Final Audit] Không có thay đổi nào khác về source code Java/SQL sau khi audit toàn bộ - project của Claude 1 đã đúng kiến trúc, đúng MySQL, đúng quy tắc đặt tên _24110317.
- [Claude 3] Chỉ cập nhật tài liệu handoff (AI_HANDOFF, TASK_STATUS, OPEN_QUESTIONS). Không thay đổi source/SQL/pom.
- [Claude 3] Migration MySQL -> SQL Server: pom.xml, db.properties, database.sql (T-SQL), BookDAO, OtpDAO, UserDAO, ReviewDAO (upsert), DATABASE_SCHEMA.md, AI_CONTEXT.md. Phát hiện thêm ngoài danh sách ban đầu: ON DUPLICATE KEY UPDATE (ReviewDAO) và AVG(tinyint) trả int trên SQL Server.
- [Gio hang/COD/Lich su don] Them: bang cart_items, orders, order_items (database.sql + database_cart_orders.sql); model CartItem/Order/OrderItem/OrderStatus; DAO Cart/Order; service Cart/Order; controller Cart/Checkout/Order; UserAuthFilter + AppInitListener; JSP cart/checkout/orders/order-detail; tag add-to-cart/money/status-badge; nut "Them vao gio" o home/products/book-detail; header co Gio hang + Don hang; CSS/JS. Sua: LoginController (dem gio), WebUtil (cartCount), web.xml, mail.properties (bo thong tin dang nhap that), .gitignore.
