package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * API日志统计摘要，用于监控页顶部指标卡展示整体调用质量和耗时边界。
 */
@Setter
@Getter
public class ApiLogSummaryVO implements Serializable {

    /**
     * 时间范围内的总调用次数。
     */
    private Long totalCount;

    /**
     * request_status 为 success 的调用次数。
     */
    private Long successCount;

    /**
     * request_status 为 fail 的调用次数。
     */
    private Long failCount;

    /**
     * 成功调用占比，服务层统一计算两位小数，避免前端和SQL重复计算。
     */
    private Double successRate;

    /**
     * 平均耗时，单位毫秒。
     */
    private Double avgCostTime;

    /**
     * 最大耗时，单位毫秒。
     */
    private Long maxCostTime;
}
