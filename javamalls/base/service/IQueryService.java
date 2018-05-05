package com.javamalls.base.service;

import java.util.List;
import java.util.Map;

public abstract interface IQueryService {
    public abstract List query(String paramString, Map paramMap, int paramInt1, int paramInt2);

    public abstract List executeNativeQuery(String nnq, Map params, int begin, int max);

    public abstract int executeNativeSQL(String nnq);
}
