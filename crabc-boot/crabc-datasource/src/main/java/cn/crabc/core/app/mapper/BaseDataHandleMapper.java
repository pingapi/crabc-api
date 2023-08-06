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

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.List;
import java.util.Map;

/**
 * 通用数据操作 Mapper
 *
 * @author yuqf
 */
public interface BaseDataHandleMapper {

    /**
     * 查询类SQL
     * @param params
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "executeQuery")
    List<Map<String, Object>> executeQuery(Map<String, Object> params);

    /**
     * 新增类SQL
     * @param params
     * @return
     */
    @InsertProvider(type = BaseSelectProvider.class, method = "executeInsert")
    Integer executeInsert(Map<String, Object> params);

    /**
     * 修改类SQL
     * @param params
     * @return
     */
    @UpdateProvider(type = BaseSelectProvider.class, method = "executeUpdate")
    Integer executeUpdate(Map<String, Object> params);

    /**
     * 删除类SQL
     * @param params
     * @return
     */
    @UpdateProvider(type = BaseSelectProvider.class, method = "executeDelete")
    Integer executeDelete(Map<String, Object> params);
}
