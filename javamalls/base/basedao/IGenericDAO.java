package com.javamalls.base.basedao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract interface IGenericDAO<T> {
    public abstract Object get(Serializable paramSerializable);

    public abstract void save(T paramT);

    public abstract void remove(Serializable paramSerializable);

    public abstract void update(T paramT);

    public abstract Object getBy(String paramString, Object paramObject);

    public abstract List executeNamedQuery(String paramString, Object[] paramArrayOfObject,
                                           int paramInt1, int paramInt2);

    public abstract List<T> find(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract List query(String paramString, Map paramMap, int paramInt1, int paramInt2);
    
    public abstract Long queryCount(String paramString, Map paramMap);

    public abstract int batchUpdate(String paramString, Object[] paramArrayOfObject);

    public abstract List executeNativeNamedQuery(String paramString);

    public abstract List executeNativeQuery(String paramString, Object[] paramArrayOfObject,
                                            int paramInt1, int paramInt2);

    public abstract int executeNativeSQL(String paramString);

    public abstract void flush();
}
