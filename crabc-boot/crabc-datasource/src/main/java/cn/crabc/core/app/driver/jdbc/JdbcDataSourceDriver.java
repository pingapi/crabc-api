package cn.crabc.core.app.driver.jdbc;

import cn.crabc.core.app.mapper.BaseDataHandleMapper;
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
    private JdbcMeataData jdbcMeataData;

    public JdbcDataSourceDriver(BaseDataHandleMapper baseDataHandleMapper) {
        this.baseDataHandleMapper = baseDataHandleMapper;
        jdbcStatement = new JdbcStatement(baseDataHandleMapper);
        jdbcMeataData = new JdbcMeataData();
    }

    @Override
    public MetaDataMapper getMetaData() {
        return jdbcMeataData;
    }

    @Override
    public StatementMapper getStatement() {
        return jdbcStatement;
    }
}
