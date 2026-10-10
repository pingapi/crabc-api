package cn.crabc.core.plugin.impl;

import cn.crabc.core.plugin.DataPlugin;
import cn.crabc.core.plugin.PluginKeys;
import cn.crabc.core.plugin.util.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 单对象插件（singleObject）
 * <p>
 * 将集合结果转换为单个对象返回：
 * selectMode=first 时返回第一个对象；selectMode=condition 时按条件匹配返回第一个满足的对象。
 * 结果集为空或无匹配时返回 null。与 treeTransform 互斥（前端已约束，执行器对冲突配置兜底告警）。
 *
 * @author yuqf
 */
@Component
public class SingleObjectPlugin implements DataPlugin {

    private static final Logger log = LoggerFactory.getLogger(SingleObjectPlugin.class);

    private static final String MODE_CONDITION = "condition";

    @Override
    public String getName() {
        return PluginKeys.SINGLE_OBJECT;
    }

    @Override
    public Object apply(Object data, Map<String, Object> config, Map<String, Object> requestParams) {
        List<Map<String, Object>> rows = PluginUtils.asRowList(data);
        if (rows == null) {
            // 非列表数据（如已是单对象）直接返回
            return data;
        }
        if (rows.isEmpty()) {
            return null;
        }
        if (MODE_CONDITION.equals(PluginUtils.str(config.get("selectMode")))) {
            String conditionField = PluginUtils.str(config.get("conditionField"));
            String conditionOperator = PluginUtils.str(config.get("conditionOperator"));
            Object conditionValue = config.get("conditionValue");
            if (conditionField == null || conditionOperator == null) {
                throw new IllegalArgumentException("单对象插件条件匹配模式必须配置条件字段和操作符");
            }
            if (!PluginUtils.containsField(rows, conditionField)) {
                log.warn("数据插件[{}]条件字段[{}]不存在于结果集中，将按无匹配处理", getName(), conditionField);
                return null;
            }
            // 正则预编译一次，避免逐行编译
            Pattern pattern = PluginUtils.isRegexOperator(conditionOperator)
                    ? PluginUtils.compileRegex(conditionValue) : null;
            for (Map<String, Object> row : rows) {
                Object actual = row.get(conditionField);
                boolean matched = pattern != null
                        ? PluginUtils.matchesRegex(actual, pattern)
                        : PluginUtils.matches(actual, conditionOperator, conditionValue);
                if (matched) {
                    return row;
                }
            }
            return null;
        }
        // 默认返回第一个对象
        return rows.getFirst();
    }
}
