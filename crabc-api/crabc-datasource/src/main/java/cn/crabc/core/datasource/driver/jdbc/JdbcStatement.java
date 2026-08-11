/*
 * Copyright 2023, crabc.cn (creabc@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.crabc.core.datasource.driver.jdbc;

import cn.crabc.core.datasource.config.JdbcDataSourceRouter;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.enums.ErrorStatusEnum;
import cn.crabc.core.datasource.exception.CustomException;
import cn.crabc.core.datasource.mapper.BaseDataHandleMapper;
import cn.crabc.core.datasource.util.PageInfo;
import cn.crabc.core.spi.StatementMapper;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.mapping.SqlSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.regex.Pattern;

/**
 * JDBC SQL执行器，承接单条查询/变更和多脚本DML执行。
 */
public class JdbcStatement implements StatementMapper<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(JdbcStatement.class);
    private static final String[] SCRIPT_TAGS = {"</if>","</foreach>","</where>","</set>","</choose>","</when>","</trim>","</otherwise"};
    private final BaseDataHandleMapper baseMapper;
    private static final int PAGE_SIZE = 1000;
    private static final int PAGE_NUM = 1;
    private final Configuration myBatisConfiguration = new Configuration();

    public JdbcStatement(BaseDataHandleMapper baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public Map<String, Object> selectOne(String dataSourceId, String schema, String sql, Object params) {
        List<Map<String, Object>> maps = selectList(dataSourceId, schema, sql, params);
        return maps.isEmpty() ? new HashMap<>() : maps.get(0);
    }

    @Override
    public List<Map<String, Object>> selectList(String dataSourceId, String schema, String sql, Object params) {
        PageInfo page = selectPage(dataSourceId, schema, sql, params, PAGE_NUM, PAGE_SIZE);
        return page.getList();
    }

    @Override
    public PageInfo selectPage(String dataSourceId, String schema, String sql, Object params, int pageNum, int pageSize) {
        String execType = null;
        List<LinkedHashMap<String, Object>> list = new ArrayList<>();
        boolean pageHelperStarted = false;
        try {
            Map<String, Object> paramsMap = setParams(dataSourceId, schema, sql, params);
            execType = (String)paramsMap.get(BaseConstant.BASE_API_EXEC_TYPE);
            
            Object pageSetup = paramsMap.get(BaseConstant.PAGE_SETUP);
            int pageCount = pageSetup != null ? Integer.parseInt(pageSetup.toString()) : 0;

            // 页面预览结果不需要查询总数
            if ("preview".equals(execType)) {
                PageHelper.startPage(pageNum, pageSize, false);
                pageHelperStarted = true;
            }else if (pageCount != 0 && !checkPage(sql)) {
                PageHelper.startPage(pageNum, pageSize);
                pageHelperStarted = true;
            }
            list = baseMapper.executeQuery(paramsMap);

        } catch (Exception e) {
            Throwable cause = e.getCause();
            String errorMsg = cause == null ? e.getMessage() : cause.getMessage();
            log.error("SQL执行失败，请检查SQL是否正常: {}", errorMsg, e);
            
            if (execType == null) {
                throw new CustomException(51000, errorMsg);
            } else {
                LinkedHashMap<String, Object> errorMap = new LinkedHashMap<>();
                errorMap.put("执行异常", "SQL执行失败：" + errorMsg);
                list.add(errorMap);
            }
        } finally {
            // 确保PageHelper被正确清理
            if (pageHelperStarted) {
                PageHelper.clearPage();
            }
            // 清理ThreadLocal，释放数据源key引用
            JdbcDataSourceRouter.remove();
        }
        return new PageInfo<>(list, pageNum, pageSize);
    }

    @Override
    public int insert(String dataSourceId, String schema, String sql, Object params) {
        try {
            Map<String, Object> paramsMap = setParams(dataSourceId, schema, sql, params);
            return baseMapper.executeInsert(paramsMap);
        } catch (Exception e) {
            log.error("SQL执行失败，请检查SQL是否正常", e);
            throw new CustomException(ErrorStatusEnum.API_SQL_ERROR.getCode(), ErrorStatusEnum.API_SQL_ERROR.getMassage());
        } finally {
            JdbcDataSourceRouter.remove();
        }
    }

    @Override
    public int delete(String dataSourceId, String schema, String sql, Object params) {
        try {
            Map<String, Object> paramsMap = setParams(dataSourceId, schema, sql, params);
            return baseMapper.executeDelete(paramsMap);
        } catch (Exception e) {
            log.error("SQL执行失败，请检查SQL是否正常", e);
            throw new CustomException(ErrorStatusEnum.API_SQL_ERROR.getCode(), ErrorStatusEnum.API_SQL_ERROR.getMassage());
        } finally {
            JdbcDataSourceRouter.remove();
        }
    }

    @Override
    public int update(String dataSourceId, String schema, String sql, Object params) {
        try {
            Map<String, Object> paramsMap = setParams(dataSourceId, schema, sql, params);
            return baseMapper.executeUpdate(paramsMap);
        } catch (Exception e) {
            log.error("SQL执行失败，请检查SQL是否正常", e);
            throw new CustomException(ErrorStatusEnum.API_SQL_ERROR.getCode(), ErrorStatusEnum.API_SQL_ERROR.getMassage());
        } finally {
            JdbcDataSourceRouter.remove();
        }
    }

    /**
     * 多脚本DML使用同一连接顺序执行，事务开关决定失败时是否回滚已执行脚本。
     */
    @Override
    public int executeBatchDml(String dataSourceId, String schema, List<String> sqlList, Object params, boolean transactionEnabled) {
        Map<String, Object> paramsMap = copyParams(params);
        String dataSourceKey = buildDataSourceKey(dataSourceId, schema, paramsMap);
        Connection connection = null;
        boolean oldAutoCommit = true;
        boolean autoCommitChanged = false;
        try {
            connection = getConnection(dataSourceKey, schema, paramsMap);
            oldAutoCommit = connection.getAutoCommit();
            if (transactionEnabled && oldAutoCommit) {
                connection.setAutoCommit(false);
                autoCommitChanged = true;
            }

            int affectedRows = 0;
            for (String sql : sqlList) {
                affectedRows += executeDml(connection, sql, paramsMap);
            }
            if (transactionEnabled) {
                connection.commit();
            }
            return affectedRows;
        } catch (Exception e) {
            rollbackQuietly(connection, transactionEnabled);
            log.error("多脚本SQL执行失败，transactionEnabled: {}", transactionEnabled, e);
            throw new CustomException(ErrorStatusEnum.API_SQL_ERROR.getCode(), ErrorStatusEnum.API_SQL_ERROR.getMassage());
        } finally {
            restoreAutoCommit(connection, autoCommitChanged, oldAutoCommit);
            closeQuietly(connection);
            JdbcDataSourceRouter.remove();
        }
    }

    /**
     * 单条SQL仍沿用Mapper动态数据源路由，保持原有查询/变更执行路径不变。
     */
    private Map<String, Object> setParams(String dataSourceId, String schema, String sql, Object params) {
        Map<String, Object> paramsMap = copyParams(params);
        paramsMap.put(BaseConstant.BASE_SQL, sql);
        dataSourceId = buildDataSourceKey(dataSourceId, schema, paramsMap);
        JdbcDataSourceRouter.setDataSourceKey(dataSourceId);
        return paramsMap;
    }

    /**
     * 批量执行会追加内部控制参数，复制Map避免污染调用方原始参数。
     */
    private Map<String, Object> copyParams(Object params) {
        Map<String, Object> paramsMap = new HashMap<>();
        if (params instanceof Map) {
            paramsMap.putAll((Map<String, Object>) params);
        }
        return paramsMap;
    }

    /**
     * schema路由key保持和原单条SQL一致，便于JDBC连接按库/Schema执行。
     */
    private String buildDataSourceKey(String dataSourceId, String schema, Map<String, Object> paramsMap) {
        if (schema != null && !schema.isEmpty()) {
            String dataSourceType = (String)paramsMap.getOrDefault(BaseConstant.DATA_SOURCE_TYPE, "");
            return String.format("%s:%s:%s", dataSourceId, dataSourceType, schema);
        }
        return dataSourceId;
    }

    /**
     * 批量DML直接取同一个物理连接，事务边界才能覆盖全部脚本。
     */
    private Connection getConnection(String dataSourceKey, String schema, Map<String, Object> paramsMap) throws SQLException {
        Connection connection = JdbcDataSourceRouter.getDataSource(dataSourceKey).getConnection();
        if (schema != null && !schema.isEmpty()) {
            String dataSourceType = (String) paramsMap.getOrDefault(BaseConstant.DATA_SOURCE_TYPE, "");
            if (BaseConstant.CATALOG_DATA_SOURCE.contains(dataSourceType)) {
                connection.setCatalog(schema);
            } else {
                connection.setSchema(schema);
            }
        }
        return connection;
    }

    /**
     * 每条脚本先交给MyBatis生成BoundSql，再用PreparedStatement绑定参数执行。
     */
    private int executeDml(Connection connection, String sql, Map<String, Object> paramsMap) throws SQLException {
        BoundSql boundSql = buildBoundSql(sql, paramsMap);
        try (PreparedStatement statement = connection.prepareStatement(boundSql.getSql())) {
            bindParameters(statement, boundSql, paramsMap);
            return statement.executeUpdate();
        }
    }

    /**
     * 动态SQL片段只有出现MyBatis标签时才包script，普通SQL保持原样解析。
     */
    private BoundSql buildBoundSql(String sql, Map<String, Object> paramsMap) {
        LanguageDriver languageDriver = myBatisConfiguration.getDefaultScriptingLanguageInstance();
        SqlSource sqlSource = languageDriver.createSqlSource(myBatisConfiguration, addScript(sql), Map.class);
        return sqlSource.getBoundSql(paramsMap);
    }

    /**
     * MyBatis LanguageDriver需要script根标签才能解析foreach/if等动态标签。
     */
    private String addScript(String sql) {
        for (String tag : SCRIPT_TAGS) {
            if (sql.contains(tag)) {
                return "<script> " + sql + " </script>";
            }
        }
        return sql;
    }

    /**
     * 按BoundSql映射顺序绑定参数，避免多脚本执行时手写拼接参数。
     */
    private void bindParameters(PreparedStatement statement, BoundSql boundSql, Map<String, Object> paramsMap) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        for (int i = 0; i < parameterMappings.size(); i++) {
            String property = parameterMappings.get(i).getProperty();
            Object value = getParameterValue(boundSql, paramsMap, property);
            if (value == null) {
                statement.setNull(i + 1, Types.NULL);
            } else {
                statement.setObject(i + 1, value);
            }
        }
    }

    /**
     * foreach等动态标签会生成additionalParameter，普通参数再从Map或嵌套属性中读取。
     */
    private Object getParameterValue(BoundSql boundSql, Map<String, Object> paramsMap, String property) {
        if (boundSql.hasAdditionalParameter(property)) {
            return boundSql.getAdditionalParameter(property);
        }
        if (paramsMap.containsKey(property)) {
            return paramsMap.get(property);
        }
        MetaObject metaObject = myBatisConfiguration.newMetaObject(paramsMap);
        return metaObject.getValue(property);
    }

    /**
     * 只有开启事务时才显式回滚，未开启时保留数据库默认自动提交语义。
     */
    private void rollbackQuietly(Connection connection, boolean transactionEnabled) {
        if (!transactionEnabled || connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            log.error("多脚本SQL事务回滚失败", rollbackException);
        }
    }

    /**
     * 连接来自池，归还前恢复自动提交状态，避免影响后续复用连接。
     */
    private void restoreAutoCommit(Connection connection, boolean autoCommitChanged, boolean oldAutoCommit) {
        if (connection == null || !autoCommitChanged) {
            return;
        }
        try {
            connection.setAutoCommit(oldAutoCommit);
        } catch (SQLException autoCommitException) {
            log.error("恢复连接自动提交状态失败", autoCommitException);
        }
    }

    /**
     * 批量DML自行持有连接，执行结束必须归还连接池。
     */
    private void closeQuietly(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException closeException) {
            log.error("关闭多脚本SQL连接失败", closeException);
        }
    }

    private boolean checkPage(String sql) {
        String[] patterns = {
            "(?i)limit.*?\\d",      // mysql,tidb
            "(?i)offset.*?\\d",     // postgres, sqlserver2012+
            "(?i)ROWNUM.*?\\d"      // oracle
        };
        
        for (String pattern : patterns) {
            if (Pattern.compile(pattern).matcher(sql).find()) {
                return true;
            }
        }
        return false;
    }
}
