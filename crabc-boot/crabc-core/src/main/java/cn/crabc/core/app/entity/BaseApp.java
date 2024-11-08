package cn.crabc.core.app.entity;


import lombok.Getter;
import lombok.Setter;

/**
 * 应用秘密
 *
 * @author yuqf
 */
@Setter
@Getter
public class BaseApp extends BaseEntity {

    /**
     * 应用ID
     */
    private Long appId;
    /**
     * 应用名
     */
    private String appName;
    /**
     * 应用描述
     */
    private String appDesc;
    /**
     * 认证code
     */
    private String appCode;
    /**
     * 密钥ID
     */
    private String appKey;
    /**
     * 密钥
     */
    private String appSecret;
    /**
     *  策略类型：白名单：white、黑名单black
     */
    private String strategyType;
    /**
     * ip地址，分号分割
     */
    private String ips;
    /**
     * 状态：1启用，0，禁用
     */
    private Integer enabled;
    /**
     * 关联的apiId
     */
    private Long apiId;
}
