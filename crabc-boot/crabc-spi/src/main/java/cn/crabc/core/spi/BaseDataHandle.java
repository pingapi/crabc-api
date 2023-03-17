package cn.crabc.core.spi;

import java.util.List;

/**
 * 数据操作
 *
 * @author yuqf
 */
public interface BaseDataHandle<T> {

    T selectOne(String dataSourceId, String schema, String sql, Object params);

    List<T> selectList(String dataSourceId, String schema, String sql, Object params);

    Object selectPage(String dataSourceId, String schema, String sql, Object params, int pageNum, int pageSize);

    int insert(String dataSourceId, String schema, String sql, Object params);

    int delete(String dataSourceId, String schema, String sql, Object params);

    int update(String dataSourceId, String schema, String sql, Object params);
}
