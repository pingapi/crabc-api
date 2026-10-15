package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * api详情参数
 */
@Setter
@Getter
public class ApiParamsVO {

    // 请求参数
    List<RequestParamsVO> reqParams;

    //响应参数
    List<RequestParamsVO> resParams;
}
