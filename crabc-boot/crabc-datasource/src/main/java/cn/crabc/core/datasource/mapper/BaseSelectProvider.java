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
package cn.crabc.core.datasource.mapper;
import cn.crabc.core.datasource.constant.BaseConstant;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * mybatis SQL提供器
 *
 * @author yuqf
 */
@Mapper
public class BaseSelectProvider {

    // 标签
    private static final String[] tags = {"</if>","</foreach>","</where>","</set>","</choose>","</when>","</trim>","</otherwise"};

    /**
     * 添加脚本标签前缀
     * @param sql
     * @return
     */
    private String addScript(String sql){
        for(String tag : tags) {
            if (sql.contains(tag)) {
                sql = "<script> " + sql + " </script>";
                break;
            }
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
