package cn.crabc.core.datasource.driver.jdbc;

import cn.crabc.core.datasource.config.JdbcDataSourceRouter;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
    public String test(BaseDataSource baseDataSource) {
        Connection connection = null;
        try {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setUsername(baseDataSource.getUsername());
            dataSource.setPassword(baseDataSource.getPassword());
            String jdbcUrl = baseDataSource.getJdbcUrl();
            dataSource.setJdbcUrl(jdbcUrl);
            if (jdbcUrl != null && jdbcUrl.toLowerCase().startsWith("jdbc:jtds")) {
                dataSource.setConnectionTestQuery("SELECT 1");
                dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
            }
            dataSource.setInitializationFailTimeout(1);
            dataSource.setConnectionTimeout(2000);
            connection = dataSource.getConnection();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            log.error("--数据库测试异常：{}", e.getMessage());
            return cause == null ? e.getMessage() : cause.getLocalizedMessage();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return "1";
    }

    @Override
    public void init(BaseDataSource ds) {
        String datasourceId = ds.getDatasourceId();
        DataSource oldDataSource = null;
        if (JdbcDataSourceRouter.exist(datasourceId)) {
            oldDataSource = JdbcDataSourceRouter.getDataSource(datasourceId);
        }
        String username = ds.getUsername();
        String password = ds.getPassword();
        String jdbcUrl = ds.getJdbcUrl();

        HikariDataSource dataSource = new HikariDataSource();
        this.analysisJdbcUrl(jdbcUrl, dataSource);

        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMaxLifetime(900000);
        dataSource.setIdleTimeout(300000);
        dataSource.setConnectionTimeout(10000);
        dataSource.setKeepaliveTime(300000);

        JdbcDataSourceRouter.setDataSource(datasourceId, dataSource);
        // 销毁旧的数据源链接
        if (oldDataSource != null) {
            if (oldDataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) oldDataSource;
                druidDataSource.close();
            } else if (oldDataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) oldDataSource;
                hikariDataSource.close();
            }
        }
    }

    /**
     * 指定mysql的schema
     * @param jdbcUrl
     * @return
     */
    private void analysisJdbcUrl(String jdbcUrl, HikariDataSource dataSource){
        // mysql数据库链接加上指定SCHEMA
        try {
            if (jdbcUrl != null && jdbcUrl.toLowerCase().startsWith("jdbc:jtds:")){
                dataSource.setConnectionTestQuery("SELECT 1");
                dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
            }
        }catch (Exception e){
            log.error("测试数据源解析jdbcUrl异常{}",e.getMessage());
        }finally {
            dataSource.setJdbcUrl(jdbcUrl);
        }
    }
    @Override
    public void destroy(String dataSourceId) {
        JdbcDataSourceRouter.destroy(dataSourceId);
    }
}
