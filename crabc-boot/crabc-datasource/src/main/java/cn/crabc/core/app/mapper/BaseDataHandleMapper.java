package cn.crabc.core.app.mapper;

import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;
import java.util.Map;

/**
 * 通用数据操作 Mapper
 *
 * @author yuqf
 */
public interface BaseDataHandleMapper {

    /**
     * 查询类SQL
     * @param params
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "executeQuery")
    List<Map<String, Object>> executeQuery(Map<String, Object> params);

    /**
     * 操作类SQL
     * @param params
     * @return
     */
    @SelectProvider(type = BaseSelectProvider.class, method = "executeUpdate")
    Object executeUpdate(Map<String, Object> params);

}
