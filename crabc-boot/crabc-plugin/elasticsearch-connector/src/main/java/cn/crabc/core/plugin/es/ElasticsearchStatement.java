package cn.crabc.core.plugin.es;

import cn.crabc.core.spi.StatementMapper;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticsearchStatement implements StatementMapper {

    private static Logger log = LoggerFactory.getLogger(ElasticsearchStatement.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public Object selectOne(String dataSourceId, String schema, String sql, Object params) {
        return null;
    }

    @Override
    public List selectList(String dataSourceId, String schema, String sql, Object params) {
        return this.selectPage(dataSourceId, schema, sql, params, 1, 10);
    }

    /**
     * 配置查询参数
     * @param params
     * @param sql
     * @return
     */
    private String makeSql(Object params, String sql){
        if (params == null) {
            return sql;
        }
        Map<String,Object> paramMap = (Map<String, Object>) params;
        for (String key : paramMap.keySet()) {
            String filed = "#{" +key+"}";
            Object valueObj = paramMap.get(key);
            if (sql.contains(filed)) {
                String value = null;
                if (valueObj instanceof List) {
                    List<String> list = (List<String>) valueObj;
                    value = this.inSql(list);
                }else{
                    value = valueObj.toString();
                }
                sql = sql.replace(filed, paramMap.get(key) != null ? "'" + value + "'" : null);
            }
        }
        return sql;
    }

    /**
     * 组装in查询
     * @param list
     * @return
     */
    private String inSql(List<String> list){
        String value = null;
        for(int i = 0; i< list.size(); i++) {
             if (i == list.size() -1){
                value = value+"";
            }else {
                value = value +"',";
            }
        }
        return  value;
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
        // 参数
        sql = makeSql(params, sql);
        //String query = "/_xpack/_sql?format=json";   //es 8 以上语法
        String query = "/_xpack/sql?format=json";      //es 8 以下语法
        Request post = new Request("POST", query);
        List<String> replaces = new ArrayList<>();

        JSONObject map = new JSONObject();
        map.put("query", String.format(sql, replaces.toArray()));
        map.put("fetch_size", pageSize);
        post.setJsonEntity(map.toJSONString());
        List<Map<String,Object>> result = new ArrayList<>();
        try {
            Response response = client.getLowLevelClient().performRequest(post);
            String body = EntityUtils.toString(response.getEntity());
            Map<String,Object> dataMap = objectMapper.readValue(body, Map.class);
            Object columns = dataMap.get("columns");
            Object rows = dataMap.get("rows");
            if (columns != null && rows != null) {
                List<Map<String,Object>> fields = (List<Map<String, Object>>) columns;
                List<List<Object>> values = (List<List<Object>>) rows;
                for(List<Object> row : values) {
                    Map<String,Object> rowMap = new HashMap<>();
                    for(int i = 0; i < row.size(); i++) {
                        Object key = fields.get(i).get("name");
                        if (key == null || "".equals(key.toString().trim())){
                            continue;
                        }
                        Object value = row.get(i);
                        rowMap.put(key.toString(), value);
                    }
                    result.add(rowMap);
                }
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
