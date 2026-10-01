# REQUIREMENTS (Đề 02, MSSV 24110317)
Maven Web + Servlet + JDBC + JSP + Sitemesh, 3 tầng (Presentation/Business/DAO). Không Spring/JPA.
Class/Interface/Controller/Service có hậu tố `_24110317`.
- Câu 1: cấu trúc 3 tầng, DB, Sitemesh (User/Admin), header (Trang Chủ, Sản phẩm, Đăng nhập, Trang quản trị chỉ ADMIN), footer (Họ tên, MSSV, Mã đề).
- Câu 2: đăng ký + OTP mail, đăng nhập, đăng xuất, session; USER -> /home, ADMIN -> /admin/books, sai -> /login.
- Câu 3: Home theo tác giả, 3 sách/trang, phân trang "Trang trước – 1 2 3 4 – Trang sau".
- Câu 4: chi tiết sách + reviews + form thêm review.
- Câu 6: CRUD Books có phân trang (ADMIN).

## Bo sung (vai tro User)
1. Gio hang: them / xoa / sua so luong trong gioi han (ton kho, toi da 10 cuon/sach).
2. Thanh toan don hang bang COD.
3. Lich su dat hang loc theo trang thai: moi, da xac nhan, chuan bi hang, van chuyen, giao hang, da giao, huy, hoan.
