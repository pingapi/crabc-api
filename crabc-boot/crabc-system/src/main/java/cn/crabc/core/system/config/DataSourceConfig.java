package cn.crabc.core.system.config;

import cn.crabc.core.app.config.JdbcDataSourceRouter;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.app.driver.jdbc.JdbcDataSourceDriver;
import cn.crabc.core.app.mapper.BaseDataHandleMapper;
import cn.crabc.core.spi.DataSourceDriver;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * 数据源路由配置
 *
 * @author yuqf
 */
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 默认系统数据源
     *
     * @return
     */
    @Bean
    public JdbcDataSourceRouter defaultDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(2);
        dataSource.setMinIdle(2);
        dataSource.setMaxActive(10);
        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(6000);
        dataSource.setKeepAlive(true);
        //  配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(600000);
        dataSource.setMaxEvictableIdleTimeMillis(900000);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(10000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        JdbcDataSourceRouter dynamic = new JdbcDataSourceRouter();
        dynamic.setTargetDataSources(new HashMap<>());
        // 设置默认数据源
        dynamic.setDefaultTargetDataSource(dataSource);
        return dynamic;
    }

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
}
