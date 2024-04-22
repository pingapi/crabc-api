package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseApiLog;
import cn.crabc.core.app.entity.param.ApiLogParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * API访问日志 Mapper接口
 *
 * @author yuqf
 */
@Mapper
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
