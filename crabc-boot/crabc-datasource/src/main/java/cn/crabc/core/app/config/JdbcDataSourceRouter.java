package cn.crabc.core.app.config;

import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.app.exception.CustomException;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;

/**
 * JDBC数据源 动态路由
 *
 * @author yuqf
 */
public class JdbcDataSourceRouter extends AbstractRoutingDataSource {

    /**
     * 当前线程数据源KEY
     */
    private static final ThreadLocal<String> DATA_SOURCE_KEY = new ThreadLocal<>();

    /**
     * 获取数据源key
     *
     * @return
     */
    public static String getDataSourceKey() {
        return JdbcDataSourceRouter.DATA_SOURCE_KEY.get();
    }

    /**
     * 设置数据源key
     *
     * @param key
     */
    public static void setDataSourceKey(String key) {
        JdbcDataSourceRouter.DATA_SOURCE_KEY.set(key);
    }

    /**
     * 移除数据源
     */
    public static void remove() {
        JdbcDataSourceRouter.DATA_SOURCE_KEY.remove();
    }

    /**
     * 判断数据源是否存在
     */
    public static boolean exist(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceId);
        if (dataSource != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取数据源
     *
     * @param dataSourceId
     * @return
     */
    public static DataSource getDataSource(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceId);
        if (dataSource == null) {
            throw new CustomException(51001, "数据源不存在！");

        }
        return dataSource;
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public static DataSource getDataSource() {
        String dataSourceKey = getDataSourceKey();
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceKey);
        if (dataSource == null) {
            throw new CustomException(51001, "数据源不存在！");
        }

        return DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceKey);
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
     * @return
     */
    @Override
    protected DataSource determineTargetDataSource() {
        Object dataSourceId = this.determineCurrentLookupKey();
        // 默认系统数据源
        if (dataSourceId == null) {
            return super.getResolvedDefaultDataSource();
        }
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceId.toString());
        if (dataSource == null) {
            throw new CustomException(51001, "数据源不存在！");
        }
        return dataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
