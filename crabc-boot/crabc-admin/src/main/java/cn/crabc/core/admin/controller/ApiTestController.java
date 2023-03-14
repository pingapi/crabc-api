package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.BaseApiSql;
import cn.crabc.core.admin.entity.param.ApiTestParam;
import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.admin.service.system.IBaseApiSqlService;
import cn.crabc.core.admin.service.system.IBaseApiTestService;
import cn.crabc.core.admin.util.Result;
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
    @Autowired
    private IBaseApiSqlService baseApiSqlService;


    /**
     * 运行预览
     * @param apiTest
     * @return
     */
    @PostMapping("/running")
    public Result runApiSql(@RequestBody ApiTestParam apiTest) {
        PreviewVO previewVO = baseapitestService.sqlPreview(apiTest.getDatasourceId(), apiTest.getSchemaName(),apiTest.getSqlScript());
        return Result.success(previewVO);
    }

    /**
     * 在线测试API
     * @param params
     * @return
     */
    @PostMapping("/verify/{apiId}")
    public Result testApiSql(@PathVariable Long apiId, @RequestBody ApiTestParam params) {
//        BaseApiSql apiSql = baseApiSqlService.getApiSql(apiId);
//        if (apiSql == null){
//            return Result.error(52002,"API不存在");
//        }
        if (params.getDatasourceType() == null){
            params.setDatasourceType("mysql");
        }
        Map<String,Object> map = new HashMap<>();
        long start = System.currentTimeMillis();
        Object list = baseapitestService.testApi(params.getDatasourceId(),params.getSchemaName(), params.getSqlScript(), params.getRequestParams());
        long end = System.currentTimeMillis();
        map.put("data", JSON.toJSONString(Result.success(list)));
        map.put("runTime",end - start);
        map.put("code",0);
        return Result.success(map);
    }
}
