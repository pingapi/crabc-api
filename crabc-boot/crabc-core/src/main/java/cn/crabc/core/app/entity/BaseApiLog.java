package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * api访问日志
 *
 * @author yuqf
 */
@Setter
@Getter
public class BaseApiLog implements Serializable {

    private Long logId;
    /**
     * api业务唯一ID
     */
    private Long apiId;
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口路径
     */
    private String apiPath;
    /**
     * 请求方式 get、post、put、delete、aptch
     */
    private String apiMethod;
    /**
     * 授权类型：none、code、secret
     */
    private String authType;
    private String appName;
    private String requestIp;
    private String queryParam;
    private String requestBody;
    private String responseBody;
    private Integer bodySize;
    private String requestStatus;
    private Date requestTime;
    private Date responseTime;
    private Long costTime;
    private Integer responseCode;
}
