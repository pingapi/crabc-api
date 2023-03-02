package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.service.system.IBaseApiSqlService;
import cn.crabc.core.system.entity.BaseApiSql;
import cn.crabc.core.system.mapper.BaseApiSqlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * API SQL信息 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseApiSqlServiceImpl implements IBaseApiSqlService {

    @Autowired
    private BaseApiSqlMapper apiSqlMapper;

    @Override
    public BaseApiSql getApiSql(Long apiId) {
        return apiSqlMapper.selectApiSql(apiId);
    }

    @Override
    public Integer addApiSql(BaseApiSql baseApiSql) {
        return apiSqlMapper.insertApiSql(baseApiSql);
    }

    @Override
    public Integer updateApiSql(BaseApiSql baseApiSql) {
        return apiSqlMapper.updateApiSql(baseApiSql);
    }

    @Override
    public Integer deleteApiSql(Integer apiId) {
        return apiSqlMapper.deleteApiSql(apiId);
    }
}
