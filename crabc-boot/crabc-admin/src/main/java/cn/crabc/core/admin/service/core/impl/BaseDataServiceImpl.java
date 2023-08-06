package cn.crabc.core.admin.service.core.impl;


import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.admin.service.core.IBaseDataService;
import cn.crabc.core.admin.util.SQLUtil;
import cn.crabc.core.app.constant.BaseConstant;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.StatementMapper;
import cn.crabc.core.spi.bean.BaseDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public PreviewVO sqlPreview(String datasourceId, String schema, String sql) {
        StatementMapper statementMapper = dataSourceManager.getStatementMapper(datasourceId);
        Map<String, Object> params = new HashMap<>();
        params.put(BaseConstant.BASE_API_EXEC_TYPE, "preview");
        List<Map<String, Object>> list = statementMapper.selectList(datasourceId, schema, sql, params);
        PreviewVO preview = new PreviewVO();
        if (list != null && !list.isEmpty()) {
            preview.setData(list);
            Set<String> fieldName = list.get(0).keySet();
            preview.setMetadata(fieldName);
        }
        return preview;
    }

    @Override
    public Object execute(String datasourceId, String schema, String sql, Map<String, Object> params) {
        StatementMapper statementMapper = dataSourceManager.getStatementMapper(datasourceId);
        String sqlType = SQLUtil.getOperateType(sql);
        if ("insert".equalsIgnoreCase(sqlType)) {
            return statementMapper.insert(datasourceId, schema, sql, params);
        }else if("update".equalsIgnoreCase(sqlType)){
            return statementMapper.update(datasourceId, schema, sql,params);
        }else if("delete".equalsIgnoreCase(sqlType)){
            return statementMapper.delete(datasourceId, schema, sql,params);
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
