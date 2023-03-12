package cn.crabc.core.admin.controller;

import cn.crabc.core.admin.entity.BaseGroup;
import cn.crabc.core.admin.entity.vo.BaseGroupVO;
import cn.crabc.core.admin.service.system.IBaseGroupService;
import cn.crabc.core.admin.util.Result;
import cn.crabc.core.admin.util.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * api分组
 */

@RestController
@RequestMapping("/api/box/sys/group")
public class BaseGroupController {

    @Autowired
    private IBaseGroupService iBaseGroupService;

    /**
     * 分组树
     * @return
     */
    @GetMapping("/tree")
    public Result groupTree(Long apiId){
        List<BaseGroupVO> baseGroups = iBaseGroupService.groupTree(UserThreadLocal.getUserId(), apiId);
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
