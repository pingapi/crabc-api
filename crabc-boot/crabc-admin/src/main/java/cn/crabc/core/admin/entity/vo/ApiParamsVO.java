package cn.crabc.core.admin.entity.vo;

import java.util.List;

/**
 * api详情参数
 */
public class ApiParamsVO {

    // 请求参数
    List<RequestParamsVO> reqParams;

    //响应参数
    List<RequestParamsVO> resParams;

    public List<RequestParamsVO> getReqParams() {
        return reqParams;
    }

    public void setReqParams(List<RequestParamsVO> reqParams) {
        this.reqParams = reqParams;
    }

    public List<RequestParamsVO> getResParams() {
        return resParams;
    }

    public void setResParams(List<RequestParamsVO> resParams) {
        this.resParams = resParams;
    }
}
