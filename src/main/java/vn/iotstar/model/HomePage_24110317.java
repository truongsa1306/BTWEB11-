package vn.iotstar.model;

import java.util.ArrayList;
import java.util.List;

/** Du lieu 1 trang Home: 1 tac gia + toi da 3 sach cua tac gia do. */
public class HomePage_24110317 {
    private Author_24110317 author;
    private List<Book_24110317> books = new ArrayList<>();
    private int page = 1;
    private int totalPages = 0;

    public Author_24110317 getAuthor() { return author; }
    public void setAuthor(Author_24110317 author) { this.author = author; }
    public List<Book_24110317> getBooks() { return books; }
    public void setBooks(List<Book_24110317> books) { this.books = books; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
