package cn.crabc.core.app.entity;

import cn.crabc.core.spi.bean.BaseDataSource;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *
 * 数据源配置
 *
 * @author yuqf
 */
@Setter
@Getter
@JsonIgnoreProperties(value = {"secretKey"})
public class BaseDatasource extends BaseDataSource {

    /**
     * 租户
     */
    private Integer tenantId;

    private String createBy;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新者
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;
}
