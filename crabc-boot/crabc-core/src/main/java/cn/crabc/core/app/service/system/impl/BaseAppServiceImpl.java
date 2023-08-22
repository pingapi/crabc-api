package cn.crabc.core.app.service.system.impl;

import cn.crabc.core.app.entity.BaseApp;
import cn.crabc.core.app.entity.vo.BaseAppExcelVO;
import cn.crabc.core.app.mapper.BaseAppMapper;
import cn.crabc.core.app.service.system.IBaseAppService;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.app.util.UserThreadLocal;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
            throw new CustomException(ErrorStatusEnum.APP_NOT_FOUNT.getCode(), ErrorStatusEnum.APP_NOT_FOUNT.getMassage());
        }
        app.setUpdateTime(new Date());
        app.setUpdateBy(UserThreadLocal.getUserId());
        return baseAppMapper.update(app);
    }

    @Override
    public Integer deleteApp(Long appId) {
        return baseAppMapper.delete(appId);
    }

    @Override
    public List<BaseAppExcelVO> getAppExcelList(String appName) {
        List<BaseApp> baseApps = baseAppMapper.selectList(appName);
        List<BaseAppExcelVO> list = new ArrayList<>();
        for (BaseApp app : baseApps) {
            BaseAppExcelVO excelVO = new BaseAppExcelVO();
            BeanUtils.copyProperties(app, excelVO);
            list.add(excelVO);
        }
        return list;
    }

    @Override
    public Integer addAppList(List<BaseAppExcelVO> data) {
        for (BaseAppExcelVO excelVO : data) {
            BaseApp app = new BaseApp();
            BeanUtils.copyProperties(excelVO, app);
            app.setEnabled(1);
            app.setCreateBy(UserThreadLocal.getUserId());
            app.setCreateTime(new Date());
            app.setUpdateTime(new Date());
            baseAppMapper.insert(app);
        }
        return 1;
    }
}
