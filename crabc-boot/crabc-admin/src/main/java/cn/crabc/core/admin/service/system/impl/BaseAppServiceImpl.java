package cn.crabc.core.admin.service.system.impl;

import cn.crabc.core.admin.entity.BaseApp;
import cn.crabc.core.admin.mapper.BaseAppMapper;
import cn.crabc.core.admin.service.system.IBaseAppService;
import cn.crabc.core.admin.util.PageInfo;
import cn.crabc.core.admin.util.UserThreadLocal;
import cn.crabc.core.app.exception.CustomException;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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
    public PageInfo<BaseApp> appPage(String appName, String appCode, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseApp> list = baseAppMapper.selectList(appName);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public List<BaseApp> appList(String appName) {
        return baseAppMapper.selectList(appName);
    }

    @Override
    public Integer addApp(BaseApp app) {
        app.setStrategyType("white");
        app.setEnabled(1);
        app.setCreateBy(UserThreadLocal.getUserId());
        app.setCreateTime(new Date());
        return baseAppMapper.insert(app);
    }

    @Override
    public Integer updateApp(BaseApp app) {
        BaseApp baseApp = baseAppMapper.selectOne(app.getAppId());
        if (baseApp == null) {
            throw new CustomException(52012, "应用不存在");
        }
        app.setUpdateTime(new Date());
        app.setUpdateBy(UserThreadLocal.getUserId());
        return baseAppMapper.update(app);
    }

    @Override
    public Integer deleteApp(Long appId) {
        return baseAppMapper.delete(appId);
    }
}
