package cn.crabc.core.plugin.impl;

import cn.crabc.core.plugin.DataPlugin;
import cn.crabc.core.plugin.PluginKeys;
import cn.crabc.core.plugin.util.PluginUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 字段重命名插件（fieldMapping）
 * <p>
 * 将 sourceField 字段重命名为 targetField，源字段为空时可填充 defaultValue，
 * removeSource 为 true 时删除源字段。
 *
 * @author yuqf
 */
@Component
public class FieldMappingPlugin implements DataPlugin {

    @Override
    public String getName() {
        return PluginKeys.FIELD_MAPPING;
    }

    @Override
    public Object apply(Object data, Map<String, Object> config, Map<String, Object> requestParams) {
        List<Map<String, Object>> rows = PluginUtils.asRowList(data);
        if (rows == null || rows.isEmpty()) {
            return data;
        }
        if (!(config.get("mappings") instanceof List<?> mappings) || mappings.isEmpty()) {
            return data;
        }
        for (Map<String, Object> row : rows) {
            for (Object item : mappings) {
                if (!(item instanceof Map<?, ?> rawMapping)) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> mapping = (Map<String, Object>) rawMapping;
                String sourceField = PluginUtils.str(mapping.get("sourceField"));
                String targetField = PluginUtils.str(mapping.get("targetField"));
                if (sourceField == null || targetField == null) {
                    continue;
                }
                Object value = row.get(sourceField);
                // 源字段为空时填充默认值
                if (PluginUtils.isBlank(value) && !PluginUtils.isBlank(mapping.get("defaultValue"))) {
                    value = mapping.get("defaultValue");
                }
                row.put(targetField, value);
                boolean removeSource = Boolean.TRUE.equals(mapping.get("removeSource"));
                if (removeSource) {
                    row.remove(sourceField);
                }
            }
        }
        return rows;
    }
}
