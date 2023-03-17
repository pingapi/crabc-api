package cn.crabc.core.app.mapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

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
     * 新增类SQL
     * @param params
     * @return
     */
    @InsertProvider(type = BaseSelectProvider.class, method = "executeInsert")
    Integer executeInsert(Map<String, Object> params);

    /**
     * 修改类SQL
     * @param params
     * @return
     */
    @UpdateProvider(type = BaseSelectProvider.class, method = "executeUpdate")
    Integer executeUpdate(Map<String, Object> params);

    /**
     * 删除类SQL
     * @param params
     * @return
     */
    @UpdateProvider(type = BaseSelectProvider.class, method = "executeDelete")
    Integer executeDelete(Map<String, Object> params);
}
