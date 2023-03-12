package cn.crabc.core.admin.service.core;

import java.util.List;
import java.util.Map;

/**
 *  * API请求处理 服务类
 *
 * @author yuqf
 */
public interface IApiRequestSerice {

    List<Map<String, Object>> process(Map<String, Object> params);
}
