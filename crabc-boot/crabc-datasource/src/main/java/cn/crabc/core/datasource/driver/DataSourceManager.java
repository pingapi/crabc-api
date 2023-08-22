package cn.crabc.core.datasource.driver;

import cn.crabc.core.datasource.exception.CustomException;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源驱动管理
 *
 * @author yuqf
 */
public class DataSourceManager {

    /**
     * 数据源插件类型
     */
    private Map<String, DataSourceDriver> PLUGIN_TYPE = new ConcurrentHashMap<>();

    /**
     * 数据源插驱动接池
     */
    private final Map<String, DataSourceDriver> DATA_SOURCE_POOL_PLUGIN = new ConcurrentHashMap<>();

    /**
     * JDBC数据源连接池
     */
    public static final Map<String, DataSource> DATA_SOURCE_POOL_JDBC = new ConcurrentHashMap<>();

    /**
     * 支持的关系型数据源类型
     */
    private static final List<String> JDBC_DATA_SOURCE_TYPE = Arrays.asList("mysql", "mariadb", "oracle", "sqlserver", "postgresql", "db2", "sqlite", "tidb", "opengauss", "oceanbase", "polardb", "tdsql", "dm", "gbase", "hive2");
    /**
     * 默认数据源驱动实现
     */
    private DataSourceDriver defaultDriver;

    public DataSourceManager(DataSourceDriver dataSourceDriver) {
        // 默认JDBC驱动
        this.defaultDriver = dataSourceDriver;
        // 初始化加载驱动插件
        init();
    }

    /**
     * 初始化加载插件驱动
     */
    public void init() {
        ServiceLoader<DataSourceDriver> loader = ServiceLoader.load(DataSourceDriver.class);
        for (DataSourceDriver driver : loader) {
            String driverName = driver.getName();
            if (driverName == null || "".equals(driverName)) {
                throw new CustomException(51003, "插件名称不能为空");
            }
            if (PLUGIN_TYPE.containsKey(driverName)) {
                throw new CustomException(51004, "该插件名称" + driverName + "已存在");
            }
            PLUGIN_TYPE.putIfAbsent(driverName.toLowerCase(), driver);
        }
    }

    /**
     * 插件创建数据源
     *
     * @param dataSource
     */
    public void createDataSource(BaseDataSource dataSource) {
        String datasourceType = dataSource.getDatasourceType();
        DataSourceDriver dataSourceDriver = PLUGIN_TYPE.get(datasourceType);
        if (dataSourceDriver != null) {
            // 初始化
            dataSourceDriver.init(dataSource);
            DATA_SOURCE_POOL_PLUGIN.putIfAbsent(dataSource.getDatasourceId(), dataSourceDriver);
        } else {
            dataSourceDriver = this.defaultDriver;
            dataSourceDriver.init(dataSource);
        }
    }

    /**
     * 测试数据源
     *
     * @param dataSource
     * @return
     */
    public String test(BaseDataSource dataSource) {
        String datasourceType = dataSource.getDatasourceType();
        DataSourceDriver dataSourceDriver = PLUGIN_TYPE.get(datasourceType);
        if (dataSourceDriver != null) {
            return dataSourceDriver.test(dataSource);
        } else if (JDBC_DATA_SOURCE_TYPE.contains(datasourceType)) {
            return defaultDriver.test(dataSource);
        } else {
            throw new CustomException(51001, "暂不支持" + datasourceType + "数据源类型！");
        }
    }


    /**
     * 获取数据源驱动
     *
     * @param datasourceId
     * @return
     */
    public DataSourceDriver getDataSource(String datasourceId) {
        DataSourceDriver dataSourceDriver = null;
        DataSource dataSource = DATA_SOURCE_POOL_JDBC.get(datasourceId);
        if (dataSource != null) {
            dataSourceDriver = this.defaultDriver;
        } else {
            dataSourceDriver = DATA_SOURCE_POOL_PLUGIN.get(datasourceId);
        }
        if (dataSourceDriver == null) {
            throw new CustomException(51001, "数据源不存在！");
        }
        return dataSourceDriver;
    }

    /**
     * 元数据对象
     * @param datasourceId
     * @return
     */
    public MetaDataMapper getMetaData(String datasourceId){
        DataSourceDriver dataSource = this.getDataSource(datasourceId);
        return dataSource.getMetaData();
    }

    /**
     * 数据处理对象
     * @param datasourceId
     * @return
     */
    public StatementMapper getStatementMapper(String datasourceId){
        DataSourceDriver dataSource = this.getDataSource(datasourceId);
        return dataSource.getStatement();
    }
    
    /**
     * 删除数据源驱动
     *
     * @param datasourceId
     */
    public void remove(String datasourceId) {
        DataSourceDriver dataSourceDriver = DATA_SOURCE_POOL_PLUGIN.get(datasourceId);
        if (dataSourceDriver != null) {
            dataSourceDriver.destroy(datasourceId);
            DATA_SOURCE_POOL_PLUGIN.remove(datasourceId);
        }
        DataSource dataSource = DATA_SOURCE_POOL_JDBC.get(datasourceId);
        if (dataSource != null && dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            hikariDataSource.close();
        }
        DATA_SOURCE_POOL_JDBC.remove(datasourceId);
    }
}
