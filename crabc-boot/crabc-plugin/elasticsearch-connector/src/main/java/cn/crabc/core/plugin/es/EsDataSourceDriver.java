
/*
 * Copyright 2023, crabc.cn (creabc@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * es数据源 驱动实现
 *
 * @author yuqf
 */
public class EsDataSourceDriver implements DataSourceDriver {

    private static Logger log = LoggerFactory.getLogger(EsDataSourceDriver.class);
    public static Map<String, RestHighLevelClient> DATA_SOURCE_POOL = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "elasticsearch";
    }

    @Override
    public Integer test(BaseDataSource dataSource) {
        try {
            // 构建
            RestHighLevelClient client = this.buildClient(dataSource);
            // 测试联通性
            client.indices().exists(new GetIndexRequest(), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 1;
    }

    /**
     * 初始化
     * @param dataSource
     */
    @Override
    public void init(BaseDataSource dataSource) {
        String datasourceId = dataSource.getDatasourceId();
        // 构建
        RestHighLevelClient client = null;
        try {
            client = this.buildClient(dataSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        DATA_SOURCE_POOL.put(datasourceId, client);
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

    /**
     * 构建ES客户端
     * @param dataSource
     * @return
     */
    private RestHighLevelClient buildClient(BaseDataSource dataSource) throws Exception {
        String username = dataSource.getUsername();
        String password = dataSource.getPassword();
        String schema = dataSource.getExtend();
        String host = dataSource.getHost();
        Integer port = Integer.parseInt(dataSource.getPort());
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
            // 信任所有
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();
        SSLIOSessionStrategy sessionStrategy = new SSLIOSessionStrategy(sslContext, NoopHostnameVerifier.INSTANCE);
        //支持集群模式
        String[] splitHosts = host.split(",");
        HttpHost[] hosts = new HttpHost[splitHosts.length];
        for (int i = 0; i < splitHosts.length; i++) {
            hosts[i] = new HttpHost(splitHosts[i], port, schema);
        }
        RestClientBuilder builder = RestClient.builder(hosts)
                .setHttpClientConfigCallback(httpAsyncClientBuilder -> {
                    if (username != null && password != null) {
                        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
                        return httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()).setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(3)).setDefaultCredentialsProvider(credentialsProvider).setSSLStrategy(sessionStrategy);
                    } else {
                        return httpAsyncClientBuilder.setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()).setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(1)).setSSLStrategy(sessionStrategy);
                    }
                });
        return new RestHighLevelClient(builder);
    }

    /**
     * 销毁
     * @param dataSourceId
     */
    @Override
    public void destroy(String dataSourceId) {
        RestHighLevelClient client = DATA_SOURCE_POOL.get(dataSourceId);
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DATA_SOURCE_POOL.remove(dataSourceId);
        }
    }

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
            RestHighLevelClient client = DATA_SOURCE_POOL.get(dataSourceId);
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
            RestHighLevelClient client = DATA_SOURCE_POOL.get(dataSourceId);
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
            throw new RuntimeException("ES数据源查询索引字段异常!"+ e.getMessage());
        }
    }
}
