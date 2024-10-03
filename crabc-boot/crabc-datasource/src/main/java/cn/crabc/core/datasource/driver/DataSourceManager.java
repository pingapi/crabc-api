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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源驱动管理
 *
 * @author yuqf
 */
public class DataSourceManager {

    /**
     * JDBC数据源连接池
     */
    public static final Map<String, DataSource> DATA_SOURCE_POOL_JDBC = new ConcurrentHashMap<>();

    /**
     * 支持的关系型数据源类型
     */
    private static final List<String> JDBC_DATA_SOURCE_TYPE = Arrays.asList("mysql", "mariadb", "oracle", "sqlserver", "postgresql","sybase", "db2","doris", "sqlite", "tidb","starrocks","clickhouse", "opengauss","gaussdb", "oceanbase", "polardb", "tdsql", "dm", "gbase");
    /**
     * 默认数据源驱动实现
     */
    private DataSourceDriver defaultDriver;

    public DataSourceManager(DataSourceDriver dataSourceDriver) {
        // 默认JDBC驱动
        this.defaultDriver = dataSourceDriver;
    }

    /**
     * 创建数据源
     *
     * @param dataSource
     */
    public void createDataSource(BaseDataSource dataSource) {
        this.defaultDriver.init(dataSource);
    }

    /**
     * 测试数据源
     *
     * @param dataSource
     * @return
     */
    public String test(BaseDataSource dataSource) {
        String datasourceType = dataSource.getDatasourceType();
        if (JDBC_DATA_SOURCE_TYPE.contains(datasourceType)) {
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
        DataSource dataSource = DATA_SOURCE_POOL_JDBC.get(datasourceId);
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
            hikariDataSource.close();
        }
        DATA_SOURCE_POOL_JDBC.remove(datasourceId);
    }
}
