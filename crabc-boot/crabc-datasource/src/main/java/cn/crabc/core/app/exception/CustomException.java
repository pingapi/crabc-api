package cn.crabc.core.app.exception;

/**
 * 自定义异常结构
 *
 * @author yuqf
 */
public class CustomException extends RuntimeException {

    private int code;
    private String message;

    public CustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
