package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.PluginException;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * es数据源 元数据获取
 *
 * @author yuqf
 */
public class ElasticsearchMetaData implements MetaDataMapper {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<String> getCatalogs(String dataSourceId) {
        List<String> catalogs = new ArrayList<>();
        catalogs.add("index");
        return catalogs;
    }

    @Override
    public List<Schema> getSchemas(String dataSourceId, String catalog) {
        List<Schema> schemas = new ArrayList<>();
        try {
            RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
            GetAliasesResponse alias = client.indices().getAlias(new GetAliasesRequest(), RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> map = alias.getAliases();
            map.forEach((index, v) -> {
                if (!index.startsWith(".")) {
                    Schema schema = new Schema();
                    schema.setSchema(index);
                    schema.setCatalog(dataSourceId);
                    schemas.add(schema);
                }
            });
        } catch (Exception e) {
            throw new PluginException(51002, "查询ES索引列表失败，请检查数据源是否正确");
        }
        return schemas;
    }

    @Override
    public List<Table> getTables(String dataSourceId, String catalog, String schema) {
        List<Table> tables = new ArrayList<>();
        Table table = new Table();
        table.setTableName("_doc");
        table.setSchema(schema);
        table.setCatalog(dataSourceId);
        tables.add(table);
        return tables;
    }

    @Override
    public List<Column> getColumns(String dataSourceId, String catalog, String schema, String table) {
        try {
            List<Column> columns = new ArrayList<>();
            RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
            GetIndexResponse response = client.indices().get(new GetIndexRequest(schema), RequestOptions.DEFAULT);
            //响应状态
            Object properties = response.getMappings().get(schema).sourceAsMap().get("properties");
            Map<String, Object> columnMap = null;
            if (properties instanceof Map) {
                columnMap = (Map<String, Object>) properties;
            } else {
                columnMap = objectMapper.readValue(objectMapper.writeValueAsString(properties), Map.class);
            }
            columnMap.forEach((k, v) -> {
                Map<String, Object> json = null;
                if (v instanceof Map) {
                    json = (Map<String, Object>) v;
                }
                Column column = new Column();
                column.setColumnName(k);
                column.setColumnType(json != null && json.get("type") != null ? json.get("type").toString() : null);
                column.setSchema(schema);
                column.setCatalog(dataSourceId);
                column.setTableName("_doc");
                columns.add(column);
            });
            return columns;
        } catch (Exception e) {
            throw new PluginException(51004, "查询ES字段列表失败，请检查数据源是否正确");
        }
    }
}
