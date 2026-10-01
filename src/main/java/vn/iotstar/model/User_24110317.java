package vn.iotstar.model;

import java.sql.Timestamp;

/** Bang users. Role: is_admin = true -> ADMIN, nguoc lai USER. */
public class User_24110317 {
    private int id;
    private String email;
    private String fullname;
    private Integer phone;
    private String passwd;
    private Timestamp signupDate;
    private Timestamp lastLogin;
    private boolean admin;
    private boolean active;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public Integer getPhone() { return phone; }
    public void setPhone(Integer phone) { this.phone = phone; }
    public String getPasswd() { return passwd; }
    public void setPasswd(String passwd) { this.passwd = passwd; }
    public Timestamp getSignupDate() { return signupDate; }
    public void setSignupDate(Timestamp signupDate) { this.signupDate = signupDate; }
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getRole() { return admin ? "ADMIN" : "USER"; }

    /** Ten hien thi: fullname, neu trong thi dung email. */
    public String getDisplayName() {
        return (fullname == null || fullname.isBlank()) ? email : fullname;
    }
}
