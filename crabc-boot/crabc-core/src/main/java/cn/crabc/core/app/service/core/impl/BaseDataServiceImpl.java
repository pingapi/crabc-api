package cn.crabc.core.app.service.core.impl;


import cn.crabc.core.app.entity.vo.PreviewVO;
import cn.crabc.core.app.service.core.IBaseDataService;
import cn.crabc.core.app.util.SQLUtil;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.driver.DataSourceManager;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 接口执行服务 实现类
 *
 * @author yuqf
 */
@Service
public class BaseDataServiceImpl implements IBaseDataService {

    @Autowired
    private DataSourceManager dataSourceManager;
    @Override
    public String testConnection(BaseDataSource dataSource) {
        return dataSourceManager.test(dataSource);
    }

    @Override
    public PreviewVO sqlPreview(String datasourceId,String datasourceType, String schema, String sql) {
        StatementMapper statementMapper = dataSourceManager.getStatementMapper(datasourceId);
        Map<String, Object> params = new HashMap<>();
        // 数据源类型
        if (datasourceType != null) {
            params.put(BaseConstant.DATA_SOURCE_TYPE, datasourceType);
        }
        params.put(BaseConstant.BASE_API_EXEC_TYPE, "preview");
        Object result = this.execute(datasourceId, datasourceType, schema, sql, params);
        PreviewVO preview = new PreviewVO();
        if (result instanceof List) {
            List<Map<String, Object>> list = (List<Map<String, Object>>)result;
            if (!list.isEmpty()) {
                preview.setData(list);
                Set<String> fieldName = list.get(0).keySet();
                preview.setMetadata(fieldName);
            }
        }
        return preview;
    }

    @Override
    public Object execute(String datasourceId, String datasourceType, String schema, String sql, Map<String, Object> params) {
        StatementMapper statementMapper = dataSourceManager.getStatementMapper(datasourceId);
        // 数据源类型
        if (datasourceType != null && !params.containsKey(BaseConstant.DATA_SOURCE_TYPE)) {
            params.put(BaseConstant.DATA_SOURCE_TYPE, datasourceType);
        }
        String sqlType = SQLUtil.getOperateType(sql);
        if ("insert".equalsIgnoreCase(sqlType)) {
            List<Map<String, Object>> list = new ArrayList<>();
            int insert = statementMapper.insert(datasourceId, schema, sql, params);
            Map<String, Object> result = new HashMap<>();
            result.put("执行结果", "执行成功，影响条数："+insert);
            list.add(result);
            return list;
        }else if("update".equalsIgnoreCase(sqlType)){
            List<Map<String, Object>> list = new ArrayList<>();
            int update = statementMapper.update(datasourceId, schema, sql, params);
            Map<String, Object> result = new HashMap<>();
            result.put("执行结果", "执行成功，影响条数："+update);
            list.add(result);
            return list;
        }else if("delete".equalsIgnoreCase(sqlType)){
            List<Map<String, Object>> list = new ArrayList<>();
            int delete = statementMapper.delete(datasourceId, schema, sql, params);
            Map<String, Object> result = new HashMap<>();
            result.put("执行结果", "执行成功，影响条数："+delete);
            list.add(result);
            return list;
        } else {
            Object pageNum = params.get(BaseConstant.PAGE_NUM);
            Object pageSize = params.get(BaseConstant.PAGE_SIZE);
            if (pageNum != null && pageSize != null) {
                return statementMapper.selectPage(datasourceId, schema, sql, params, Integer.parseInt(pageNum.toString()), Integer.parseInt(pageSize.toString()));
            } else {
                return statementMapper.selectList(datasourceId, schema, sql, params);
            }
        }
    }
}
