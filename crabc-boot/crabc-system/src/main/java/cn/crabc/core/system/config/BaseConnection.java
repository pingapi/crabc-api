package cn.crabc.core.system.config;

import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.SQLException;

public class BaseConnection extends DruidPooledConnection {
    public BaseConnection(DruidConnectionHolder holder) {
        super(holder);
    }
    @Override
    public void setSchema(String schema) throws SQLException {
        this.conn.setSchema(schema);
//        if (this.holder.statementPool != null) {
//            this.holder.clearStatementCache();
//        }
    }
}
