package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.util.List;

/**
 * es驱动实现
 *
 * @author yuqf
 */
public class EsDataSourceDriver implements DataSourceDriver {

    ElasticsearchClient client;

    @Override
    public String getName() {
        return "elasticsearch";
    }

    @Override
    public Integer test(BaseDataSource dataSource) {
        try {
            ElasticsearchClient testClient = createClient(dataSource);
            testClient.indices().exists(e -> e.index("test"));
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

    @Override
    public void init(BaseDataSource dataSource) {
        client = createClient(dataSource);
    }

    /**
     * 连接
     *
     * @param dataSource
     * @return
     */
    public ElasticsearchClient createClient(BaseDataSource dataSource) {
        RestClient client = RestClient.builder(new HttpHost(dataSource.getHost(), Integer.parseInt(dataSource.getPort()), "http")).build();
        ElasticsearchTransport transport = new RestClientTransport(client, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @Override
    public void destroy(String dataSourceId) {

    }

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

    @Override
    public Object selectOne(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectList(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectPage(String dataSourceId, String schema, String sql, Object params, int limit, int offset) {
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
