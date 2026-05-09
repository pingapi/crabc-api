package cn.crabc.core.app.entity.vo;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.BaseApiSql;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * API详情
 */
@Setter
@Getter
public class ApiInfoVO {
    /**
     * 基本信息
     */
    private BaseApiInfo baseInfo;

    /**
     * sql
     */
    private BaseApiSql sqlInfo;
    /**
     * 查询引擎
     */
    private  String queryEngine;

    /**
     * 请求参数
     */
    private List<BaseApiParam> requestParam = new ArrayList<>();

    /**
     * 返回参数
     */
    private List<BaseApiParam> responseParam = new ArrayList<>();

    /**
     * 是否存在已发布接口暂存内容
     */
    private Boolean hasDraft = false;

}
