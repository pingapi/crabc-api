package cn.crabc.core.datasource.driver.jdbc;

import cn.crabc.core.datasource.config.JdbcDataSourceRouter;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
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
            dataSource.setInitializationFailTimeout(1);
            dataSource.setConnectionTimeout(2000);
            this.setDriverClass(dataSource, baseDataSource.getDatasourceType());
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
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setMinimumIdle(1);
        dataSource.setMaximumPoolSize(10);
        dataSource.setMaxLifetime(900000);
        dataSource.setIdleTimeout(300000);
        dataSource.setConnectionTimeout(10000);
        dataSource.setKeepaliveTime(300000);
        this.setDriverClass(dataSource, ds.getDatasourceType());

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

    @Override
    public void destroy(String dataSourceId) {
        JdbcDataSourceRouter.destroy(dataSourceId);
    }

    /**
     * 加载特殊驱动
     * @param dataSource
     * @param datasourceType
     */
    private void setDriverClass(HikariDataSource dataSource, String datasourceType){
        if("dm".equals(datasourceType)){
            dataSource.setDriverClassName(JdbcConstants.DM_DRIVER);
        } else if (datasourceType.startsWith("gbase8")) {
            dataSource.setDriverClassName(JdbcConstants.GBASE_DRIVER);
        } else if (datasourceType.startsWith("kingbase8")) {
            dataSource.setDriverClassName(JdbcConstants.KINGBASE8_DRIVER);
        } else if ("xugu".equals(datasourceType)) {
            dataSource.setDriverClassName(JdbcConstants.XUGU_DRIVER);
        } else if ("oceanbase".equals(datasourceType)) {
            dataSource.setDriverClassName(JdbcConstants.OCEANBASE_DRIVER2);
            // SyBase
        } else if (dataSource.getJdbcUrl().toLowerCase().startsWith("jdbc:jtds:")) {
            dataSource.setConnectionTestQuery("SELECT 1");
            dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        }
    }
}
