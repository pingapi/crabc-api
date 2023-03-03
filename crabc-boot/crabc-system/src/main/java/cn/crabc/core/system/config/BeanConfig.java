package cn.crabc.core.system.config;

import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.app.driver.jdbc.JdbcDataSourceDriver;
import cn.crabc.core.app.mapper.BaseDataHandleMapper;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.system.component.BaseCache;
import cn.crabc.core.system.component.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean 加载配置
 *
 * @author yuqf
 */
@Configuration
@EnableCaching
public class BeanConfig {

    @Value("${crabc.cache.type:redis}")
    private String cacheType;

    /**
     * JDBC 数据源驱动
     *
     * @param baseDataHandleMapper
     * @return
     */
    @Bean
    public DataSourceDriver dataSourceDriver(BaseDataHandleMapper baseDataHandleMapper) {
        return new JdbcDataSourceDriver(baseDataHandleMapper);
    }

    /**
     * 数据源插件驱动
     *
     * @param jdbcDataSourceDriver
     * @return
     */
    @Bean
    public DataSourceManager dataSourceDriverManager(DataSourceDriver jdbcDataSourceDriver) {
        DataSourceManager driverManager = new DataSourceManager(jdbcDataSourceDriver);
        return driverManager;
    }

    /**
     * 缓存管理类
     * @param redisClient
     * @return
     */
    @Bean
    public BaseCache baseCache(@Autowired(required = false) RedisClient redisClient){
        if (redisClient != null && "redis".equalsIgnoreCase(cacheType)){
            return new BaseCache(redisClient);
        }else{
            return new BaseCache();
        }
    }
}
