package cn.crabc.core.system.service.system;

import cn.crabc.core.system.entity.BaseApp;

import java.util.List;

/**
 * 应用 服务接口
 *
 * @author yuqf
 */
public interface IBaseAppService {

    List<BaseApp> getAppList(String appName);
}
