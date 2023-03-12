package cn.crabc.core.admin.service.system.impl;

import cn.crabc.core.admin.entity.BaseApiSql;
import cn.crabc.core.admin.mapper.BaseApiSqlMapper;
import cn.crabc.core.admin.service.system.IBaseApiSqlService;
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
