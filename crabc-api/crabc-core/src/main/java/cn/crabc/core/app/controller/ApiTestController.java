package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.param.ApiTestParam;
import cn.crabc.core.app.entity.vo.PreviewVO;
import cn.crabc.core.app.enums.ResultTypeEnum;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
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
            if(e instanceof CustomException) {
                CustomException ex = (CustomException) e;
                return Result.error("测试异常，请检查参数或者SQL是否正常！" +ex.getMsg());
            }
            return Result.error("测试异常，请检查参数或者SQL是否正常！");
        }
    }

    /**
     * 构建运行时参数Map，供SQL执行层使用。
     * body 参数来自 bodyData（页面Body编辑器，可能是JSON字符串或对象），
     * query 参数来自 requestParams/requestParam 数组中 paramPosition=query 的项。
     */
    private Map<String, Object> buildParamsMap(ApiTestParam params) throws Exception {
        Map<String, Object> paramsMap = new HashMap<>();

        // 1. 解析 body 参数：bodyData 可能是 JSON 字符串、双重编码字符串或对象
        Object bodyData = params.getBodyData();
        if (bodyData instanceof String) {
            String bodyStr = (String) bodyData;
            // 兼容双重序列化：bodyData 可能以引号开头（JSON 字符串字面量），先剥掉外层引号还原 JSON 文本
            if (bodyStr.startsWith("\"") && bodyStr.endsWith("\"")) {
                bodyStr = jsonMapper.readValue(bodyStr, String.class);
            }
            if (bodyStr.startsWith("{")) {
                Map<String, Object> bodyMap = jsonMapper.readValue(bodyStr, HashMap.class);
                if (bodyMap != null) {
                    paramsMap.putAll(bodyMap);
                }
            } else if (bodyStr.startsWith("[")) {
                List<?> bodyList = jsonMapper.readValue(bodyStr, List.class);
                paramsMap.put("list", bodyList);
            }
        } else if (bodyData instanceof Map) {
            paramsMap.putAll((Map<String, Object>) bodyData);
        } else if (bodyData instanceof List) {
            paramsMap.put("list", bodyData);
        }

        // 2. 解析 requestParams：
        //    - Map 形式：可能是已组装好的键值对（如 {"list":["1","2"],"pageSetup":0}），直接合并；
        //    - List 形式：参数定义数组，仅取 paramPosition=query 的项，body/header 参数值由 bodyData/请求头单独注入
        Object requestParams = params.getRequestParams();
        if (requestParams instanceof Map) {
            Map<String, Object> requestMap = (Map<String, Object>) requestParams;
            // 单个参数定义（含 paramName/value 键）也按定义处理，否则视为键值对直接合并
            if (requestMap.containsKey("paramName") && requestMap.containsKey("value")) {
                Object name = requestMap.get("paramName");
                Object value = requestMap.get("value");
                if (name != null && value != null && !"".equals(value)) {
                    paramsMap.put(name.toString(), value);
                }
            } else {
                paramsMap.putAll(requestMap);
            }
        } else if (requestParams instanceof List) {
            List<Map<String, Object>> paramsList = (List<Map<String, Object>>) requestParams;
            for (Map<String, Object> entry : paramsList) {
                String position = entry.get("paramPosition") == null
                    ? "query" : String.valueOf(entry.get("paramPosition"));
                // 跳过 body/header 位置参数，避免无 value 的 Array 类型被误判为必填失败
                if (!"query".equalsIgnoreCase(position)) {
                    continue;
                }
                Object name = entry.get("paramName");
                if (name == null) {
                    continue;
                }
                Object value = entry.get("value");
                if (value == null || "".equals(value)) {
                    continue;
                }
                // Array 类型做逗号分隔转换
                String paramType = String.valueOf(entry.get("paramType"));
                if ("Array".equalsIgnoreCase(paramType) && !(value instanceof List)) {
                    value = Arrays.asList(value.toString().split(","));
                }
                paramsMap.put(name.toString(), value);
            }
        }

        // 3. 兼容旧字段 queryParam（Map 形式）
        Map<String, Object> queryParam = params.getQueryParam();
        if (queryParam != null && !queryParam.isEmpty()) {
            paramsMap.putAll(queryParam);
        }

        // 分页开关
        if (params.getPageSetup() != null) {
            paramsMap.put("pageSetup", params.getPageSetup());
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
    private String formatResultData(Object data, String resultType) {
        if (ResultTypeEnum.ONE.getName().equals(resultType) && data instanceof List) {
            List<Object> list = (List<Object>) data;
            return jsonMapper.writeValueAsString(Result.success(list.isEmpty() ? null : list.get(0)));
        }
        return jsonMapper.writeValueAsString(Result.success(data));
    }
}
