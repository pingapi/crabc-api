package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.BaseAppApi;
import cn.crabc.core.admin.entity.param.ApiInfoParam;
import cn.crabc.core.admin.entity.vo.ApiComboBoxVO;
import cn.crabc.core.admin.entity.vo.ApiInfoVO;
import cn.crabc.core.admin.entity.vo.ColumnParseVo;
import cn.crabc.core.admin.entity.vo.SqlParseVO;
import cn.crabc.core.admin.service.system.IBaseApiInfoService;
import cn.crabc.core.admin.util.PageInfo;
import cn.crabc.core.admin.util.Result;
import cn.crabc.core.admin.util.SQLUtil;
import cn.crabc.core.admin.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * API 基本信息
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/api/info")
public class ApiInfoController {

    @Autowired
    private IBaseApiInfoService apiInfoService;

    /**
     * API分页列表
     *
     * @param keyword
     * @param devType
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(String keyword, String devType, Integer pageNum, Integer pageSize) {
        PageInfo<BaseApiInfo> page = apiInfoService.getApiPage(keyword, devType, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * API详情
     *
     * @param apiId
     * @return
     */
    @GetMapping
    public Result info(Long apiId) {
        ApiInfoVO apiDetail = apiInfoService.getApiDetail(apiId);
        return Result.success(apiDetail);
    }

    /**
     * 分组下API列表
     *
     * @param groupId
     * @return
     */
    @GetMapping("/group/list")
    public Result list(Integer groupId) {
        List<ApiComboBoxVO> list = apiInfoService.getApiListGroup(groupId);
        return Result.success(list);
    }

    /**
     * 新增编辑API
     *
     * @param apiInfoParam
     * @return
     */
    @PostMapping
    public Result addOrUpdate(@RequestBody ApiInfoParam apiInfoParam) {
        Long result;
        if (apiInfoParam.getBaseInfo().getApiId() == null) {
            result = apiInfoService.addApiInfo(apiInfoParam);
        } else {
            result = apiInfoService.updateApiInfo(apiInfoParam);
        }
        return Result.success(result);
    }

    /**
     * api发布
     *
     * @param api
     * @return
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody ApiInfoParam api) {
        boolean check = SQLUtil.checkSql(api.getBaseInfo().getSqlScript(), api.getBaseInfo().getDatasourceType());
        if (check == false) {
            return Result.error("不支持该SQL的操作类型!");
        }
        apiInfoService.apiPublish(api);
        return Result.success();
    }

    /**
     * 更新API状态
     *
     * @param baseApiInfo
     * @return
     */
    @PostMapping("/state")
    public Result updateState(@RequestBody BaseApiInfo baseApiInfo) {
        apiInfoService.updateApiState(baseApiInfo.getApiId(), null, baseApiInfo.getEnabled());
        return Result.success();
    }

    /**
     * 删除API
     *
     * @param apiId
     * @return
     */
    @DeleteMapping("/{apiId}")
    public Result deleteApi(@PathVariable Long apiId) {
        return Result.success(apiInfoService.deleteApi(apiId, UserThreadLocal.getUserId()));
    }

    /**
     * SQL解析
     *
     * @param
     * @return
     */
    @PostMapping("/sqlParse")
    public Result sqlParse(@RequestBody SqlParseVO sqlParse) {
        SqlParseVO sqlParseVO = new SqlParseVO();
        // 条件字段
        Set<String> paramNames = SQLUtil.parseParams(sqlParse.getSqlScript());
        sqlParseVO.setReqColumns(paramNames);
        // 返回字段
        Set<ColumnParseVo> resColumns = new HashSet<>();
        Set<String> resNames = SQLUtil.analyzeSQL(sqlParse.getSqlScript(), sqlParse.getDatasourceType());
        for (String name : resNames) {
            ColumnParseVo resColumn = new ColumnParseVo();
            resColumn.setColName(name);
            resColumn.setColType("STRING");
            resColumn.setItemIndex(0);
            resColumns.add(resColumn);
        }
        sqlParseVO.setResColumns(resColumns);
        return Result.success(sqlParseVO);
    }

    /**
     * 校验接口url
     *
     * @param api
     * @return
     */
    @PostMapping("/check")
    public Result checkPath(@RequestBody BaseApiInfo api) {
        Boolean result = apiInfoService.checkApiPath(api.getApiId(),api.getApiPath(), api.getApiMethod());
        if (result) {
            return Result.error("50011", "该接口地址已存在");
        }
        return Result.success();
    }

    /**
     * 查询关联应用的API
     *
     * @param appId
     * @return
     */
    @GetMapping("/choosed")
    public Result chooseApi(Long appId, Integer pageNum, Integer pageSize) {
        return Result.success(apiInfoService.getChooseApi(appId, pageNum, pageSize));
    }

    /**
     * 查询未关联应用的API
     *
     * @param appId
     * @return
     */
    @GetMapping("/choose/list")
    public Result noChooseApi(Long appId, Integer pageNum, Integer pageSize) {
        return Result.success(apiInfoService.getNotChooseApi(appId, pageNum, pageSize));
    }

    /**
     * 保存API应用授权关系
     *
     * @param appApis
     * @return
     */
    @PostMapping("/choosed")
    public Result addChooseApi(@RequestBody BaseAppApi appApis) {
        return Result.success(apiInfoService.addChooseApi(appApis));
    }

}
