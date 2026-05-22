package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.param.ApiTestParam;
import cn.crabc.core.app.entity.vo.PreviewVO;
import cn.crabc.core.app.enums.ResultTypeEnum;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

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
    private JsonMapper jsonMapper;

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
        // 参数校验
        if (params.getDatasourceId() == null) {
            return Result.error(ErrorStatusEnum.PARAM_NOT_FOUNT.getCode(), ErrorStatusEnum.PARAM_NOT_FOUNT.getMassage());
        }
        
        // 设置默认数据源类型
        params.setDatasourceType(params.getDatasourceType() == null ? "mysql" : params.getDatasourceType());

        try {
            // 构建参数Map
            Map<String, Object> paramsMap = buildParamsMap(params);
            
            // 执行SQL
            Object data = baseDataService.execute(
                params.getDatasourceId(),
                params.getDatasourceType(), 
                params.getSchemaName(),
                params.getSqlScript(), 
                paramsMap
            );

            // 处理返回结果
            return Result.success(formatResultData(data, params.getResultType()));
            
        } catch (Exception e) {
            return Result.error("测试异常，请检查参数或者SQL是否正常！");
        }
    }

    /**
     * 构建参数Map
     */
    private Map<String, Object> buildParamsMap(ApiTestParam params) throws Exception {
        Map<String, Object> paramsMap = new HashMap<>();
        
        // 处理body参数
        if (params.getBodyData() != null && params.getBodyData().startsWith("{")) {
            paramsMap = jsonMapper.readValue(params.getBodyData(), HashMap.class);
        }else if (params.getBodyData() != null && params.getBodyData().startsWith("[")) {
            List list = jsonMapper.readValue(params.getBodyData(), List.class);
            paramsMap.put("list", list);
        }

        // 处理query参数
        Object requestParams = params.getRequestParams();
        if (requestParams instanceof Map) {
            Map<String, Object> queryParam = (Map<String, Object>) requestParams;
            if (validateParams(queryParam)) {
                paramsMap.putAll(queryParam);
            } else {
                throw new IllegalArgumentException("参数校验失败");
            }
            paramsMap.put("pageSetup", params.getPageSetup());
        } else if (requestParams instanceof List) {
            List<Map<String,Object>> paramsList = (List<Map<String, Object>>) requestParams;
            for (Map<String, Object> entry : paramsList) {
                if (validateParams(entry)) {
                    if (entry.containsKey("name")) {
                        paramsMap.put(entry.get("name").toString(), entry.get("value"));
                    }
                } else {
                    throw new IllegalArgumentException("参数校验失败");
                }
            }
            if (paramsList.isEmpty()) {
                paramsMap.put("pageSetup", params.getPageSetup());
            }
        }
        // 事务开关只由测试弹窗显式传入，开发页运行/预览不进入多脚本执行入口。
        paramsMap.put(BaseConstant.TRANSACTION_ENABLED, normalizeTransactionEnabled(params.getTransactionEnabled()));
        return paramsMap;
    }

    /**
     * 测试参数兼容旧前端请求，未传事务开关时按关闭处理。
     */
    private Integer normalizeTransactionEnabled(Integer transactionEnabled) {
        return transactionEnabled == null ? 0 : transactionEnabled;
    }

    /**
     * 格式化返回数据
     */
    private String formatResultData(Object data, String resultType) throws Exception {
        if (ResultTypeEnum.ONE.getName().equals(resultType) && data instanceof List) {
            List<Object> list = (List<Object>) data;
            return jsonMapper.writeValueAsString(Result.success(list.isEmpty() ? null : list.get(0)));
        }
        return jsonMapper.writeValueAsString(Result.success(data));
    }

    /**
     * 校验并处理参数
     */
    private boolean validateParams(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return true;
        }
        
        String paramType = String.valueOf(params.get("paramType"));
        Object paramValue = params.get("value");

        if ("Array".equalsIgnoreCase(paramType)) {
            if (paramValue == null || "".equals(paramValue)) {
                return false;
            }
            String[] values = paramValue.toString().split(",");
            params.put("value", Arrays.asList(values));
        }
        return true;
    }
}
