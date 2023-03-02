package cn.crabc.core.system.entity.vo;

import java.util.List;

public class GroupVO {
    private Integer groupId;
    private String groupName;
    private Integer parentId;
    private List<GroupVO> children;

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

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }


}
