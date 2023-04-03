package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.param.ApiTestParam;
import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.admin.service.system.IBaseApiTestService;
import cn.crabc.core.admin.util.Result;
import cn.crabc.core.admin.util.SQLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private IBaseApiTestService baseapitestService;
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
        boolean check = SQLUtil.previewCheckSql(api.getSqlScript(), api.getDatasourceType());
        if (!check) {
            return Result.error("运行只支持查询，其他操作请使用预览功能");
        }
        PreviewVO previewVO = baseapitestService.sqlPreview(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript());
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
        String dbTpye = SQLUtil.getSqlType(params.getSqlScript(), params.getDatasourceType());
        if (dbTpye == null) {
            return Result.error("不支持该操作类型");
        }
        if (params.getDatasourceType() == null) {
            params.setDatasourceType("mysql");
        }
        Map<String, Object> map = new HashMap<>();
        long start = System.currentTimeMillis();
        String sql = params.getSqlScript();
        if ("select".equalsIgnoreCase(dbTpye)){
            // 查询sql支持动态条件参数
            Map<String, Object> paramMap = params.getRequestParams();
            for (String key : paramMap.keySet()) {
                 if (paramMap.get(key) == null || "".equals(paramMap.get(key).toString().trim())) {
                    // 非比传参数，去掉SQL中相应的条件
                    sql =SQLUtil.regexSql(sql, key);
                }
            }
        }
        Object list = baseapitestService.testApi(params.getDatasourceId(), params.getSchemaName(),sql, dbTpye, params.getRequestParams());
        long end = System.currentTimeMillis();
        map.put("data", objectMapper.writeValueAsString(Result.success(list)));
        map.put("runTime", end - start);
        map.put("code", 0);
        return Result.success(map);
    }
}
