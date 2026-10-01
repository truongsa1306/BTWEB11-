# CODING_CONVENTION
- Tên class/interface: <Tên>_24110317. Servlet có hậu tố Controller_24110317.
- Luồng: JSP -> Controller -> Service -> DAO -> JDBC. Không SQL trong JSP.
- DAO: try-with-resources, PreparedStatement; lỗi SQL bọc DataAccessException_24110317.
- Lỗi form: ValidationException_24110317 (map field->message); lỗi nghiệp vụ: BusinessException_24110317.
- Output JSP luôn <c:out> (chống XSS). POST -> redirect (PRG).
