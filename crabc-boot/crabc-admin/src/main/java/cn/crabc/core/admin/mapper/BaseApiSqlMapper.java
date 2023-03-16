package cn.crabc.core.admin.mapper;

import cn.crabc.core.admin.entity.BaseApiSql;
import org.apache.ibatis.annotations.Param;

/**
 * API SQL配置 Mapper接口
 *
 * @author yuqf
 */
public interface BaseApiSqlMapper{

    /**
     * 根据apiId查询SQL配置
     * @param apiId
     * @return
     */
    BaseApiSql selectApiSql(Long apiId);

    /**
     * 插入API服务SQL配置
     * @param baseApiSql
     * @return
     */
    Integer insertApiSql(BaseApiSql baseApiSql);

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
    Integer deleteApiSql(@Param("apiId") Long apiId, @Param("userId") String userId);
}
