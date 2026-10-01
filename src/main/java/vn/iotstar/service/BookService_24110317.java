package vn.iotstar.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vn.iotstar.dao.AuthorDAO_24110317;
import vn.iotstar.dao.BookDAO_24110317;
import vn.iotstar.exception.BusinessException_24110317;
import vn.iotstar.exception.ValidationException_24110317;
import vn.iotstar.model.Author_24110317;
import vn.iotstar.model.Book_24110317;
import vn.iotstar.model.HomePage_24110317;
import vn.iotstar.model.PageResult_24110317;

/** Nghiep vu sach: trang Home theo tac gia, san pham, CRUD admin. */
public class BookService_24110317 {
    public static final int HOME_PAGE_SIZE = 3;     // Cau 3: 03 sach / trang
    public static final int PRODUCT_PAGE_SIZE = 6;
    public static final int ADMIN_PAGE_SIZE = 5;    // Cau 6: phan trang CRUD

    private final BookDAO_24110317 bookDAO = new BookDAO_24110317();
    private final AuthorDAO_24110317 authorDAO = new AuthorDAO_24110317();

    /**
     * Trang Home: sach duoc nhom theo tac gia, moi trang = 1 tac gia + toi da 3 sach.
     * Tac gia co n sach chiem ceil(n/3) trang. Tong so trang = tong cac ceil(n/3).
     */
    public HomePage_24110317 getHomePage(int page) {
        List<Author_24110317> authors = authorDAO.findAllWithBooks();
        int totalPages = 0;
        for (Author_24110317 a : authors) {
            totalPages += (int) Math.ceil(a.getBookCount() / (double) HOME_PAGE_SIZE);
        }
        HomePage_24110317 result = new HomePage_24110317();
        result.setTotalPages(totalPages);
        if (totalPages == 0) {
            return result;
        }
        page = Math.max(1, Math.min(page, totalPages));
        result.setPage(page);

        int remaining = page - 1;
        for (Author_24110317 a : authors) {
            int pagesOfAuthor = (int) Math.ceil(a.getBookCount() / (double) HOME_PAGE_SIZE);
            if (remaining < pagesOfAuthor) {
                result.setAuthor(a);
                result.setBooks(bookDAO.findByAuthor(a.getAuthorId(), remaining * HOME_PAGE_SIZE, HOME_PAGE_SIZE));
                break;
            }
            remaining -= pagesOfAuthor;
        }
        return result;
    }

    public PageResult_24110317<Book_24110317> getBooksPage(int page, int pageSize) {
        int total = bookDAO.count();
        int totalPages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        page = Math.max(1, Math.min(page, totalPages));
        return new PageResult_24110317<>(bookDAO.findPage((page - 1) * pageSize, pageSize), page, pageSize, total);
    }

    public Book_24110317 getById(int id) {
        return id <= 0 ? null : bookDAO.findById(id);
    }

    public List<Author_24110317> getAllAuthors() {
        return authorDAO.findAll();
    }

    public void delete(int id) {
        if (!bookDAO.delete(id)) {
            throw new BusinessException_24110317("Sách không tồn tại hoặc đã bị xóa.");
        }
    }

    /** Validate du lieu form (request parameters) va tao Book; nem ValidationException neu loi. */
    public Book_24110317 parseAndValidate(Map<String, String[]> p) {
        Map<String, String> err = new LinkedHashMap<>();
        Book_24110317 b = new Book_24110317();
        b.setBookid(parseIntOr(get(p, "bookid"), 0));

        String title = get(p, "title");
        if (title.isEmpty()) err.put("title", "Vui lòng nhập tiêu đề.");
        else if (title.length() > 200) err.put("title", "Tiêu đề tối đa 200 ký tự.");
        b.setTitle(title);

        String isbnStr = get(p, "isbn");
        if (isbnStr.isEmpty()) {
            err.put("isbn", "Vui lòng nhập mã ISBN.");
        } else if (!isbnStr.matches("\\d{1,10}") || Long.parseLong(isbnStr) > Integer.MAX_VALUE || Long.parseLong(isbnStr) <= 0) {
            err.put("isbn", "ISBN phải là số nguyên dương (tối đa 2147483647).");
        } else {
            int isbn = Integer.parseInt(isbnStr);
            if (bookDAO.existsIsbn(isbn, b.getBookid())) err.put("isbn", "ISBN đã tồn tại ở sách khác.");
            b.setIsbn(isbn);
        }

        String publisher = get(p, "publisher");
        if (publisher.isEmpty()) err.put("publisher", "Vui lòng nhập nhà xuất bản.");
        else if (publisher.length() > 100) err.put("publisher", "Nhà xuất bản tối đa 100 ký tự.");
        b.setPublisher(publisher);

        String priceStr = get(p, "price");
        if (priceStr.isEmpty()) {
            err.put("price", "Vui lòng nhập giá.");
        } else {
            try {
                BigDecimal price = new BigDecimal(priceStr).setScale(2, RoundingMode.HALF_UP);
                if (price.signum() < 0 || price.compareTo(new BigDecimal("9999.99")) > 0) {
                    err.put("price", "Giá phải trong khoảng 0 - 9999.99 (cột decimal(6,2)).");
                } else {
                    b.setPrice(price);
                }
            } catch (NumberFormatException e) {
                err.put("price", "Giá không hợp lệ.");
            }
        }

        String dateStr = get(p, "publishDate");
        if (dateStr.isEmpty()) {
            err.put("publishDate", "Vui lòng chọn ngày xuất bản.");
        } else {
            try {
                b.setPublishDate(Date.valueOf(LocalDate.parse(dateStr)));
            } catch (DateTimeParseException e) {
                err.put("publishDate", "Ngày xuất bản không hợp lệ (yyyy-MM-dd).");
            }
        }

        String cover = get(p, "coverImage");
        if (cover.isEmpty()) err.put("coverImage", "Vui lòng nhập ảnh bìa (tên file trong assets/covers hoặc URL).");
        else if (cover.length() > 100) err.put("coverImage", "Ảnh bìa tối đa 100 ký tự.");
        b.setCoverImage(cover);

        String qtyStr = get(p, "quantity");
        if (qtyStr.isEmpty()) {
            err.put("quantity", "Vui lòng nhập số lượng.");
        } else if (!qtyStr.matches("\\d{1,9}")) {
            err.put("quantity", "Số lượng phải là số nguyên >= 0.");
        } else {
            b.setQuantity(Integer.parseInt(qtyStr));
        }

        b.setDescription(get(p, "description"));

        List<Integer> authorIds = parseAuthorIds(p.get("authorIds"));
        if (authorIds.isEmpty()) {
            err.put("authorIds", "Vui lòng chọn ít nhất 1 tác giả.");
        } else {
            Set<Integer> valid = new HashSet<>();
            for (Author_24110317 a : authorDAO.findAll()) valid.add(a.getAuthorId());
            if (!valid.containsAll(authorIds)) err.put("authorIds", "Tác giả không hợp lệ.");
        }
        b.setAuthorIds(authorIds);

        if (!err.isEmpty()) {
            throw new ValidationException_24110317(err);
        }
        return b;
    }

    public int create(Book_24110317 b) {
        return bookDAO.insert(b, b.getAuthorIds());
    }

    public void update(Book_24110317 b) {
        if (!bookDAO.update(b, b.getAuthorIds())) {
            throw new BusinessException_24110317("Sách không tồn tại hoặc đã bị xóa.");
        }
    }

    public static List<Integer> parseAuthorIds(String[] raw) {
        List<Integer> ids = new ArrayList<>();
        if (raw != null) {
            for (String s : raw) {
                int id = parseIntOr(s, 0);
                if (id > 0 && !ids.contains(id)) ids.add(id);
            }
        }
        return ids;
    }

    private static String get(Map<String, String[]> p, String key) {
        String[] v = p.get(key);
        return (v == null || v.length == 0 || v[0] == null) ? "" : v[0].trim();
    }

    private static int parseIntOr(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
