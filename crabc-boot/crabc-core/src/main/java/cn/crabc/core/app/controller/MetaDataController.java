package cn.crabc.core.app.controller;

import cn.crabc.core.app.entity.BaseDatasource;
import cn.crabc.core.app.service.system.IBaseDataSourceService;
import cn.crabc.core.app.util.Result;
import cn.crabc.core.app.util.SQLUtil;
import cn.crabc.core.datasource.constant.BaseConstant;
import cn.crabc.core.datasource.driver.DataSourceManager;
import cn.crabc.core.spi.MetaDataMapper;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 元数据管理
 *
 * @author yuqf
 */
@RestController
@RequestMapping("/api/box/sys/metadata/")
public class MetaDataController {

    @Autowired
    private DataSourceManager dataSourceManager;
    @Autowired
    private IBaseDataSourceService baseDataSourceService;

    /**
     * 数据源列表
     *
     * @param name
     * @return
     */
    @GetMapping("/dataSources")
    public Result dataSources(String name) {
        List<BaseDatasource> dataSourceList = baseDataSourceService.getDataSourceList(name);
        Map<String, List<BaseDatasource>> listMap = dataSourceList.stream().collect(Collectors.groupingBy(BaseDatasource::getDatasourceType));
        return Result.success(listMap);
    }

    //@Cacheable(cacheNames = "schemaCache", cacheManager = "metaDataManager", key = "#datasourceId")
    @GetMapping("/schemas")
    public Result getSchemas(@RequestParam("datasourceId") String datasourceId,String datasourceType,  String catalog) {
        MetaDataMapper metaData = dataSourceManager.getMetaData(datasourceId);
        List schemas;
        if (BaseConstant.CATALOG_DATA_SOURCE.contains(datasourceType)){
            schemas = metaData.getCatalogs(datasourceId);
        }else{
            schemas = metaData.getSchemas(datasourceId, catalog);
        }
        return Result.success(schemas);
    }

    //@Cacheable(cacheNames = "tableCache", cacheManager = "metaDataManager", key = "#datasourceId+'_'+#schema")
    @GetMapping("/tables")
    public Result getTables(@RequestParam("datasourceId") String datasourceId, @RequestParam("schema") String schema,String datasourceType) {
        MetaDataMapper metaData = dataSourceManager.getMetaData(datasourceId);
        List<Table> tables;
        if (BaseConstant.CATALOG_DATA_SOURCE.contains(datasourceType)){
            tables = metaData.getTables(datasourceId, schema, null);
        }else{
            tables = metaData.getTables(datasourceId, null, schema);
        }
        return Result.success(tables);
    }

    //@Cacheable(cacheNames = "columnsCache", cacheManager = "metaDataManager", key = "#datasourceId+'_'+#schema+'_'+#table")
    @GetMapping("/columns")
    public Result getColumns(@RequestParam("datasourceId") String datasourceId, @RequestParam("schema") String schema, @RequestParam("table") String table, String datasourceType) {
        return Result.success(this.getColumnsList(datasourceId, schema, table, datasourceType));
    }

    /**
     * 字段列表
     * @return
     */
    private List<Column> getColumnsList(String datasourceId, String schema, String table, String datasourceType) {
        MetaDataMapper metaData = dataSourceManager.getMetaData(datasourceId);
        List<Column> columns;
        if (BaseConstant.CATALOG_DATA_SOURCE.contains(datasourceType)){
            columns = metaData.getColumns(datasourceId, schema, null, table);
        }else{
            columns = metaData.getColumns(datasourceId, null, schema, table);
        }
        return columns;
    }
    /**
     * 脚本模版
     * @param datasourceId
     * @param schema
     * @param table
     * @return
     */
    @GetMapping("/script")
    public Result getScript(String datasourceId, String schema, String table,String datasourceType) {
        List<String> columnName = new ArrayList<>();
        if (table == null || "".equals(table)) {
            table = "t_user";
            columnName.add("id");
            columnName.add("user_name");
            columnName.add("create_time");
            columnName.add("create_by");
        }else{
            List<Column> columns = this.getColumnsList(datasourceId, schema, table, datasourceType);
            columnName = columns.stream().map(Column::getColumnName).collect(Collectors.toList());
        }
        return Result.success(getSqlTemplate(columnName, table));
    }

    /**
     * 通用SQL拼接
     * @param columnName
     * @param table
     * @return
     */
    private List<Map<String,Object>> getSqlTemplate(List<String> columnName, String table){
        String Str = String.join(",",columnName);
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> select = new HashMap<>();
        String sql = "select " + Str + " from " + table;
        select.put("type", "Select");
        select.put("value", sql);
        list.add(select);

        Map<String,Object> insert = new HashMap<>();
        StringBuffer bufferSql = new StringBuffer();
        bufferSql.append("insert into " +table +" values (");
        for (int i = 0; i < columnName.size(); i++) {
            if (i == columnName.size() - 1){
                bufferSql.append("#{"+ SQLUtil.underlineToCamel(columnName.get(i))+"})");
            }else{
                bufferSql.append("#{"+SQLUtil.underlineToCamel(columnName.get(i))+"}, ");
            }
        }
        insert.put("type", "Insert");
        insert.put("value", bufferSql);
        list.add(insert);

        Map<String,Object> update = new HashMap<>();
        StringBuffer updateSql = new StringBuffer();
        updateSql.append("update " +table +" set ");
        for (int i = 0; i < columnName.size(); i++) {
            if (i == columnName.size() - 1){
                updateSql.append(columnName.get(i)+ "= #{"+SQLUtil.underlineToCamel(columnName.get(i))+"}");
            }else{
                updateSql.append(columnName.get(i)+ "= #{"+SQLUtil.underlineToCamel(columnName.get(i))+"},");
            }
        }
        updateSql.append(" where id=#{id}");
        update.put("type", "Update");
        update.put("value", updateSql);
        list.add(update);

        Map<String,Object> delete = new HashMap<>();
        String deleteSql = "delete from " + table +" where id=#{id}";
        delete.put("type", "Delete");
        delete.put("value", deleteSql);
        list.add(delete);
        return list;
    }
}
