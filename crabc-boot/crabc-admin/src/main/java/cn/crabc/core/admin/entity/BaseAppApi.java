package cn.crabc.core.admin.entity;

import java.util.Date;
import java.util.List;

/**
 * 应用和API关联关系
 *
 * @author yuqf
 */
public class BaseAppApi {

    private Long appId;

    private Long apiId;

    private String createBy;

    private Date createTime;

    private List<Long> apiIds;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public List<Long> getApiIds() {
        return apiIds;
    }

    public void setApiIds(List<Long> apiIds) {
        this.apiIds = apiIds;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
