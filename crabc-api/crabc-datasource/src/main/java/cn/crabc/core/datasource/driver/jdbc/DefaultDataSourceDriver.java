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
import java.util.concurrent.TimeUnit;

/**
 * 默认通用方法实现类
 *
 * @author yuqf
 */
public abstract class DefaultDataSourceDriver implements DataSourceDriver {
    private static final Logger log = LoggerFactory.getLogger(DefaultDataSourceDriver.class);

    @Override
    public String getName() {
        return "jdbc";
    }

    @Override
    public String test(BaseDataSource baseDataSource) {
        Connection connection = null;
        HikariDataSource dataSource = null;
        try {
            dataSource = createHikariDataSource(baseDataSource, true);
            connection = dataSource.getConnection();

        } catch (Exception e) {
            Throwable cause = e.getCause();
            log.error("数据库测试异常：{}", e.getMessage());
            return cause == null ? e.getMessage() : cause.getLocalizedMessage();
        }finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            if (dataSource != null) {
                dataSource.close();
            }
        }
        return "1";
    }

    @Override
    public void init(BaseDataSource ds) {
        String datasourceId = ds.getDatasourceId().toString();
        DataSource oldDataSource = JdbcDataSourceRouter.exist(datasourceId) ? 
                                 JdbcDataSourceRouter.getDataSource(datasourceId) : null;

        HikariDataSource dataSource = createHikariDataSource(ds, false);
        
        // 连接池预热
        boolean initSuccess = false;
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(1)) {
                log.debug("数据源初始化成功.");
                initSuccess = true;
            } else {
                log.error("数据源初始化失败.");
            }
        } catch (SQLException e) {
            log.error("Failed to warm up DataSource. Shutting down.", e);
        }
        
        // 仅在初始化成功时才添加到路由池
        if (initSuccess) {
            JdbcDataSourceRouter.setDataSource(datasourceId, dataSource);
            
            // 关闭旧数据源
            if (oldDataSource != null){
                if (oldDataSource instanceof DruidDataSource) {
                    ((DruidDataSource) oldDataSource).close();
                } else if (oldDataSource instanceof HikariDataSource) {
                    ((HikariDataSource) oldDataSource).close();
                }
            }
        } else {
            // 初始化失败，立即关闭新数据源，不添加到路由池
            dataSource.close();
        }
    }

    @Override
    public void destroy(String dataSourceId) {
        JdbcDataSourceRouter.destroy(dataSourceId);
    }

    private HikariDataSource createHikariDataSource(BaseDataSource ds, boolean isTest) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setUsername(ds.getUsername());
        dataSource.setPassword(ds.getPassword());
        dataSource.setJdbcUrl(ds.getJdbcUrl());
        
        if (isTest) {
            dataSource.setMinimumIdle(0);
            dataSource.setInitializationFailTimeout(1);
            dataSource.setConnectionTimeout(2000);
        } else {
            if (ds.getMinIdle() != null) {
                dataSource.setMinimumIdle(ds.getMinIdle());
            }else{
                dataSource.setMinimumIdle(1);
            }
            if (ds.getMaxActive() != null) {
                dataSource.setMaximumPoolSize(ds.getMaxActive());
            }else{
                dataSource.setMaximumPoolSize(2);
            }
            if (ds.getMaxLifetime() != null) {
                dataSource.setMaxLifetime(ds.getMaxLifetime());
            }else{
                dataSource.setMaxLifetime(TimeUnit.MINUTES.toMillis(15L));
            }
            if (ds.getIdleTimeout() != null) {
                dataSource.setIdleTimeout(ds.getIdleTimeout());
            }else{
                // 设置空闲连接超时时间，默认10分钟
                dataSource.setIdleTimeout(TimeUnit.MINUTES.toMillis(10L));
            }
            if (ds.getConnectTimeout() != null) {
                dataSource.setConnectionTimeout(ds.getConnectTimeout());
            }else{
                // 设置连接超时时间，默认30秒
                dataSource.setConnectionTimeout(TimeUnit.SECONDS.toMillis(30L));
            }
            if (ds.getKeepaliveTime() != null && ds.getKeepaliveTime() != 0) {
                dataSource.setKeepaliveTime(ds.getKeepaliveTime());
            }else{
                dataSource.setKeepaliveTime(TimeUnit.MINUTES.toMillis(2L));
            }
            dataSource.setLeakDetectionThreshold(TimeUnit.MINUTES.toMillis(2L));

            if ("oracle".equalsIgnoreCase(ds.getDatasourceType())) {
                // Oracle连接测试查询
                dataSource.setConnectionTestQuery("SELECT 1 FROM DUAL");
                // 设置连接初始化SQL，确保连接可用
                dataSource.setConnectionInitSql("SELECT 1 FROM DUAL");
            }
            
            // 自动提交设置
            dataSource.setAutoCommit(true);
        }
        
        setDriverClass(dataSource, ds.getDatasourceType());
        return dataSource;
    }

    /**
     * 加载特殊驱动
     */
    private void setDriverClass(HikariDataSource dataSource, String datasourceType) {
        if (datasourceType == null) {
            return;
        }
        switch (datasourceType.toLowerCase()) {
            case "dm":
                dataSource.setDriverClassName(JdbcConstants.DM_DRIVER);
                break;
            case "oceanbase":
                dataSource.setDriverClassName(JdbcConstants.OCEANBASE_DRIVER2);
                break;
            case "clickhouse":
                dataSource.setDriverClassName(JdbcConstants.CLICKHOUSE_DRIVER_NEW);
                break;
            case "duckdb":
                dataSource.setDriverClassName("org.duckdb.DuckDBDriver");
                break;
            case "dolphindb":
                dataSource.setDriverClassName("com.dolphindb.jdbc.Driver");
                break;
        }
    }
}
