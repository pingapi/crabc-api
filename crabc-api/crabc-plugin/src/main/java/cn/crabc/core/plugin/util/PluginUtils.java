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
    @SuppressWarnings({"unchecked", "rawtypes"})
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
            case "regex" -> !blank && Pattern.compile(str(expected)).matcher(str(actual)).find();
            case "in" -> !blank && inList(actual, expected);
            case "isNull" -> blank;
            case "isNotNull" -> !blank;
            default -> false;
        };
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
     * 按类型脱敏
     *
     * @param value 原始值
     * @param type  脱敏类型：general、phone、idCard、email、bankCard、name、address
     */
    public static String mask(String value, String type) {
        if (value == null || value.isBlank()) {
            return value;
        }
        int len = value.length();
        return switch (type == null ? "general" : type) {
            case "phone" -> len <= 7 ? value : value.substring(0, 3) + "****" + value.substring(len - 4);
            case "idCard" -> len <= 10 ? value : value.substring(0, 6) + "********" + value.substring(len - 4);
            case "email" -> {
                int at = value.indexOf('@');
                yield at <= 1 ? value : value.charAt(0) + "****" + value.substring(at);
            }
            case "bankCard" -> len <= 4 ? value : "************" + value.substring(len - 4);
            case "name" -> len <= 1 ? value : value.charAt(0) + "*".repeat(len - 1);
            case "address" -> len <= 3 ? value : value.substring(0, 3) + "****";
            // 通用脱敏：保留首尾字符
            default -> len <= 2 ? value.charAt(0) + "*" : value.charAt(0) + "*".repeat(len - 2) + value.charAt(len - 1);
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
}
