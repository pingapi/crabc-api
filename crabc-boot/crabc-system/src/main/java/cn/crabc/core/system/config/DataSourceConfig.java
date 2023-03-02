package cn.crabc.core.system.config;

import cn.crabc.core.app.config.JdbcDataSourceRouter;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.app.driver.jdbc.JdbcDataSourceDriver;
import cn.crabc.core.app.mapper.BaseDataHandleMapper;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.system.component.BaseCache;
import cn.crabc.core.system.component.RedisClient;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
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
        JdbcDataSourceRouter dynamic = new JdbcDataSourceRouter();
        dynamic.setTargetDataSources(new HashMap<>());
        // 设置默认数据源
        dynamic.setDefaultTargetDataSource(dataSource);
        return dynamic;
    }
}
