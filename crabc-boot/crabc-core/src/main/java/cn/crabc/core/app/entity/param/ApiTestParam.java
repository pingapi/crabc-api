package cn.crabc.core.app.entity.param;

public class ApiTestParam {

    private String sqlScript;

    private String datasourceId;

    private String datasourceType;

    private String schemaName;

    private String sqlParams;

    private String resultType;

    private Object requestParams;

    private String bodyData;

    public String getSqlScript() {
        return sqlScript;
    }

    public void setSqlScript(String sqlScript) {
        this.sqlScript = sqlScript;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(String datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(String sqlParams) {
        this.sqlParams = sqlParams;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Object getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Object requestParams) {
        this.requestParams = requestParams;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getBodyData() {
        return bodyData;
    }

    public void setBodyData(String bodyData) {
        this.bodyData = bodyData;
    }
}
