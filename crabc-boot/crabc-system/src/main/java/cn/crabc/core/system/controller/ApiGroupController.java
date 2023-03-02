package cn.crabc.core.system.controller;

import cn.crabc.core.system.entity.BaseGroup;
import cn.crabc.core.system.service.system.IBaseGroupService;
import cn.crabc.core.system.util.Result;
import cn.crabc.core.system.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * api分组
 */

@RestController
@RequestMapping("/api/box/sys/group")
public class ApiGroupController {

    @Autowired
    private IBaseGroupService iBaseGroupService;

    /**
     * 分组树
     * @return
     */
    @GetMapping("/tree")
    public Result groupTree(){
        List<BaseGroup> baseGroups = iBaseGroupService.groupTree(UserThreadLocal.getUserId());
        return Result.success(baseGroups);
    }

    /**
     * 新增分组
     * @param baseGroup
     * @return
     */
    @PostMapping
    public Result addGroup(@RequestBody BaseGroup baseGroup){
        baseGroup.setCreateBy(UserThreadLocal.getUserId());
        iBaseGroupService.addGroup(baseGroup);
        return Result.success();
    }

    @PutMapping
    public Result updateGroup(@RequestBody BaseGroup baseGroup){
        iBaseGroupService.updateGroup(baseGroup,UserThreadLocal.getUserId());
        return Result.success();
    }

    @DeleteMapping
    public Result deleteGroup(@PathVariable Integer groupId){
        iBaseGroupService.delteGroup(groupId,UserThreadLocal.getUserId());
        return Result.success();
    }
}
