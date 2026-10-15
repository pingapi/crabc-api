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
package cn.crabc.core.spi;

import java.util.List;

/**
 * 基础操作 Mapper
 *
 * @author yuqf
 */
public interface StatementMapper<T> extends Mapper<T> {

    /**
     * 查询单个对象
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    T selectOne(String dataSourceId, String schema, String sql, Object params);

    /**
     * 查询对象列表
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    List<T> selectList(String dataSourceId, String schema, String sql, Object params);

    /**
     * 查询分页对象
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @param pageNum
     * @param pageSize
     * @return
     */
    Object selectPage(String dataSourceId, String schema, String sql, Object params, int pageNum, int pageSize);

    /**
     * 新增
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    int insert(String dataSourceId, String schema, String sql, Object params);

    /**
     * 删除
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    int delete(String dataSourceId, String schema, String sql, Object params);

    /**
     * 修改
     *
     * @param dataSourceId
     * @param schema
     * @param sql
     * @param params
     * @return
     */
    int update(String dataSourceId, String schema, String sql, Object params);

    /**
     * 批量执行多条DML脚本，调用方负责保证脚本只包含insert/update/delete。
     *
     * @param dataSourceId 数据源ID
     * @param schema schema或catalog
     * @param sqlList 已拆分的DML脚本
     * @param params SQL参数
     * @param transactionEnabled 是否开启事务
     * @return 总影响行数
     */
    int executeBatchDml(String dataSourceId, String schema, List<String> sqlList, Object params, boolean transactionEnabled);
}
