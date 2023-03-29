package cn.crabc.core.plugin.mongodb;

import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;

/**
 * mongodb 驱动实现
 *
 * @author yuqf
 */
public class MongodbDataSourceDriver implements DataSourceDriver {

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String test(BaseDataSource dataSource) {
        return null;
    }

    @Override
    public void init(BaseDataSource dataSource) {

    }

    @Override
    public void destroy(String dataSourceId) {

    }

    @Override
    public MetaDataMapper getMetaData() {
        return null;
    }

    @Override
    public StatementMapper getStatement() {
        return null;
    }
}