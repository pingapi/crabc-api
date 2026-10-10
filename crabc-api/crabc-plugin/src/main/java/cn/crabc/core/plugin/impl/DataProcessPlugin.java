package cn.crabc.core.plugin.impl;

import cn.crabc.core.plugin.DataPlugin;
import cn.crabc.core.plugin.PluginKeys;
import cn.crabc.core.plugin.util.PluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    private static final Logger log = LoggerFactory.getLogger(DataProcessPlugin.class);

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
        if (!PluginUtils.containsField(rows, field)) {
            log.warn("数据插件[{}]过滤字段[{}]不存在于结果集中，规则已跳过", getName(), field);
            return rows;
        }
        // 正则预编译一次，避免逐行编译带来的开销与被滥用的风险
        Pattern pattern = PluginUtils.isRegexOperator(operator) ? PluginUtils.compileRegex(value) : null;
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Object actual = row.get(field);
            boolean matched = pattern != null
                    ? PluginUtils.matchesRegex(actual, pattern)
                    : PluginUtils.matches(actual, operator, value);
            if (matched) {
                result.add(row);
            }
        }
        return result;
    }

    /**
     * 数据排序（多规则顺序应用形成多级排序）
     * <p>
     * 先一次性抽取排序键再排序，避免比较阶段反复做字符串/数值转换；空值始终排在最后。
     */
    private void sort(List<Map<String, Object>> rows, Map<String, Object> rule) {
        String field = PluginUtils.str(rule.get("field"));
        if (field == null) {
            return;
        }
        if (!PluginUtils.containsField(rows, field)) {
            log.warn("数据插件[{}]排序字段[{}]不存在于结果集中，规则已跳过", getName(), field);
            return;
        }
        boolean asc = !"desc".equalsIgnoreCase(PluginUtils.str(rule.get("sortOrder")));
        int sign = asc ? 1 : -1;
        List<RowSortKey> keys = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            Object value = row.get(field);
            keys.add(new RowSortKey(row, PluginUtils.str(value), PluginUtils.toNumber(value)));
        }
        keys.sort((a, b) -> {
            boolean aBlank = PluginUtils.isBlank(a.text());
            boolean bBlank = PluginUtils.isBlank(b.text());
            if (aBlank || bBlank) {
                if (aBlank && bBlank) {
                    return 0;
                }
                // 空值不参与方向反转，恒排最后
                return aBlank ? 1 : -1;
            }
            int compare = a.number() != null && b.number() != null
                    ? a.number().compareTo(b.number())
                    : a.text().compareTo(b.text());
            return sign * compare;
        });
        rows.clear();
        for (RowSortKey key : keys) {
            rows.add(key.row());
        }
    }

    /**
     * 数据脱敏
     */
    private void encrypt(List<Map<String, Object>> rows, Map<String, Object> rule) {
        if (!(rule.get("encryptFields") instanceof List<?> fields) || fields.isEmpty()) {
            return;
        }
        String encryptType = PluginUtils.str(rule.get("encryptType"));
        for (Object fieldObj : fields) {
            String field = PluginUtils.str(fieldObj);
            if (!PluginUtils.containsField(rows, field)) {
                log.warn("数据插件[{}]脱敏字段[{}]不存在于结果集中，规则已跳过", getName(), field);
            }
        }
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
     * <p>
     * concat 直接把 null 视作空串；数字运算遇到非数字操作数时按行跳过并告警，
     * 不因单行脏数据中断整个接口。
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
        if (!PluginUtils.containsField(rows, sourceField)) {
            log.warn("数据插件[{}]计算源字段[{}]不存在于结果集中，规则已跳过", getName(), sourceField);
            return;
        }
        if ("concat".equals(calcOperator)) {
            for (Map<String, Object> row : rows) {
                Object source = row.get(sourceField);
                if (PluginUtils.isBlank(source)) {
                    continue;
                }
                Object operand = fixedValue ? calcValue : row.get(valueField);
                row.put(newField, PluginUtils.str(source) + (operand == null ? "" : PluginUtils.str(operand)));
            }
            return;
        }
        int skipped = 0;
        for (Map<String, Object> row : rows) {
            Object source = row.get(sourceField);
            Object operand = fixedValue ? calcValue : row.get(valueField);
            BigDecimal a = PluginUtils.toNumber(source);
            BigDecimal b = PluginUtils.toNumber(operand);
            if (a == null || b == null || ("/".equals(calcOperator) && BigDecimal.ZERO.compareTo(b) == 0)) {
                skipped++;
                continue;
            }
            row.put(newField, calc(a, b, calcOperator));
        }
        if (skipped > 0) {
            log.warn("数据插件[{}]字段计算有 {} 行因源字段为空/非数字或除数为 0 被跳过（源字段：{}）",
                    getName(), skipped, sourceField);
        }
    }

    /**
     * 单行数值计算
     */
    private Object calc(BigDecimal a, BigDecimal b, String operator) {
        BigDecimal result = switch (operator) {
            case "+" -> a.add(b);
            case "-" -> a.subtract(b);
            case "*" -> a.multiply(b);
            case "/" -> a.divide(b, 6, RoundingMode.HALF_UP);
            default -> throw new IllegalArgumentException("不支持的计算操作符：" + operator);
        };
        // 无小数部分时按整数返回，避免 3.0 形式
        if (result.stripTrailingZeros().scale() <= 0) {
            return result.stripTrailingZeros().toBigInteger().longValue();
        }
        return result.doubleValue();
    }

    /**
     * 排序键：预先抽取文本与数值形态，避免比较阶段重复转换
     */
    private record RowSortKey(Map<String, Object> row, String text, BigDecimal number) {
    }
}
