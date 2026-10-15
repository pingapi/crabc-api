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

/**
 * 数据源驱动
 *
 * @author yuqf
 */
public interface DataSourceDriver<T> {

    /**
     * 数据源名称
     *
     * @return
     */
    String getName();

    /**
     * 测试连接
     *
     * @param dataSource
     * @return
     */
    String test(BaseDataSource dataSource);

    /**
     * 初始化
     *
     * @param dataSource
     */
    void init(BaseDataSource dataSource);

    /**
     * 销毁
     *
     * @param dataSourceId
     */
    void destroy(String dataSourceId);

    /**
     * 元数据对象
     *
     * @return
     */
    MetaDataMapper getMetaData();

    /**
     * 处理对象
     *
     * @return
     */
    StatementMapper getStatement();
}
