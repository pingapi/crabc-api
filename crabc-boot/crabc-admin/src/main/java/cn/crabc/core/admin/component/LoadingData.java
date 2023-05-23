package cn.crabc.core.admin.component;

import cn.crabc.core.admin.service.system.IBaseApiInfoService;
import cn.crabc.core.admin.service.system.IBaseDataSourceService;
import cn.crabc.core.admin.util.RSAUtils;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.bean.BaseDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动初始化数据
 *
 * @author yuqf
 */
//@Component
public class LoadingData implements InitializingBean {

    @Autowired
    private IBaseApiInfoService iBaseApiInfoService;
    @Autowired
    private IBaseDataSourceService iBaseDataSourceService;
    @Autowired
    private DataSourceManager dataSourceManager;

    @Override
    public void afterPropertiesSet() {
        // 加载数据源
        List<BaseDataSource> baseDataSources = iBaseDataSourceService.getList();
        for (BaseDataSource dataSource : baseDataSources) {
            String priKey = dataSource.getSecretKey();
            try {
                if (dataSource.getPassword() != null) {
                    String pwd = RSAUtils.decryptByPriKey(priKey, dataSource.getPassword());
                    dataSource.setPassword(pwd);
                }
                dataSourceManager.createDataSource(dataSource);
            } catch (Exception e) {

            }
        }
        // 加载发布的API
        iBaseApiInfoService.initApi();
    }
}
