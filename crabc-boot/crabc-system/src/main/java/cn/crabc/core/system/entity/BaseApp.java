package cn.crabc.core.system.entity;


/**
 * 应用秘密
 *
 * @author yuqf
 */
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

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}
