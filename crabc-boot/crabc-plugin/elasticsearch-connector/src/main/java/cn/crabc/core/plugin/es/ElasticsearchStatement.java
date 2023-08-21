package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.PluginException;
import cn.crabc.core.spi.StatementMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
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
        if (paramMap == null || paramMap.size() == 0) {
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
            if (i < list.size() - 1) {
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
            Map<String, Object> paramsMap = new HashMap<>();
            if (params instanceof Map) {
                paramsMap = (Map<String, Object>) params;
            }
            Map<String, Object> dataMap = this.query(dataSourceId, sql, paramsMap, pageSize);
            Object columns = dataMap.get("columns");
            Object rows = dataMap.get("rows");
            // 分页光标
            Object cursor = dataMap.get("cursor");
            if (rows == null || columns == null) {
                result.put("pageCursor", "0");
                result.put("pageNum", pageNum);
                result.put("pageSize", pageSize);
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
            result.put("total", -1);
            result.put("pageCursor", cursor);
            result.put("pageNum", pageNum);
            result.put("pageSize", pageSize);

        } catch (Exception e) {
            log.error("--es数据查询失败,sql:" + sql, e);
            throw new PluginException("es查询异常,请检查SQL是否正确");
        }
        return result;
    }


    /**
     * 查询es
     *
     * @param dataSourceId
     * @param sql
     * @param paramsMap
     * @param pageSize
     * @return
     */
    public Map<String, Object> query(String dataSourceId, String sql, Map<String, Object> paramsMap, int pageSize) {
        RestHighLevelClient client = ElasticsearchDataSourceDriver.getConnectionClient(dataSourceId);
        if (client == null) {
            throw new PluginException("es数据源不存在");
        }
        // 参数
        sql = makeSql(paramsMap, sql);

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
            throw new PluginException("es查询异常,请检查SQL是否正确,(索引名请使用双引号括起来)");
        }
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
                throw new PluginException("es数据源新增数据失败");
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
