package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * API日志统计总览，用于兼容旧接口一次性返回监控页指标卡和图表数据。
 */
@Setter
@Getter
public class ApiLogStatisticsVO implements Serializable {

    /**
     * 顶部摘要指标。
     */
    private ApiLogSummaryVO summary;

    /**
     * 每日调用趋势数据。
     */
    private List<ApiLogDailyTrendVO> dailyTrend;

    /**
     * 成功/失败状态占比数据。
     */
    private List<ApiLogGroupCountVO> statusPie;

    /**
     * 调用量最高的接口列表，固定返回Top10。
     */
    private List<ApiLogGroupCountVO> topApis;

    /**
     * 平均耗时最高的成功接口列表，固定返回Top10。
     */
    private List<ApiLogGroupCountVO> topCostApis;

    /**
     * 访问量最高的IP列表，固定返回Top10。
     */
    private List<ApiLogGroupCountVO> topIps;
}
