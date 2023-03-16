package cn.crabc.core.admin.util;


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
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  SQL解析工具类
 *
 * @author yuqf
 */
public class SQLUtil {

    private final static Logger log = LoggerFactory.getLogger(SQLUtil.class);

    public final static String PARAM_PATTERN = "(?<=\\#\\{)(.+?)(?=\\})";


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
     * 解析查询字段
     * @param sql
     * @param dbType
     * @return
     */
    public static Set<String> analyzeSQL(String sql, String dbType){
        Set<String> aliasList = new HashSet<>();
        try {
            List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql,DbType.valueOf(dbType),false);
            if(sqlStatementList == null || sqlStatementList.isEmpty()){
                //throw new IllegalArgumentException("不是有效语句");
                return aliasList;
            }else if(sqlStatementList.size() > 1){
                //throw new IllegalArgumentException("检测到多条语句，请不用使用;分号");
                return aliasList;
            }

            if(!(sqlStatementList.get(0) instanceof SQLSelectStatement)){
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
                }else {
                    //自动生成虚拟别名
                    normalizedName = "_col"+i;
                }
                String alias = item.computeAlias();
                if(alias == null){
                    aliasList.add(normalizedName + "," + normalizedName);
                }else {
                    aliasList.add(normalizedName + ',' + alias);
                }
            }

            return aliasList;

        }catch (Exception e){
            log.info("SQL解析失败：{}",e.getMessage());
            return aliasList;
        }
    }
    public List<String> parseOperation(String sql, String dbType){

        List<String> tableNameList = new ArrayList<>();
        //格式化输出
        String sqlResult = SQLUtils.format(sql, dbType);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement sqlStatement : stmtList) {
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            sqlStatement.accept(visitor);
            Map<TableStat.Name, TableStat> tables = visitor.getTables();
            Set<TableStat.Name> tableNameSet = tables.keySet();
            for (TableStat.Name name : tableNameSet) {
                String tableName = name.getName();
                if (StringUtils.isNotBlank(tableName)) {
                    tableNameList.add(tableName);
                }
            }
        }
        return null;
    }

    /**
     * 正则解析请求参数
     * @param sql
     * @return
     */
    public static Set<String> parseParams(String sql) {
        Set<String> columns = new HashSet<>();
        try{
            sql = sqlCommentReplace(sql);
            Pattern pattern = Pattern.compile(PARAM_PATTERN);
            Matcher m = pattern.matcher(sql);
            while (m.find()) {
                columns.add(m.group(0));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return columns;
    }
}