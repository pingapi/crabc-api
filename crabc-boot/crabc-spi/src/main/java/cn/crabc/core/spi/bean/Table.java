package cn.crabc.core.spi.bean;


import java.io.Serializable;
import java.util.Date;

/**
 * 表属性
 *
 * @author yuqf
 */
public class Table implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 数据源Id
     */
    private String datasourceId;
    /**
     * 库
     */
    private String catalog;
    /**
     * 模式
     */
    private String schema;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 表类型。典型的类型是“TABLE”，“VIEW”，“SYSTEM TABLE”，“GLOBAL TEMPORARY”，“LOCAL TEMPORARY”，“ALIAS”，“SYNONYM”。
     */
    private String tableType;
    /**
     * 表备注
     */
    private String remarks;
    /**
     * 表状态
     */
    private String status;
    /**
     * 表大小（字节单位）
     */
    private Long size;
    /**
     * 表行数
     */
    private Long rows;
    /**
     * 表创建时间
     */
    private Date createTime;
    /**
     * 表修改时间
     */
    private Date updateTime;

    public String getDatasourceId() {
        return datasourceId;
    }

    public void setDatasourceId(String datasourceId) {
        this.datasourceId = datasourceId;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getRows() {
        return rows;
    }

    public void setRows(Long rows) {
        this.rows = rows;
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
