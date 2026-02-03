package cn.crabc.core.datasource.config;

import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.driver.DataSourceManager;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * JDBC数据源 动态路由
 *
 * @author yuqf
 */
public class JdbcDataSourceRouter extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(JdbcDataSourceRouter.class);
    /**
     * 当前线程数据源KEY
     */
    private static final ThreadLocal<String> DATA_SOURCE_KEY = new InheritableThreadLocal<>();

    /**
     * 获取数据源key
     */
    public static String getDataSourceKey() {
        return DATA_SOURCE_KEY.get();
    }

    /**
     * 设置数据源key
     */
    public static void setDataSourceKey(String key) {
        DATA_SOURCE_KEY.set(key);
    }

    /**
     * 移除数据源
     */
    public static void remove() {
        DATA_SOURCE_KEY.remove();
    }

    /**
     * 判断数据源是否存在
     */
    public static boolean exist(String dataSourceId) {
        return DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId)) != null;
    }

    /**
     * 获取数据源ID
     * @param dataSourceId
     * @return
     */
    private static String getDataSourceId(String dataSourceId) {
        return dataSourceId == null ? null : dataSourceId.split(":")[0];
    }

    /**
     * 销毁数据源
     */
    public static void destroy(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId));
        if (dataSource == null) {
            return;
        }
        try {
            if (dataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) dataSource;
                druidDataSource.close();
            } else if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                hikariDataSource.close();
            }
        } finally {
            DataSourceManager.DATA_SOURCE_POOL_JDBC.remove(dataSourceId);
        }
    }

    /**
     * 获取指定数据源
     */
    public static DataSource getDataSource(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId));
        if (dataSource == null) {
            throw new CustomException(ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getCode(), 
                ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getMassage());
        }
        return dataSource;
    }

    /**
     * 获取当前数据源
     */
    public static DataSource getDataSource() {
        String dataSourceKey = getDataSourceKey();
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceKey));
        if (dataSource == null) {
            throw new CustomException(ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getCode(), ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getMassage());
        }

        return dataSource;
    }

    /**
     * 添加数据源
     *
     * @param dataSourceId
     **/
    public static void setDataSource(String dataSourceId, DataSource dataSource) {
        DataSourceManager.DATA_SOURCE_POOL_JDBC.put(dataSourceId, dataSource);
    }

    /**
     * 切换数据源
     */
    @Override
    protected DataSource determineTargetDataSource() {
        Object dataSourceKey = determineCurrentLookupKey();
        if (dataSourceKey == null) {
            return super.getResolvedDefaultDataSource();
        }
        String dataSourceId = getDataSourceId(dataSourceKey.toString());
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceId);
        if (dataSource == null) {
            throw new CustomException(ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getCode(),
                ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getMassage());
        }
        return dataSource;
    }

    /**
     * 获取连接
     *
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = null;
        Object dataSourceKey = null;
        try {
            connection = determineTargetDataSource().getConnection();
            dataSourceKey = determineCurrentLookupKey();
            
            if (dataSourceKey != null && dataSourceKey.toString().contains(":")) {
                String[] dataSourceInfo = dataSourceKey.toString().split(":");
                if (dataSourceInfo.length == 3) {
                    setConnectionSchema(connection, dataSourceInfo[1], dataSourceInfo[2]);
                }
            }
            return connection;
        } catch (Exception e) {
            log.error("数据源连接获取失败, dataSourceKey: {}", dataSourceKey, e);
            throw e;
        }
    }
    
    /**
     * 设置连接的schema
     */
    private void setConnectionSchema(Connection connection, String dataSourceType, String schema) throws SQLException {
        if (BaseConstant.CATALOG_DATA_SOURCE.contains(dataSourceType)) {
            connection.setCatalog(schema);
        } else {
            connection.setSchema(schema);
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
