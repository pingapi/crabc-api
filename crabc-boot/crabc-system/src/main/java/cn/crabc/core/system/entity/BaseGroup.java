package cn.crabc.core.system.entity;

import java.util.List;

/**
 * api分组
 *
 * @author yuqf
 */

public class BaseGroup extends BaseEntity{

    private Integer groupId;

    private String groupName;

    private String groupDesc;

    private Integer parentId;

    private List<BaseGroup> children;

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

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public List<BaseGroup> getChildren() {
        return children;
    }

    public void setChildren(List<BaseGroup> children) {
        this.children = children;
    }
}
