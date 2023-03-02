package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseDatasource;
import cn.crabc.core.system.util.PageInfo;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.system.mapper.BaseDataSourceMapper;
import cn.crabc.core.system.service.system.IBaseDataSourceService;
import com.github.pagehelper.PageHelper;
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

    @Override
    public PageInfo<BaseDataSource> getDataSourcePage(String dataSourceName, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseDataSource> list = dataSourceMapper.selectDataSourceList(dataSourceName);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public List<BaseDataSource> getDataSourceList(String dataSourceName) {
        return dataSourceMapper.selectDataSourceList(dataSourceName);
    }

    @Override
    public BaseDataSource getDataSource(Integer dataSourceId) {
        return dataSourceMapper.selectDataSource(dataSourceId);
    }

    @Override
    public Integer addDataSource(BaseDatasource dataSource) {
        if (dataSource.getDatasourceId() != null) {
            dataSource.setDatasourceId(UUID.randomUUID().toString());
            dataSource.setUpdateTime(new Date());
            return this.updateDataSource(dataSource);
        }
        return dataSourceMapper.insertDataSource(dataSource);
    }

    @Override
    public Integer updateDataSource(BaseDatasource dataSource) {
        return dataSourceMapper.updateDataSource(dataSource);
    }

    @Override
    public Integer deleteDataSource(Integer dataSourceId) {
        return dataSourceMapper.deleteDataSource(dataSourceId);
    }
}
