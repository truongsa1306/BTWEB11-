# KTQT – BookStore (Servlet + JDBC + JSP + Sitemesh, SQL Server)
MSSV 24110317 – Đề 02. Bản này bổ sung **Giỏ hàng, Thanh toán COD, Lịch sử đơn hàng** cho vai trò **User**.

## 1. Chạy project
1. SSMS: mở `database.sql` → Execute (tạo mới toàn bộ DB `BookStore`, **xóa dữ liệu cũ**).
   *Nếu đã có DB và không muốn mất dữ liệu:* chỉ chạy `database_cart_orders.sql` (thêm 3 bảng mới + đơn mẫu, chạy lại nhiều lần vẫn an toàn).
2. Kiểm tra `src/main/resources/db.properties` (mặc định `sa` / `123`, cổng 1433).
3. `mvn clean package` → deploy `target/KTQT.war` lên Tomcat 10.1+ → http://localhost:8080/KTQT/
4. Tài khoản User mẫu: `an.nguyen@example.com` / `User@123` (đã có sẵn 8 đơn hàng, mỗi trạng thái 1 đơn). Admin: `admin@bookstore.com` / `Admin@123`.
5. Gửi OTP mail thật: đặt biến môi trường `MAIL_USER`, `MAIL_PASSWORD` (đừng ghi mật khẩu vào `mail.properties` rồi đẩy GitHub). Không cấu hình thì OTP in ra console Tomcat.

## 2. Chức năng đã làm (chỉ vai trò User; Admin bị chuyển về `/admin/books`)
| Chức năng | URL | Ghi chú |
|---|---|---|
| Thêm vào giỏ | nút "Thêm vào giỏ" ở Trang chủ / Sản phẩm / Chi tiết sách | Chưa đăng nhập → chuyển `/login`, đăng nhập xong quay lại trang cũ |
| Xem giỏ, **sửa** số lượng (ô nhập hoặc nút − / +), **xóa** 1 dòng, **xóa toàn bộ** | `/cart` | Giỏ lưu trong DB (`cart_items`), không mất khi đăng xuất |
| **Giới hạn số lượng** | | 1 ≤ SL ≤ min(**tồn kho**, **10 cuốn/sách**) (hằng số `CartService_24110317.MAX_PER_ITEM`). Sách hết hàng không thêm được; nếu tồn kho giảm sau khi đã bỏ vào giỏ, dòng đó tô đỏ và chặn thanh toán |
| Thanh toán **COD** | `/checkout` | Nhập người nhận / SĐT (`0xxxxxxxxx`) / địa chỉ / ghi chú. Đặt hàng = 1 transaction: tạo đơn → lưu dòng hàng (tên + giá lúc đặt) → **trừ kho có điều kiện** → xóa giỏ. Hết hàng giữa chừng → rollback toàn bộ |
| Lịch sử đơn, **lọc theo trạng thái** (có số đơn mỗi tab), phân trang 5 đơn/trang | `/orders?status=...&page=...` | |
| Chi tiết đơn + thanh tiến trình theo trạng thái | `/orders/detail?id=` | Chỉ xem được đơn của chính mình |
| Hủy đơn (chỉ khi "Đơn hàng mới") | nút trong chi tiết đơn | Hoàn lại tồn kho |

## 3. Trạng thái đơn hàng (cột `orders.status`)
| Giá trị trong DB | Hiển thị |
|---|---|
| `NEW` | Đơn hàng mới |
| `CONFIRMED` | Đã xác nhận |
| `PREPARING` | Chuẩn bị hàng |
| `SHIPPING` | Vận chuyển |
| `DELIVERING` | Giao hàng |
| `DELIVERED` | Đã giao |
| `CANCELLED` | Đơn hàng hủy |
| `RETURNED` | Đơn hàng hoàn |

**Quan sát đổi trạng thái:** đăng nhập User → mở `/orders` → trong SSMS chạy:
```sql
SELECT order_id, userid, status FROM orders;
UPDATE orders SET status = 'CONFIRMED', updated_at = GETDATE() WHERE order_id = 1;  -- đổi sang trạng thái bất kỳ ở bảng trên
```
→ F5 trang `/orders`: đơn chuyển sang đúng tab, huy hiệu màu và thanh tiến trình trong chi tiết đổi theo. (Đổi trạng thái bằng SQL **không** tự hoàn kho; chỉ nút "Hủy đơn" của khách mới hoàn kho.)

## 4. Kịch bản kiểm thử nhanh
1. Chưa đăng nhập bấm "Thêm vào giỏ" → bị đưa về `/login`; đăng nhập `an.nguyen@...` → quay lại trang sách.
2. Thêm "Mắt biếc" × 3 rồi thêm × 8 → báo lỗi vượt giới hạn 10. Sách "Kafka bên bờ biển" (kho = 0) hiện "Hết hàng".
3. Trong giỏ: bấm +/−, nhập 0 hoặc 11 (báo lỗi), Xóa 1 dòng, Xóa toàn bộ.
4. Thanh toán: để trống / SĐT sai → báo lỗi từng ô, giữ nguyên dữ liệu đã nhập; nhập đúng → sang chi tiết đơn, giỏ về 0, kho giảm (`SELECT quantity FROM books WHERE bookid = ...`).
5. `/orders`: bấm lần lượt 9 tab; đổi trạng thái bằng SQL như mục 3.
6. Đăng nhập `binh.tran@...` thử mở `/orders/detail?id=1` (đơn của user khác) → 404.



