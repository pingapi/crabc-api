package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.StatementMapper;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchStatement implements StatementMapper {

    private static Logger log = LoggerFactory.getLogger(ElasticsearchStatement.class);
    @Override
    public Object selectOne(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectList(String dataSourceId, String schema, String sql, Object params) {
        return this.selectPage(dataSourceId, schema, sql, params, 1, 15);
    }

    @Override
    public List selectPage(String dataSourceId, String index, String sql, Object params, int pageNum, int pageSize) {
        if (dataSourceId.contains(":")) {
            dataSourceId = dataSourceId.split(":")[0];
        }
        RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
        if (client == null) {
            throw new RuntimeException("es数据源为空");
        }
        Request post = new Request("POST", "/_sql/translate");
        List<String> replaces = new ArrayList<>();

        JSONObject map = new JSONObject();
        map.put("query", String.format(sql, replaces.toArray()));
        post.setJsonEntity(map.toJSONString());
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            Response response = client.getLowLevelClient().performRequest(post);
            Map<String, Object> scriptParameters = new HashMap<>();
            SearchTemplateRequest request = new SearchTemplateRequest();
            //添加分页
            if (pageSize != 0) {
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
                jsonObject.put("from", (pageNum -1) * pageSize);
                request.setScript(jsonObject.toString());
            } else {
                request.setScript(EntityUtils.toString(response.getEntity()));
            }
            //指定索引
            request.setRequest(new SearchRequest(index));
            //设置为内联
            request.setScriptType(ScriptType.INLINE);
            //设置查询参数
            request.setScriptParams(scriptParameters);

            SearchResponse searchResponse = client.searchTemplate(request, RequestOptions.DEFAULT).getResponse();
            SearchHits searchHits = searchResponse.getHits();
//            log.info("es查询返回结果："+JSONObject.toJSONString(searchHits));
            if (pageSize != 0) {
                SearchHit[] hits = searchHits.getHits();
                if (hits != null && hits.length > 0) {
                    for (SearchHit sh : hits) {
                        Map<String, Object> m = new HashMap<>();
                        if (sh.getSourceAsMap() != null && sh.getSourceAsMap().size() > 0) {
                            m.putAll(sh.getSourceAsMap());
                        }
                        if (sh.getFields() != null && sh.getFields().size() > 0) {
                            sh.getFields().entrySet().forEach(e -> {
                                if (e.getValue() != null && e.getValue().getValues() != null && e.getValue().getValues().size() > 0) {
                                    m.put(e.getKey(), e.getValue().getValues().get(0));
                                } else {
                                    m.put(e.getKey(), null);
                                }
                            });
                        }
                        result.add(m);
                    }
                }
            } else {
                TotalHits totalHits = searchHits.getTotalHits();
                Map<String, Object> objectMap = new HashMap<>();
                objectMap.put("count", totalHits.value);
                result.add(objectMap);
            }
        } catch (Exception e) {
            log.error("--es数据查询失败,sql:" + sql, e);
            throw new RuntimeException("es查询异常,请检查SQL是否正确,(索引名请使用双引号括起来)");
        }
        return result;
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
