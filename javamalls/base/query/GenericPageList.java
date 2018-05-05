package com.javamalls.base.query;

import java.util.Map;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.query.support.IQuery;
import com.javamalls.base.query.support.IQueryObject;

public class GenericPageList extends PageList {
    private static final long serialVersionUID = 6730593239674387757L;
    protected String          scope;
    protected Class           cls;

    public GenericPageList(Class cls, IQueryObject queryObject, IGenericDAO dao) {
        this(cls, queryObject.getQuery(), queryObject.getParameters(), dao);
    }

    public GenericPageList(Class cls, String scope, Map paras, IGenericDAO dao) {
        this.cls = cls;
        this.scope = scope;
        IQuery query = new GenericQuery(dao);
        query.setParaValues(paras);
        setQuery(query);
    }

    public void doList(int currentPage, int pageSize) {
        String totalSql = "select COUNT(obj) from " + this.cls.getName() + " obj where "
                          + this.scope;
        super.doList(pageSize, currentPage, totalSql, this.scope);
    }
    
    public void doNolastList(int currentPage, int pageSize) {
        String totalSql = "select COUNT(obj) from " + this.cls.getName() + " obj where "
                          + this.scope;
        super.doNoLastList(pageSize, currentPage, totalSql, this.scope,null);
    }
    
    public int getCount() {
        String totalSql = "select COUNT(obj) from " + this.cls.getName() + " obj where "
                          + this.scope;
        return super.doRowCount(totalSql);
    }
}
