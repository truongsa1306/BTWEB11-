package vn.iotstar.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Trang thai don hang. Gia tri trong DB (cot orders.status) chinh la ten enum (NEW, CONFIRMED, ...).
 * step: vi tri tren duong di binh thuong (0..5); don huy / don hoan nam ngoai duong di nen step = -1.
 */
public enum OrderStatus_24110317 {
    NEW("Đơn hàng mới", "new", 0),
    CONFIRMED("Đã xác nhận", "confirmed", 1),
    PREPARING("Chuẩn bị hàng", "preparing", 2),
    SHIPPING("Vận chuyển", "shipping", 3),
    DELIVERING("Giao hàng", "delivering", 4),
    DELIVERED("Đã giao", "delivered", 5),
    CANCELLED("Đơn hàng hủy", "cancelled", -1),
    RETURNED("Đơn hàng hoàn", "returned", -1);

    private final String label;
    private final String cssClass;
    private final int step;

    OrderStatus_24110317(String label, String cssClass, int step) {
        this.label = label;
        this.cssClass = cssClass;
        this.step = step;
    }

    public String getCode() { return name(); }
    public String getLabel() { return label; }
    public String getCssClass() { return cssClass; }
    public int getStep() { return step; }
    public boolean isOnTrack() { return step >= 0; }

    /** Chuyen gia tri DB / tham so URL thanh enum; khong hop le -> null. */
    public static OrderStatus_24110317 fromCode(String code) {
        if (code == null) {
            return null;
        }
        String c = code.trim();
        for (OrderStatus_24110317 s : values()) {
            if (s.name().equalsIgnoreCase(c)) {
                return s;
            }
        }
        return null;
    }

    /** 6 buoc cua don hang binh thuong (dung ve thanh tien trinh). */
    public static List<OrderStatus_24110317> track() {
        List<OrderStatus_24110317> list = new ArrayList<>();
        for (OrderStatus_24110317 s : values()) {
            if (s.isOnTrack()) {
                list.add(s);
            }
        }
        return list;
    }
}
