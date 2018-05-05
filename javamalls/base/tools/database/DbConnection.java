package com.javamalls.base.tools.database;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**数据库连接
 *                       
 * @Filename: DbConnection.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Repository
public class DbConnection {
    @Autowired
    private DataSource                          dataSource;
    public static final ThreadLocal<Connection> thread = new ThreadLocal();

    public Connection getConnection() {
        Connection conn = (Connection) thread.get();
        if (conn == null) {
            try {
                conn = this.dataSource.getConnection();
                thread.set(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public void closeAll() {
        try {
            Connection conn = (Connection) thread.get();
            if (conn != null) {
                conn.close();
                thread.set(null);
            }
        } catch (Exception e) {
            try {
                throw e;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }
}
