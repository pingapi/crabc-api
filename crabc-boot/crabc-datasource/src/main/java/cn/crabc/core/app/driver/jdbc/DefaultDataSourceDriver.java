package cn.crabc.core.app.driver.jdbc;

import cn.crabc.core.app.config.JdbcDataSourceRouter;
import cn.crabc.core.app.exception.CustomException;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import com.alibaba.druid.util.JdbcUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认通用方法实现类
 *
 * @author yuqf
 */
public abstract class DefaultDataSourceDriver implements DataSourceDriver {
    Logger log = LoggerFactory.getLogger(DefaultDataSourceDriver.class);

    @Override
    public String getName() {
        return "jdbc";
    }

    @Override
    public Integer test(BaseDataSource baseDataSource) {
        Connection connection = null;
        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setUsername(baseDataSource.getUsername());
            dataSource.setPassword(baseDataSource.getPassword());
            dataSource.setJdbcUrl(baseDataSource.getJdbcUrl());
            dataSource.setInitializationFailTimeout(1);
            dataSource.setConnectionTimeout(2000);
            connection = dataSource.getConnection();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            log.error("--{}", e.getMessage());
            throw new CustomException(51005, cause == null ? e.getMessage() : cause.getLocalizedMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
    }

    @Override
    public void init(BaseDataSource ds) {
        String datasourceId = ds.getDatasourceId();
        if (JdbcDataSourceRouter.exist(datasourceId)) {
            this.destroy(datasourceId);
        }
        String username = ds.getUsername();
        String password = ds.getPassword();
        String jdbcUrl = ds.getJdbcUrl();
        // mysql数据库链接加上指定SCHEMA
        if (jdbcUrl != null && jdbcUrl.contains(":")) {
            boolean mysql = JdbcUtils.isMysqlDbType(jdbcUrl.trim().split(":")[1]);
            if (mysql && !jdbcUrl.contains("databaseTerm=SCHEMA") && jdbcUrl.contains("?")) {
                jdbcUrl = jdbcUrl + "&databaseTerm=SCHEMA";
            } else if (mysql && !jdbcUrl.contains("databaseTerm=SCHEMA") && !jdbcUrl.contains("?")) {
                jdbcUrl = jdbcUrl + "?databaseTerm=SCHEMA";
            }
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setMinimumIdle(5);
        dataSource.setMaxLifetime(1800000);
        dataSource.setIdleTimeout(600000);
        dataSource.setConnectionTimeout(10000);
        dataSource.setKeepaliveTime(300000);

        JdbcDataSourceRouter.setDataSource(datasourceId, dataSource);
    }

    @Override
    public void destroy(String dataSourceId) {
        JdbcDataSourceRouter.destroy(dataSourceId);
    }
}
