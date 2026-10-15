package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 应用和API关联关系
 *
 * @author yuqf
 */
@Setter
@Getter
public class BaseAppApi {

    private Long appId;

    private Long apiId;

    private String createBy;

    private Date createTime;

    private List<Long> apiIds;

}
