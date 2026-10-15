package cn.crabc.core.app.entity.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Setter
@Getter
public class SqlParseVO {

    private Set<String> reqColumns;

    private Set<ColumnParseVo> resColumns;

    private String sqlScript;

    private String datasourceType;

}
