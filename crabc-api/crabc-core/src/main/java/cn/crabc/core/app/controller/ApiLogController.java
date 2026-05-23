package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.param.ApiLogParam;
import cn.crabc.core.app.service.system.IBaseApiLogService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API访问日志
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/api/log")
public class ApiLogController {

    @Autowired
    private IBaseApiLogService iBaseApiLogService;

    /**
     * 日志分页
     * @param apiLogParam
     * @return
     */
    @PostMapping("/page")
    public Result logList(@RequestBody ApiLogParam apiLogParam) {
        PageInfo page = iBaseApiLogService.page(apiLogParam);
        return Result.success(page);
    }

    /**
     * 日志摘要统计
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return 监控页顶部指标卡数据
     */
    @PostMapping("/summary")
    public Result summary(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.summary(apiLogParam));
    }

    /**
     * 日志每日调用趋势
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return 按天聚合的调用趋势
     */
    @PostMapping("/dailyTrend")
    public Result dailyTrend(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.dailyTrend(apiLogParam));
    }

    /**
     * 日志请求结果占比
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return success/fail等状态分组计数
     */
    @PostMapping("/statusPie")
    public Result statusPie(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.statusPie(apiLogParam));
    }

    /**
     * 接口调用量Top10
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return 按接口聚合的调用量Top10
     */
    @PostMapping("/topApis")
    public Result topApis(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.topApis(apiLogParam));
    }

    /**
     * 接口耗时Top10
     *
     * @param apiLogParam 统计筛选条件，服务层固定只统计成功调用
     * @return 按接口平均耗时聚合的成功调用Top10
     */
    @PostMapping("/topCostApis")
    public Result topCostApis(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.topCostApis(apiLogParam));
    }

    /**
     * IP访问量Top10
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return 按request_ip分组聚合的访问量Top10
     */
    @PostMapping("/topIps")
    public Result topIps(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.topIps(apiLogParam));
    }

    /**
     * 日志统计兼容接口
     *
     * @param apiLogParam 统计筛选条件，未传时间时默认近七天
     * @return 监控页指标卡和图表数据
     */
    @PostMapping("/statistics")
    public Result statistics(@RequestBody ApiLogParam apiLogParam) {
        return Result.success(iBaseApiLogService.statistics(apiLogParam));
    }
}
