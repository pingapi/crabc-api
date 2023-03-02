package cn.crabc.core.app.mapper;
import cn.crabc.core.app.constant.BaseConstant;

import java.util.Map;

/**
 * mybatis SQL提供器
 *
 * @author yuqf
 */
public class BaseSelectProvider {


    /**
     * 查询类
     * @param params
     * @return
     */
    public String executeQuery(Map<String,Object> params) {
        return params.get(BaseConstant.BASE_SQL).toString();
    }


    /**
     * 操作类
     * @param params
     * @return
     */
    public String executeUpdate(Map<String,Object> params) {
        return params.get(BaseConstant.BASE_SQL).toString();
    }
}
