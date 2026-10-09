package cn.crabc.core.plugin;

import java.util.Map;

/**
 * 数据插件 SPI
 * <p>
 * 每个 SQL 类型接口可在 base_api_info.plugin_config 中保存 JSON 配置，
 * 插件链按 {@link PluginKeys#ORDER} 定义的顺序对 SQL 执行结果进行后处理。
 * 数据结构对齐前端 RightPluginConfig.vue 的 pluginConfig 定义。
 *
 * @author yuqf
 */
public interface DataPlugin {

    /**
     * 插件标识，对应 plugin_config JSON 中的 key，如 dataProcess、fieldMapping
     *
     * @return 插件 key
     */
    String getName();

    /**
     * 应用插件逻辑
     *
     * @param data          SQL 执行结果，通常为 List&lt;Map&gt; 行集合；DML 场景可能为数字，插件应直接返回原值
     * @param config        该插件的配置节点（plugin_config JSON 中 key 对应的子对象，含 enabled 及插件参数）
     * @param requestParams 当前请求参数（部分插件如脚本处理可能用到）
     * @return 处理后的数据
     */
    Object apply(Object data, Map<String, Object> config, Map<String, Object> requestParams);
}
