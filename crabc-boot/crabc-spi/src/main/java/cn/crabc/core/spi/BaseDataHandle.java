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

    Object execute(String dataSourceId, String schema, String sql, Object params);

    int insert(String sql, T entity);

    int delete(String sql, T entity);

    int update(String sql, T entity);
}
