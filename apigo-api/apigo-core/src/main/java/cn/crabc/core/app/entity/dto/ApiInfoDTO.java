package cn.crabc.core.app.entity.dto;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.BaseApp;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * API相关的详情信息
 *
 * @author yuqf
 */
@Setter
@Getter
public class ApiInfoDTO extends BaseApiInfo {

    /**
     * 请求时间
     */
    private Date requestDate;

    private Long requestTime;

    private String userId;

    /**
     * 有权限的应用
     */
    private List<BaseApp> appList = new ArrayList<>();

    /**
     * API参数
     */
    private List<BaseApiParam> requestParams;

}
