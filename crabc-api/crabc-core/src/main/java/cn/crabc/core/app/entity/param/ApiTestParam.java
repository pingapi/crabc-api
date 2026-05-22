package cn.crabc.core.app.entity.param;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * API测试参数，承载测试弹窗传入的SQL、数据源和运行时参数。
 */
@Setter
@Getter
public class ApiTestParam {

    private String sqlScript;

    private String datasourceId;

    private String datasourceType;

    private String schemaName;

    private String sqlParams;

    private Integer pageSetup;

    /**
     * 是否开启事务，1 开启、0 关闭；仅测试多脚本DML时传给执行层
     */
    private Integer transactionEnabled;

    private String resultType;

    private Object requestParams;

    private String bodyData;

    private Map<String,Object> queryParam;
}
