package cn.crabc.core.app.entity.vo;

import cn.crabc.core.app.entity.BaseApiInfo;
import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.app.entity.BaseApiSql;

import java.util.ArrayList;
import java.util.List;

/**
 * API详情
 */
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

    public BaseApiInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseApiInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public BaseApiSql getSqlInfo() {
        return sqlInfo;
    }

    public void setSqlInfo(BaseApiSql sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public String getQueryEngine() {
        return queryEngine;
    }

    public void setQueryEngine(String queryEngine) {
        this.queryEngine = queryEngine;
    }

    public List<BaseApiParam> getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(List<BaseApiParam> requestParam) {
        this.requestParam = requestParam;
    }

    public List<BaseApiParam> getResponseParam() {
        return responseParam;
    }

    public void setResponseParam(List<BaseApiParam> responseParam) {
        this.responseParam = responseParam;
    }
}
