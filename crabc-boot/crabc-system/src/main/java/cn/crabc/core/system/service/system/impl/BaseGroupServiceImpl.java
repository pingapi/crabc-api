package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseGroup;
import cn.crabc.core.system.mapper.BaseGroupMapper;
import cn.crabc.core.system.service.system.IBaseGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * api分组 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseGroupServiceImpl implements IBaseGroupService {

    @Autowired
    private BaseGroupMapper baseGroupMapper;

    @Override
    public Integer addGroup(BaseGroup group) {
        group.setCreateTime(new Date());
        return baseGroupMapper.insert(group);
    }

    @Override
    public Integer updateGroup(BaseGroup group, String userId) {
        BaseGroup baseGroup = baseGroupMapper.selectOne(group.getGroupId());
        if (baseGroup == null){
            new RuntimeException("分组不存在");
        }
        if (!userId.equals(baseGroup.getCreateBy())){
            new RuntimeException("非法操作");
        }
        group.setCreateBy(userId);
        group.setUpdateTime(new Date());
        return baseGroupMapper.update(group);
    }

    @Override
    public Integer delteGroup(Integer groupId, String userId) {
        BaseGroup baseGroup = baseGroupMapper.selectOne(groupId);
        if (baseGroup == null){
            new RuntimeException("分组不存在");
        }
        if (!userId.equals(baseGroup.getCreateBy())){
            new RuntimeException("非法操作");
        }
        return baseGroupMapper.delete(groupId);
    }

    @Override
    public List<BaseGroup> groupTree(String userId) {
        List<BaseGroup> treeList = new ArrayList<>();
        List<BaseGroup> list = baseGroupMapper.selectList(userId);
        List<BaseGroup> rootList = list.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
        //寻找子节点
        rootList.forEach(tree -> treeList.add(findChildren(tree, list)));
        return treeList;
    }

    /**
     * 查找子级
     * @param tree
     * @param list
     * @return
     */
    private BaseGroup findChildren(BaseGroup tree, List<BaseGroup> list) {
        list.stream().filter(node -> tree.getGroupId().equals(node.getParentId())).collect(Collectors.toList()).forEach(node -> {
            if (tree.getChildren() == null) {
                tree.setChildren(new ArrayList<>());
            }
            tree.getChildren().add(findChildren(node, list));
        });
        return tree;
    }
}
