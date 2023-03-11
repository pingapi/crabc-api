package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseApiLog;
import cn.crabc.core.system.entity.param.ApiLogParam;
import cn.crabc.core.system.mapper.BaseApiLogMapper;
import cn.crabc.core.system.service.system.IBaseApiLogService;
import cn.crabc.core.system.util.PageInfo;
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
