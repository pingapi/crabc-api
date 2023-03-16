package cn.crabc.core.admin.service.core;

import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Table;

import java.util.List;
import java.util.Map;

/**
 * 通用方法服务类
 *
 * @author yuqf
 */
public interface IBaseDataService {

    List<String> getSchemas(String dataSourceId);

    List<Table> getTables(String dataSourceId, String schema);

    List<Column> getColumns(String dataSourceId, String schema, String table);

    /**
     * 测试数据库
     *
     * @param dataSource
     * @return
     */
    Integer testConnection(BaseDataSource dataSource);

    /**
     * 通用查询方法
     *
     * @param datasourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    List<Map<String, Object>> query(String datasourceId, String schema, String sql, List<Object> params);

    /**
     * 通用操作方法
     *
     * @param datasourceId
     * @param sql
     * @param params
     * @return
     */
    Integer execute(String datasourceId, String schema, String sql, List<Object> params);
}
