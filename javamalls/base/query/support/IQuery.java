package com.javamalls.base.query.support;

import java.util.List;
import java.util.Map;

public abstract interface IQuery<T> {
    public abstract int getRows(String paramString);

    public abstract List<T> getResult(String paramString);

    public abstract void setFirstResult(int paramInt);

    public abstract void setMaxResults(int paramInt);

    public abstract void setParaValues(Map<String, Object> paramMap);

    public abstract List<T> getResult(String paramString, int paramInt1, int paramInt2);
}
