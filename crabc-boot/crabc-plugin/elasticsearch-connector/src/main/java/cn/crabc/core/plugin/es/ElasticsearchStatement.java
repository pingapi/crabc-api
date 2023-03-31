package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.StatementMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
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
import org.elasticsearch.search.aggregations.Aggregations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * es数据源 操作执行类
 *
 * @author yuqf
 */
public class ElasticsearchStatement implements StatementMapper {

    private static Logger log = LoggerFactory.getLogger(ElasticsearchStatement.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object selectOne(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectList(String dataSourceId, String schema, String sql, Object params) {
        Map<String, Object> map = this.selectPage(dataSourceId, schema, sql, params, 1, 100);
        return (List) map.get("list");
    }

    /**
     * 配置查询参数
     *
     * @param paramMap
     * @param sql
     * @return
     */
    private String makeSql(Map<String, Object> paramMap, String sql) {
        if (paramMap == null) {
            return sql;
        }
        for (String key : paramMap.keySet()) {
            String filed = "#{" + key + "}";
            Object valueObj = paramMap.get(key);
            if (sql.contains(filed)) {
                String value = null;
                if (valueObj instanceof List) {
                    List<String> list = (List<String>) valueObj;
                    value = this.inSql(list);
                } else {
                    value = valueObj.toString();
                }
                sql = sql.replace(filed, paramMap.get(key) != null ? "'" + value + "'" : null);
            }
        }
        return sql;
    }

    /**
     * 组装in查询
     *
     * @param list
     * @return
     */
    private String inSql(List<String> list) {
        String value = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                value = value + "";
            } else {
                value = value + "',";
            }
        }
        return value;
    }

    @Override
    public Map<String, Object> selectPage(String dataSourceId, String index, String sql, Object params, int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            Map<String, Object> dataMap = this.query(dataSourceId, sql, pageNum, pageSize);
            Object columns = dataMap.get("columns");
            Object rows = dataMap.get("rows");
            // 分页光标
            Object cursor = dataMap.get("cursor");
            if (columns == null || rows == null) {
                result.put("list", list);
                return result;
            }
            List<Map<String, Object>> fields = (List<Map<String, Object>>) columns;
            List<List<Object>> values = (List<List<Object>>) rows;
            for (List<Object> row : values) {
                Map<String, Object> rowMap = new HashMap<>();
                for (int i = 0; i < row.size(); i++) {
                    Object key = fields.get(i).get("name");
                    if (key == null || "".equals(key.toString().trim())) {
                        continue;
                    }
                    Object value = row.get(i);
                    rowMap.put(key.toString(), value);
                }
                list.add(rowMap);
            }
            result.put("list", list);
            // 是否分页
            if (params instanceof Map) {
                Map<String, Object> paramsMap = (Map<String, Object>) params;
                Object pageSetup = paramsMap.get("pageSetup");
                int pageCount = pageSetup != null ? Integer.parseInt(pageSetup.toString()) : 0;
                if (0 == pageCount){
                    return result;
                }
                // 分页设置
                if (2 == pageCount) {
                    result.put("total", count(dataSourceId,sql,params));
                } else {
                    result.put("total", -1);
                }
                result.put("pageCursor", cursor);
                result.put("pageNum", pageNum);
                result.put("pageSize", pageSize);
            }
        } catch (Exception e) {
            log.error("--es数据查询失败,sql:" + sql, e);
            throw new RuntimeException("es查询异常,请检查SQL是否正确,(索引名请使用双引号括起来)");
        }

        return result;
    }

    /**
     * 查询计数
     * @param dataSourceId
     * @param sql
     * @param params
     * @return
     */
    public Integer count(String dataSourceId, String sql, Object params){
        sql = "SELECT count(1) FROM(" + sql +")";
        Map<String, Object> dataMap = this.query(dataSourceId, sql, params, 1);
        Object rows = dataMap.get("rows");
        if (rows == null){
            return -1;
        }
        List<List<Object>> values = (List<List<Object>>) rows;
        if (values.size() > 0){
            List<Object> objects = values.get(0);
            if (objects != null && objects.size() > 0){
                return Integer.parseInt(objects.get(0).toString());
            }
        }
        return -1;
    }

    /**
     * 查询es
     *
     * @param dataSourceId
     * @param sql
     * @param params
     * @param pageSize
     * @return
     */
    public Map<String, Object> query(String dataSourceId, String sql, Object params, int pageSize) {
        RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
        if (client == null) {
            throw new RuntimeException("es数据源不存在");
        }
        Map<String, Object> paramsMap = null;
        if (params instanceof Map) {
            paramsMap = (Map<String, Object>) params;
            // 参数
            sql = makeSql(paramsMap, sql);
        }
        String query = "/_sql?format=json";
        Request request = new Request("POST", query);

        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("query", sql);
        queryMap.put("fetch_size", pageSize);
        if (paramsMap != null && paramsMap.containsKey("pageCursor")) {
            queryMap.put("cursor", paramsMap.get("pageCursor").toString());
        }
        try {
            request.setJsonEntity(objectMapper.writeValueAsString(queryMap));
            Response response = client.getLowLevelClient().performRequest(request);
            String body = EntityUtils.toString(response.getEntity());
            Map<String, Object> dataMap = objectMapper.readValue(body, Map.class);
            return dataMap;
        } catch (Exception e) {
            log.error("--es数据查询失败,sql:" + sql, e);
            throw new RuntimeException("es查询异常,请检查SQL是否正确,(索引名请使用双引号括起来)");
        }
    }

    public List executeDsl(String dataSourceId, String index, String sql, Object params, int pageNum, int pageSize) {
        RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
        if (client == null) {
            throw new RuntimeException("es数据源为空");
        }
        Request post = new Request("POST", "/_sql/translate");
        if (params instanceof Map) {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            sql = makeSql(paramsMap, sql);
        }
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("query", sql);
        queryMap.put("fetch_size", pageSize);
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            post.setJsonEntity(objectMapper.writeValueAsString(queryMap));
            Response response = client.getLowLevelClient().performRequest(post);
            SearchTemplateRequest request = new SearchTemplateRequest();
            if (pageSize > 0) {
                Map<String, Object> jsonMap = objectMapper.readValue(EntityUtils.toString(response.getEntity()), Map.class);
                jsonMap.put("from", (pageNum - 1) * pageSize);
                request.setScript(objectMapper.writeValueAsString(jsonMap));
            } else {
                request.setScript(EntityUtils.toString(response.getEntity()));
            }
            //指定索引
            request.setRequest(new SearchRequest(index));
            //设置为内联
            request.setScriptType(ScriptType.INLINE);
            //设置查询参数
            request.setScriptParams(new HashMap<>());

            SearchResponse searchResponse = client.searchTemplate(request, RequestOptions.DEFAULT).getResponse();
            SearchHits searchHits = searchResponse.getHits();
            Aggregations aggregations = searchResponse.getAggregations();
//            log.info("es查询返回结果："+JSONObject.toJSONString(searchHits));
            if (pageSize > 0) {
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
    public int insert(String dataSourceId, String index, String sql, Object params) {
        RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
        IndexRequest indexRequest = new IndexRequest(index);
        if (params instanceof Map) {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            indexRequest.source(paramsMap);
            try {
                client.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
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
