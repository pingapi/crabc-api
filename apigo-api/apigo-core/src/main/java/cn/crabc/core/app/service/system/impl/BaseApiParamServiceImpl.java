package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.vo.ApiParamsVO;
import cn.crabc.core.app.entity.vo.RequestParamsVO;
import cn.crabc.core.app.mapper.BaseApiParamMapper;
import cn.crabc.core.app.service.system.IBaseApiParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Value("${crabc.result.code:code}")
    private String resultCode;
    @Value("${crabc.result.msg:msg}")
    private String resultMsg;
    @Value("${crabc.result.data:data}")
    private String resultData;

    public static final String REQUEST ="request";
    /**
     * 响应参数
     */
    public static final String RESPONSE ="response";

    @Override
    public ApiParamsVO getApiDetailsParams(Long apiId) {
        ApiParamsVO apiParams = new ApiParamsVO();
        List<RequestParamsVO> requestParams = baseApiParamMapper.selectApiParams(apiId);
        Map<String, List<RequestParamsVO>> paramMap = requestParams.stream().collect(Collectors.groupingBy(RequestParamsVO::getParamModel));
        apiParams.setReqParams(paramMap.get(REQUEST));
        List<RequestParamsVO> responseParam = paramMap.get(RESPONSE);
        apiParams.setResParams(this.setResponseParams(responseParam));
        return apiParams;
    }

    /**
     * 处理返回参数
     * @param responseParams
     * @return
     */
    public List<RequestParamsVO> setResponseParams(List<RequestParamsVO> responseParams) {
        List<RequestParamsVO> list = new ArrayList<>();
        if (responseParams == null || responseParams.isEmpty()) {
            return list;
        }
        RequestParamsVO responseCode = new RequestParamsVO();
        responseCode.setParamName(resultCode);
        responseCode.setParamDesc("状态码：0 成功");
        responseCode.setParamType("Int");
        responseCode.setExample("0");
        responseCode.setRequired("Y");
        list.add(responseCode);

        RequestParamsVO responseMsg = new RequestParamsVO();
        responseMsg.setParamName(resultMsg);
        responseMsg.setParamDesc("错误信息");
        responseMsg.setParamType("String");
        responseMsg.setExample("");
        responseMsg.setChildren(null);
        responseCode.setRequired("Y");
        list.add(responseMsg);

        RequestParamsVO responseData = new RequestParamsVO();
        responseData.setParamName(resultData);
        responseData.setParamDesc("数据");
        responseData.setParamType("Array");
        responseData.setExample("");
        responseCode.setRequired("Y");
        responseData.setChildren(responseParams);
        list.add(responseData);
        return list;
    }

}
