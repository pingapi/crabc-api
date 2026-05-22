package cn.crabc.core.datasource.driver;

import cn.crabc.core.datasource.exception.CustomException;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
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
        return defaultDriver.test(dataSource);
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
        // 只有连接池数据源需要显式关闭，DuckDB直连数据源只需从缓存移除。
        if (dataSource instanceof DruidDataSource druidDataSource) {
            druidDataSource.close();
        } else if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
        DATA_SOURCE_POOL_JDBC.remove(datasourceId);
    }
}
