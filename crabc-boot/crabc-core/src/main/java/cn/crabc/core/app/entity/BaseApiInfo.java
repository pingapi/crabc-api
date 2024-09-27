package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * API基本信息
 *
 * @author yuqf
 */
@Setter
@Getter
public class BaseApiInfo extends BaseEntity {

    /**
     * api业务唯一ID
     */
    private Long apiId;
    /**
     * 接口名称
     */
    private String apiName;
    /**
     * 接口路径
     */
    private String apiPath;
    /**
     * 请求方式 get、post、put、delete、aptch
     */
    private String apiMethod;
    /**
     * API类型：sql、table
     */
    private String apiType;
    /**
     * 授权类型：none、code、secret
     */
    private String authType;
    /**
     * 接口状态：编辑edit、审批audit、发布release、销毁destroy
     */
    private String apiStatus;

    /**
     * 开放启用 1/0
     */
    private Integer enabled;
    /**
     * 分组ID
     */
    private Integer groupId;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * API描述
     */
    private String remarks;

    /**
     * 版本
     */
    private String version;

    /**
     * sql类型，select、insert、update、delete
     */
    private String sqlType;
    /**
     * 返回结果类型：one、array、excel
     *
     */
    private String resultType;
    /**
     * sql语句脚本
     */
    private String sqlScript;

    /**
     * 是否显示脚本1/0
     */
    private String showSqlScript;

    /**
     * 分页设置，不分页：0、只分页：page、分页并统计：pageCount
     */
    private Integer pageSetup;

    /**
     * 数据源Id
     */
    private String datasourceId;

    /**
     * 数据源名称
     */
    private String datasourceName;

    /**
     * schema
     */
    private String schemaName;

    /**
     * 数据源类型
     */
    private String datasourceType;

    /**
     * 发布时间
     */
    private Date releaseTime;

    /**
     * 父级关联ID
     */
    private Long parentId;

}
