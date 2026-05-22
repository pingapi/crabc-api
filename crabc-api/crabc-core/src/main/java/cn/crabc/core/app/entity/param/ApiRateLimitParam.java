package cn.crabc.core.app.entity.param;

import lombok.Getter;
import lombok.Setter;

/**
 * API限流配置参数，用于发布接口列表设置单窗口限流规则。
 */
@Getter
@Setter
public class ApiRateLimitParam {

    /**
     * 接口ID，保存限流配置时定位发布接口。
     */
    private Long apiId;

    /**
     * 时间窗口数值，和windowUnit一起换算成秒；为空时表示清空限流。
     */
    private Integer windowValue;

    /**
     * 时间窗口单位：SECOND、MINUTE、HOUR。
     */
    private String windowUnit;

    /**
     * 窗口内允许请求次数；为空时表示清空限流。
     */
    private Integer limitCount;
}
