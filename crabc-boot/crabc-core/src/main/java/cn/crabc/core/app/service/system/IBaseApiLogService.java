package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.BaseApiLog;
import cn.crabc.core.app.entity.param.ApiLogParam;
import cn.crabc.core.datasource.util.PageInfo;


/**
 * API日志 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiLogService {

    /**
     * 添加日志
     *
     * @param log
     * @return
     */
    Integer addLog(BaseApiLog log);

    /**
     * 日志列表
     *
     * @param param
     * @return
     */
    PageInfo page(ApiLogParam param);

    /**
     * 日志详情
     *
     * @param logId
     * @return
     */
    BaseApiLog logDetail(Long logId);
}
