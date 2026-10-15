package cn.crabc.core.plugin;

import cn.crabc.core.plugin.util.PluginUtils;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据插件配置校验器
 * <p>
 * 在接口保存/暂存阶段拦截非法配置：JSON 不合法、包含未实现的插件 key、
 * 体积或规则数量超限、插件参数缺失、互斥插件同时启用、正则表达式超长等，
 * 避免"配置保存成功但运行时静默失效"。
 *
 * @author yuqf
 */
@Component
public class DataPluginConfigValidator {

    /**
     * 配置 JSON 最大长度（字符）
     */
    public static final int MAX_CONFIG_LENGTH = 8192;

    /**
     * 单个插件允许的最大规则数量
     */
    private static final int MAX_RULES = 50;

    /**
     * 当前版本已实现的插件 key
     */
    private static final Set<String> SUPPORTED_KEYS = Set.of(
            PluginKeys.DATA_PROCESS,
            PluginKeys.FIELD_MAPPING,
            PluginKeys.TREE_TRANSFORM,
            PluginKeys.SINGLE_OBJECT
    );

    private static final Set<String> DATA_PROCESS_RULE_TYPES = Set.of("filter", "sort", "encrypt", "calculate");

    private static final Set<String> NO_VALUE_OPERATORS = Set.of("isNull", "isNotNull");

    private static final TypeReference<Map<String, Object>> CONFIG_TYPE = new TypeReference<>() {
    };

    private final JsonMapper jsonMapper;

    public DataPluginConfigValidator(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    /**
     * 校验插件配置
     *
     * @param pluginConfigJson 插件配置 JSON（空表示不启用插件）
     * @throws IllegalArgumentException 配置非法时抛出，message 可直接返回给用户
     */
    public void validate(String pluginConfigJson) {
        if (pluginConfigJson == null || pluginConfigJson.isBlank()) {
            return;
        }
        if (pluginConfigJson.length() > MAX_CONFIG_LENGTH) {
            throw new IllegalArgumentException("数据插件配置过大，最多 " + MAX_CONFIG_LENGTH + " 个字符");
        }
        Map<String, Object> config;
        try {
            config = jsonMapper.readValue(pluginConfigJson, CONFIG_TYPE);
        } catch (Exception e) {
            throw new IllegalArgumentException("数据插件配置不是合法的 JSON 对象");
        }
        if (config == null || config.isEmpty()) {
            return;
        }
        // 1. 未实现的插件 key 直接拒绝，避免配置后静默失效
        List<String> unsupported = config.keySet().stream().filter(key -> !SUPPORTED_KEYS.contains(key)).toList();
        if (!unsupported.isEmpty()) {
            throw new IllegalArgumentException("存在未实现的数据插件：" + String.join("、", unsupported));
        }
        // 2. 互斥校验
        if (isEnabled(config.get(PluginKeys.TREE_TRANSFORM)) && isEnabled(config.get(PluginKeys.SINGLE_OBJECT))) {
            throw new IllegalArgumentException("Tree形结构与单对象插件不能同时启用");
        }
        // 3. 各插件参数校验
        validateDataProcess(config.get(PluginKeys.DATA_PROCESS));
        validateFieldMapping(config.get(PluginKeys.FIELD_MAPPING));
        validateTreeTransform(config.get(PluginKeys.TREE_TRANSFORM));
        validateSingleObject(config.get(PluginKeys.SINGLE_OBJECT));
    }

    private void validateDataProcess(Object node) {
        if (!isEnabled(node)) {
            return;
        }
        Map<String, Object> config = asMap(node, PluginKeys.DATA_PROCESS);
        List<Map<String, Object>> rules = asList(config.get("rules"), "过滤清洗规则");
        if (rules.size() > MAX_RULES) {
            throw new IllegalArgumentException("过滤清洗规则数量不能超过 " + MAX_RULES + " 条");
        }
        for (Map<String, Object> rule : rules) {
            String type = PluginUtils.str(rule.get("type"));
            if (type == null || !DATA_PROCESS_RULE_TYPES.contains(type)) {
                throw new IllegalArgumentException("过滤清洗包含不支持的规则类型：" + type);
            }
            switch (type) {
                case "filter" -> {
                    requireText(rule.get("field"), "过滤规则必须选择字段");
                    String operator = PluginUtils.str(rule.get("operator"));
                    if (operator == null) {
                        throw new IllegalArgumentException("过滤规则必须选择操作符");
                    }
                    if (!NO_VALUE_OPERATORS.contains(operator) && PluginUtils.isBlank(rule.get("value"))) {
                        throw new IllegalArgumentException("过滤规则必须填写比较值");
                    }
                    if (PluginUtils.isRegexOperator(operator)) {
                        checkRegex(rule.get("value"));
                    }
                }
                case "sort" -> requireText(rule.get("field"), "排序规则必须选择字段");
                case "encrypt" -> {
                    if (!(rule.get("encryptFields") instanceof List<?> fields) || fields.isEmpty()) {
                        throw new IllegalArgumentException("脱敏规则必须选择脱敏字段");
                    }
                }
                case "calculate" -> {
                    requireText(rule.get("sourceField"), "字段计算必须选择源字段");
                    requireText(rule.get("field"), "字段计算必须填写结果字段名");
                    requireText(rule.get("calcOperator"), "字段计算必须选择计算操作符");
                    if ("field".equals(PluginUtils.str(rule.get("valueType")))) {
                        requireText(rule.get("valueField"), "字段计算必须选择操作数字段");
                    } else if (PluginUtils.isBlank(rule.get("calcValue"))) {
                        throw new IllegalArgumentException("字段计算必须填写固定值");
                    }
                }
                default -> {
                    // 已由类型白名单拦截
                }
            }
        }
    }

    private void validateFieldMapping(Object node) {
        if (!isEnabled(node)) {
            return;
        }
        Map<String, Object> config = asMap(node, PluginKeys.FIELD_MAPPING);
        List<Map<String, Object>> mappings = asList(config.get("mappings"), "字段重命名映射");
        if (mappings.size() > MAX_RULES) {
            throw new IllegalArgumentException("字段重命名映射数量不能超过 " + MAX_RULES + " 条");
        }
        for (Map<String, Object> mapping : mappings) {
            requireText(mapping.get("sourceField"), "字段重命名必须选择源字段");
            requireText(mapping.get("targetField"), "字段重命名必须填写目标字段");
        }
    }

    private void validateTreeTransform(Object node) {
        if (!isEnabled(node)) {
            return;
        }
        Map<String, Object> config = asMap(node, PluginKeys.TREE_TRANSFORM);
        requireText(config.get("idField"), "Tree形结构必须配置 ID 字段");
        requireText(config.get("parentIdField"), "Tree形结构必须配置父 ID 字段");
    }

    private void validateSingleObject(Object node) {
        if (!isEnabled(node)) {
            return;
        }
        Map<String, Object> config = asMap(node, PluginKeys.SINGLE_OBJECT);
        String selectMode = PluginUtils.str(config.get("selectMode"));
        if ("condition".equals(selectMode)) {
            requireText(config.get("conditionField"), "单对象按条件匹配时必须配置条件字段");
            String operator = PluginUtils.str(config.get("conditionOperator"));
            requireText(operator, "单对象按条件匹配时必须配置操作符");
            if (PluginUtils.isRegexOperator(operator)) {
                checkRegex(config.get("conditionValue"));
            }
        }
    }

    private void checkRegex(Object value) {
        try {
            PluginUtils.compileRegex(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Map<String, Object> asMap(Object node, String key) {
        if (!(node instanceof Map<?, ?> map)) {
            throw new IllegalArgumentException("数据插件[" + key + "]配置格式不正确");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> config = (Map<String, Object>) map;
        return config;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asList(Object value, String label) {
        if (value == null) {
            return List.of();
        }
        if (!(value instanceof List<?> list)) {
            throw new IllegalArgumentException(label + "配置格式不正确");
        }
        List<Map<String, Object>> result = new ArrayList<>(list.size());
        for (Object item : list) {
            if (!(item instanceof Map)) {
                throw new IllegalArgumentException(label + "配置格式不正确");
            }
            result.add((Map<String, Object>) item);
        }
        return result;
    }

    private void requireText(Object value, String message) {
        if (PluginUtils.isBlank(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    private boolean isEnabled(Object node) {
        if (!(node instanceof Map<?, ?> map)) {
            return false;
        }
        Object enabled = map.get("enabled");
        if (enabled instanceof Boolean b) {
            return b;
        }
        return enabled != null && Boolean.parseBoolean(enabled.toString());
    }
}
