package cn.crabc.core.app.entity.param;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.BaseApiSql;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * API请求参数
 *
 * @author yuqf
 */
@Setter
@Getter
public class ApiInfoParam {

    /**
     * 基本信息
     */
    private BaseApiInfo baseInfo;

    /**
     * sql
     */
    private BaseApiSql sqlInfo;

    /**
     * 请求参数
     */
    private List<BaseApiParam> requestParam;

    /**
     * 返回参数
     */
    private List<BaseApiParam> responseParam;
    /**
     * 查询引擎
     */
    private  String queryEngine;
}
