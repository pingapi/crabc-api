package cn.crabc.core.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据插件链执行器
 * <p>
 * 解析 base_api_info.plugin_config 配置 JSON，按 {@link PluginKeys#ORDER} 顺序
 * 依次应用已启用的插件。配置为空或解析失败时直接返回原始数据；
 * 未注册实现的插件 key 跳过（预留扩展）。
 *
 * @author yuqf
 */
@Component
public class DataPluginExecutor {

    private static final Logger log = LoggerFactory.getLogger(DataPluginExecutor.class);

    private static final TypeReference<Map<String, Object>> CONFIG_TYPE = new TypeReference<>() {
    };

    private final JsonMapper jsonMapper;

    /** 插件注册表：key -> 插件实例 */
    private final Map<String, DataPlugin> pluginRegistry = new HashMap<>();

    public DataPluginExecutor(JsonMapper jsonMapper, List<DataPlugin> plugins) {
        this.jsonMapper = jsonMapper;
        for (DataPlugin plugin : plugins) {
            pluginRegistry.put(plugin.getName(), plugin);
        }
        log.info("数据插件注册完成：{}", pluginRegistry.keySet());
    }

    /**
     * 执行插件链
     *
     * @param pluginConfigJson 插件配置 JSON 字符串（可为空）
     * @param data             SQL 执行结果
     * @param requestParams    请求参数
     * @return 处理后的数据；无需处理时返回原数据
     */
    public Object execute(String pluginConfigJson, Object data, Map<String, Object> requestParams) {
        if (data == null || pluginConfigJson == null || pluginConfigJson.isBlank()) {
            return data;
        }
        Map<String, Object> config;
        try {
            config = jsonMapper.readValue(pluginConfigJson, CONFIG_TYPE);
        } catch (Exception e) {
            log.warn("数据插件配置解析失败，跳过插件处理：{}", e.getMessage());
            return data;
        }
        if (config.isEmpty()) {
            return data;
        }
        for (String key : PluginKeys.ORDER) {
            Object node = config.get(key);
            if (!(node instanceof Map<?, ?> rawNode)) {
                continue;
            }
            if (!isEnabled(rawNode)) {
                continue;
            }
            DataPlugin plugin = pluginRegistry.get(key);
            if (plugin == null) {
                // 预留插件 key，尚未实现
                log.debug("数据插件[{}]未注册实现，跳过", key);
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> pluginConfig = (Map<String, Object>) rawNode;
            try {
                data = plugin.apply(data, pluginConfig, requestParams);
            } catch (DataPluginException e) {
                throw e;
            } catch (Exception e) {
                throw new DataPluginException("数据插件执行失败[" + key + "]：" + e.getMessage(), e);
            }
        }
        return data;
    }

    /**
     * 判断插件节点是否启用
     */
    private boolean isEnabled(Map<?, ?> node) {
        Object enabled = node.get("enabled");
        if (enabled instanceof Boolean b) {
            return b;
        }
        return enabled != null && Boolean.parseBoolean(enabled.toString());
    }
}
