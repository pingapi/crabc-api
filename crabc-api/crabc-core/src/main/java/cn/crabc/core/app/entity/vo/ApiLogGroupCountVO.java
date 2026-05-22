package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * API日志分组统计结果，用于状态占比、接口调用量Top和接口耗时Top等轻量图表。
 */
@Setter
@Getter
public class ApiLogGroupCountVO implements Serializable {

    /**
     * 分组主键，例如状态或API编号。
     */
    private String groupKey;

    /**
     * 分组展示名称，例如接口名称或状态名称。
     */
    private String groupName;

    /**
     * API路径，仅接口Top统计需要返回。
     */
    private String apiPath;

    /**
     * 分组调用次数。
     */
    private Long count;

    /**
     * 分组平均耗时，单位毫秒，仅接口耗时排行需要返回。
     */
    private Double avgCostTime;

    /**
     * 分组最大耗时，单位毫秒，仅接口耗时排行需要返回。
     */
    private Long maxCostTime;
}
