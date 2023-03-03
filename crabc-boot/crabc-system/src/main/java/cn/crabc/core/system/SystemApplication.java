package cn.crabc.core.system;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 基础通用开发平台-系统模块
 *
 * @author yuqf
 */
@MapperScan("cn.crabc.core.*.mapper")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("crabc-system 启动成功!!!");
    }
}
