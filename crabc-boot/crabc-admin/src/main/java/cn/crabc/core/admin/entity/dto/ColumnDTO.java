package cn.crabc.core.admin.entity.dto;

import java.io.Serializable;

/**
 * 字段信息
 *
 * @author yuqf
 */
public class ColumnDTO implements Serializable {
    /** 名称 */
    private String columnName;
    /** 类型 */
    private String columnType;
    /** 别名 */
    private String alias;
    /** 操作符 */
    private String operator;

    private String nullable;

    private String value;

    private String remarks;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getNullable() {
        return nullable;
    }

    public void setNullable(String nullable) {
        this.nullable = nullable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
