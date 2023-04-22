package cn.crabc.core.admin.service.system;

import cn.crabc.core.admin.entity.vo.ApiParamsVO;

/**
 * API参数 服务接口
 *
 * @author yuqf
 */
public interface IBaseApiParamService {

    ApiParamsVO getApiDetailsParams(Long apiId);
}
