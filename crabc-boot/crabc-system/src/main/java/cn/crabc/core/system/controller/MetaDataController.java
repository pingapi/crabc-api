package cn.crabc.core.system.controller;

import cn.crabc.core.system.config.RedisConfig;
import cn.crabc.core.system.component.RedisClient;
import cn.crabc.core.system.service.system.IBaseDataSourceService;
import cn.crabc.core.system.util.Result;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
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
    private DataSourceDriver jdbcDataSource;
    @Autowired
    private RedisClient redisService;
    @Autowired
    private IBaseDataSourceService baseDataSourceService;

    /**
     * 数据源列表
     * @param name
     * @return
     */
    @GetMapping("/dataSources")
    public Result dataSources(String name){
        List<BaseDataSource> dataSourceList = baseDataSourceService.getDataSourceList(name);
        Map<String, List<BaseDataSource>> listMap = dataSourceList.stream().collect(Collectors.groupingBy(BaseDataSource::getDatasourceType));
        return Result.success(listMap);
    }

    @GetMapping("/schemas")
    public Result getSchemas(@RequestParam("datasourceId") String datasourceId, @RequestParam(defaultValue = "1",required = false) String catalog) {
        String key = RedisConfig.CACHE_METADATA_TABLE + datasourceId;
        List<Schema> schemas = redisService.getHashMap(key, catalog);
        if (schemas == null) {
            schemas = jdbcDataSource.getSchemas(datasourceId, catalog);
            if (schemas != null && !schemas.isEmpty()) {
                redisService.setHashMap(key, catalog, schemas);
            }
        }
        return Result.success(schemas);
    }

    @GetMapping("/tables")
    public Result getTables(@RequestParam("datasourceId") String datasourceId, @RequestParam("schema") String schema) {
        String key = RedisConfig.CACHE_METADATA_TABLE + datasourceId;
        List<Table> tables = redisService.getHashMap(key, schema);
        if (tables == null) {
            tables = jdbcDataSource.getTables(datasourceId, null, schema);
            if (tables != null && !tables.isEmpty()) {
                redisService.setHashMap(key, schema, tables);
            }
        }
        return Result.success(tables);
    }

    @GetMapping("/columns")
    public Result getColumns(@RequestParam("datasourceId") String datasourceId, @RequestParam("schema") String schema, @RequestParam("table") String table) {
        String key = RedisConfig.CACHE_METADATA_COLUMN + datasourceId + ":" + schema;
        List<Column> columns = redisService.getHashMap(key, table);
        if (columns == null) {
            columns = jdbcDataSource.getColumns(datasourceId, null, schema, table);
            if (columns != null && !columns.isEmpty()) {
                redisService.setHashMap(key, table, columns);
            }
        }
        return Result.success(columns);
    }
}
