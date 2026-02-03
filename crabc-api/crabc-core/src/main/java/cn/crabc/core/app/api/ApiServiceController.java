package cn.crabc.core.app.api;

import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.dto.ApiInfoDTO;
import cn.crabc.core.app.enums.ResultTypeEnum;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.ApiThreadLocal;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API 接收处理层
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/web")
public class ApiServiceController {

    @Autowired
    private IBaseDataService baseDataService;

    /**
     * API GET请求方法
     *
     * @return
     */
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.DELETE})
    public Result getService(@RequestParam(required = false) Map<String, Object> paramMap, HttpServletResponse response) {
        ApiInfoDTO api = ApiThreadLocal.get();
        if (api == null) {
            response.setStatus(400);
            return Result.error(ErrorStatusEnum.API_INVALID.getCode(), ErrorStatusEnum.API_INVALID.getMassage());
        }
        
        // 参数校验
        if (!validateParams(api, paramMap)) {
            response.setStatus(400);
            return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
        }

        return executeApi(api, paramMap);
    }

    /**
     * API post请求
     *
     * @param paramMap
     * @param body
     * @return
     */
    @RequestMapping(value = "/**", method = {RequestMethod.POST, RequestMethod.PUT})
    public Result postService(@RequestParam(required = false) Map<String, Object> paramMap, @RequestBody(required = false) Object body, HttpServletResponse response) {
        ApiInfoDTO api = ApiThreadLocal.get();
        if (api == null) {
            response.setStatus(400);
            return Result.error(ErrorStatusEnum.API_INVALID.getCode(), ErrorStatusEnum.API_INVALID.getMassage());
        }

        // 合并请求参数
        Map<String, Object> mergedParams = mergeParams(paramMap, body);
        
        // 参数校验
        if (!checkParams(api.getRequestParams(), mergedParams)) {
            response.setStatus(400);
            return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
        }

        mergedParams.put(BaseConstant.PAGE_SETUP, api.getPageSetup());
        return executeApi(api, mergedParams);
    }

    /**
     * 校验参数
     *
     * @param apiParams
     * @param paramMap
     * @return
     */
    public boolean checkParams(List<BaseApiParam> apiParams, Map<String, Object> paramMap) {
        if (apiParams == null || paramMap == null) {
            return true;
        }
        
        for (BaseApiParam param : apiParams) {
            String paramName = param.getParamName();
            String paramType = param.getParamType();
            Object value = paramMap.get(paramName);

            // 必填参数校验
            if ("Y".equals(param.getRequired()) && !paramMap.containsKey(paramName)) {
                return false;
            }

            // Array类型参数处理
            if ("Array".equalsIgnoreCase(paramType)) {
                if (value == null || "".equals(value)) {
                    return false;
                }
                String[] values = value.toString().split(",");
                paramMap.put(paramName, Arrays.asList(values));
            }
        }
        return true;
    }

    /**
     * 验证请求参数
     */
    private boolean validateParams(ApiInfoDTO api, Map<String, Object> paramMap) {
        if (paramMap == null) {
            return true;
        }
        
        paramMap.put(BaseConstant.PAGE_SETUP, api.getPageSetup() == null ? 0 : api.getPageSetup());
        
        return checkParams(api.getRequestParams(), paramMap) && 
               !(api.getPageSetup() == 1 && !paramMap.containsKey(BaseConstant.PAGE_NUM));
    }

    /**
     * 合并请求参数
     */
    private Map<String, Object> mergeParams(Map<String, Object> paramMap, Object body) {
        Map<String, Object> mergedParams = new HashMap<>();
        if (paramMap != null) {
            mergedParams.putAll(paramMap);
        }
        if (body instanceof Map) {
            mergedParams.putAll((Map<String, Object>) body);
        }
        return mergedParams;
    }

    /**
     * 执行API调用
     */
    private Result executeApi(ApiInfoDTO api, Map<String, Object> params) {
        Object data = baseDataService.execute(api.getDatasourceId().toString(), api.getDatasourceType(),
                                            api.getSchemaName(), api.getSqlScript(), params);
                                            
        if (ResultTypeEnum.ONE.getName().equals(api.getResultType()) && data instanceof List) {
            List<Object> list = (List<Object>) data;
            return Result.success(list.isEmpty() ? null : list.get(0));
        }
        return Result.success(data);
    }
}
