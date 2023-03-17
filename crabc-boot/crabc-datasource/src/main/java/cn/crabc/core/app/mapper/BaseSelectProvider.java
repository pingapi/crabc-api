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
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        return sql;
    }


    /**
     * 新增类
     * @param params
     * @return
     */
    public String executeInsert(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        return sql;
    }

    /**
     * 修改类
     * @param params
     * @return
     */
    public String executeUpdate(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        return sql;
    }

    /**
     * 删除类
     * @param params
     * @return
     */
    public String executeDelete(Map<String,Object> params) {
        String sql = params.get(BaseConstant.BASE_SQL).toString();
        params.remove(BaseConstant.BASE_SQL);
        return sql;
    }
}
