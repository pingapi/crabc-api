
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
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.PluginException;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * es数据源 驱动实现
 *
 * @author yuqf
 */
public class ElasticsearchDataSourceDriver implements DataSourceDriver {
    public static Map<String, RestHighLevelClient> DATA_SOURCE_POOL = new ConcurrentHashMap<>();
    private ElasticsearchStatement statement = new ElasticsearchStatement();
    private ElasticsearchMetaData metaData = new ElasticsearchMetaData();

    @Override
    public String getName() {
        return "elasticsearch";
    }

    @Override
    public String test(BaseDataSource dataSource) {
        try {
            // 构建
            RestHighLevelClient client = this.buildClient(dataSource);
            // 测试联通性
            client.indices().exists(new GetIndexRequest("test"), RequestOptions.DEFAULT);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            return cause == null ? e.getMessage() : cause.getLocalizedMessage();
        }
        return "1";
    }

    /**
     * 初始化
     *
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
            throw new PluginException("初始化es时失败");
        }
        DATA_SOURCE_POOL.put(datasourceId, client);
    }

    /**
     * 构建ES客户端
     *
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
     *
     * @param dataSourceId
     */
    @Override
    public void destroy(String dataSourceId) {
        RestHighLevelClient client = DATA_SOURCE_POOL.get(dataSourceId);
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                throw new PluginException("es数据源销毁失败");
            }
            DATA_SOURCE_POOL.remove(dataSourceId);
        }
    }

    @Override
    public MetaDataMapper getMetaData() {
        return metaData;
    }

    @Override
    public StatementMapper getStatement() {
        return statement;
    }

    /**
     * 获取连接客户端
     *
     * @param dataSourceId
     * @return
     */
    public static RestHighLevelClient getConnectionClient(String dataSourceId) {
        return DATA_SOURCE_POOL.get(dataSourceId);
    }
}
