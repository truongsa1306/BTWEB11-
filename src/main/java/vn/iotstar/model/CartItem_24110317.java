package vn.iotstar.model;

import java.math.BigDecimal;

/** Mot dong trong gio hang (cart_items JOIN books). */
public class CartItem_24110317 {
    private int bookid;
    private String title;
    private String coverImage;
    private BigDecimal price = BigDecimal.ZERO;
    private int quantity;     // so luong trong gio
    private int stock;        // ton kho (books.quantity)
    private int maxAllowed;   // gioi han duoc phep dat = min(ton kho, toi da moi sach)

    public int getBookid() { return bookid; }
    public void setBookid(int bookid) { this.bookid = bookid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price == null ? BigDecimal.ZERO : price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public int getMaxAllowed() { return maxAllowed; }
    public void setMaxAllowed(int maxAllowed) { this.maxAllowed = maxAllowed; }

    public BigDecimal getLineTotal() { return price.multiply(BigDecimal.valueOf(quantity)); }

    /** Het hang. */
    public boolean isOutOfStock() { return stock <= 0; }

    /** So luong trong gio vuot qua so luong duoc phep (vd ton kho giam sau khi them vao gio). */
    public boolean isOverLimit() { return quantity > maxAllowed; }

    /** Co the dat hang duoc. */
    public boolean isValid() { return !isOutOfStock() && !isOverLimit(); }
}
