package cn.crabc.core.app.entity.vo;

import cn.crabc.core.app.entity.BaseEntity;
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
public class BaseApiInfoVO extends BaseEntity {

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
     * 权限级别：public、default、private
     */
    private String apiLevel;

    /**
     * 开放启用 1/0
     */
    private Integer enabled;

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
     * 分页设置，不分页：0、只分页：page、分页并统计：pageCount
     */
    private Integer pageSetup;

    private String sqlScript;

    private String showSqlScript;
    /**
     * 发布时间
     */
    private Date releaseTime;

    private Integer applyed;

}
