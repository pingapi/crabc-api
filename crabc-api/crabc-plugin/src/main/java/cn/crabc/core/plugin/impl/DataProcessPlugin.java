package cn.crabc.core.plugin.impl;

import cn.crabc.core.plugin.DataPlugin;
import cn.crabc.core.plugin.PluginKeys;
import cn.crabc.core.plugin.util.PluginUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 过滤清洗插件（dataProcess）
 * <p>
 * 规则列表按配置顺序依次执行，支持四种规则类型：
 * filter（数据过滤）、sort（数据排序）、encrypt（数据脱敏）、calculate（字段计算）
 *
 * @author yuqf
 */
@Component
public class DataProcessPlugin implements DataPlugin {

    @Override
    public String getName() {
        return PluginKeys.DATA_PROCESS;
    }

    @Override
    public Object apply(Object data, Map<String, Object> config, Map<String, Object> requestParams) {
        List<Map<String, Object>> rows = PluginUtils.asRowList(data);
        if (rows == null || rows.isEmpty()) {
            return data;
        }
        if (!(config.get("rules") instanceof List<?> rules) || rules.isEmpty()) {
            return data;
        }
        List<Map<String, Object>> result = new ArrayList<>(rows);
        for (Object item : rules) {
            if (!(item instanceof Map<?, ?> rawRule)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> rule = (Map<String, Object>) rawRule;
            String type = PluginUtils.str(rule.get("type"));
            switch (type == null ? "" : type) {
                case "filter" -> result = filter(result, rule);
                case "sort" -> sort(result, rule);
                case "encrypt" -> encrypt(result, rule);
                case "calculate" -> calculate(result, rule);
                default -> {
                    // 未知规则类型跳过
                }
            }
        }
        return result;
    }

    /**
     * 数据过滤：保留满足条件的行
     */
    private List<Map<String, Object>> filter(List<Map<String, Object>> rows, Map<String, Object> rule) {
        String field = PluginUtils.str(rule.get("field"));
        String operator = PluginUtils.str(rule.get("operator"));
        Object value = rule.get("value");
        if (field == null || operator == null) {
            return rows;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (PluginUtils.matches(row.get(field), operator, value)) {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * 数据排序（原列表就地排序，多规则顺序应用形成多级排序）
     */
    private void sort(List<Map<String, Object>> rows, Map<String, Object> rule) {
        String field = PluginUtils.str(rule.get("field"));
        boolean asc = !"desc".equalsIgnoreCase(PluginUtils.str(rule.get("sortOrder")));
        if (field == null) {
            return;
        }
        Comparator<Map<String, Object>> comparator = Comparator.comparing(
                row -> PluginUtils.str(row.get(field)) == null ? "" : PluginUtils.str(row.get(field)),
                (a, b) -> {
                    if (a.isEmpty() && b.isEmpty()) {
                        return 0;
                    }
                    if (a.isEmpty()) {
                        return 1;
                    }
                    if (b.isEmpty()) {
                        return -1;
                    }
                    return PluginUtils.compare(a, b);
                });
        rows.sort(asc ? comparator : comparator.reversed());
    }

    /**
     * 数据脱敏
     */
    private void encrypt(List<Map<String, Object>> rows, Map<String, Object> rule) {
        if (!(rule.get("encryptFields") instanceof List<?> fields) || fields.isEmpty()) {
            return;
        }
        String encryptType = PluginUtils.str(rule.get("encryptType"));
        for (Map<String, Object> row : rows) {
            for (Object fieldObj : fields) {
                String field = PluginUtils.str(fieldObj);
                if (field == null) {
                    continue;
                }
                Object value = row.get(field);
                if (value instanceof String s) {
                    row.put(field, PluginUtils.mask(s, encryptType));
                } else if (value != null) {
                    row.put(field, PluginUtils.mask(PluginUtils.str(value), encryptType));
                }
            }
        }
    }

    /**
     * 字段计算：新字段 = 源字段 op 操作数（字段值或固定值）
     */
    private void calculate(List<Map<String, Object>> rows, Map<String, Object> rule) {
        String sourceField = PluginUtils.str(rule.get("sourceField"));
        String calcOperator = PluginUtils.str(rule.get("calcOperator"));
        String newField = PluginUtils.str(rule.get("field"));
        boolean fixedValue = "fixed".equalsIgnoreCase(PluginUtils.str(rule.get("valueType")));
        String valueField = PluginUtils.str(rule.get("valueField"));
        Object calcValue = rule.get("calcValue");
        if (sourceField == null || newField == null || calcOperator == null) {
            return;
        }
        for (Map<String, Object> row : rows) {
            Object source = row.get(sourceField);
            if (PluginUtils.isBlank(source)) {
                continue;
            }
            Object operand = fixedValue ? calcValue : row.get(valueField);
            row.put(newField, calc(source, operand, calcOperator));
        }
    }

    /**
     * 单行计算
     */
    private Object calc(Object source, Object operand, String operator) {
        // concat 直接拼接
        if ("concat".equals(operator)) {
            return PluginUtils.str(source) + PluginUtils.str(operand);
        }
        BigDecimal a = PluginUtils.toNumber(source);
        BigDecimal b = PluginUtils.toNumber(operand);
        if (a == null || b == null) {
            throw new IllegalArgumentException("字段计算的操作数必须为数字：" + source + " " + operator + " " + operand);
        }
        BigDecimal result = switch (operator) {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            case "/" -> {
                if (BigDecimal.ZERO.compareTo(b) == 0) {
                    throw new IllegalArgumentException("字段计算除数为 0");
                }
                yield a.divide(b, 6, RoundingMode.HALF_UP);
            }
            default -> throw new IllegalArgumentException("不支持的计算操作符：" + operator);
        };
        // 无小数部分时按整数返回，避免 3.0 形式
        if (result.stripTrailingZeros().scale() <= 0) {
            return result.stripTrailingZeros().toBigInteger().longValue();
        }
        return result.doubleValue();
    }
}
