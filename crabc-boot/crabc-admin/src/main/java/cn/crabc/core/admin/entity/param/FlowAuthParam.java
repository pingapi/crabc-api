package cn.crabc.core.admin.entity.param;

import java.util.List;

public class FlowAuthParam {

    private List<Integer> apiId;

    private Integer id;

    public List<Integer> getApiId() {
        return apiId;
    }

    public void setApiId(List<Integer> apiId) {
        this.apiId = apiId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
