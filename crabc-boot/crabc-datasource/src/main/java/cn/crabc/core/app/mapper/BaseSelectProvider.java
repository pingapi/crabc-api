/*
 * Copyright 2023, crabc.cn (creabc@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.crabc.core.app.mapper;
import cn.crabc.core.app.constant.BaseConstant;

import java.util.Map;

/**
 * mybatis SQL提供器
 *
 * @author yuqf
 */
public class BaseSelectProvider {

    /**
     * 添加脚本标签前缀
     * @param sql
     * @return
     */
    private String addScript(String sql){
        if (sql.contains("</if>") || sql.contains("</foreach>") || sql.contains("<where>") || sql.contains("<set>") || sql.contains("<choose>") || sql.contains("<when>") || sql.contains("</trim>")) {
            sql = "<script> " + sql + " </script>";
        }
        return sql;
    }

    /**
     * 查询类
     * @param params
     * @return
     */
    public String executeQuery(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        sql = this.addScript(sql);
        return sql;
    }


    /**
     * 新增类
     * @param params
     * @return
     */
    public String executeInsert(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        sql = this.addScript(sql);
        return sql;
    }

    /**
     * 修改类
     * @param params
     * @return
     */
    public String executeUpdate(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        sql = this.addScript(sql);
        return sql;
    }

    /**
     * 删除类
     * @param params
     * @return
     */
    public String executeDelete(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        sql = this.addScript(sql);
        return sql;
    }
}
