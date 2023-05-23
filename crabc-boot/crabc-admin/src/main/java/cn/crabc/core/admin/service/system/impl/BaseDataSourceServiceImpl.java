package cn.crabc.core.admin.service.system.impl;

import cn.crabc.core.admin.entity.BaseDatasource;
import cn.crabc.core.admin.mapper.BaseDataSourceMapper;
import cn.crabc.core.admin.service.core.IBaseDataService;
import cn.crabc.core.admin.service.system.IBaseDataSourceService;
import cn.crabc.core.admin.util.PageInfo;
import cn.crabc.core.admin.util.RSAUtils;
import cn.crabc.core.admin.util.UserThreadLocal;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.bean.BaseDataSource;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
    @Autowired
    @Qualifier("dataCache")
    Cache<String, Object> caffeineCache;

    @Scheduled(cron = "*/40 * * * * ?")
    public void task(){
        init();
    }
    @Override
    public void init() {
        List<BaseDataSource> baseDataSources = this.getList();
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
        String password = dataSource.getPassword();
        dataSource.setClassify("jdbc");
        dataSource.setCreateBy(UserThreadLocal.getUserId());
        dataSource.setCreateTime(new Date());
        String pwd = null;
        if (password != null && !"".equals(password)) {
            // 解密后的密码
            pwd = this.decryptPwd(password);
            dataSource.setPassword(pwd);
            // 再次加密
            this.encryptPwd(dataSource);
        }
        dataSourceMapper.insertDataSource(dataSource);
        if (pwd != null) {
            dataSource.setPassword(pwd);
        }
        this.addCache(dataSource);
        return 1;
    }

    @Override
    public Integer updateDataSource(BaseDatasource dataSource) {
        this.parsePassword(dataSource);
        dataSource.setUpdateBy(UserThreadLocal.getUserId());
        dataSource.setUpdateTime(new Date());
        this.addCache(dataSource);
        // 加密
        this.encryptPwd(dataSource);
        return dataSourceMapper.updateDataSource(dataSource);
    }

    /**
     * 更新到缓存
     *
     * @param dataSource
     */
    private void addCache(BaseDatasource dataSource) {
        try {
            // 新增或更新数据源
            BaseDataSource ds = new BaseDataSource();
            BeanUtils.copyProperties(dataSource, ds);
            dataSourceManager.createDataSource(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密密码
     *
     * @param dataSource
     */
    private void encryptPwd(BaseDatasource dataSource) {
        try {
            // 加密数据库密码
            RSAUtils.RSAKeyPair rsaKeyPair = RSAUtils.getKey();
            String pubKey = rsaKeyPair.getPublicKey();
            String encryptPwd = RSAUtils.encryptByPubKey(pubKey, dataSource.getPassword());
            dataSource.setPassword(encryptPwd);
            dataSource.setSecretKey(rsaKeyPair.getPrivateKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析密码
     *
     * @param password
     * @return
     */
    private String decryptPwd(String password) {
        try {
            Object priKey = caffeineCache.getIfPresent("priKey_" + UserThreadLocal.getUserId());
            String pwd = RSAUtils.decryptByPriKey(priKey.toString(), password);
            return pwd;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
            pwd = this.decryptPwd(password);
        }
        if (pwd == null && dataSource.getDatasourceId() !=null && !"".equals(dataSource.getDatasourceId())) {
            BaseDatasource baseDatasource = dataSourceMapper.selectOne(Integer.parseInt(dataSource.getDatasourceId()));
            try {
                pwd = RSAUtils.decryptByPriKey(baseDatasource.getSecretKey(), baseDatasource.getPassword());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        dataSource.setPassword(pwd);
    }
}
