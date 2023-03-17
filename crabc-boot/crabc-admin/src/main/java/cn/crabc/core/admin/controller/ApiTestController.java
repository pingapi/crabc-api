package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.param.ApiTestParam;
import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.admin.service.system.IBaseApiTestService;
import cn.crabc.core.admin.util.Result;
import cn.crabc.core.admin.util.SQLUtil;
import com.alibaba.fastjson2.JSON;
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


    /**
     * 运行预览
     *
     * @param api
     * @return
     */
    @PostMapping("/running")
    public Result runApiSql(@RequestBody ApiTestParam api) {
        boolean check = SQLUtil.checkSql(api.getSqlScript(), api.getDatasourceType());
        if (check == false) {
            return Result.error("不支持该SQL的操作类型");
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
    public Result testApiSql(@PathVariable Long apiId, @RequestBody ApiTestParam params) {
        boolean check = SQLUtil.checkSql(params.getSqlScript(), params.getDatasourceType());
        if (check == false) {
            return Result.error("不支持该SQL的操作类型");
        }
        if (params.getDatasourceType() == null) {
            params.setDatasourceType("mysql");
        }
        Map<String, Object> map = new HashMap<>();
        long start = System.currentTimeMillis();
        Object list = baseapitestService.testApi(params.getDatasourceId(), params.getSchemaName(), params.getSqlScript(), params.getRequestParams());
        long end = System.currentTimeMillis();
        map.put("data", JSON.toJSONString(Result.success(list)));
        map.put("runTime", end - start);
        map.put("code", 0);
        return Result.success(map);
    }
}
