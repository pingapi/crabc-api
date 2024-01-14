package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.BaseDatasource;
import cn.crabc.core.app.mapper.BaseDataSourceMapper;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.service.system.IBaseDataSourceService;
import cn.crabc.core.app.util.SQLUtil;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.UserThreadLocal;
import cn.crabc.core.datasource.driver.DataSourceManager;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * 数据源 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseDataSourceServiceImpl implements IBaseDataSourceService {

    @Autowired
    private BaseDataSourceMapper dataSourceMapper;
    @Autowired
    private DataSourceManager dataSourceManager;
    @Autowired
    private IBaseDataService iBaseDataService;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 启动加载
     */
    @PostConstruct
    public void load() {
        initLocal();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
        t.start();
    }

    /**
     * 把本机的数据库加载到缓存
     */
    private void initLocal() {
        BaseDatasource baseDatasource = dataSourceMapper.selectOne(1);
        if (baseDatasource != null) {
            return;
        }
        BaseDatasource local = new BaseDatasource();
        local.setDatasourceId("1");
        local.setEnabled(1);
        local.setDatasourceType("mysql");
        local.setDatasourceName("local");
        local.setJdbcUrl(jdbcUrl);
        local.setPassword(Base64.getEncoder().encodeToString(password.getBytes()));
        local.setUsername(username);
        local.setRemarks("本机数据库");
        this.addDataSource(local);
    }

    @Override
    @Scheduled(cron = "0 0/5 * * * ?")
    public void init() {
        List<BaseDataSource> baseDataSources = this.getList();
        for (BaseDataSource dataSource : baseDataSources) {
            byte[] decode = Base64.getDecoder().decode(dataSource.getPassword());
            dataSource.setPassword(new String(decode));
            dataSourceManager.createDataSource(dataSource);
        }
    }

    @Override
    public List<BaseDataSource> getList() {
        return dataSourceMapper.list();
    }

    @Override
    public PageInfo<BaseDatasource> getDataSourcePage(String dataSourceName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseDatasource> list = dataSourceMapper.selectList(dataSourceName);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public List<BaseDatasource> getDataSourceList(String dataSourceName) {
        return dataSourceMapper.selectList(dataSourceName);
    }

    @Override
    public BaseDatasource getDataSource(Integer dataSourceId) {
        return dataSourceMapper.selectOne(dataSourceId);
    }

    @Override
    public Integer addDataSource(BaseDatasource dataSource) {
        dataSource.setClassify("jdbc");
        dataSource.setCreateBy(UserThreadLocal.getUserId());
        dataSource.setCreateTime(new Date());
        // 判断是否是自定义
        if ("custom".equals(dataSource.getDatasourceType())){
            String dataSourceType = SQLUtil.getDataSourceType(dataSource.getJdbcUrl());
            dataSource.setDatasourceType(dataSourceType);
        }
        Integer result = dataSourceMapper.insertDataSource(dataSource);
        this.addCache(dataSource);
        return result;
    }

    @Override
    public Integer updateDataSource(BaseDatasource dataSource) {
        this.parsePassword(dataSource);
        dataSource.setUpdateBy(UserThreadLocal.getUserId());
        dataSource.setUpdateTime(new Date());
        dataSource.setPassword(Base64.getEncoder().encodeToString(dataSource.getPassword().getBytes()));
        this.addCache(dataSource);
        return dataSourceMapper.updateDataSource(dataSource);
    }

    /**
     * 更新到缓存
     *
     * @param dataSource
     */
    private void addCache(BaseDatasource dataSource) {
        // 新增或更新数据源
        BaseDataSource ds = new BaseDataSource();
        BeanUtils.copyProperties(dataSource, ds);
        byte[] decode = Base64.getDecoder().decode(dataSource.getPassword());
        ds.setPassword(new String(decode));
        dataSourceManager.createDataSource(ds);
    }

    @Override
    public Integer deleteDataSource(Integer dataSourceId) {
        // 删除缓存中的数据源
        dataSourceManager.remove(dataSourceId.toString());
        return dataSourceMapper.deleteDataSource(dataSourceId);
    }

    @Override
    public String test(BaseDatasource dataSource) {
        this.parsePassword(dataSource);
        if ("custom".equals(dataSource.getDatasourceType())){
            String dataSourceType = SQLUtil.getDataSourceType(dataSource.getJdbcUrl());
            dataSource.setDatasourceType(dataSourceType);
        }
        return iBaseDataService.testConnection(dataSource);
    }

    /**
     * 更新/测试时解析数据库密码
     *
     * @param dataSource
     */
    private void parsePassword(BaseDatasource dataSource) {
        String password = dataSource.getPassword();
        String pwd = null;
        if (password != null && !"".equals(password.trim())) {
            byte[] decode = Base64.getDecoder().decode(password);
            pwd = new String(decode);
        }else if (dataSource.getDatasourceId() != null){
            BaseDatasource baseDatasource = dataSourceMapper.selectOne(Integer.parseInt(dataSource.getDatasourceId()));
            byte[] decode = Base64.getDecoder().decode(baseDatasource.getPassword());
            pwd = new String(decode);
        }
        dataSource.setPassword(pwd);
    }
}
