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
        Map<String, Object> map = new HashMap<>();
        String sql = params.getSqlScript();
        Object data = baseDataService.execute(params.getDatasourceId(),params.getDatasourceType(), params.getSchemaName(),sql, params.getRequestParams());
        if (ResultTypeEnum.ONE.getName().equals(params.getResultType()) && data instanceof List) {
            List<Object> list  = (List<Object>) data;
            map.put("data", objectMapper.writeValueAsString(Result.success(list.isEmpty() ? null: list.get(0))));
        }else{
            map.put("data", objectMapper.writeValueAsString(Result.success(data)));
        }
        map.put("code", 0);
        return Result.success(map);
    }
}
