package cn.crabc.core.app.service.system;

import cn.crabc.core.app.entity.BaseUser;
import cn.crabc.core.datasource.util.PageInfo;

/**
 * 用户信息 接口
 *
 * @author yuqf
 */
public interface IBaseUserService {

    /**
     * 用户分页列表
     * @return
     */
    PageInfo page(String userName, Integer pageNum, Integer pageSize);

    /**
     * 查询用户信息
     * @param userName
     * @return
     */
    BaseUser getUserByName(String userName);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    BaseUser getUserById(Long userId);

    /**
     * 编辑用户
     * @param baseUser
     * @return
     */
    Integer updateUser(BaseUser baseUser);

    /**
     * 新增用户
     * @param baseUser
     * @return
     */
    Integer addUser(BaseUser baseUser);
}
