package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class GroupVO {
    private Integer groupId;
    private String groupName;
    private Integer parentId;
    private List<GroupVO> children;

}
