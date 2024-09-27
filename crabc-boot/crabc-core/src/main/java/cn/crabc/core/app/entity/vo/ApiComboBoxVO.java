package cn.crabc.core.app.entity.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiComboBoxVO {

    private Long apiId;

    private String apiName;

    private String apiType;

    private String apiStatus;

    private Integer groupId;
}
