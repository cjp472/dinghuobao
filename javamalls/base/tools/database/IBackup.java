package com.javamalls.base.tools.database;

import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**备份接口
 *                       
 * @Filename: IBackup.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
public abstract interface IBackup {
    public abstract boolean createSqlScript(HttpServletRequest paramHttpServletRequest,
                                            String paramString1, String paramString2,
                                            String paramString3, String paramString4)
                                                                                     throws Exception;

    public abstract boolean executSqlScript(String paramString) throws Exception;

    public abstract List<String> getTables() throws Exception;

    public abstract String queryDatabaseVersion();

    public abstract boolean execute(String paramString);

    public abstract boolean export(String paramString1, String paramString2);

    public abstract ResultSet query(String paramString);
}
