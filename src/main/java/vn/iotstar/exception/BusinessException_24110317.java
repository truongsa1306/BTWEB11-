package vn.iotstar.exception;

/** Loi nghiep vu (tang Service) - message hien thi truc tiep cho nguoi dung. */
public class BusinessException_24110317 extends RuntimeException {
    private final String code;

    public BusinessException_24110317(String message) {
        this(message, null);
    }

    public BusinessException_24110317(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() { return code; }
}
