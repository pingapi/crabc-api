package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.param.ApiTestParam;
import cn.crabc.core.app.entity.vo.PreviewVO;
import cn.crabc.core.app.enums.ResultTypeEnum;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API运行和测试
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/test")
public class ApiTestController {

    @Autowired
    private IBaseDataService baseDataService;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 运行预览
     *
     * @param api
     * @return
     */
    @PostMapping("/running")
    public Result runApiSql(@RequestBody ApiTestParam api) {
        if (api.getDatasourceId() == null) {
            return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
        }
        PreviewVO previewVO = baseDataService.sqlPreview(api.getDatasourceId(),api.getDatasourceType(), api.getSchemaName(), api.getSqlScript());
        return Result.success(previewVO);
    }

    /**
     * 在线测试API
     *
     * @param params
     * @return
     */
    @PostMapping("/verify/{apiId}")
    public Result testApiSql(@PathVariable Long apiId, @RequestBody ApiTestParam params) throws Exception {
        if (params.getDatasourceId() == null) {
            return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
        }
        if (params.getDatasourceType() == null) {
            params.setDatasourceType("mysql");
        }
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // body, 不支持数组对象
            if (params.getBodyData() != null && params.getBodyData().startsWith("{")) {
                paramsMap = objectMapper.readValue(params.getBodyData(), HashMap.class);
            }
            // querya参数
            Object requestParams = params.getRequestParams();
            if (requestParams instanceof Map) {
                Map<String, Object> queryParam = (Map<String, Object>) requestParams;
                // 校验参数
                if (!splitAarry(queryParam)) {
                    return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
                }
                paramsMap.putAll(queryParam);
            }else if (requestParams instanceof List) {
                List<Map<String,Object>> paramsList = (List<Map<String, Object>>) requestParams;
                for (Map<String, Object> entry : paramsList) {
                    // 校验参数
                    if (!splitAarry(entry)) {
                        return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
                    }
                    paramsMap.put(entry.get("name").toString(), entry.get("value"));
                }
            }

            String sql = params.getSqlScript();
            Object data = baseDataService.execute(params.getDatasourceId(),params.getDatasourceType(), params.getSchemaName(),sql, paramsMap);
            if (ResultTypeEnum.ONE.getName().equals(params.getResultType()) && data instanceof List) {
                List<Object> list  = (List<Object>) data;
                resultMap.put("data", objectMapper.writeValueAsString(Result.success(list.isEmpty() ? null: list.get(0))));
            }else{
                resultMap.put("data", objectMapper.writeValueAsString(Result.success(data)));
            }
            resultMap.put("code", 0);
        }catch (Exception e) {
            return Result.error("测试异常，请检查参数或者SQL是否正常！");
        }
        return Result.success(resultMap);
    }
    /**
     * 校验参数
     *
     * @param queryParam
     * @return
     */
    public boolean splitAarry(Map<String, Object> queryParam) {
        if (queryParam == null ||queryParam.isEmpty()) {
            return true;
        }
        String paramType = queryParam.get("paramType")+"";
        Object paramValue = queryParam.get("value");

        // Array类型的拼接参数 进行拆分
        if ("Array".equalsIgnoreCase(paramType) && paramValue != null) {
            String[] values = paramValue.toString().split(",");
            queryParam.put("value", Arrays.asList(values));
        }else if ("Array".equalsIgnoreCase(paramType) && "".equals(paramValue)) {
            return false;
        }
        return true;
    }
}
