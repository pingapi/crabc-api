package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseApp;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 应用配置 Mapper接口
 *
 * @author yuqf
 */
@Mapper
public interface BaseAppMapper {

    /**
     * 应用列表
     * @return
     */
    List<BaseApp> selectList(String appName);

    /**
     * 应用和API的关系
     * @return
     */
    List<BaseApp> selectApiApp(Long apiId);

    /**
     * 应用对象
     * @param appId
     * @return
     */
    BaseApp selectOne(Long appId);

    /**
     * 新增应用
     * @param app
     * @return
     */
    Integer insert(BaseApp app);

    /**
     * 编辑应用
     * @param app
     * @return
     */
    Integer update(BaseApp app);

    /**
     * 删除应用
     * @param appId
     * @return
     */
    Integer delete(Long appId);
}
