package vn.iotstar.exception;

import java.util.Map;

/** Loi validate form: field -> message. */
public class ValidationException_24110317 extends BusinessException_24110317 {
    private final Map<String, String> errors;

    public ValidationException_24110317(Map<String, String> errors) {
        super("Dữ liệu không hợp lệ");
        this.errors = errors;
    }

    public Map<String, String> getErrors() { return errors; }
}
