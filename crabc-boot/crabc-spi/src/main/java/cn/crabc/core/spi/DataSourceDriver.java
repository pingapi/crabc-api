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

import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;

import java.util.List;

/**
 * 数据源驱动方法
 *
 * @author yuqf
 */
public interface DataSourceDriver<T> extends BaseDataHandle<T> {

    /**
     * 数据源名称
     * @return
     */
    String getName();

    /**
     * 测试数据源连接
     * @param dataSource
     * @return
     */
    Integer test(BaseDataSource dataSource);

    /**
     * 初始化
     * @param dataSource
     */
    void init(BaseDataSource dataSource);

    /**
     * 销毁
     * @param dataSourceId
     */
    void destroy(String dataSourceId);

    List<String> getCatalogs(String dataSourceId);

    /**
     * 获取数据源Schema
     * @param dataSourceId
     * @param catalog
     * @return
     */
    List<Schema> getSchemas(String dataSourceId, String catalog);

    /**
     * 获取数据表列表
     * @param dataSourceId
     * @param catalog
     * @param schema
     * @return
     */
    List<Table> getTables(String dataSourceId, String catalog, String schema);

    /**
     * 获取数据表字段列表
     * @param dataSourceId
     * @param catalog
     * @param schema
     * @param table
     * @return
     */
    List<Column> getColumns(String dataSourceId, String catalog, String schema, String table);
}
