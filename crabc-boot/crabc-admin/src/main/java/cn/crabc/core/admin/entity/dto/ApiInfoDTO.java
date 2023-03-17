package cn.crabc.core.admin.entity.dto;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.BaseApp;

import java.util.Date;
import java.util.List;

/**
 * API相关的详情信息
 *
 * @author yuqf
 */
public class ApiInfoDTO extends BaseApiInfo {

    /**
     * 请求时间
     */
    private Date requestDate;

    private Long requestTime;

    /**
     * 有权限的应用
     */
    private List<BaseApp> appList;

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
    }

    public List<BaseApp> getAppList() {
        return appList;
    }

    public void setAppList(List<BaseApp> appList) {
        this.appList = appList;
    }
}
