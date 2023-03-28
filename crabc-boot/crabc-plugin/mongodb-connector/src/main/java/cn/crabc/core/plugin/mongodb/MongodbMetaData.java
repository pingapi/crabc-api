package cn.crabc.core.plugin.mongodb;

import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;

import java.util.List;

public class MongodbMetaData implements MetaDataMapper {
    @Override
    public List<String> getCatalogs(String dataSourceId) {
        return null;
    }

    @Override
    public List<Schema> getSchemas(String dataSourceId, String catalog) {
        return null;
    }

    @Override
    public List<Table> getTables(String dataSourceId, String catalog, String schema) {
        return null;
    }

    @Override
    public List<Column> getColumns(String dataSourceId, String catalog, String schema, String table) {
        return null;
    }
}
