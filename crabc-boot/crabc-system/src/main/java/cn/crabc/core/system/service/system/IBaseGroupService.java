package cn.crabc.core.system.service.system;

import cn.crabc.core.system.entity.BaseGroup;
import cn.crabc.core.system.entity.vo.BaseGroupVO;

import java.util.List;

/**
 * api分组 服务接口
 *
 * @author yuqf
 */
public interface IBaseGroupService {

    /**
     * 新增分组
     * @param group
     * @return
     */
    Integer addGroup(BaseGroup group);

    /**
     * 编辑分组
     * @param group
     * @param userId
     * @return
     */
    Integer updateGroup(BaseGroup group, String userId);

    /**
     * 删除分组
     * @param groupId
     * @param userId
     * @return
     */
    Integer delteGroup(Integer groupId, String userId);

    /**
     * 分组树
     * @param userId
     * @param apiId
     * @return
     */
    List<BaseGroupVO> groupTree(String userId, Long apiId);
}
