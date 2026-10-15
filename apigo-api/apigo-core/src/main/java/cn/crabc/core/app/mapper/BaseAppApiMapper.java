package cn.crabc.core.app.mapper;

import cn.crabc.core.app.entity.BaseAppApi;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseAppApiMapper {

    /**
     * 新增关系
     *
     * @param list
     * @return
     */
    Integer insert(List<BaseAppApi> list);

    /**
     * 删除关系
     *
     * @param appId
     * @param userId
     * @return
     */
    Integer delete(@Param("appId") Long appId, @Param("userId") String userId);
}
