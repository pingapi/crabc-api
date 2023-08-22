package cn.crabc.core.app.entity.dto;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.BaseApp;

import java.util.ArrayList;
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

    private String userId;

    /**
     * 有权限的应用
     */
    private List<BaseApp> appList = new ArrayList<>();

    /**
     * API参数
     */
    private List<BaseApiParam> requestParams;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<BaseApiParam> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<BaseApiParam> requestParams) {
        this.requestParams = requestParams;
    }
}
