package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.StatementMapper;

import java.util.List;

public class ElasticsearchStatement implements StatementMapper {
    @Override
    public Object selectOne(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectList(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public Object selectPage(String dataSourceId, String schema, String sql, Object params, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public int insert(String dataSourceId, String schema, String sql, Object params) {
        return 0;
    }

    @Override
    public int delete(String dataSourceId, String schema, String sql, Object params) {
        return 0;
    }

    @Override
    public int update(String dataSourceId, String schema, String sql, Object params) {
        return 0;
    }
}
