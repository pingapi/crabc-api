package cn.crabc.core.system.component;

import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.bean.DataSource;
import cn.crabc.core.system.entity.dto.ApiInfoDTO;
import cn.crabc.core.system.service.system.IBaseApiInfoService;
import cn.crabc.core.system.service.system.IBaseDataSourceService;
import cn.crabc.core.system.util.RSAUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 启动初始化数据
 *
 * @author yuqf
 */
@Component
public class LoadingData implements InitializingBean {

    @Autowired
    private IBaseApiInfoService iBaseApiInfoService;
    @Autowired
    private IBaseDataSourceService iBaseDataSourceService;
    @Autowired
    private DataSourceManager dataSourceManager;

    @Autowired
    private BaseCache baseCache;

    @Override
    public void afterPropertiesSet() {
        // 加载数据源
        List<DataSource> baseDataSources = iBaseDataSourceService.getList();
        for (DataSource dataSource : baseDataSources) {
            String priKey = dataSource.getSecretKey();
            try {
                String pwd = RSAUtils.decryptByPriKey(priKey, dataSource.getPassword());
                dataSource.setPassword(pwd);
                dataSourceManager.createDataSource(dataSource);
            } catch (Exception e) {

            }
        }

        List<ApiInfoDTO> apis = iBaseApiInfoService.getApiDetail();
        baseCache.setApiCache(apis);
    }
}
