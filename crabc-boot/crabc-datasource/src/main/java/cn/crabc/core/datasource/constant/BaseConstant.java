package cn.crabc.core.datasource.constant;

/**
 * 常量类
 *
 * @author yuqf
 */
public class BaseConstant {

    /**
     * SQL脚本
     */
    public final static String BASE_SQL = "base_sql";

    /**
     * 数据源ID
     */
    public final static String DATA_SOURCE_ID = "data_source_id";

    /**
     * 页码
     */
    public final static String PAGE_NUM = "pageNum";

    /**
     * 每页大小
     */
    public final static String PAGE_SIZE = "pageSize";

    /**
     * 分页设置
     */
    public final static String PAGE_SETUP = "pageSetup";

    /**
     * 分页统计
     */
    public final static Integer PAGE_COUNT = 2;

    /**
     * 只分页
     */
    public final static Integer PAGE_ONLY = 1;

    /**
     * redis
     */
    public final static String REDIS_CACHE = "redis";

    /**
     * API数据缓存
     */
    public static final String CACHE_API_DETAIL = "api_detail";

    public static final String CACHE_METADATA_CATALOG = "metadata_catalog:";
    /**
     * redis缓存schema
     */
    public static final String CACHE_METADATA_SCHEMA = "metadata_schema:";
    /**
     * redis缓存表
     */
    public static final String CACHE_METADATA_TABLE = "metadata_table:";
    /**
     * redis缓存字段
     */
    public static final String CACHE_METADATA_COLUMN = "metadata_column:";
    /**
     * SQL执行方式
     */
    public static final String BASE_API_EXEC_TYPE = "base_api_exec_type:";
}
