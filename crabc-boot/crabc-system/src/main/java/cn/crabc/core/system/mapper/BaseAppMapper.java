package cn.crabc.core.system.mapper;

import cn.crabc.core.system.entity.BaseApp;

import java.util.List;

/**
 * 应用配置 Mapper接口
 *
 * @author yuqf
 */
public interface BaseAppMapper {

    /**
     * 应用列表
     * @return
     */
    List<BaseApp> selectList();

    /**
     * 应用和API的关系
     * @return
     */
    List<BaseApp> selectAppApi();
}
