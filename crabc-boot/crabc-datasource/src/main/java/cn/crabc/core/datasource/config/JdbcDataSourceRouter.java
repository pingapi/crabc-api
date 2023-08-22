package cn.crabc.core.datasource.config;

import cn.crabc.core.datasource.driver.DataSourceManager;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;
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
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId));
        if (dataSource != null) {
            return true;
        }
        return false;
    }

    /**
     * 获取数据源ID
     * @param dataSourceId
     * @return
     */
    private static String getDataSourceId(String dataSourceId){
        return dataSourceId == null ? null : dataSourceId.split(":")[0];
    }

    /**
     * 销毁
     *
     * @param dataSourceId
     * @return
     */
    public static void destroy(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId));
        if (dataSource instanceof DruidDataSource) {
            DruidDataSource druidDataSource = (DruidDataSource) dataSource;
            druidDataSource.close();
        } else if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            hikariDataSource.close();
        }
        DataSourceManager.DATA_SOURCE_POOL_JDBC.remove(dataSourceId);
    }

    /**
     * 获取数据源
     *
     * @param dataSourceId
     * @return
     */
    public static DataSource getDataSource(String dataSourceId) {
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(getDataSourceId(dataSourceId));
        if (dataSource == null) {
            throw new CustomException(ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getCode(), ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getMassage());

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
     *
     * @return
     */
    @Override
    protected DataSource determineTargetDataSource() {
        Object dataSourceKey = this.determineCurrentLookupKey();
        // 默认系统数据源
        if (dataSourceKey == null) {
            return super.getResolvedDefaultDataSource();
        }
        String dataSourceId = getDataSourceId(dataSourceKey.toString());
        DataSource dataSource = DataSourceManager.DATA_SOURCE_POOL_JDBC.get(dataSourceId);

        if (dataSource == null) {
            throw new CustomException(ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getCode(), ErrorStatusEnum.DATASOURCE_NOT_FOUNT.getMassage());
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
        Connection connection = this.determineTargetDataSource().getConnection();
        Object dataSourceKey = this.determineCurrentLookupKey();
        try {
            if (dataSourceKey != null && dataSourceKey.toString().contains(":")) {
                String[] dataSourceStr = dataSourceKey.toString().split(":");
                String schema = dataSourceStr[1];
                connection.setSchema(schema);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return connection;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceKey();
    }
}
