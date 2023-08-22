package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.vo.ApiParamsVO;
import cn.crabc.core.app.entity.vo.RequestParamsVO;
import cn.crabc.core.app.mapper.BaseApiParamMapper;
import cn.crabc.core.app.service.system.IBaseApiParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API参数 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseApiParamServiceImpl implements IBaseApiParamService {

    @Autowired
    private BaseApiParamMapper baseApiParamMapper;

    @Override
    public ApiParamsVO getApiDetailsParams(Long apiId) {
        ApiParamsVO apiParams = new ApiParamsVO();
        List<RequestParamsVO> requestParams = baseApiParamMapper.selectApiParams(apiId);
        Map<String, List<RequestParamsVO>> paramMap = requestParams.stream().collect(Collectors.groupingBy(RequestParamsVO::getParamModel));
        apiParams.setReqParams(paramMap.get("request"));
        apiParams.setResParams(paramMap.get("response"));
        return apiParams;
    }
}
