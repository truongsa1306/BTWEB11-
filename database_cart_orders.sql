-- =====================================================================
-- database_cart_orders.sql - Gio hang + Don hang (COD) + Lich su don hang
-- Chay SAU database.sql (can bang users, books). Co the chay lai nhieu lan (khong xoa du lieu).
-- Neu da co DB BookStore cu: chi can chay file nay trong SSMS.
-- =====================================================================
USE BookStore;
GO
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

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
