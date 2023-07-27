package cn.crabc.core.admin.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import java.util.Date;

/**
 * 应用导出导入
 *
 * @author yuqf
 */
@HeadRowHeight(22)
@ColumnWidth(16)
public class BaseAppExcelVO {

    /**
     * 应用ID
     */
    @ExcelProperty("应用编号")
    private Long appId;
    /**
     * 应用名
     */
    @ExcelProperty("应用名称")
    private String appName;
    /**
     * 应用描述
     */
    @ExcelProperty("应用描述")
    private String appDesc;
    /**
     * 认证code
     */
    @ColumnWidth(30)
    @ExcelProperty("AppCode")
    private String appCode;
    /**
     * 密钥ID
     */
    @ColumnWidth(30)
    @ExcelProperty("AppKey")
    private String appKey;
    /**
     * 密钥
     */
    @ColumnWidth(30)
    @ExcelProperty("AppSecret")
    private String appSecret;
    /**
     *  策略类型：白名单：white、黑名单black
     */
    @ExcelProperty("策略类型")
    private String strategyType;
    /**
     * ip地址，分号分割
     */
    @ExcelProperty("ip地址")
    private String ips;
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ExcelProperty("更新时间")
    private Date updateTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
