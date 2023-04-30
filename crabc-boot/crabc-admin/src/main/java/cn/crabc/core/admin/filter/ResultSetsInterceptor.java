package cn.crabc.core.admin.filter;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;

/**
 * ResultSet 拦截器
 *
 * @author yuqf
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class ResultSetsInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        List<Map<String, Object>> list = new ArrayList<>();

        ResultSetHandler resultSetHandler = (ResultSetHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject
                .forObject(resultSetHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY,
                        new DefaultReflectorFactory());
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
        //获取节点属性的集合
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        if (resultMaps.size() == 0) {
            return invocation.proceed();
        }
        Class<?> resultType = resultMaps.get(0).getType();
        if (resultType.getName().equals("java.util.Map")) {
            Statement statement = (Statement) invocation.getArgs()[0];
            ResultSet resultSet = statement.getResultSet();

            if (resultSet != null) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                List<String> columns = new ArrayList<>();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    columns.add(metaData.getColumnName(i));
                }
                // 没有数据时只返回列名
                if (!resultSet.next()) {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    for (String colName : columns) {
                        map.put(colName, null);
                    }
                    list.add(map);
                    return list;
                }
                while (resultSet.next()) {
                    LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                    for (String colName : columns) {
                        map.put(colName, resultSet.getObject(colName));
                    }
                    list.add(map);
                }
                return list;
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }
}
