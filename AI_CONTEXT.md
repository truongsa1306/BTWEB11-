# AI_CONTEXT
Package gốc vn.iotstar: controller (Servlet @WebServlet), service (nghiệp vụ/validate), dao (JDBC PreparedStatement), model, filter (Encoding, SiteMesh, Auth), util (DBConnection, PasswordUtil PBKDF2, MailUtil, WebUtil), exception.
Views: src/main/webapp/WEB-INF/views (JSP, JSTL jakarta.tags.*), decorators (user.jsp, admin.jsp + _header/_flash/_footer.jspf), tags (cover, pagination, stars).
Sitemesh: SiteMeshFilter_24110317 khai báo trong web.xml; decorator path là tên tương đối (prefix mặc định /WEB-INF/decorators/).
Session attr: "user" (User_24110317), "returnUrl", "otpEmail", flash/flashType.
Cấu hình: resources/db.properties, mail.properties (ghi đè bằng biến môi trường DB_*, MAIL_*).
Chạy: chạy database.sql trong SSMS (SQL Server 2017+), kiểm tra db.properties (sa/123), mvn clean package, deploy target/KTQT.war lên Tomcat 10.1+ -> http://localhost:8080/KTQT/
