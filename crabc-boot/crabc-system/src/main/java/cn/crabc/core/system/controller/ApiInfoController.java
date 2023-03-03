package cn.crabc.core.system.controller;

import cn.crabc.core.system.entity.BaseApiInfo;
import cn.crabc.core.system.entity.param.ApiInfoParam;
import cn.crabc.core.system.entity.vo.ApiComboBoxVO;
import cn.crabc.core.system.entity.vo.ApiInfoVO;
import cn.crabc.core.system.entity.vo.ColumnParseVo;
import cn.crabc.core.system.entity.vo.SqlParseVO;
import cn.crabc.core.system.service.system.IBaseApiInfoService;
import cn.crabc.core.system.util.PageInfo;
import cn.crabc.core.system.util.Result;
import cn.crabc.core.system.util.SQLUtils;
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
     * @param appId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result page(String keyword, Integer appId, Integer pageNum, Integer pageSize) {
        PageInfo<BaseApiInfo> page = apiInfoService.getApiPage(keyword, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * API详情
     * @param apiId
     * @return
     */
    @GetMapping
    public Result info(Long apiId){
        ApiInfoVO apiDetail = apiInfoService.getApiDetail(apiId);
        return Result.success(apiDetail);
    }

    /**
     * 分组下API列表
     * @param groupId
     * @return
     */
    @GetMapping("/group/list")
    public Result list(Integer groupId){
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
     * @param apiInfoParam
     * @return
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody ApiInfoParam apiInfoParam) {
        apiInfoService.apiPublish(apiInfoParam);
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
     * SQL解析
     * @param
     * @return
     */
    @PostMapping("/sqlParse")
    public Result sqlParse(@RequestBody SqlParseVO sqlParse){
        SqlParseVO sqlParseVO = new SqlParseVO();
        // 返回字段
        Set<String> paramNames = SQLUtils.parseParams(sqlParse.getSqlScript());
        sqlParseVO.setReqColumns(paramNames);
        // 条件字段
        Set<ColumnParseVo> resColumns = new HashSet<>();
        Set<String> resNames = SQLUtils.parseColumns(sqlParse.getSqlScript());
        for(String name : resNames){
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
     * @param baseApiInfo
     * @return
     */
    @PostMapping("/check")
    public Result checkPath(@RequestBody BaseApiInfo baseApiInfo){
        return Result.success();
    }

}
