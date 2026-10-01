package vn.iotstar.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vn.iotstar.dao.BookDAO_24110317;
import vn.iotstar.dao.ReviewDAO_24110317;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.Review_24110317;
import vn.iotstar.model.User_24110317;

/** Nghiep vu review sach. */
public class ReviewService_24110317 {
    private static final int MAX_LENGTH = 2000;
    private final ReviewDAO_24110317 reviewDAO = new ReviewDAO_24110317();
    private final BookDAO_24110317 bookDAO = new BookDAO_24110317();

    public List<Review_24110317> getReviews(int bookId) {
        return reviewDAO.findByBook(bookId);
    }

    /** Them review (hoac cap nhat review cu cua cung user cho cung sach). */
    public void addReview(User_24110317 user, int bookId, String ratingStr, String text) {
        if (user == null) {
            throw new BusinessException_24110317("Bạn cần đăng nhập để gửi review.");
        }
        if (bookDAO.findById(bookId) == null) {
            throw new BusinessException_24110317("Sách không tồn tại.");
        }
        Map<String, String> err = new LinkedHashMap<>();
        text = text == null ? "" : text.trim();
        if (text.isEmpty()) {
            err.put("review_text", "Nội dung review không được để trống.");
        } else if (text.length() > MAX_LENGTH) {
            err.put("review_text", "Nội dung review tối đa " + MAX_LENGTH + " ký tự.");
        }
        Integer rating = null;
        if (ratingStr != null && !ratingStr.isBlank()) {
            try {
                rating = Integer.valueOf(ratingStr.trim());
            } catch (NumberFormatException e) {
                rating = 0;
            }
            if (rating < 1 || rating > 5) {
                err.put("rating", "Điểm đánh giá phải từ 1 đến 5.");
            }
        }
        if (!err.isEmpty()) {
            throw new ValidationException_24110317(err);
        }
        reviewDAO.save(user.getId(), bookId, rating, text);
    }
}
