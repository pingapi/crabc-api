package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.param.ApiTestParam;
import cn.crabc.core.app.entity.vo.PreviewVO;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.app.util.SQLUtil;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
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
//        boolean check = SQLUtil.previewCheckSql(api.getSqlScript(), api.getDatasourceType());
//        if (!check) {
//            return Result.error("运行只支持查询，其他操作请使用预览功能");
//        }
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
        Object list = baseDataService.execute(params.getDatasourceId(),params.getDatasourceType(), params.getSchemaName(),sql, params.getRequestParams());
        map.put("data", objectMapper.writeValueAsString(Result.success(list)));
        map.put("code", 0);
        return Result.success(map);
    }
}
