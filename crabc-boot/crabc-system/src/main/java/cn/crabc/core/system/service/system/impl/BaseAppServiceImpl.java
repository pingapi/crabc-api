package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseApp;
import cn.crabc.core.system.mapper.BaseAppMapper;
import cn.crabc.core.system.service.system.IBaseAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseAppServiceImpl implements IBaseAppService {

    @Autowired
    private BaseAppMapper baseAppMapper;

    @Override
    public List<BaseApp> getAppList(String appName) {
        return baseAppMapper.selectList();
    }
}
