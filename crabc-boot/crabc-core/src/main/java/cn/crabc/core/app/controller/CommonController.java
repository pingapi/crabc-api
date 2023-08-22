package cn.crabc.core.app.controller;


import cn.crabc.core.app.entity.vo.ApiParamsVO;
import cn.crabc.core.app.entity.vo.BaseApiInfoVO;
import cn.crabc.core.app.service.system.IBaseApiInfoService;
import cn.crabc.core.app.service.system.IBaseApiParamService;
import cn.crabc.core.app.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API公共信息
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/common")
public class CommonController {

    @Autowired
    private IBaseApiInfoService apiInfoService;
    @Autowired
    private IBaseApiParamService apiParamService;

    /**
     * API详情信息
     *
     * @param apiId
     * @return
     */
    @GetMapping("/api/details")
    public Result detail(Long apiId) {
        Integer applyCount = 1;
        BaseApiInfoVO apiDetail = apiInfoService.getApiDetail(apiId);
        if ("0".equals(apiDetail.getShowSqlScript())){
            apiDetail.setSqlScript("***");
        }
        apiDetail.setApplyed(applyCount);
        return Result.success(apiDetail);
    }

    /**
     * API参数
     *
     * @param apiId
     * @return
     */
    @GetMapping("/api/params")
    public Result params(Long apiId) {
        ApiParamsVO apiDetailsParams = apiParamService.getApiDetailsParams(apiId);
        return Result.success(apiDetailsParams);
    }
}
