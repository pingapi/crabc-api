package cn.crabc.core.admin.entity.param;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.BaseApiParam;
import cn.crabc.core.admin.entity.BaseApiSql;

import java.util.List;

/**
 * API请求参数
 *
 * @author yuqf
 */
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
