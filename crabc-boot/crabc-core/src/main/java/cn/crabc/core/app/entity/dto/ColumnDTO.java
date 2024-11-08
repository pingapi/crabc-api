package cn.crabc.core.app.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 字段信息
 *
 * @author yuqf
 */
@Setter
@Getter
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
}
