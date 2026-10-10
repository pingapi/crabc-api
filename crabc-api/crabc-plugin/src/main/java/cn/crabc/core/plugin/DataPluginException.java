package cn.crabc.core.plugin;

/**
 * 数据插件执行异常
 * <p>
 * 仅承载"可对外的泛化原因"与插件标识，不携带任何行数据值；
 * 详细堆栈由执行器记录到日志，避免通过接口响应泄露业务数据。
 *
 * @author yuqf
 */
public class DataPluginException extends RuntimeException {

    /**
     * 出错的插件标识，配置解析失败等非插件级错误时为 null
     */
    private final String pluginKey;

    public DataPluginException(String pluginKey, String message) {
        super(message);
        this.pluginKey = pluginKey;
    }

    public DataPluginException(String pluginKey, String message, Throwable cause) {
        super(message, cause);
        this.pluginKey = pluginKey;
    }

    public String getPluginKey() {
        return pluginKey;
    }
}
