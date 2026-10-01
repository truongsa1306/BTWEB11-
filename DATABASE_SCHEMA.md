# DATABASE_SCHEMA (SQL Server, DB: BookStore) – xem database.sql
Kết nối: jdbc:sqlserver://localhost:1433;databaseName=BookStore;encrypt=true;trustServerCertificate=true | sa/123 | driver mssql-jdbc 12.6.1.jre11. Cần SQL Server 2017+ (STRING_AGG), bật TCP/IP + Mixed Mode.
users(id PK IDENTITY, email UNIQUE varchar(50), fullname nvarchar(50), phone int, passwd varchar(255), signup_date datetime, last_login datetime, is_admin bit, is_active bit*)
author(author_id PK IDENTITY, author_name nvarchar(100), date_of_birth date)
books(bookid PK IDENTITY, isbn int (unique filtered index), title nvarchar(200), publisher nvarchar(100), price decimal(6,2), description nvarchar(max), publish_date date, cover_image varchar(100), quantity int)
book_author(bookid, author_id) PK kép, FK ON DELETE CASCADE
rating(userid, bookid) PK kép, rating tinyint, review_text nvarchar(max), FK CASCADE
otp_codes*(id IDENTITY, user_id FK, otp_code, expires_at, used bit, attempts, created_at)   (* = bổ sung ngoài đề)
Khác đề (có chủ đích): title/publisher/author_name/description/review_text dùng NVARCHAR thay varchar/text để lưu tiếng Việt; passwd 255 (hash PBKDF2).
Tài khoản mẫu: admin@bookstore.com/Admin@123 (ADMIN); an.nguyen@example.com/User@123 (USER, và 6 user khác cùng mật khẩu).

## Bo sung: gio hang & don hang (database_cart_orders.sql)
cart_items(userid, bookid) PK kep, quantity>0, added_at; FK CASCADE
orders(order_id PK IDENTITY, userid FK, receiver_name nvarchar(100), phone varchar(15), address nvarchar(255), note nvarchar(255) NULL, payment_method 'COD', total_amount decimal(10,2), status varchar(20) CHECK in (NEW,CONFIRMED,PREPARING,SHIPPING,DELIVERING,DELIVERED,CANCELLED,RETURNED), created_at, updated_at)
order_items(order_item_id PK IDENTITY, order_id FK CASCADE, bookid FK ON DELETE SET NULL, title nvarchar(200), unit_price decimal(6,2), quantity>0)  -- luu ten + gia luc dat
Don mau: user id=2 (an.nguyen) co 8 don, moi trang thai 1 don; user id=3 co 1 don NEW (de test phan quyen).
