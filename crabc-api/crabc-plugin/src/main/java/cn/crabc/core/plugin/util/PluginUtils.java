package cn.crabc.core.plugin.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 数据插件公共工具：取值、类型转换、条件匹配、脱敏
 *
 * @author yuqf
 */
public final class PluginUtils {

    /**
     * 正则表达式最大长度，防止超长/恶意模式带来的编译与匹配开销
     */
    public static final int MAX_REGEX_LENGTH = 200;

    private PluginUtils() {
    }

    /**
     * 转字符串（null 安全）
     */
    public static String str(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    /**
     * 是否为空白值（null 或空字符串）
     */
    public static boolean isBlank(Object value) {
        return value == null || String.valueOf(value).isBlank();
    }

    /**
     * 转 BigDecimal，非数字返回 null
     */
    public static BigDecimal toNumber(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number n) {
            return new BigDecimal(n.toString());
        }
        if (value == null) {
            return null;
        }
        try {
            return new BigDecimal(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 数值感知比较：双方均为数字时按数值比较，否则按字符串比较
     */
    public static int compare(Object a, Object b) {
        BigDecimal na = toNumber(a);
        BigDecimal nb = toNumber(b);
        if (na != null && nb != null) {
            return na.compareTo(nb);
        }
        return str(a).compareTo(str(b));
    }

    /**
     * 条件匹配
     *
     * @param actual   实际值
     * @param operator 操作符：==、!=、>、<、>=、<=、contains、notContains、startsWith、endsWith、regex、in、isNull、isNotNull
     * @param expected 目标值
     */
    public static boolean matches(Object actual, String operator, Object expected) {
        boolean blank = isBlank(actual);
        return switch (operator == null ? "" : operator) {
            case "==", "equals" -> !blank && compareEquals(actual, expected);
            case "!=", "notEquals" -> blank || !compareEquals(actual, expected);
            case ">" -> !blank && compare(actual, expected) > 0;
            case "<" -> !blank && compare(actual, expected) < 0;
            case ">=" -> !blank && compare(actual, expected) >= 0;
            case "<=" -> !blank && compare(actual, expected) <= 0;
            case "contains" -> !blank && str(actual).contains(str(expected));
            case "notContains" -> blank || !str(actual).contains(str(expected));
            case "startsWith" -> !blank && str(actual).startsWith(str(expected));
            case "endsWith" -> !blank && str(actual).endsWith(str(expected));
            case "regex" -> matchesRegex(actual, compileRegex(expected));
            case "in" -> !blank && inList(actual, expected);
            case "isNull" -> blank;
            case "isNotNull" -> !blank;
            default -> false;
        };
    }

    /**
     * 是否为正则操作符（供调用方在循环外预编译）
     */
    public static boolean isRegexOperator(String operator) {
        return "regex".equals(operator);
    }

    /**
     * 预编译正则表达式（长度超限直接失败，避免超长模式开销）
     *
     * @param expected 正则表达式文本
     * @return 编译后的 Pattern
     */
    public static Pattern compileRegex(Object expected) {
        String pattern = str(expected);
        if (pattern == null || pattern.isBlank()) {
            throw new IllegalArgumentException("正则表达式不能为空");
        }
        if (pattern.length() > MAX_REGEX_LENGTH) {
            throw new IllegalArgumentException("正则表达式长度不能超过 " + MAX_REGEX_LENGTH + " 个字符");
        }
        return Pattern.compile(pattern);
    }

    /**
     * 使用预编译 Pattern 匹配
     */
    public static boolean matchesRegex(Object actual, Pattern pattern) {
        return !isBlank(actual) && pattern.matcher(str(actual)).find();
    }

    /**
     * 数值感知相等判断
     */
    private static boolean compareEquals(Object actual, Object expected) {
        BigDecimal na = toNumber(actual);
        BigDecimal nb = toNumber(expected);
        if (na != null && nb != null) {
            return na.compareTo(nb) == 0;
        }
        return str(actual).equals(str(expected));
    }

    /**
     * in 匹配：期望值为逗号分隔列表
     */
    private static boolean inList(Object actual, Object expected) {
        String expectedStr = str(expected);
        if (expectedStr == null || expectedStr.isBlank()) {
            return false;
        }
        for (String item : expectedStr.split(",")) {
            if (str(actual).equals(item.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 按类型脱敏（非字符串值会先转字符串，数值型敏感字段建议由 SQL 侧保证为字符串）
     *
     * @param value 原始值
     * @param type  脱敏类型：general、phone、idCard、email、bankCard、name、address
     */
    public static String mask(String value, String type) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        int len = value.length();
        return switch (type == null ? "general" : type) {
            // 手机号：不足 7 位整串脱敏，否则保留前 3 后 4
            case "phone" -> len <= 7 ? "*".repeat(len) : value.substring(0, 3) + "****" + value.substring(len - 4);
            // 身份证：不足 10 位整串脱敏，否则保留前 6 后 4
            case "idCard" -> len <= 10 ? "*".repeat(len) : value.substring(0, 6) + "********" + value.substring(len - 4);
            case "email" -> {
                int at = value.indexOf('@');
                if (at > 1) {
                    yield value.charAt(0) + "****" + value.substring(at);
                }
                yield len == 1 ? value : value.charAt(0) + "*".repeat(len - 1);
            }
            // 银行卡：仅保留末 4 位
            case "bankCard" -> len <= 4 ? "*".repeat(len) : "************" + value.substring(len - 4);
            case "name" -> len == 1 ? value : value.charAt(0) + "*".repeat(len - 1);
            case "address" -> len <= 3 ? value : value.substring(0, 3) + "****";
            // 通用脱敏：保留首尾字符
            default -> switch (len) {
                case 1 -> value;
                case 2 -> value.charAt(0) + "*";
                default -> value.charAt(0) + "*".repeat(len - 2) + value.charAt(len - 1);
            };
        };
    }

    /**
     * 校验行集合：数据为 List 且元素为 Map 时返回，否则返回 null（非列表数据不参与行级插件处理）
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> asRowList(Object data) {
        if (!(data instanceof List<?> list)) {
            return null;
        }
        if (!list.isEmpty() && !(list.getFirst() instanceof Map)) {
            return null;
        }
        return (List<Map<String, Object>>) list;
    }

    /**
     * 判断结果集是否包含指定字段（以首行字段为准），用于对"配置了不存在的字段"给出告警
     *
     * @param rows  结果行
     * @param field 字段名
     */
    public static boolean containsField(List<Map<String, Object>> rows, String field) {
        if (rows == null || rows.isEmpty() || field == null || field.isBlank()) {
            return false;
        }
        return rows.getFirst().containsKey(field);
    }
}
