package cn.crabc.core.app.entity;

/**
 * API参数
 *
 * @author yuqf
 */
public class BaseApiParam {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键ID
     */
    private Long paramId;

    /** api主键 */
    private Long apiId;

    /**
     * 参数名
     */
    private String paramName;

    /**
     * 映射字段名
     */
    private String columnName;

    /**
     * 参数模型，request,response
     */
    private String paramModel;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 操作
     */
    private String operation;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 参数默认值
     */
    private String defaultValue;

    /**
     * 参数示例值
     */
    private String example;

    /**
     * 参数备注
     */
    private String paramDesc;

    /**
     * 数据源Id
     */
    private String datasourceId;

    /**
     * schema
     */
    private String schemaName;

    /**
     * 表名
     */
    private String tableName;

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getParamModel() {
        return paramModel;
    }

    public void setParamModel(String paramModel) {
        this.paramModel = paramModel;
    }

}
