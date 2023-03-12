package cn.crabc.core.admin.entity;

/**
 * API SQL配置
 *
 * @author yuqf
 */
public class BaseApiSql extends BaseEntity {

    /**
     * 自增主键ID
     */
    private Long sqlId;

    /** api主键 */
    private Long apiId;

    /**
     * sql语句脚本
     */
    private String sqlScript;

    /**
     * 分页设置，不分页：0、只分页：page、分页并统计：pageCount
     */
    private Integer pageSetup;

    /**
     * 数据源Id
     */
    private String datasourceId;

    /**
     * schema
     */
    private String schemaName;

    /**
     * 数据源类型
     */
    private String datasourceType;


    public Long getSqlId() {
        return sqlId;
    }

    public void setSqlId(Long sqlId) {
        this.sqlId = sqlId;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

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

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public Integer getPageSetup() {
        return pageSetup;
    }

    public void setPageSetup(Integer pageSetup) {
        this.pageSetup = pageSetup;
    }
}
