package cn.crabc.core.admin.api;

import cn.crabc.core.admin.entity.dto.ApiInfoDTO;
import cn.crabc.core.admin.service.core.IBaseDataService;
import cn.crabc.core.admin.util.ApiThreadLocal;
import cn.crabc.core.admin.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.PATCH})
    public Result getService(@RequestParam Map<String, Object> paramMap) {
        ApiInfoDTO api = ApiThreadLocal.get();
        if (api == null) {
            return Result.error(5001, "请求地址不存在！");
        }
        List<Object> params = new ArrayList<>();
        if (paramMap != null && paramMap.size() > 0) {
            params.add(paramMap);
        }
        Object query = null;
        if (api.getSqlScript().trim().startsWith("select") || api.getSqlScript().trim().startsWith("SELECT")) {
            query = baseDataService.query(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), params);
        } else {
            query = baseDataService.execute(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), params);
        }
        return Result.success(query);
    }

    /**
     * API post请求
     *
     * @param paramMap
     * @param body
     * @return
     */
    @RequestMapping(value = "/**", method = {RequestMethod.POST, RequestMethod.PUT})
    public Result postService(@RequestParam Map<String, Object> paramMap, @RequestBody Object body) {
        ApiInfoDTO api = ApiThreadLocal.get();
        if (api == null) {
            return Result.error(5001, "请求地址不存在！");
        }
        List<Object> params = new ArrayList<>();
        if (paramMap != null && paramMap.size() > 0) {
            params.add(paramMap);
        }
        if (body instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) body;
            params.add(map);
        } else if (body instanceof List) {
            List<Object> list = (List<Object>) body;
            params.addAll(list);
        }

        Object query = null;
        if (api.getSqlScript().trim().startsWith("select") || api.getSqlScript().trim().startsWith("SELECT")) {
            query = baseDataService.query(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), params);
        } else {
            query = baseDataService.execute(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), params);
        }
        return Result.success(query);
    }
}
