package cn.crabc.core.app.util;


import cn.crabc.core.app.entity.BaseApiParam;
import cn.crabc.core.datasource.exception.CustomException;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL解析工具类
 *
 * @author yuqf
 */
public class SQLUtil {

    private final static Logger log = LoggerFactory.getLogger(SQLUtil.class);

    public final static String PARAM_PATTERN = "(?<=[#|$]\\{)(.+?)(?=\\})";


    /**
     * 过滤SQL中的注释
     *
     * @param sql
     * @return
     */
    public static String sqlCommentReplace(String sql) {
        String SQL_COMMENT = "(?ms)('(?:''|[^'])*')|--.*?$|///*.*?//*/|\\/\\/[^\\n]*|\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*+\\/";
        Pattern p = Pattern.compile(SQL_COMMENT);
        return p.matcher(sql).replaceAll("#1");
    }

    /**
     * 检查SQL正确性时替换标签
     * @param sql
     * @return
     */
    public static String sqlFilter(String sql){
        String forRegex = "<foreach[\\s\\S]*?</foreach>";
        sql = sql.replaceAll(forRegex,"()");

        String regex = "<where>[\\s\\S]*?</where>|<if[\\s\\S]*?</if>|<set>[\\s\\S]*?</set>|<choose>[\\s\\S]*?</choose>|<when[\\s\\S]*?</when>";
        // 替换标签
        return sql.replaceAll(regex,"a");
    }

    /**
     * 解析查询字段
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static Set<String> analyzeSQL(String sql, String dbType) {
        Set<String> aliasList = new HashSet<>();
        try {
            List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, DbType.valueOf(dbType), false);
            if (sqlStatementList == null || sqlStatementList.isEmpty()) {
                //throw new IllegalArgumentException("不是有效语句");
                return aliasList;
            } else if (sqlStatementList.size() > 1) {
                throw new CustomException(55000, "不支持多条SQL语句！请删除多余的SQL");
            }

            if (!(sqlStatementList.get(0) instanceof SQLSelectStatement)) {
                return aliasList;
            }

            SQLSelectStatement selectStatement = (SQLSelectStatement) sqlStatementList.get(0);

            SQLSelect sqlSelect = selectStatement.getSelect();

            SQLSelectQueryBlock firstQueryBlock = sqlSelect.getFirstQueryBlock();
            //normalized

            for (int i = 0; i < firstQueryBlock.getSelectList().size(); i++) {
                SQLSelectItem item = firstQueryBlock.getSelectList().get(i);

                SQLExpr expr = item.getExpr();
                String normalizedName = null;


                if (expr instanceof SQLAllColumnExpr) {
                    normalizedName = "*";
                } else if (expr instanceof SQLPropertyExpr && ((SQLPropertyExpr) expr).getName().equals("*")) {
                    normalizedName = "*";
                } else if (expr instanceof SQLIdentifierExpr) {
                    //标识
                    normalizedName = ((SQLIdentifierExpr) expr).normalizedName();//normalizedName能够去掉引号关键字
                } else if (expr instanceof SQLPropertyExpr) {
                    //带owner的标识
                    normalizedName = SQLUtils.normalize(((SQLPropertyExpr) expr).getName(), DbType.valueOf(dbType));//normalizedName能够去掉引号关键字，去掉owner
                } else {
                    //自动生成虚拟别名
                    normalizedName = "_col" + i;
                }
                String alias = item.computeAlias();
                if (alias == null) {
                    aliasList.add(normalizedName + "," + normalizedName);
                } else {
                    aliasList.add(normalizedName + ',' + alias);
                }
            }

            return aliasList;

        } catch (Exception e) {
            log.info("SQL解析失败：{}", e.getMessage());
            return aliasList;
        }
    }

    /**
     * 解析SQL
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static Map<String, Object> parseSqlTable(String sql, String dbType) {
        Map<String, Object> map = new HashMap<>();
        Set<String> tableName = new HashSet<>();
        List<String> operateName = new ArrayList<>();
        try {
            //格式化
            String sqlFormat = SQLUtils.format(sql, dbType);
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sqlFormat, dbType);
            if (stmtList == null || stmtList.size() == 0) {
                return map;
            }
            if (stmtList.size() > 1) {
                throw new CustomException(55000, "不支持多条SQL语句，请删除多余的SQL");
            }
            // 只取一条执行语句解析
            SQLStatement sqlStatement = stmtList.get(0);
            SchemaStatVisitor visitor = null;
            switch (dbType.toLowerCase()) {
                case "oracle":
                    visitor = new OracleSchemaStatVisitor();
                    break;
                case "openguass":
                case "postgresql":
                    visitor = new PGSchemaStatVisitor();
                    break;
                case "sqlserver":
                    visitor = new SQLServerSchemaStatVisitor();
                    break;
                case "db2":
                    visitor = new DB2SchemaStatVisitor();
                    break;
                case "clickhouse":
                    visitor = new ClickSchemaStatVisitor();
                    break;
                case "hive":
                    visitor = new HiveSchemaStatVisitor();
                    break;
                default:
                    visitor = new MySqlSchemaStatVisitor();
            }
            sqlStatement.accept(visitor);
            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            for (Map.Entry<TableStat.Name, TableStat> table : tables.entrySet()) {
                tableName.add(table.getKey().getName());
                operateName.add(table.getValue().toString());
            }

        } catch (Exception e) {
            log.error("解析SQL异常", e);
            throw new CustomException(55001, "SQL解析失败，如果SQL中包含特殊字符,请使用双引号括起来");
        }

        map.put("tableName", tableName);
        map.put("operateName", operateName);
        return map;
    }

    /**
     * 正则解析请求参数
     *
     * @param sql
     * @return
     */
    public static Set<String> parseParams(String sql) {
        Set<String> columns = new HashSet<>();
        try {
            sql = sqlCommentReplace(sql);
            Pattern pattern = Pattern.compile(PARAM_PATTERN);
            Matcher m = pattern.matcher(sql);
            while (m.find()) {
                columns.add(m.group(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return columns;
    }

    private static final List<String> DML_TYPE = Arrays.asList("select", "insert", "update", "delete");

    /**
     * 校验SQL的合法性
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static boolean checkSql(String sql, String dbType) {
        Map<String, Object> map = parseSqlTable(sql, dbType);
        if (map.containsKey("operateName")) {
            List<String> operate = (List<String>) map.get("operateName");
            if (operate == null || operate.size() == 0) {
                return false;
            }
            String type = operate.get(0);
            if (("elasticsearch".equals(dbType) || "mongodb".equals(dbType)) &&
                    ("update".equalsIgnoreCase(type) || "delete".equalsIgnoreCase(type))) {
                return false;
            }
            return DML_TYPE.contains(type.toLowerCase());
        }
        return false;
    }

    /**
     * 校验SQL的合法性
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static boolean previewCheckSql(String sql, String dbType) {
        Map<String, Object> map = parseSqlTable(sql, dbType);
        if (map.containsKey("operateName")) {
            List<String> operate = (List<String>) map.get("operateName");
            if (operate == null || operate.size() == 0) {
                return false;
            }
            String type = operate.get(0);
            return "select".equalsIgnoreCase(type);
        }
        return false;
    }

    /**
     * 校验并返回类型
     *
     * @param sql
     * @param dbType
     * @return
     */
    public static String getSqlType(String sql, String dbType) {
        Map<String, Object> map = parseSqlTable(sql, dbType);
        if (map.containsKey("operateName")) {
            List<String> operate = (List<String>) map.get("operateName");
            if (operate == null || operate.size() == 0) {
                return null;
            }
            String type = operate.get(0);
            if (("elasticsearch".equals(dbType) || "mongodb".equals(dbType)) &&
                    ("update".equalsIgnoreCase(type) || "delete".equalsIgnoreCase(type))) {
                return null;
            }
            if (DML_TYPE.contains(type.toLowerCase())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 过滤SQL中的参数
     * @param sql
     * @param reqParams
     * @param paramsConfig
     * @return
     */
    public static String filterParams(String sql, Object reqParams, List<BaseApiParam> paramsConfig) {
        if (paramsConfig == null || paramsConfig.size() == 0) {
            // 没有查询请求参数设置，清空当前的请求参数
            return sql;
        }
        // TODO 暂不支持数组集合
        if (reqParams instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) reqParams;
            for (BaseApiParam param : paramsConfig) {
                if (param.getRequired() && !paramMap.containsKey(param.getParamName())) {
                    // 必传参数没有传，则报错提示
                    throw new CustomException(52005, "请求参数缺少！");
                } else if (!param.getRequired() && !paramMap.containsKey(param.getParamName())) {
                    // 非比传参数，去掉SQL中相应的条件
                    sql = regexSql(sql, param.getParamName());
                }
            }
        }
        return sql;
    }

    /**
     * 正则过滤请求参数
     *
     * @param sql
     * @param paramName
     * @return
     */
    public static String regexSql(String sql, String paramName) {
        String group = "(?i)group.*?(?i)by.*?" + paramName + "}";
        String and = "(?i)and.*?" + paramName + "}";
        String where = "(?i)where.*?" + paramName + "}";
        String orderDesc = "(?i)order.*?(?i)by.*?" + paramName + "}.*?(?i)sc";
        String order = "(?i)order.*?(?i)by.*?" + paramName + "}";
        if (Pattern.compile(orderDesc).matcher(sql).find()) {
            sql = sql.replaceAll(orderDesc, "");
        } else if (Pattern.compile(order).matcher(sql).find()) {
            sql = sql.replaceAll(order, "");
        } else if (Pattern.compile(group).matcher(sql).find()) {
            sql = sql.replaceAll(group, "");
        } else if (Pattern.compile(and).matcher(sql).find()) {
            sql = sql.replaceAll(and, "");
        } else if (Pattern.compile(where).matcher(sql).find()) {
            sql = sql.replaceAll(where, "where 1=1");
        }
        return sql;
    }
    /**
     * 获取sql操作类型
     * @param sql
     * @return
     */
    public static String getOperateType(String sql){
        String[] sqls = sql.trim().split(" ");
        return sqls.length > 1 ? sqls[0].toLowerCase().trim() : "select";
    }

    /**
     * 解析jdbc url里面的类型
     * @param jdbcUrl
     * @return
     */
    public static String getDataSourceType(String jdbcUrl) {
        try {
            URI uri = new URI(jdbcUrl.replace("jdbc:", ""));
            // 获取数据库类型
            return uri.getScheme();
        }catch (Exception e) {
        }
        return "custom";
    }
}