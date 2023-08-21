package cn.crabc.core.admin.service.system.impl;

import cn.crabc.core.admin.entity.BaseUser;
import cn.crabc.core.admin.mapper.BaseUserMapper;
import cn.crabc.core.admin.service.system.IBaseUserService;
import cn.crabc.core.admin.util.PageInfo;
import cn.crabc.core.app.enums.ErrorStatusEnum;
import cn.crabc.core.app.exception.CustomException;
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
        try {
            return baseUserMapper.selectOne(null,userName);
        }catch (Exception e) {
            throw new CustomException(50010,"登录失败,请检查数据库链接是否正常！");
        }
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
        baseUser.setStatus("1");
        baseUser.setUpdateTime(new Date());
        return baseUserMapper.updateUser(baseUser);
    }

    @Override
    public Integer addUser(BaseUser baseUser) {
        BaseUser user = baseUserMapper.selectOne(null, baseUser.getUsername());
        if (user != null) {
            throw new CustomException(ErrorStatusEnum.USER_REPEAT.getCode(), ErrorStatusEnum.USER_REPEAT.getMassage());
        }
        baseUser.setCreateBy("1");
        baseUser.setCreateTime(new Date());
        return baseUserMapper.insertUser(baseUser);
    }
}
