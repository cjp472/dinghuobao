package com.javamalls.base.query.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IPageList<T> extends Serializable {
    public abstract List<T> getResult();

    public abstract void setQuery(IQuery<T> paramIQuery);

    public abstract int getPages();

    public abstract int getRowCount();

    public abstract int getCurrentPage();

    public abstract int getPageSize();

    public abstract void doList(int paramInt1, int paramInt2, String paramString1,
                                String paramString2);

    public abstract void doList(int paramInt1, int paramInt2, String paramString1,
                                String paramString2, Map<String, Object> paramMap);
}
