package vn.iotstar.model;

import java.util.List;

/** Ket qua phan trang chung. */
public class PageResult_24110317<T> {
    private final List<T> items;
    private final int page;
    private final int pageSize;
    private final int totalItems;

    public PageResult_24110317(List<T> items, int page, int pageSize, int totalItems) {
        this.items = items;
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalItems() { return totalItems; }
    public int getTotalPages() { return (int) Math.ceil(totalItems / (double) pageSize); }
}
