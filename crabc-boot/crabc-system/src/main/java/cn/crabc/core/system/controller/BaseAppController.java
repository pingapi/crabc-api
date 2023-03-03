package cn.crabc.core.system.controller;

import cn.crabc.core.system.entity.BaseApp;
import cn.crabc.core.system.service.system.IBaseAppService;
import cn.crabc.core.system.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  应用管理
 *
 */
@RestController
@RequestMapping("/api/box/sys/app")
public class BaseAppController {

    @Autowired
    private IBaseAppService iBaseAppService;

    /**
     * 应用列表
     * @param appName
     * @return
     */
    @GetMapping("/list")
    public Result list(String appName){
        List<BaseApp> appList = iBaseAppService.getAppList(appName);
        return Result.success(appList);
    }
}
