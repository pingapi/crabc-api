package cn.crabc.core.admin.entity.vo;

import cn.crabc.core.admin.entity.BaseEntity;

import java.util.Date;

/**
 * API基本信息
 *
 * @author yuqf
 */
public class BaseApiInfoVO extends BaseEntity {

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
     * API类型：sql、table
     */
    private String apiType;
    /**
     * 授权类型：none、code、secret
     */
    private String authType;

    /**
     * 权限级别：public、default、private
     */
    private String apiLevel;

    /**
     * 开放启用 1/0
     */
    private Integer enabled;

    /**
     * API描述
     */
    private String remarks;

    /**
     * 版本
     */
    private String version;

    /**
     * sql类型，select、insert、update、delete
     */
    private String sqlType;

    /**
     * 分页设置，不分页：0、只分页：page、分页并统计：pageCount
     */
    private Integer pageSetup;
    /**
     * 发布时间
     */
    private Date releaseTime;

    private Integer applyed;


    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }

    public String getApiMethod() {
        return apiMethod;
    }

    public void setApiMethod(String apiMethod) {
        this.apiMethod = apiMethod;
    }

    public String getAuthType() {
        return authType;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Integer getPageSetup() {
        return pageSetup;
    }

    public void setPageSetup(Integer pageSetup) {
        this.pageSetup = pageSetup;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public Integer getApplyed() {
        return applyed;
    }

    public void setApplyed(Integer applyed) {
        this.applyed = applyed;
    }
}
