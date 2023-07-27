package cn.crabc.core.admin.entity.vo;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import java.util.Date;

/**
 * API基本信息
 *
 * @author yuqf
 */
@HeadRowHeight(22)
@ColumnWidth(16)
public class BaseApiExcelVO {

    /**
     * api业务唯一ID
     */
    @ExcelProperty("接口编号")
    private Long apiId;
    /**
     * 接口名称
     */
    @ExcelProperty("接口名称")
    private String apiName;
    /**
     * 接口路径
     */
    @ColumnWidth(25)
    @ExcelProperty("接口路径")
    private String apiPath;
    /**
     * 请求方式 get、post、put、delete、aptch
     */
    @ExcelProperty("接口方法")
    private String apiMethod;
    /**
     * 授权类型：none、code、secret
     */
    @ExcelProperty("权限类型")
    private String authType;
    /**
     * 接口状态：编辑edit、审批audit、发布release、销毁destroy
     */
    @ExcelProperty("接口状态")
    private String apiStatus;

    /**
     * 权限级别：public、default、private
     */
    @ExcelProperty("访问级别")
    private String apiLevel;
    /**
     * API描述
     */
    @ColumnWidth(25)
    @ExcelProperty("接口描述")
    private String remarks;
    @ColumnWidth(25)
    @ExcelProperty("创建时间")
    private Date createTime;
    /**
     * 发布时间
     */
    @ColumnWidth(25)
    @ExcelProperty("发布时间")
    private Date releaseTime;

    /**
     * 分组ID
     */
    @ExcelProperty("分组ID")
    private Integer groupId;

    /**
     * 分组名
     */
    @ExcelProperty("分组名称")
    private String groupName;

    /**
     * sql语句脚本
     */
    @ExcelProperty("接口脚本")
    private String sqlScript;

    /**
     * 分页设置，不分页：0、只分页：page、分页并统计：pageCount
     */
    @ExcelProperty("是否分页")
    private Integer pageSetup;

    /**
     * 数据源Id
     */
    @ExcelProperty("数据源ID")
    private String datasourceId;

    /**
     * 数据源名称
     */
    @ExcelProperty("数据源名称")
    private String datasourceName;

    /**
     * schema
     */
    @ExcelProperty("schema名称")
    private String schemaName;

    /**
     * 数据源类型
     */
    @ExcelProperty("数据源类型")
    private String datasourceType;

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

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getApiStatus() {
        return apiStatus;
    }

    public void setApiStatus(String apiStatus) {
        this.apiStatus = apiStatus;
    }

    public String getApiLevel() {
        return apiLevel;
    }

    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public Integer getPageSetup() {
        return pageSetup;
    }

    public void setPageSetup(Integer pageSetup) {
        this.pageSetup = pageSetup;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }
}
