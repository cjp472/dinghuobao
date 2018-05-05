package com.javamalls.base.query;

import java.util.List;
import java.util.Map;

import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQuery;

public class PageList implements IPageList {
    private int    rowCount;
    private int    pages;
    private int    currentPage;
    private int    pageSize;
    private List   result;
    private IQuery query;

    public PageList() {
    }

    public PageList(IQuery q) {
        this.query = q;
    }

    public void setQuery(IQuery q) {
        this.query = q;
    }

    public List getResult() {
        return this.result;
    }

    public void doList(int pageSize, int pageNo, String totalSQL, String queryHQL) {
        doList(pageSize, pageNo, totalSQL, queryHQL, null);
    }

    public void doList(int pageSize, int pageNo, String totalSQL, String queryHQL, Map params) {
        List rs = null;
        this.pageSize = pageSize;
        if (params != null) {
            this.query.setParaValues(params);
        }
        int total = this.query.getRows(totalSQL);
        if (total > 0) {
            this.rowCount = total;
            this.pages = ((this.rowCount + pageSize - 1) / pageSize);
            int intPageNo = pageNo > this.pages ? this.pages : pageNo;
            if (intPageNo < 1) {
                intPageNo = 1;
            }
            this.currentPage = intPageNo;
            if (pageSize > 0) {
                this.query.setFirstResult((intPageNo - 1) * pageSize);
                this.query.setMaxResults(pageSize);
            }
            rs = this.query.getResult(queryHQL);
        }
        this.result = rs;
    }

    public void doNoLastList(int pageSize, int pageNo, String totalSQL, String queryHQL, Map params) {
        List rs = null;
        this.pageSize = pageSize;
        if (params != null) {
            this.query.setParaValues(params);
        }
        int total = this.query.getRows(totalSQL);//总数量
        if (total > 0) {
            this.rowCount = total;
            this.pages = ((this.rowCount + pageSize - 1) / pageSize);//页数
            int intPageNo = pageNo/* > this.pages ? this.pages : pageNo*/;
            if (intPageNo < 1) {
                intPageNo = 1;
            }
            this.currentPage = intPageNo;
            if (pageSize > 0) {
                this.query.setFirstResult((intPageNo - 1) * pageSize);
                this.query.setMaxResults(pageSize);
            }
            rs = this.query.getResult(queryHQL);
        }
        this.result = rs;
    }
    
    public int doRowCount(String totalSQL) {
        
        int total = this.query.getRows(totalSQL);//总数量
        return total;
    }
    
    public int getPages() {
        return this.pages;
    }

    public int getRowCount() {
        return this.rowCount;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getNextPage() {
        int p = this.currentPage + 1;
        if (p > this.pages) {
            p = this.pages;
        }
        return p;
    }

    public int getPreviousPage() {
        int p = this.currentPage - 1;
        if (p < 1) {
            p = 1;
        }
        return p;
    }
}
