package cn.crabc.core.datasource.driver.jdbc;

import cn.crabc.core.spi.bean.BaseDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * DuckDB非池化数据源，适配嵌入式文件库不适合常驻连接池的场景。
 */
public class DuckDbDataSource implements DataSource {

    private static final String DRIVER_CLASS_NAME = "org.duckdb.DuckDBDriver";
    private static final String READ_ONLY_PROPERTY = "duckdb.read_only";

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final String datasourceType;
    private final boolean readOnly;
    private PrintWriter logWriter;
    private int loginTimeout;

    public DuckDbDataSource(BaseDataSource dataSource) {
        this.jdbcUrl = dataSource.getJdbcUrl();
        this.username = dataSource.getUsername();
        this.password = dataSource.getPassword();
        this.datasourceType = dataSource.getDatasourceType();
        this.readOnly = resolveReadOnly(dataSource.getExtend());
        loadDriver();
    }

    /**
     * 每次调用都新建物理连接，不保留池化连接，避免DuckDB文件库被连接池长时间占用。
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, buildProperties(username, password));
    }

    /**
     * DuckDB本身不依赖用户名密码，接口仍保留JDBC标准签名用于兼容调用方。
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, buildProperties(username, password));
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatasourceType() {
        return datasourceType;
    }

    /**
     * 只读模式来自extend中的duckdb.read_only配置，默认保持可读写。
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        this.loginTimeout = seconds;
    }

    @Override
    public int getLoginTimeout() {
        return loginTimeout;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("DuckDB JDBC driver does not expose parent logger");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw new SQLException("DuckDbDataSource cannot unwrap to " + iface.getName());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    /**
     * DriverManager连接属性只放DuckDB识别的只读标识和非空账号信息。
     */
    private Properties buildProperties(String username, String password) {
        Properties properties = new Properties();
        if (readOnly) {
            properties.setProperty(READ_ONLY_PROPERTY, "true");
        }
        if (username != null && !StringUtils.isBlank(username)) {
            properties.setProperty("user", username);
        }
        if (password != null && !StringUtils.isBlank(password)) {
            properties.setProperty("password", password);
        }
        return properties;
    }

    /**
     * extend当前没有强类型结构，先按明确的duckdb.read_only=true语义解析，避免误读其他扩展字段。
     */
    private boolean resolveReadOnly(String extend) {
        if (extend == null || StringUtils.isBlank(extend)) {
            return false;
        }
        String normalized = extend.replace("\"", "")
                .replace("'", "")
                .replace(" ", "")
                .toLowerCase();
        return normalized.contains(READ_ONLY_PROPERTY + ":true")
                || normalized.contains(READ_ONLY_PROPERTY + "=true");
    }

    /**
     * 显式加载驱动，避免部分运行环境未通过SPI自动注册DuckDB驱动。
     */
    private void loadDriver() {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("DuckDB JDBC驱动未加载", e);
        }
    }
}
