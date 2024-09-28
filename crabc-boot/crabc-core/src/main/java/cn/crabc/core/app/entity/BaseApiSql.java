package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * API SQL配置
 *
 * @author yuqf
 */
@Setter
@Getter
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

}
