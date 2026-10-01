package vn.iotstar.model;

/** Bang rating (userid, bookid, rating, review_text) + ten nguoi danh gia. */
public class Review_24110317 {
    private int userid;
    private int bookid;
    private Integer rating;
    private String reviewText;
    private String userName;

    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }
    public int getBookid() { return bookid; }
    public void setBookid(int bookid) { this.bookid = bookid; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
