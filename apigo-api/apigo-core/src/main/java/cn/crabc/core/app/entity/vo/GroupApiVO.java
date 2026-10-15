package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 分组下的API
 */
@Setter
@Getter
public class GroupApiVO implements Serializable {
    private Integer groupId;
    private String groupName;
    private Integer apiId;
    private String apiName;
    private Integer parentId;

    private List<GroupApiVO> child = new ArrayList<>();
}
