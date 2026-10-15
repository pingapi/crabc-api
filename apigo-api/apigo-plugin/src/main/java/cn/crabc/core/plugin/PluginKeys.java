package cn.crabc.core.plugin;

import java.util.List;

/**
 * 数据插件 key 常量与执行顺序定义
 * <p>
 * 顺序约定：先做行级清洗与字段处理，再做结构转换，最后收敛结果形态。
 * 未在插件引擎中注册实现的 key 会被执行器跳过（预留扩展）。
 *
 * @author yuqf
 */
public final class PluginKeys {

    /** 过滤清洗：filter/sort/encrypt/calculate */
    public static final String DATA_PROCESS = "dataProcess";
    /** 字段格式化（预留） */
    public static final String FIELD_FORMAT = "fieldFormat";
    /** 字段值替换（预留） */
    public static final String FIELD_REPLACE = "fieldReplace";
    /** 字段合并（预留） */
    public static final String DATA_MERGE = "dataMerge";
    /** 字段重命名 */
    public static final String FIELD_MAPPING = "fieldMapping";
    /** 字典转换（预留） */
    public static final String DICT_TRANSFORM = "dictTransform";
    /** 分组聚合（预留） */
    public static final String DATA_AGGREGATION = "dataAggregation";
    /** 多级嵌套（预留） */
    public static final String NESTED_STRUCTURE = "nestedStructure";
    /** 层级转树 */
    public static final String TREE_TRANSFORM = "treeTransform";
    /** 集合转单对象 */
    public static final String SINGLE_OBJECT = "singleObject";
    /** 数据抽样（预留） */
    public static final String DATA_SAMPLE = "dataSample";
    /** JS 脚本处理（预留，待引入 GraalJS 后实现） */
    public static final String SCRIPT_TRANSFORM = "scriptTransform";

    /**
     * 插件链执行顺序
     */
    public static final List<String> ORDER = List.of(
            DATA_PROCESS,
            FIELD_FORMAT,
            FIELD_REPLACE,
            DATA_MERGE,
            FIELD_MAPPING,
            DICT_TRANSFORM,
            DATA_AGGREGATION,
            NESTED_STRUCTURE,
            TREE_TRANSFORM,
            SINGLE_OBJECT,
            DATA_SAMPLE,
            SCRIPT_TRANSFORM
    );

    private PluginKeys() {
    }
}
