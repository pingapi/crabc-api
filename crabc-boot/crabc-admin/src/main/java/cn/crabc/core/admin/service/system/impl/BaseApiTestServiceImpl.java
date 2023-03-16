package cn.crabc.core.admin.service.system.impl;

import cn.crabc.core.admin.entity.vo.PreviewVO;
import cn.crabc.core.admin.service.system.IBaseApiTestService;
import cn.crabc.core.app.constant.BaseConstant;
import cn.crabc.core.app.driver.DataSourceManager;
import cn.crabc.core.spi.DataSourceDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * API测试和预览 服务实现
 *
 * @author yuqf
 */
@Service
public class BaseApiTestServiceImpl implements IBaseApiTestService {

    @Autowired
    private DataSourceManager dataSourceManager;

    @Override
    public PreviewVO sqlPreview(String datasourceId, String schema, String sql) {
        DataSourceDriver dataSourceDriver = dataSourceManager.getDataSource(datasourceId);
        if (schema != null && !"".equals(schema)) {
            datasourceId = datasourceId + ":" + schema;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(BaseConstant.BASE_API_EXEC_TYPE, "preview");
        List<Map<String, Object>> list = dataSourceDriver.selectList(datasourceId, schema, sql, params);
        PreviewVO preview = new PreviewVO();
        if (list != null && !list.isEmpty()) {
            preview.setData(list);
            Set<String> fieldName = list.get(0).keySet();
            preview.setMetadata(fieldName);
        }
        return preview;
    }

    @Override
    public Object testApi(String datasourceId, String schema, String sql, Map<String, Object> params) {
        DataSourceDriver dataSourceDriver = dataSourceManager.getDataSource(datasourceId);
        Object pageNum = params.get(BaseConstant.PAGE_NUM);
        Object pageSize = params.get(BaseConstant.PAGE_SIZE);
        if (pageNum != null && pageSize != null) {
            return dataSourceDriver.selectPage(datasourceId, schema, sql, params, Integer.parseInt(pageNum.toString()), Integer.parseInt(pageSize.toString()));
        } else {
            return dataSourceDriver.selectList(datasourceId, schema, sql, params);
        }
    }
}
