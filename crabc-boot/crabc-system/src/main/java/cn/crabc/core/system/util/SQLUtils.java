package cn.crabc.core.system.util;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.SimpleNode;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  SQL解析工具类
 *
 * @author yuqf
 */
public class SQLUtils {

    private final static String PATTERN = "\\#\\{\\s*(\\w+|[\\w-]+)\\s*\\}";

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
     * 临时转换sql供sql编译通过
     *
     * @param sql sql
     * @return sql
     */
    public static String sqlParamReplaceTemp(String sql) {
        sql = sqlCommentReplace(sql);
        Pattern r = Pattern.compile(PATTERN);
        Matcher m = r.matcher(sql);
        while (m.find()) {
            sql = sql.replace(m.group(), "1");
        }
        return sql;
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

    /**
     * 解析sql的查询列
     *
     * @param sql
     * @return
     */
    public static Set<String> parseColumns(String sql) {
        sql = sqlCommentReplace(sql);

        if (sql.contains("#{")) {
            sql = sqlParamReplaceTemp(sql);
        }
        Set<String> result = new HashSet<>();
        try {
            CCJSqlParserManager pm = new CCJSqlParserManager();
            Statement statement = pm.parse(new StringReader(sql));
            if (statement instanceof Select) {
                Select selectStatement = (Select) statement;
                SelectBody selectBody = selectStatement.getSelectBody();
                List<PlainSelect> plainSelects = new ArrayList<>();
                if (selectBody instanceof SetOperationList){
                    SetOperationList list = (SetOperationList) selectBody;
                    for(SelectBody sb : list.getSelects()){
                        PlainSelect plainSelect = (PlainSelect) sb;
                        plainSelects.add(plainSelect);
                    }
                }else if(selectBody instanceof PlainSelect){
                    PlainSelect plainSelect = (PlainSelect) selectBody;
                    plainSelects.add(plainSelect);
                }
                for(PlainSelect plainSelect : plainSelects){
                    List<SelectItem> selectItemlist = plainSelect.getSelectItems();
                    SelectItem selectItem = null;
                    SelectExpressionItem selectExpressionItem = null;
                    AllTableColumns allTableColumns = null;
                    Alias alias = null;
                    SimpleNode node = null;
                    if (selectItemlist != null) {
                        for (int i = 0; i < selectItemlist.size(); i++) {
                            selectItem = selectItemlist.get(i);
                            if (selectItem instanceof SelectExpressionItem) {
                                String columnName = "";
                                selectExpressionItem = (SelectExpressionItem) selectItemlist.get(i);
                                alias = selectExpressionItem.getAlias();
                                node = selectExpressionItem.getExpression().getASTNode();
                                // 针对复杂函数解析不到node,直接使用别名作为返回参数
                                if (node == null && alias != null) {
                                    columnName = "_col" + i + "," + alias.getName();
                                } else if (node == null) {
                                    columnName = "_col" + i;
                                } else {
                                    Object value = node.jjtGetValue();

                                    if (value instanceof Column) {
                                        columnName = ((Column) value).getColumnName();
                                    } else if (value instanceof Function) {
                                        //columnName = ((Function) value).toString();
                                        columnName = "_col" + i;
                                    } else {
                                        // 增加对select 'aaa' from table; 的支持
                                        columnName = value.toString();
                                        columnName = columnName.replace("'", "");
                                        columnName = columnName.replace("\"", "");
                                    }
                                    columnName = columnName + "," + (alias == null ? columnName : alias.getName());
                                }
                                //为重复字段添加尾缀
//                            int suffix = 1;
//                            String columnNameDuplicRm = columnName;
//                            while (result.contains(columnNameDuplicRm)){
//                                columnNameDuplicRm = columnName + suffix;
//                                suffix++;
//                            }
//                            result.add(columnNameDuplicRm);

                                result.add(columnName);
                            } else if (selectItem instanceof AllTableColumns) {
                                allTableColumns = (AllTableColumns) selectItemlist.get(i);
                                result.add(allTableColumns.toString());
                            } else {
                                result.add(selectItem.toString());
                            }

                        }
                    }
                }
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return result;
    }
}