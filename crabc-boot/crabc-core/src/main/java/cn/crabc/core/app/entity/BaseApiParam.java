package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * API参数
 *
 * @author yuqf
 */
@Setter
@Getter
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

}
