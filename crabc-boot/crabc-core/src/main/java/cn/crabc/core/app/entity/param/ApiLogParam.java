package cn.crabc.core.app.entity.param;

import lombok.Getter;
import lombok.Setter;

/**
 * API日志查询请求参数
 *
 * @author yuqf
 */
@Setter
@Getter
public class ApiLogParam {
    private String result;

    private String keyword;

    private String startTime;

    private String endTime;

    private Integer pageNum;

    private Integer pageSize;
}
