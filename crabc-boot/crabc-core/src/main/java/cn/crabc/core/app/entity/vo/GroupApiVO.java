package cn.crabc.core.app.entity.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 分组下的API
 */
public class GroupApiVO implements Serializable {
    private Integer groupId;
    private String groupName;
    private Integer apiId;
    private String apiName;
    private Integer parentId;

    private List<GroupApiVO> child = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (Objects.equals(this.getApiId(), ((GroupApiVO) obj).getApiId())) {
            return true;
        }
        return false;
    }
    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public List<GroupApiVO> getChild() {
        return child;
    }

    public void setChild(List<GroupApiVO> child) {
        this.child = child;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
