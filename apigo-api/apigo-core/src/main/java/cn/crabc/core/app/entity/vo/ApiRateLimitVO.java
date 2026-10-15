package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * API限流配置视图，回显单窗口限流规则和接口维度。
 */
@Getter
@Setter
public class ApiRateLimitVO {

    /**
     * 接口ID。
     */
    private Long apiId;

    /**
     * 接口名称。
     */
    private String apiName;

    /**
     * 请求方式，和URL共同作为限流维度。
     */
    private String apiMethod;

    /**
     * 接口URL，前端展示时补/api/web/前缀。
     */
    private String apiPath;

    /**
     * 时间窗口数值。
     */
    private Integer windowValue;

    /**
     * 时间窗口单位：SECOND、MINUTE、HOUR。
     */
    private String windowUnit;

    /**
     * 窗口内允许请求次数。
     */
    private Integer limitCount;
}
