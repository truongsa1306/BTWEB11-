package vn.iotstar.model;

import java.sql.Date;

/** Bang author. bookCount chi dung khi thong ke (khong luu trong DB). */
public class Author_24110317 {
    private int authorId;
    private String authorName;
    private Date dateOfBirth;
    private int bookCount;

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public int getBookCount() { return bookCount; }
    public void setBookCount(int bookCount) { this.bookCount = bookCount; }
}
