package cn.crabc.core.admin.service.core.impl;

import cn.crabc.core.admin.service.core.IApiRequestSerice;
import cn.crabc.core.admin.service.core.IBaseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * API请求处理服务 实现类
 *
 * @author yuqf
 */
@Service
public class ApiRequestServiceImpl implements IApiRequestSerice {
    @Autowired
    private IBaseDataService baseDataService;

    @Override
    public List<Map<String, Object>> process(Map<String, Object> params) {

        return null;
    }
}
