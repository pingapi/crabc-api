package cn.crabc.core.system.service.system;

import cn.crabc.core.system.entity.vo.PreviewVO;

import java.util.Map;

/**
 * API测试和预览 接口
 *
 * @author yuqf
 */
public interface IBaseApiTestService {

    /**
     * SQL预览
     * @param datasourceId
     * @param sql
     * @return
     */
    PreviewVO sqlPreview(String datasourceId, String sql);

    /**
     * 测试API
     * @param datasourceId
     * @param sql
     * @param params
     * @return
     */
    Object testApi(String datasourceId, String sql, Map<String,Object> params);
}
