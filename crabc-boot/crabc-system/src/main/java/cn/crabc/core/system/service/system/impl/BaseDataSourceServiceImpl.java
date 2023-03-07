package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.bean.DataSource;
import cn.crabc.core.system.component.BaseCache;
import cn.crabc.core.system.entity.BaseDatasource;
import cn.crabc.core.system.mapper.BaseDataSourceMapper;
import cn.crabc.core.system.service.core.IBaseDataService;
import cn.crabc.core.system.service.system.IBaseDataSourceService;
import cn.crabc.core.system.util.PageInfo;
import cn.crabc.core.system.util.RSAUtils;
import cn.crabc.core.system.util.UserThreadLocal;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public List<DataSource> getList() {
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
        // 解密后的密码
        String pwd = this.decryptPwd(password);
        dataSource.setPassword(pwd);
        dataSource.setClassify("jdbc");
        dataSource.setCreateBy(UserThreadLocal.getUserId());
        dataSource.setCreateTime(new Date());
        // 加密
        this.encryptPwd(dataSource);
        dataSourceMapper.insertDataSource(dataSource);
        dataSource.setPassword(pwd);
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
     * @param dataSource
     */
    private void addCache(BaseDatasource dataSource){
        try {
            // 新增或更新数据源
            DataSource ds = new DataSource();
            BeanUtils.copyProperties(dataSource, ds);
            dataSourceManager.createDataSource(ds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密密码
     * @param dataSource
     */
    private void encryptPwd(BaseDatasource dataSource){
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
     * @param password
     * @return
     */
    private String decryptPwd(String password){
        try {
            String pwd = RSAUtils.decryptByPriKey(BaseCache.localCeche.get("priKey_"+UserThreadLocal.getUserId()).toString(), password);
            BaseCache.localCeche.remove("priKey_" + UserThreadLocal.getUserId());
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
    public Integer test(BaseDatasource dataSource) {
        this.parsePassword(dataSource);
        return iBaseDataService.testConnection(dataSource);
    }

    /**
     * 更新/测试时解析数据库密码
     * @param dataSource
     */
    private void parsePassword(BaseDatasource dataSource) {
        String password = dataSource.getPassword();
        String pwd = null;
        if (password != null && !password.trim().equals("")) {
            pwd = this.decryptPwd(password);
        }
        if (pwd == null) {
            BaseDatasource baseDatasource = dataSourceMapper.selectOne(Integer.parseInt(dataSource.getDatasourceId()));
            try {
                pwd = RSAUtils.decryptByPriKey(baseDatasource.getSecretKey(),baseDatasource.getPassword());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        dataSource.setPassword(pwd);
    }
}
