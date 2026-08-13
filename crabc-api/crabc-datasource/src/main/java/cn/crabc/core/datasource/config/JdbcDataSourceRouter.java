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
    private static final ThreadLocal<String> DATA_SOURCE_KEY = new ThreadLocal<>();

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
        // 移除缓存
        DataSourceManager.DATA_SOURCE_POOL_JDBC.remove(dataSourceId);
        // 关闭连接池
        if (dataSource instanceof DruidDataSource) {
            ((DruidDataSource) dataSource).close();
        } else if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
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
        log.debug("---->>切换数据库连接----");
        Connection connection = this.determineTargetDataSource().getConnection();

        String originalCatalog = null;
        String originalSchema = null;
        // 标记：是否执行了catalog/schema 切换
        boolean needReset = false;

        try {
            Object dataSourceKey = this.determineCurrentLookupKey();
            if (dataSourceKey != null && dataSourceKey.toString().contains(":")) {
                String[] dataSourceStr = dataSourceKey.toString().split(":");
                if (dataSourceStr.length < 3) {
                    return connection;
                }
                String dataSourceType = dataSourceStr[1];
                String targetSchema = dataSourceStr[2];

                // 原始状态
                originalCatalog = connection.getCatalog();
                originalSchema = connection.getSchema();

                if (BaseConstant.CATALOG_DATA_SOURCE.contains(dataSourceType)) {
                    if (!equalsIgnoreNull(originalCatalog, targetSchema)) {
                        connection.setCatalog(targetSchema);
                        needReset = true;
                    }
                } else {
                    if (!equalsIgnoreNull(originalSchema, targetSchema)) {
                        connection.setSchema(targetSchema);
                        needReset = true;
                    }
                }
            }
            return connection;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeException) {
                    log.error("关闭异常连接失败", closeException);
                }
            }
            log.error("数据源连接获取失败, dataSourceKey: {}", determineCurrentLookupKey(), e);
            throw new SQLException(e);
        } finally {
            if (needReset && connection != null && !connection.isClosed()) {
                try {
                    // 还原 Catalog
                    if (!equalsIgnoreNull(connection.getCatalog(), originalCatalog)) {
                        connection.setCatalog(originalCatalog);
                    }
                    // 还原 Schema
                    if (!equalsIgnoreNull(connection.getSchema(), originalSchema)) {
                        connection.setSchema(originalSchema);
                    }
                } catch (Exception resetEx) {
                    // 还原失败 → 直接销毁脏连接
                    log.warn("重置连接schema/catalog失败，销毁脏连接", resetEx);
                    try {
                        connection.close();
                    } catch (SQLException ignored) {}
                }
            }
        }
    }

    /**
     * 对比方法
     *
     */
    private boolean equalsIgnoreNull(String original, String target) {
        if (original == null && target == null) {
            return true;
        }
        if (original == null || target == null) {
            return false;
        }
        return original.equals(target);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
