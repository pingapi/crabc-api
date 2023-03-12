package cn.crabc.core.admin.entity.vo;

import cn.crabc.core.admin.entity.BaseApiInfo;
import cn.crabc.core.admin.entity.BaseApiSql;

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
}
