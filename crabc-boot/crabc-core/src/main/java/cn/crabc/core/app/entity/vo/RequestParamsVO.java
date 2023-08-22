package cn.crabc.core.app.entity.vo;

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

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamModel() {
        return paramModel;
    }

    public void setParamModel(String paramModel) {
        this.paramModel = paramModel;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }
}
