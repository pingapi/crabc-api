package cn.crabc.core.system.mapper;

import cn.crabc.core.system.entity.BaseGroup;

import java.util.List;

/**
 * api分组 接口
 *
 * @author yuqf
 */
public interface BaseGroupMapper {

    /**
     * 新增分组
     */
    Integer insert(BaseGroup group);

    /**
     * 编辑分组
     */
    Integer update(BaseGroup group);

    /**
     * 删除分组
     * @param groupId
     */
    Integer delete(Integer groupId);

    /**
     * 删除用户分组
     * @param userId
     */
    Integer deleteAll(String userId);

    /**
     * 查询分组列表
     * @param userId
     */
    List<BaseGroup> selectList(String userId);

    /**
     * 查询详情
     * @param groupId
     */

    BaseGroup selectOne(Integer groupId);
}
