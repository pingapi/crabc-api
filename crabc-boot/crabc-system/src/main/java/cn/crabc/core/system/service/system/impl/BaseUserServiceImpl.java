package cn.crabc.core.system.service.system.impl;

import cn.crabc.core.system.entity.BaseUser;
import cn.crabc.core.system.mapper.BaseUserMapper;
import cn.crabc.core.system.service.system.IBaseUserService;
import cn.crabc.core.system.util.PageInfo;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户信息 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseUserServiceImpl implements IBaseUserService {

    @Autowired
    private BaseUserMapper baseUserMapper;
    
    @Override
    public PageInfo page(String userName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseUser> list = baseUserMapper.selectList(userName);
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public BaseUser getUserByName(String userName) {
        return baseUserMapper.selectOne(null,userName);
    }

    @Override
    public BaseUser getUserById(Long userId) {
        return baseUserMapper.selectOne(userId,null);
    }

    @Override
    public Integer updateUser(BaseUser baseUser) {
        BaseUser user = baseUserMapper.selectOne(baseUser.getUserId(), null);
        if (user == null) {
            return 0;
        }
        baseUser.setUpdateBy("1");
        baseUser.setStatus("0");
        baseUser.setUpdateTime(new Date());
        return baseUserMapper.updateUser(baseUser);
    }

    @Override
    public Integer addUser(BaseUser baseUser) {
        BaseUser user = baseUserMapper.selectOne(null, baseUser.getUserName());
        if (user == null) {
            return 0;
        }
        baseUser.setCreateBy("1");
        baseUser.setCreateTime(new Date());
        return baseUserMapper.insertUser(baseUser);
    }
}
