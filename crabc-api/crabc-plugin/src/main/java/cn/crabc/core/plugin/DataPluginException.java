package cn.crabc.core.plugin;

/**
 * 数据插件执行异常
 * <p>
 * 插件链执行失败时抛出，由上层统一转换为接口错误响应，避免脏数据静默返回。
 *
 * @author yuqf
 */
public class DataPluginException extends RuntimeException {

    public DataPluginException(String message) {
        super(message);
    }

    public DataPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
