package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.BaseApiLog;
import cn.crabc.core.app.entity.param.ApiLogParam;
import cn.crabc.core.app.mapper.BaseApiLogMapper;
import cn.crabc.core.app.service.system.IBaseApiLogService;
import cn.crabc.core.datasource.util.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * api日志 服务实现
 *
 * @author yuqf
 */
@Service
public class IBaseApiLogServiceImpl implements IBaseApiLogService {

    @Autowired
    private BaseApiLogMapper baseApiLogMapper;

    @Override
    public Integer addLog(BaseApiLog log) {
        return baseApiLogMapper.insert(log);
    }

    @Override
    public PageInfo page(ApiLogParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<BaseApiLog> list = baseApiLogMapper.selectList(param);
        return new PageInfo<>(list, param.getPageNum(), param.getPageSize());
    }

    @Override
    public BaseApiLog logDetail(Long logId) {
        return baseApiLogMapper.selectOne(logId);
    }
}
