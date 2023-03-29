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
    String testConnection(BaseDataSource dataSource);

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
     * 新增操作
     *
     * @param datasourceId
     * @param sql
     * @param params
     * @return
     */
    Integer add(String datasourceId, String schema, String sql, List<Object> params);

    /**
     * 修改操作
     *
     * @param datasourceId
     * @param sql
     * @param params
     * @return
     */
    Integer update(String datasourceId, String schema, String sql, List<Object> params);

    /**
     * 删除操作
     *
     * @param datasourceId
     * @param sql
     * @param params
     * @return
     */
    Integer delete(String datasourceId, String schema, String sql, List<Object> params);
}
