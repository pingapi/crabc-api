package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestParamsVO {
    /**
     * 参数名
     */
    private String paramName;

    /**
     * 参数模型，request,response
     */
    private String paramModel;

    /**
     * 是否必填
     */
    private String required;

    /**
     * 操作
     */
    private String operation;

    /**
     * 参数类型
     */
    private String paramType;

    /**
     * 参数默认值
     */
    private String defaultValue;

    /**
     * 参数示例值
     */
    private String example;

    /**
     * 参数备注
     */
    private String paramDesc;

}
