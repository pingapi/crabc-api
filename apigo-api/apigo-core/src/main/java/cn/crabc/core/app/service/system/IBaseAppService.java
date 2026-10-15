package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.BaseApp;
import cn.crabc.core.datasource.util.PageInfo;

import java.util.List;

/**
 * 应用 服务接口
 *
 * @author yuqf
 */
public interface IBaseAppService {

    /**
     * 应用分页列表
     * @param appName
     * @param appCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<BaseApp> appPage(String appName, String appCode, Integer pageNum, Integer pageSize);

    /**
     * 列表
     * @param appName
     * @return
     */
    List<BaseApp> appList(String appName);

    /**
     * 新增应用
     * @param app
     * @return
     */
    Integer addApp(BaseApp app);

    /**
     * 编辑应用
     * @param app
     * @return
     */
    Integer updateApp(BaseApp app);

    /**
     * 删除应用
     * @param appId
     * @return
     */
    Integer deleteApp(Long appId);

}
