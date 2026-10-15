package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * API日志每日趋势统计，用于监控页折线图展示调用量、成功量、失败量和平均耗时走势。
 */
@Setter
@Getter
public class ApiLogDailyTrendVO implements Serializable {

    /**
     * 统计日期，格式为 yyyy-MM-dd。
     */
    private String statisticDate;

    /**
     * 当日总调用次数。
     */
    private Long totalCount;

    /**
     * 当日成功调用次数。
     */
    private Long successCount;

    /**
     * 当日失败调用次数。
     */
    private Long failCount;

    /**
     * 当日平均耗时，单位毫秒。
     */
    private Double avgCostTime;
}
