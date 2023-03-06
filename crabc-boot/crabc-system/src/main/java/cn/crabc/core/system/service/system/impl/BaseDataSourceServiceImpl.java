package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.bean.DataSource;
import cn.crabc.core.system.component.BaseCache;
import cn.crabc.core.system.entity.BaseDatasource;
import cn.crabc.core.system.mapper.BaseDataSourceMapper;
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
import java.util.UUID;

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
        Integer result = 0;
        String password = dataSource.getPassword();
        dataSource.setPassword(this.parsePwd(password));
        dataSource.setClassify("jdbc");
        dataSource.setCreateBy(UserThreadLocal.getUserId());
        dataSource.setUpdateTime(new Date());
        result = dataSourceMapper.insertDataSource(dataSource);

        this.addCache(dataSource);
        return result;
    }

    @Override
    public Integer updateDataSource(BaseDatasource dataSource) {
        String password = dataSource.getPassword();
        String pwd = null;
        if (password != null && !password.trim().equals("")) {
            pwd = this.parsePwd(password);
        }
        if (pwd == null) {
            BaseDatasource baseDatasource = dataSourceMapper.selectOne(Integer.parseInt(dataSource.getDatasourceId()));
            pwd = baseDatasource.getPassword();
        }
        dataSource.setPassword(pwd);
        dataSource.setUpdateTime(new Date());
        dataSource.setUpdateTime(new Date());
        this.addCache(dataSource);

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
     * 解析密码
     * @param password
     * @return
     */
    private String parsePwd(String password){
        try {
            String pwd = RSAUtils.decryptByPriKey(BaseCache.localCeche.get("priKey").toString(), password);
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
}
