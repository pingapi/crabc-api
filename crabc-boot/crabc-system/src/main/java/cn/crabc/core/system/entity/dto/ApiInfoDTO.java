package cn.crabc.core.system.entity.dto;

import cn.crabc.core.system.entity.BaseApiInfo;
import cn.crabc.core.system.entity.BaseApp;

import java.util.List;

/**
 * API相关的详情信息
 *
 * @author yuqf
 */
public class ApiInfoDTO extends BaseApiInfo {
    /**
     * SQL脚本
     */
    private String sqlScript;
    /**
     * 数据源ID
     */
    private String datasourceId;
    /**
     * 数据源类型
     */
    private String datasourceType;
    /**
     * 有权限的应用
     */
    private List<BaseApp> appList;

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public List<BaseApp> getAppList() {
        return appList;
    }

    public void setAppList(List<BaseApp> appList) {
        this.appList = appList;
    }
}
