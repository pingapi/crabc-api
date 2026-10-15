package cn.crabc.core.datasource.driver.jdbc;

import cn.crabc.core.datasource.mapper.BaseDataHandleMapper;
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.StatementMapper;

/**
 * JDBC通用操作实现类
 *
 * @author yuqf
 */
public class JdbcDataSourceDriver extends DefaultDataSourceDriver {
    private BaseDataHandleMapper baseDataHandleMapper;
    private JdbcStatement jdbcStatement;
    private JdbcMetaData jdbcMetaData;

    public JdbcDataSourceDriver(BaseDataHandleMapper baseDataHandleMapper) {
        this.baseDataHandleMapper = baseDataHandleMapper;
        jdbcStatement = new JdbcStatement(baseDataHandleMapper);
        jdbcMetaData = new JdbcMetaData();
    }

    @Override
    public MetaDataMapper getMetaData() {
        return jdbcMetaData;
    }

    @Override
    public StatementMapper getStatement() {
        return jdbcStatement;
    }
}
