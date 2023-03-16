package cn.crabc.core.admin.entity.vo;


import java.util.Set;

public class SqlParseVO {

    private Set<String> reqColumns;

    private Set<ColumnParseVo> resColumns;

    private String sqlScript;

    private String datasourceType;

    public Set<String> getReqColumns() {
        return reqColumns;
    }

    public void setReqColumns(Set<String> reqColumns) {
        this.reqColumns = reqColumns;
    }

    public Set<ColumnParseVo> getResColumns() {
        return resColumns;
    }

    public void setResColumns(Set<ColumnParseVo> resColumns) {
        this.resColumns = resColumns;
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
}
