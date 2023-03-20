package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
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

public class ElasticsearchMetaData implements MetaDataMapper {
    private static Logger log = LoggerFactory.getLogger(ElasticsearchMetaData.class);

    private RestHighLevelClient client;

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
            Schema schema = new Schema();
            schema.setSchema("index");
            schema.setCatalog(dataSourceId);
            schemas.add(schema);
        } catch (Exception e) {
            log.error("查询ES Schema清单失败!", e);
            throw new RuntimeException("查询ES Schema清单失败!", e);
        }
        return schemas;
    }

    @Override
    public List<Table> getTables(String dataSourceId, String catalog, String schema) {
        try {
            RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
            List<Table> tables = new ArrayList<>();
            GetAliasesResponse alias = client.indices().getAlias(new GetAliasesRequest(), RequestOptions.DEFAULT);
            Map<String, Set<AliasMetadata>> map = alias.getAliases();
            map.forEach((index, v) -> {
                if (!index.startsWith(".")) {
                    Table table = new Table();
                    table.setTableName(index);
                    table.setRemarks(null);
                    table.setTableType(null);
                    table.setCatalog(dataSourceId);
                    table.setSchema(schema);
                    tables.add(table);
                }
            });
            return tables;
        } catch (Exception e) {
            log.error("-查询ES的索引异常", e);
            throw new RuntimeException("ES数据源索引列表查询异常!" + e.getMessage());
        }
    }

    @Override
    public List<Column> getColumns(String dataSourceId, String catalog, String schema, String table) {
        try {
            List<Column> columns = new ArrayList<>();
            RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
            GetIndexResponse response = client.indices().get(new GetIndexRequest(table), RequestOptions.DEFAULT);
            //响应状态
            System.out.println(response.getAliases());
            Object properties = response.getMappings().get(table).sourceAsMap().get("properties");
            JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(properties));
            jsonObject.forEach((k, v) -> {
                JSONObject json = JSONObject.parseObject(v.toString());
                System.out.println();
                Column column = new Column();
                column.setColumnName(k);
                column.setColumnType(json.get("type").toString());
                column.setSchema(schema);
                column.setCatalog(dataSourceId);
                column.setTableName(table);
                columns.add(column);
            });
            return columns;
        } catch (Exception e) {
            log.error("-查询ES索引字段异常!", e);
            throw new RuntimeException("ES数据源查询索引字段异常!" + e.getMessage());
        }
    }
}
