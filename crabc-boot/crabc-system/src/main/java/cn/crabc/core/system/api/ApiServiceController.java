package cn.crabc.core.system.api;

import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.util.ApiThreadLocal;
import cn.crabc.core.system.util.RequestUtils;
import cn.crabc.core.system.util.Result;
import cn.crabc.core.system.service.core.IBaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
     * API请求方法
     *
     * @param request
     * @return
     */
    @RequestMapping("/**")
    public Result service(HttpServletRequest request) {
        ApiInfoDTO apiInfo = ApiThreadLocal.get();
        if (apiInfo == null) {
            return Result.error(5001,  "请求地址不存在！");
        }
        Map<String, Object> params = RequestUtils.getParameters(request);
        // post请求才解析body
        if ("POST".equals(request.getMethod().toUpperCase())) {
            Map<String, Object> bodyMap = RequestUtils.getBodyMap(request);
            if (bodyMap != null && !bodyMap.isEmpty()) {
                params.putAll(bodyMap);
            }
        }
        List<Map<String, Object>> query = baseDataService.query(apiInfo.getDatasourceId(), apiInfo.getDatasourceType(), apiInfo.getSqlScript(), params);
        return Result.success(query);
    }
}
