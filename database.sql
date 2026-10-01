-- =====================================================================
-- database.sql - BookStore (Lap trinh Web - De 02 - MSSV 24110317)
-- SQL Server 2017+ (can STRING_AGG). Chay toan bo file trong SSMS hoac: sqlcmd -S localhost -U sa -P 123 -i database.sql
-- Tai khoan mau: admin@bookstore.com / Admin@123 (ADMIN); an.nguyen@example.com / User@123 (USER)
-- Luu y: cac cot chu co dau dung NVARCHAR (de gom varchar/text) de luu dung tieng Viet.
-- =====================================================================
IF DB_ID(N'BookStore') IS NULL
    CREATE DATABASE BookStore;
GO
USE BookStore;
GO
SET QUOTED_IDENTIFIER ON;   -- bat buoc cho filtered index (sqlcmd mac dinh OFF)
SET ANSI_NULLS ON;
GO

DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS otp_codes;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS book_author;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS users;
GO

CREATE TABLE users (
    id          INT IDENTITY(1,1) NOT NULL,
    email       VARCHAR(50)    NOT NULL,
    fullname    NVARCHAR(50)   NULL,
    phone       INT            NULL,
    passwd      VARCHAR(255)   NOT NULL,   -- PBKDF2-HmacSHA256: pbkdf2$iter$salt$hash (de goc: varchar(32))
    signup_date DATETIME       NULL CONSTRAINT df_users_signup DEFAULT GETDATE(),
    last_login  DATETIME       NULL,
    is_admin    BIT            NULL CONSTRAINT df_users_admin DEFAULT 0,
    is_active   BIT            NOT NULL CONSTRAINT df_users_active DEFAULT 0,   -- them: kich hoat bang OTP
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE author (
    author_id     INT IDENTITY(1,1) NOT NULL,
    author_name   NVARCHAR(100) NULL,
    date_of_birth DATE          NULL,
    CONSTRAINT pk_author PRIMARY KEY (author_id)
);

CREATE TABLE books (
    bookid       INT IDENTITY(1,1) NOT NULL,
    isbn         INT            NULL,
    title        NVARCHAR(200)  NULL,
    publisher    NVARCHAR(100)  NULL,
    price        DECIMAL(6,2)   NULL,
    description  NVARCHAR(MAX)  NULL,      -- de goc: text
    publish_date DATE           NULL,
    cover_image  VARCHAR(100)   NULL,
    quantity     INT            NULL,
    CONSTRAINT pk_books PRIMARY KEY (bookid)
);
-- UNIQUE isbn nhung cho phep nhieu NULL
CREATE UNIQUE INDEX uq_books_isbn ON books (isbn) WHERE isbn IS NOT NULL;

CREATE TABLE book_author (
    bookid    INT NOT NULL,
    author_id INT NOT NULL,
    CONSTRAINT pk_book_author PRIMARY KEY (bookid, author_id),
    CONSTRAINT fk_ba_book   FOREIGN KEY (bookid)    REFERENCES books (bookid)     ON DELETE CASCADE,
    CONSTRAINT fk_ba_author FOREIGN KEY (author_id) REFERENCES author (author_id) ON DELETE CASCADE
);

CREATE TABLE rating (
    userid      INT     NOT NULL,
    bookid      INT     NOT NULL,
    rating      TINYINT NULL,
    review_text NVARCHAR(MAX) NULL,        -- de goc: text
    CONSTRAINT pk_rating PRIMARY KEY (userid, bookid),
    CONSTRAINT fk_rating_user FOREIGN KEY (userid) REFERENCES users (id)     ON DELETE CASCADE,
    CONSTRAINT fk_rating_book FOREIGN KEY (bookid) REFERENCES books (bookid) ON DELETE CASCADE
);

-- Bang bo sung (de khong co) phuc vu kich hoat tai khoan bang OTP qua mail
CREATE TABLE otp_codes (
    id         INT IDENTITY(1,1) NOT NULL,
    user_id    INT        NOT NULL,
    otp_code   VARCHAR(6) NOT NULL,
    expires_at DATETIME   NOT NULL,
    used       BIT        NOT NULL CONSTRAINT df_otp_used DEFAULT 0,
    attempts   INT        NOT NULL CONSTRAINT df_otp_attempts DEFAULT 0,
    created_at DATETIME   NOT NULL CONSTRAINT df_otp_created DEFAULT GETDATE(),
    CONSTRAINT pk_otp PRIMARY KEY (id),
    CONSTRAINT fk_otp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE INDEX idx_otp_user ON otp_codes (user_id);
GO

-- ---------- users (mat khau da hash PBKDF2) ----------
SET IDENTITY_INSERT users ON;
INSERT INTO users (id, email, fullname, phone, passwd, signup_date, last_login, is_admin, is_active) VALUES
(1, N'admin@bookstore.com', N'Quản Trị Viên', 900000001, N'pbkdf2$65536$jKxHd+X9hce4J974o/FQ/Q==$jveNgEptRV2U7rIkEGK5K6hmO3/bPSIxwv3cSsG0Ip0=', GETDATE(), NULL, 1, 1),
(2, N'an.nguyen@example.com', N'Nguyễn Văn An', 900000002, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(3, N'binh.tran@example.com', N'Trần Thị Bình', 900000003, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(4, N'cuong.le@example.com', N'Lê Quốc Cường', 900000004, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(5, N'dung.pham@example.com', N'Phạm Thùy Dung', 900000005, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(6, N'em.hoang@example.com', N'Hoàng Minh Em', 900000006, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(7, N'giang.vu@example.com', N'Vũ Hương Giang', 900000007, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1),
(8, N'hai.dang@example.com', N'Đặng Thanh Hải', 900000008, N'pbkdf2$65536$VPJZ1ltabe1kRh1iPaP43Q==$HV3kce9CVwblg3DPckkbBBN6UHuUKC/ec3GEIMNhgx0=', GETDATE(), NULL, 0, 1);
SET IDENTITY_INSERT users OFF;
GO

-- ---------- author ----------
SET IDENTITY_INSERT author ON;
INSERT INTO author (author_id, author_name, date_of_birth) VALUES
(1, N'Nguyễn Nhật Ánh', N'1955-05-07'),
(2, N'Tô Hoài', N'1920-09-27'),
(3, N'Nam Cao', N'1915-10-29'),
(4, N'Paulo Coelho', N'1947-08-24'),
(5, N'J.K. Rowling', N'1965-07-31'),
(6, N'Haruki Murakami', N'1949-01-12');
SET IDENTITY_INSERT author OFF;
GO

-- ---------- books (cover_image = ten file trong src/main/webapp/assets/covers hoac URL day du) ----------
SET IDENTITY_INSERT books ON;
INSERT INTO books (bookid, isbn, title, publisher, price, description, publish_date, cover_image, quantity) VALUES
(1, 970000001, N'Mắt biếc', N'NXB Trẻ', 6.50, N'Câu chuyện tình đơn phương của Ngạn dành cho Hà Lan, gắn với làng Đo Đo thơ mộng.', N'1990-08-01', N'cover01.svg', 40),
(2, 970000002, N'Cho tôi xin một vé đi tuổi thơ', N'NXB Trẻ', 5.90, N'Những ký ức tuổi thơ tinh nghịch, trong trẻo được kể bằng giọng văn dí dỏm.', N'2008-12-01', N'cover02.svg', 35),
(3, 970000003, N'Tôi thấy hoa vàng trên cỏ xanh', N'NXB Trẻ', 7.20, N'Tuổi thơ nghèo khó ở vùng quê Phú Yên qua đôi mắt của cậu bé Thiều.', N'2010-12-09', N'cover03.svg', 52),
(4, 970000004, N'Kính vạn hoa', N'NXB Kim Đồng', 4.80, N'Bộ truyện dài về ba cậu bé Quý ròm, Tiểu Long, Hạnh và những cuộc phiêu lưu học trò.', N'1995-06-01', N'cover04.svg', 60),
(5, 970000005, N'Cô gái đến từ hôm qua', N'NXB Trẻ', 5.50, N'Chuyện tình học trò xen lẫn bí ẩn về cô bé Thư đến từ quá khứ.', N'1995-03-15', N'cover05.svg', 28),
(6, 970000006, N'Dế Mèn phiêu lưu ký', N'NXB Kim Đồng', 4.50, N'Cuộc phiêu lưu của chú Dế Mèn qua thế giới loài vật - tác phẩm thiếu nhi kinh điển.', N'1941-05-01', N'cover06.svg', 80),
(7, 970000007, N'Vợ chồng A Phủ', N'NXB Văn học', 3.90, N'Truyện ngắn về cuộc đời Mị và A Phủ ở vùng núi Tây Bắc.', N'1952-01-01', N'cover07.svg', 30),
(8, 970000008, N'Truyện Tây Bắc', N'NXB Văn học', 4.20, N'Tập truyện về con người và cảnh sắc miền núi Tây Bắc.', N'1953-07-01', N'cover08.svg', 25),
(9, 970000009, N'Chí Phèo', N'NXB Văn học', 3.50, N'Bi kịch của người nông dân bị tha hóa trong xã hội cũ.', N'1941-02-01', N'cover09.svg', 45),
(10, 970000010, N'Lão Hạc', N'NXB Văn học', 3.50, N'Truyện ngắn cảm động về người nông dân nghèo và cậu Vàng.', N'1943-01-01', N'cover10.svg', 38),
(11, 970000011, N'Đời thừa', N'NXB Văn học', 3.80, N'Nỗi day dứt của nhà văn Hộ giữa lý tưởng và cơm áo gạo tiền.', N'1943-04-01', N'cover11.svg', 20),
(12, 970000012, N'Sống mòn', N'NXB Văn học', 4.60, N'Cuộc sống mòn mỏi của giới viên chức nghèo trong thời kỳ chiến tranh.', N'1944-06-01', N'cover12.svg', 18),
(13, 970000013, N'Nhà giả kim', N'NXB Văn học', 8.90, N'Hành trình đi tìm kho báu của cậu bé chăn cừu Santiago và bài học về theo đuổi giấc mơ.', N'1988-04-15', N'cover13.svg', 70),
(14, 970000014, N'Veronika quyết chết', N'NXB Hội Nhà văn', 7.80, N'Cô gái trẻ chọn cái chết nhưng lại tìm thấy ý nghĩa của sự sống trong bệnh viện tâm thần.', N'1998-09-01', N'cover14.svg', 22),
(15, 970000015, N'Bên bờ sông Piedra tôi ngồi khóc', N'NXB Hội Nhà văn', 8.20, N'Câu chuyện tình yêu và đức tin của Pilar bên dòng sông Piedra.', N'1994-05-01', N'cover15.svg', 26),
(16, 970000016, N'Harry Potter và Hòn đá phù thủy', N'NXB Trẻ', 12.50, N'Cậu bé Harry Potter khám phá thế giới phép thuật tại trường Hogwarts.', N'1997-06-26', N'cover16.svg', 90),
(17, 970000017, N'Harry Potter và Phòng chứa bí mật', N'NXB Trẻ', 12.50, N'Năm học thứ hai đầy hiểm nguy với căn phòng bí mật của Slytherin.', N'1998-07-02', N'cover17.svg', 75),
(18, 970000018, N'Harry Potter và Tù nhân ngục Azkaban', N'NXB Trẻ', 13.00, N'Sirius Black vượt ngục và bí mật về quá khứ của gia đình Potter.', N'1999-07-08', N'cover18.svg', 66),
(19, 970000019, N'Harry Potter và Chiếc cốc lửa', N'NXB Trẻ', 14.50, N'Harry bất ngờ trở thành dũng sĩ thứ tư của Giải đấu tam pháp thuật.', N'2000-07-08', N'cover19.svg', 58),
(20, 970000020, N'Rừng Na Uy', N'NXB Hội Nhà văn', 9.90, N'Nỗi cô đơn và ký ức tuổi trẻ của Toru Watanabe ở Tokyo những năm 1960.', N'1987-09-04', N'cover20.svg', 33),
(21, 970000021, N'Kafka bên bờ biển', N'NXB Hội Nhà văn', 10.90, N'Hai dòng truyện song song đan xen giữa thực và mộng của cậu bé Kafka Tamura.', N'2002-09-12', N'cover21.svg', 0),
(22, 970000022, N'Tuyển tập truyện ngắn Việt Nam', N'NXB Giáo dục', 9.50, N'Tuyển chọn truyện ngắn tiêu biểu của Tô Hoài và Nam Cao dành cho học sinh.', N'2005-08-20', N'cover22.svg', 15);
SET IDENTITY_INSERT books OFF;
GO

-- ---------- book_author ----------
INSERT INTO book_author (bookid, author_id) VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),
(6, 2),
(7, 2),
(8, 2),
(9, 3),
(10, 3),
(11, 3),
(12, 3),
(13, 4),
(14, 4),
(15, 4),
(16, 5),
(17, 5),
(18, 5),
(19, 5),
(20, 6),
(21, 6),
(22, 2),
(22, 3);

-- ---------- rating (review) ----------
INSERT INTO rating (userid, bookid, rating, review_text) VALUES
(8, 1, 5, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(4, 1, 5, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(2, 1, 3, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(8, 2, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(7, 3, 3, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(4, 3, 5, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(3, 3, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(3, 4, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(6, 4, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(8, 4, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(2, 4, 3, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(5, 5, 3, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(7, 5, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(2, 5, 5, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(8, 5, 5, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(3, 5, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(6, 5, 4, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(4, 5, 3, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(4, 6, 3, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(3, 7, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(8, 7, 3, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(4, 7, 4, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(6, 7, 4, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(2, 7, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(5, 7, 5, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(7, 7, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(5, 8, 5, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(3, 9, 3, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(5, 9, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(4, 9, 3, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(2, 10, 5, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(4, 10, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(6, 10, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(5, 10, 3, N'Bản in đẹp, giấy tốt, giao hàng nhanh.'),
(4, 11, 3, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(8, 11, 3, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(2, 11, 4, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(5, 11, 5, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(7, 11, 3, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(3, 12, 4, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(5, 12, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(5, 13, 4, N'Bản in đẹp, giấy tốt, giao hàng nhanh.'),
(8, 13, 3, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(6, 13, 5, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(7, 13, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(4, 13, 5, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(2, 13, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(4, 14, 3, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(7, 14, 5, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(6, 14, 4, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(6, 15, 4, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(8, 15, 5, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(4, 15, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(5, 15, 5, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(2, 15, 5, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(3, 15, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(7, 15, 5, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(5, 16, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(4, 16, 3, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(6, 16, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(3, 16, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(7, 16, 5, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(8, 16, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(4, 17, 4, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(4, 18, 5, N'Bản in đẹp, giấy tốt, giao hàng nhanh.'),
(5, 18, 3, N'Văn phong dễ đọc, mình đã giới thiệu cho nhiều người.'),
(3, 18, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(8, 18, 5, N'Sách hay, đọc cuốn hút từ đầu đến cuối.'),
(2, 19, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(8, 19, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(3, 19, 5, N'Tạm được, kỳ vọng nhiều hơn một chút.'),
(7, 19, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(4, 21, 4, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(3, 21, 5, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(5, 21, 5, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(8, 21, 5, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(6, 21, 4, N'Bản in đẹp, giấy tốt, giao hàng nhanh.'),
(4, 22, 3, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(5, 22, 5, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(2, 22, 5, N'Nội dung sâu sắc, đáng để đọc lại nhiều lần.'),
(3, 22, 5, N'Rất phù hợp làm quà tặng cho bạn bè.'),
(6, 22, 5, N'Cốt truyện ổn nhưng đoạn giữa hơi chậm.'),
(7, 22, 4, N'Đọc xong vẫn còn nhiều cảm xúc, rất đáng tiền.'),
(8, 22, 4, N'Tạm được, kỳ vọng nhiều hơn một chút.');

-- =====================================================================
-- PHAN BO SUNG: GIO HANG
-- (noi dung giong database_cart_orders.sql)
-- =====================================================================
-- ---------- cart_items: gio hang cua user (1 dong / (user, sach)) ----------
IF OBJECT_ID(N'dbo.cart_items', N'U') IS NULL
CREATE TABLE cart_items (
    userid   INT      NOT NULL,
    bookid   INT      NOT NULL,
    quantity INT      NOT NULL CONSTRAINT ck_cart_qty CHECK (quantity > 0),
    added_at DATETIME NOT NULL CONSTRAINT df_cart_added DEFAULT GETDATE(),
    CONSTRAINT pk_cart_items PRIMARY KEY (userid, bookid),
    CONSTRAINT fk_cart_user FOREIGN KEY (userid) REFERENCES users (id)     ON DELETE CASCADE,
    CONSTRAINT fk_cart_book FOREIGN KEY (bookid) REFERENCES books (bookid) ON DELETE CASCADE
);
GO

-- ---------- orders ----------
-- status: NEW | CONFIRMED | PREPARING | SHIPPING | DELIVERING | DELIVERED | CANCELLED | RETURNED
--   NEW        = Don hang moi        CONFIRMED  = Da xac nhan     PREPARING = Chuan bi hang
--   SHIPPING   = Van chuyen          DELIVERING = Giao hang       DELIVERED = Da giao
--   CANCELLED  = Don hang huy        RETURNED   = Don hang hoan
IF OBJECT_ID(N'dbo.orders', N'U') IS NULL
CREATE TABLE orders (
    order_id       INT IDENTITY(1,1) NOT NULL,
    userid         INT            NOT NULL,
    receiver_name  NVARCHAR(100)  NOT NULL,
    phone          VARCHAR(15)    NOT NULL,
    address        NVARCHAR(255)  NOT NULL,
    note           NVARCHAR(255)  NULL,
    payment_method VARCHAR(10)    NOT NULL CONSTRAINT df_orders_pay DEFAULT 'COD',
    total_amount   DECIMAL(10,2)  NOT NULL,
    status         VARCHAR(20)    NOT NULL CONSTRAINT df_orders_status DEFAULT 'NEW',
    created_at     DATETIME       NOT NULL CONSTRAINT df_orders_created DEFAULT GETDATE(),
    updated_at     DATETIME       NOT NULL CONSTRAINT df_orders_updated DEFAULT GETDATE(),
    CONSTRAINT pk_orders PRIMARY KEY (order_id),
    CONSTRAINT fk_orders_user FOREIGN KEY (userid) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_orders_status CHECK (status IN ('NEW','CONFIRMED','PREPARING','SHIPPING','DELIVERING','DELIVERED','CANCELLED','RETURNED')),
    CONSTRAINT ck_orders_payment CHECK (payment_method IN ('COD'))
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_orders_user_status' AND object_id = OBJECT_ID(N'dbo.orders'))
    CREATE INDEX idx_orders_user_status ON orders (userid, status, created_at DESC);
GO

-- ---------- order_items: luu ten + gia tai thoi diem dat (khong doi khi sach doi gia / bi xoa) ----------
IF OBJECT_ID(N'dbo.order_items', N'U') IS NULL
CREATE TABLE order_items (
    order_item_id INT IDENTITY(1,1) NOT NULL,
    order_id      INT           NOT NULL,
    bookid        INT           NULL,       -- NULL neu sach da bi admin xoa
    title         NVARCHAR(200) NOT NULL,
    unit_price    DECIMAL(6,2)  NOT NULL,
    quantity      INT           NOT NULL CONSTRAINT ck_oi_qty CHECK (quantity > 0),
    CONSTRAINT pk_order_items PRIMARY KEY (order_item_id),
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
    CONSTRAINT fk_oi_book  FOREIGN KEY (bookid)   REFERENCES books (bookid)    ON DELETE SET NULL
);
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'idx_oi_order' AND object_id = OBJECT_ID(N'dbo.order_items'))
    CREATE INDEX idx_oi_order ON order_items (order_id);
GO

-- ---------- Du lieu mau (chi chen khi bang orders dang rong): user an.nguyen (id=2) co du 8 trang thai ----------
IF NOT EXISTS (SELECT 1 FROM orders)
BEGIN
    SET IDENTITY_INSERT orders ON;
    INSERT INTO orders (order_id, userid, receiver_name, phone, address, note, payment_method, total_amount, status, created_at, updated_at) VALUES
    (1, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', N'Giao giờ hành chính', 'COD', 21.90, 'NEW',        DATEADD(HOUR, -1,  GETDATE()), DATEADD(HOUR, -1,  GETDATE())),
    (2, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', NULL,                    'COD', 12.50, 'CONFIRMED',  DATEADD(HOUR, -5,  GETDATE()), DATEADD(HOUR, -4,  GETDATE())),
    (3, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', NULL,                    'COD',  7.00, 'PREPARING',  DATEADD(DAY,  -1,  GETDATE()), DATEADD(HOUR, -20, GETDATE())),
    (4, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', NULL,                    'COD', 14.50, 'SHIPPING',   DATEADD(DAY,  -2,  GETDATE()), DATEADD(DAY,  -1,  GETDATE())),
    (5, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', N'Gọi trước khi giao',   'COD', 17.10, 'DELIVERING', DATEADD(DAY,  -3,  GETDATE()), DATEADD(HOUR, -3,  GETDATE())),
    (6, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', NULL,                    'COD', 13.50, 'DELIVERED',  DATEADD(DAY,  -7,  GETDATE()), DATEADD(DAY,  -4,  GETDATE())),
    (7, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', NULL,                    'COD',  9.50, 'CANCELLED',  DATEADD(DAY,  -9,  GETDATE()), DATEADD(DAY,  -9,  GETDATE())),
    (8, 2, N'Nguyễn Văn An', '0900000002', N'12 Võ Văn Ngân, TP. Thủ Đức, TP.HCM', N'Sách bị rách bìa',    'COD', 13.00, 'RETURNED',   DATEADD(DAY, -14,  GETDATE()), DATEADD(DAY, -10,  GETDATE())),
    (9, 3, N'Trần Thị Bình',  '0900000003', N'45 Lê Lợi, Quận 1, TP.HCM',          NULL,                    'COD',  9.60, 'NEW',        DATEADD(HOUR, -2,  GETDATE()), DATEADD(HOUR, -2,  GETDATE()));
    SET IDENTITY_INSERT orders OFF;

    INSERT INTO order_items (order_id, bookid, title, unit_price, quantity) VALUES
    (1, 1,  N'Mắt biếc',                          6.50, 2),
    (1, 13, N'Nhà giả kim',                       8.90, 1),
    (2, 16, N'Harry Potter và Hòn đá phù thủy',  12.50, 1),
    (3, 9,  N'Chí Phèo',                          3.50, 1),
    (3, 10, N'Lão Hạc',                           3.50, 1),
    (4, 19, N'Harry Potter và Chiếc cốc lửa',    14.50, 1),
    (5, 20, N'Rừng Na Uy',                        9.90, 1),
    (5, 3,  N'Tôi thấy hoa vàng trên cỏ xanh',    7.20, 1),
    (6, 6,  N'Dế Mèn phiêu lưu ký',               4.50, 3),
    (7, 22, N'Tuyển tập truyện ngắn Việt Nam',    9.50, 1),
    (8, 18, N'Harry Potter và Tù nhân ngục Azkaban', 13.00, 1),
    (9, 4,  N'Kính vạn hoa',                      4.80, 2);
END
GO

-- =====================================================================
-- DOI TRANG THAI DON DE QUAN SAT LICH SU (vi du):
--   UPDATE orders SET status = 'CONFIRMED',  updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'PREPARING',  updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'SHIPPING',   updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'DELIVERING', updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'DELIVERED',  updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'CANCELLED',  updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'RETURNED',   updated_at = GETDATE() WHERE order_id = 1;
--   UPDATE orders SET status = 'NEW',        updated_at = GETDATE() WHERE order_id = 1;   -- ve lai ban dau
-- Xem nhanh: SELECT order_id, userid, status, total_amount, updated_at FROM orders ORDER BY order_id;
-- =====================================================================
