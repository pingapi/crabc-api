package cn.crabc.core.admin.service.system;

import cn.crabc.core.admin.entity.vo.PreviewVO;

import java.util.Map;

/**
 * API测试和预览 接口
 *
 * @author yuqf
 */
public interface IBaseApiTestService {

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
    Object testApi(String datasourceId,String schema, String sql, Map<String, Object> params);
}
