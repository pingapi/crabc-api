package cn.crabc.core.admin.api;

import cn.crabc.core.admin.entity.dto.ApiInfoDTO;
import cn.crabc.core.admin.service.core.IBaseDataService;
import cn.crabc.core.admin.util.ApiThreadLocal;
import cn.crabc.core.admin.util.Result;
import cn.crabc.core.app.enums.ErrorStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Result getService(@RequestParam Map<String, Object> paramMap) {
        ApiInfoDTO api = ApiThreadLocal.get();
        if (api == null) {
            return Result.error(ErrorStatusEnum.API_INVALID.getCode(), ErrorStatusEnum.API_INVALID.getMassage());
        }
        Object data = baseDataService.execute(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), paramMap);
        return Result.success(data);
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
            return Result.error(ErrorStatusEnum.API_INVALID.getCode(), ErrorStatusEnum.API_INVALID.getMassage());
        }
        if (paramMap == null) {
            paramMap = new HashMap<>();
        }
        if (body instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) body;
            paramMap.putAll(map);
        }
        Object data = baseDataService.execute(api.getDatasourceId(), api.getSchemaName(), api.getSqlScript(), paramMap);
        return Result.success(data);
    }
}
