package cn.crabc.core.admin.service.system;

import cn.crabc.core.admin.entity.BaseApiSql;

/**
 * API SQL配置 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiSqlService {

    /**
     * 根据apiId查询SQL信息
     * @param apiId
     * @return
     */
    BaseApiSql getApiSql(Long apiId);


    /**
     * 插入API服务SQL配置
     * @param baseApiSql
     * @return
     */
    Integer addApiSql(BaseApiSql baseApiSql);

    /**
     * 更新API服务SQL配置
     * @param baseApiSql
     * @return
     */
    Integer updateApiSql(BaseApiSql baseApiSql);

    /**
     * 删除
     * @param apiId
     * @return
     */
    Integer deleteApiSql(Long apiId, String userId);
}
