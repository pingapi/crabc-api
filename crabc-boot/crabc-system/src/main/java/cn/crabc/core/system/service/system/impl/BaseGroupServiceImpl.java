package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseApiInfo;
import cn.crabc.core.system.entity.BaseGroup;
import cn.crabc.core.system.entity.vo.ApiComboBoxVO;
import cn.crabc.core.system.entity.vo.BaseGroupVO;
import cn.crabc.core.system.mapper.BaseApiInfoMapper;
import cn.crabc.core.system.mapper.BaseGroupMapper;
import cn.crabc.core.system.service.system.IBaseGroupService;
import org.springframework.beans.BeanUtils;
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
    @Autowired
    private BaseApiInfoMapper apiInfoMapper;

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
    public List<BaseGroupVO> groupTree(String userId, Long apiId) {
        List<BaseGroupVO> treeList = new ArrayList<>();
        List<BaseGroupVO> list = baseGroupMapper.selectList(userId);
        if (apiId != null) {
            this.getApiInfo(apiId, list);
        }
        List<BaseGroupVO> rootList = list.stream().filter(vo -> vo.getParentId() == 0).collect(Collectors.toList());
        //寻找子节点
        rootList.forEach(tree -> treeList.add(findChildren(tree, list)));
        return treeList;
    }

    /**
     * 加载分组下的api信息
     * @param apiId
     * @param list
     * @return
     */
    private List<BaseGroupVO> getApiInfo(Long apiId, List<BaseGroupVO> list){
        BaseApiInfo baseApiInfo = apiInfoMapper.selectApiById(apiId);
        if (baseApiInfo != null) {
            for(BaseGroupVO group : list) {
                if (group.getGroupId() == baseApiInfo.getGroupId()){
                    List<ApiComboBoxVO> apis = new ArrayList<>();
                    ApiComboBoxVO api = new ApiComboBoxVO();
                    BeanUtils.copyProperties(baseApiInfo, api);
                    apis.add(api);
                    group.setApis(apis);
                }
            }
        }
        return list;
    }

    /**
     * 查找子级
     * @param tree
     * @param list
     * @return
     */
    private BaseGroupVO findChildren(BaseGroupVO tree, List<BaseGroupVO> list) {
        list.stream().filter(node -> tree.getGroupId().equals(node.getParentId())).collect(Collectors.toList()).forEach(node -> {
            if (tree.getChildren() == null) {
                tree.setChildren(new ArrayList<>());
            }
            tree.getChildren().add(findChildren(node, list));
        });
        return tree;
    }
}
