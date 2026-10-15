package cn.crabc.core.spi.bean;

import java.io.Serializable;

/**
 * Schema属性
 *
 * @author yuqf
 */
public class Schema implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 数据源Id */
    private String datasourceId;
    /** 库 */
    private String catalog;
    /** 模式 */
    private String schema;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
