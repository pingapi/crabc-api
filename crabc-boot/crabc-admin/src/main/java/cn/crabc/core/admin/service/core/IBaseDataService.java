package cn.crabc.core.admin.service.core;

import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.spi.bean.BaseDataSource;
import java.util.Map;

/**
 * 通用方法服务类
 *
 * @author yuqf
 */
public interface IBaseDataService {
    /**
     * 测试数据库
     *
     * @param dataSource
     * @return
     */
    String testConnection(BaseDataSource dataSource);

    /**
     * SQL预览
     *
     * @param datasourceId
     * @param schema
     * @param sql
     * @return
     */
    PreviewVO sqlPreview(String datasourceId, String schema, String sql);

    /**
     * 测试API
     *
     * @param datasourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    Object execute(String datasourceId,String schema, String sql, Map<String, Object> params);
}
