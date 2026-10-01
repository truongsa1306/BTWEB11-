package vn.iotstar.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Bang orders (+ truong tong hop dung cho danh sach: itemCount, lineCount, firstTitle). */
public class Order_24110317 {
    private int orderId;
    private int userId;
    private String receiverName;
    private String phone;
    private String address;
    private String note;
    private String paymentMethod;
    private BigDecimal totalAmount;
    private OrderStatus_24110317 status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<OrderItem_24110317> items = new ArrayList<>();

    // Truong phu cho trang danh sach
    private int itemCount;      // tong so cuon
    private int lineCount;      // so dong san pham
    private String firstTitle;  // ten san pham dau tien

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public OrderStatus_24110317 getStatus() { return status; }
    public void setStatus(OrderStatus_24110317 status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public List<OrderItem_24110317> getItems() { return items; }
    public void setItems(List<OrderItem_24110317> items) { this.items = items; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public int getLineCount() { return lineCount; }
    public void setLineCount(int lineCount) { this.lineCount = lineCount; }
    public String getFirstTitle() { return firstTitle; }
    public void setFirstTitle(String firstTitle) { this.firstTitle = firstTitle; }

    /** Ma don hien thi, vd DH000012. */
    public String getCode() { return String.format("DH%06d", orderId); }

    /** Chi don moi (chua xac nhan) moi duoc khach tu huy. */
    public boolean isCancellable() { return status == OrderStatus_24110317.NEW; }
}
