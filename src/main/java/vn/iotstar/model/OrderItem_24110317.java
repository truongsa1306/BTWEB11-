package vn.iotstar.model;

import java.math.BigDecimal;

/** Mot dong chi tiet don hang (ten + gia duoc luu tai thoi diem dat). */
public class OrderItem_24110317 {
    private int id;
    private int orderId;
    private Integer bookid;      // null neu sach da bi xoa
    private String title;
    private String coverImage;   // lay tu books (co the null)
    private BigDecimal unitPrice;
    private int quantity;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public Integer getBookid() { return bookid; }
    public void setBookid(Integer bookid) { this.bookid = bookid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getLineTotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
