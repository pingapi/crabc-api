package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseAppApi;
import cn.crabc.core.app.entity.param.ApiInfoParam;
import cn.crabc.core.app.entity.vo.*;
import cn.crabc.core.app.service.system.IBaseApiInfoService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.app.util.SQLUtil;
import cn.crabc.core.app.util.UserThreadLocal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        ApiInfoVO apiDetail = apiInfoService.getApiInfo(apiId);
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
        apiInfoService.apiPublish(api);
        return Result.success(api.getBaseInfo().getApiId());
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
     * 销毁发布接口
     * @param apiId
     * @return
     */
    @DeleteMapping("/destroy/{apiId}")
    public Result destroy(@PathVariable Long apiId) {
        apiInfoService.destroyApiInfo(apiId);
        return Result.success();
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
        String sql = sqlParse.getSqlScript();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.lastIndexOf(";"));
        }
        // 对</foreach>标签取值特殊处理
        Set<String> fields = new HashSet<>();
        if (sql.contains("</foreach>")) {
            String regex = "collection='(.*?)'";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                String field = matcher.group(1);
                fields.add(field);
            }
        }
        // 提取if标签里面的请求参数
        if (sql.contains("<if ") || sql.contains("<when ")) {
            String regex = " test=['\"](\\w+)\\s*(?:!=|>=|<=|==|<|>|&lt;|&lt;=|&gt;|&gt;=)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                String field = matcher.group(1);
                fields.add(field);
            }
        }
        String forRegex = "<foreach[\\s\\S]*?</foreach>";
        sql = sql.replaceAll(forRegex,"()");
        // 条件字段
        Set<String> paramNames = SQLUtil.parseParams(sql);
        if (!fields.isEmpty()) {
            paramNames.addAll(fields);
        }
        sqlParseVO.setReqColumns(paramNames);
        // 返回字段
        sql = SQLUtil.sqlFilter(sql);
        // 判断去除标签后的 sql是否正确，自动补全条件
        if (sql.trim().toLowerCase().endsWith("from")) {
            sql = sql +" test ";
        }else if (sql.trim().toLowerCase().endsWith("where")) {
            sql = sql +" 1=1 ";
        }
        Set<ColumnParseVo> resColumns = new HashSet<>();
        Set<String> resNames = SQLUtil.analyzeSQL(sql.trim(), sqlParse.getDatasourceType());
        if (resNames.isEmpty()){
            sql = SQLUtil.checkTable(sql, " test ");
            resNames = SQLUtil.analyzeSQL(sql.trim(), sqlParse.getDatasourceType());
        }
        for (String name : resNames) {
            if (name.startsWith("*")){
                continue;
            }
            ColumnParseVo resColumn = new ColumnParseVo();
            resColumn.setColName(name);
            resColumn.setColType("String");
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
