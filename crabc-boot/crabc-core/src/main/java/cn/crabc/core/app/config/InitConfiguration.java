package cn.crabc.core.app.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 配置加载
 */
@EnableCaching
@EnableScheduling
@ComponentScan("cn.crabc.core")
@Configuration
public class InitConfiguration {
}
