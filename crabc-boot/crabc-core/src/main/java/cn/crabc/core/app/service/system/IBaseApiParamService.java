package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.vo.ApiParamsVO;

/**
 * API参数 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiParamService {

    ApiParamsVO getApiDetailsParams(Long apiId);
}
