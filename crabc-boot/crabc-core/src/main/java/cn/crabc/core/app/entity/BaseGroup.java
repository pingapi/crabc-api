package cn.crabc.core.app.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * api分组
 *
 * @author yuqf
 */
@Setter
@Getter
public class BaseGroup extends BaseEntity{

    private Integer groupId;

    private String groupName;

    private String groupDesc;

    private Integer parentId;
}
