package cn.crabc.core.app.driver.jdbc;

import cn.crabc.core.app.config.JdbcDataSourceRouter;
import cn.crabc.core.spi.DataSourceDriver;
import cn.crabc.core.spi.bean.BaseDataSource;
import cn.crabc.core.spi.bean.Column;
import cn.crabc.core.spi.bean.Schema;
import cn.crabc.core.spi.bean.Table;
import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 默认通用方法实现类
 *
 * @author yuqf
 */
public abstract class DefaultDataSourceDriver implements DataSourceDriver<Map<String, Object>> {
    Logger log =  LoggerFactory.getLogger(DefaultDataSourceDriver.class);

    @Override
    public Integer test(BaseDataSource baseDataSource) {
        Connection connection = null;
        try {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(baseDataSource.getJdbcUrl());
            dataSource.setUsername(baseDataSource.getUsername());
            dataSource.setPassword(baseDataSource.getPassword());
            dataSource.setMinIdle(1);
            dataSource.setMaxIdle(1);
            dataSource.setMaxActive(1);
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            return 0;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return 1;
    }

    @Override
    public void init(BaseDataSource baseDataSource) {
        if (JdbcDataSourceRouter.exist(baseDataSource.getDatasourceId())) {
            return;
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(baseDataSource.getJdbcUrl());
        dataSource.setUsername(baseDataSource.getUsername());
        dataSource.setPassword(baseDataSource.getPassword());
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);
        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(6000);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(5000);
        dataSource.setKeepAlive(true);
        //  配置一个连接在池中最小生存的时间，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(600000);
        dataSource.setMaxEvictableIdleTimeMillis(900000);
        // dataSource.setValidationQuery("select 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        // dataSource.setFilters("stat,wall");

        if (baseDataSource.getDatasourceType() != null) {
            dataSource.setDbType(baseDataSource.getDatasourceType());
        }
        JdbcDataSourceRouter.setDataSource(baseDataSource.getDatasourceId(), dataSource);
    }

    @Override
    public void destroy() {
    }

    @Override
    public List<String> getCatalogs(String dataSourceId) {
        List<String> catalogs = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        DataSource dataSource = JdbcDataSourceRouter.getDataSource(dataSourceId);
        try {
            connection = dataSource.getConnection();
            resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String catalogName = resultSet.getString(1);
                catalogs.add(catalogName);
            }
        } catch (Exception e) {
            throw new IllegalStateException("query catalogs is error", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return catalogs;
    }

    @Override
    public List<Schema> getSchemas(String dataSourceId, String catalog) {
        List<Schema> schemas = new ArrayList<>();
        Connection connection = null;
        ResultSet resultSet = null;
        DataSource dataSource = JdbcDataSourceRouter.getDataSource(dataSourceId);
        try {
            connection = dataSource.getConnection();
            String dbType = "mysql";
            if (dataSource instanceof DruidDataSource){
                DruidDataSource druidDataSource  = (DruidDataSource) dataSource;
                dbType = druidDataSource.getDbType();
            }
            if ("mysql".equalsIgnoreCase(dbType)) {
                resultSet = connection.getMetaData().getCatalogs();
            } else {
                resultSet = connection.getMetaData().getSchemas(catalog, null);
            }
            while (resultSet.next()) {
                Schema schema = new Schema();
                String schemaName = resultSet.getString(1);
                schema.setSchema(schemaName);
                schema.setCatalog(catalog);
//                schema.setDatasourceId(dataSourceId);

                schemas.add(schema);
            }
        } catch (Exception e) {
            throw new IllegalStateException("query schemas is error", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return schemas;
    }

    @Override
    public List<Table> getTables(String dataSourceId, String catalog, String schema) {
        List<Table> tables = new ArrayList<>();
        DataSource dataSource = JdbcDataSourceRouter.getDataSource(dataSourceId);
        String[] tableType = {"TABLE", "VIEW"};
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            resultSet = connection.getMetaData().getTables(catalog, schema, null, tableType);
            while (resultSet.next()) {
                Table table = new Table();
                table.setTableName(resultSet.getString("TABLE_NAME"));
                table.setRemarks(resultSet.getString("REMARKS"));
                table.setTableType(resultSet.getString("TABLE_TYPE"));
                table.setCatalog(resultSet.getString("TABLE_CAT"));
               // table.setDatasourceId(dataSourceId);
                table.setSchema(schema);
                tables.add(table);
            }
        } catch (Exception e) {
            throw new IllegalStateException("query tables is error", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return tables;
    }

    @Override
    public List<Column> getColumns(String dataSourceId, String catalog, String schema, String table) {
        List<Column> columns = new ArrayList<>();
        DataSource dataSource = JdbcDataSourceRouter.getDataSource(dataSourceId);
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            resultSet = connection.getMetaData().getColumns(catalog, schema, table, null);
            while (resultSet.next()) {
                Column column = new Column();
                column.setColumnName(resultSet.getString("COLUMN_NAME"));
                column.setRemarks(resultSet.getString("REMARKS"));
                column.setColumnType(resultSet.getString("TYPE_NAME"));
                column.setColumnSize(resultSet.getString("COLUMN_SIZE"));
                column.setColumnDefault(resultSet.getString("COLUMN_DEF"));
                column.setDecimalDigits(resultSet.getString("DECIMAL_DIGITS"));
                column.setCatalog(resultSet.getString("TABLE_CAT"));

                //column.setDatasourceId(dataSourceId);
                column.setSchema(schema);
                column.setTableName(table);
                columns.add(column);
            }
        } catch (Exception e) {
            throw new IllegalStateException("query columns is error", e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return columns;
    }
}
