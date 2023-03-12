package cn.crabc.core.admin.mapper;

import cn.crabc.core.admin.entity.BaseApiLog;
import cn.crabc.core.admin.entity.param.ApiLogParam;

import java.util.List;

/**
 * API访问日志 Mapper接口
 *
 * @author yuqf
 */
public interface BaseApiLogMapper {

    /**
     * 添加日志
     *
     * @param log
     * @return
     */
    Integer insert(BaseApiLog log);

    /**
     * 日志列表
     *
     * @param param
     * @return
     */
    List<BaseApiLog> selectList(ApiLogParam param);

    /**
     * 日志详情
     *
     * @param logId
     * @return
     */
    BaseApiLog selectOne(Long logId);
}
