package cn.crabc.core.app.entity;

/**
 * 限流规则
 *
 * @author yuqf
 */
public class BaseFlowRule extends BaseEntity {

    private Integer flowId;
    private String flowName;

    private String flowGrade;

    private Integer flowCount;

    private Integer unitTime;

    private String flowType;

    //业务字段
    private Integer apiCount = 0;
    //业务字段
    private Long apiId;
    //业务字段
    private String apiPath;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public Integer getFlowId() {
        return flowId;
    }

    public void setFlowId(Integer flowId) {
        this.flowId = flowId;
    }

    public String getFlowGrade() {
        return flowGrade;
    }

    public void setFlowGrade(String flowGrade) {
        this.flowGrade = flowGrade;
    }

    public Integer getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(Integer flowCount) {
        this.flowCount = flowCount;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public Integer getApiCount() {
        return apiCount;
    }

    public void setApiCount(Integer apiCount) {
        this.apiCount = apiCount;
    }

    public Integer getUnitTime() {
        return unitTime;
    }

    public void setUnitTime(Integer unitTime) {
        this.unitTime = unitTime;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public String getApiPath() {
        return apiPath;
    }

    public void setApiPath(String apiPath) {
        this.apiPath = apiPath;
    }
}
