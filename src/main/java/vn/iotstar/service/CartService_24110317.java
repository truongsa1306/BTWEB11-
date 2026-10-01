package vn.iotstar.service;

import java.math.BigDecimal;
import java.util.List;
import vn.iotstar.dao.BookDAO_24110317;
import vn.iotstar.dao.CartDAO_24110317;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.model.Book_24110317;
import vn.iotstar.model.CartItem_24110317;
import vn.iotstar.model.User_24110317;

/** Nghiep vu gio hang: them / sua so luong / xoa, kiem tra gioi han (ton kho va toi da moi sach). */
public class CartService_24110317 {
    /** So luong toi da cua MOI cuon sach trong gio (them vao do con bi chan boi ton kho). */
    public static final int MAX_PER_ITEM = 10;

    private final CartDAO_24110317 cartDAO = new CartDAO_24110317();
    private final BookDAO_24110317 bookDAO = new BookDAO_24110317();

    /** Gio hang cua user; moi dong da duoc gan maxAllowed = min(ton kho, MAX_PER_ITEM). */
    public List<CartItem_24110317> getItems(User_24110317 user) {
        List<CartItem_24110317> items = cartDAO.findByUser(user.getId());
        for (CartItem_24110317 it : items) {
            it.setMaxAllowed(limitOf(it.getStock()));
        }
        return items;
    }

    public static BigDecimal total(List<CartItem_24110317> items) {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem_24110317 it : items) {
            sum = sum.add(it.getLineTotal());
        }
        return sum;
    }

    public static int totalQuantity(List<CartItem_24110317> items) {
        int n = 0;
        for (CartItem_24110317 it : items) {
            n += it.getQuantity();
        }
        return n;
    }

    /** Co dong nao het hang / vuot gioi han (khong the dat hang). */
    public static boolean hasProblem(List<CartItem_24110317> items) {
        for (CartItem_24110317 it : items) {
            if (!it.isValid()) {
                return true;
            }
        }
        return false;
    }

    public int countItems(User_24110317 user) {
        return cartDAO.countItems(user.getId());
    }

    /** Them sach vao gio (cong don neu da co). */
    public void add(User_24110317 user, int bookId, String qtyStr) {
        int qty = parseQuantity(qtyStr, 1);
        if (qty < 1) {
            throw new BusinessException_24110317("Số lượng không hợp lệ.");
        }
        Book_24110317 book = bookDAO.findById(bookId);
        if (book == null) {
            throw new BusinessException_24110317("Sách không tồn tại hoặc đã bị xóa.");
        }
        int stock = book.getQuantity() == null ? 0 : book.getQuantity();
        if (stock <= 0) {
            throw new BusinessException_24110317("Sách \"" + book.getTitle() + "\" đã hết hàng.");
        }
        int current = cartDAO.getQuantity(user.getId(), bookId);
        int limit = limitOf(stock);
        if (current + qty > limit) {
            throw new BusinessException_24110317("Không thể thêm: mỗi cuốn \"" + book.getTitle() + "\" tối đa " + limit
                    + " cuốn (kho còn " + stock + "), trong giỏ của bạn hiện có " + current + ".");
        }
        cartDAO.setQuantity(user.getId(), bookId, current + qty);
    }

    /** Sua so luong 1 dong gio hang thanh gia tri moi (1..gioi han). */
    public void updateQuantity(User_24110317 user, int bookId, String qtyStr) {
        if (cartDAO.getQuantity(user.getId(), bookId) == 0) {
            throw new BusinessException_24110317("Sách này không có trong giỏ hàng.");
        }
        Book_24110317 book = bookDAO.findById(bookId);
        if (book == null) {
            cartDAO.remove(user.getId(), bookId);
            throw new BusinessException_24110317("Sách không còn tồn tại, đã được bỏ khỏi giỏ hàng.");
        }
        int stock = book.getQuantity() == null ? 0 : book.getQuantity();
        int limit = limitOf(stock);
        if (limit < 1) {
            throw new BusinessException_24110317("Sách \"" + book.getTitle() + "\" đã hết hàng, hãy xóa khỏi giỏ.");
        }
        int qty = parseQuantity(qtyStr, -1);
        if (qty < 1 || qty > limit) {
            throw new BusinessException_24110317("Số lượng \"" + book.getTitle() + "\" phải từ 1 đến " + limit
                    + ". Muốn bỏ sách khỏi giỏ hãy bấm Xóa.");
        }
        cartDAO.setQuantity(user.getId(), bookId, qty);
    }

    public void remove(User_24110317 user, int bookId) {
        if (!cartDAO.remove(user.getId(), bookId)) {
            throw new BusinessException_24110317("Sách này không có trong giỏ hàng.");
        }
    }

    public void clear(User_24110317 user) {
        cartDAO.clear(user.getId());
    }

    private static int limitOf(int stock) {
        return Math.max(0, Math.min(stock, MAX_PER_ITEM));
    }

    /** Chi nhan so nguyen khong dau tu 1-4 chu so; sai -> def. */
    private static int parseQuantity(String s, int def) {
        if (s == null || !s.trim().matches("\\d{1,4}")) {
            return def;
        }
        return Integer.parseInt(s.trim());
    }
}
