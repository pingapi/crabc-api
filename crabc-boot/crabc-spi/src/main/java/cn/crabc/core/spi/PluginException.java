package cn.crabc.core.spi;

/**
 * 插件异常结构
 *
 * @author yuqf
 */
public class PluginException extends RuntimeException {

    private int code;
    private String msg;

    public PluginException(int code, String message) {
        this.code = code;
        this.msg = message;
    }
    public PluginException(String message) {
        this.code = 55000;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
