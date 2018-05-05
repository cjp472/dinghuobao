package com.javamalls.platform.service;

import com.javamalls.platform.domain.SysConfig;

public abstract interface ISysConfigService {
    public abstract boolean save(SysConfig paramSysConfig);

    public abstract boolean delete(SysConfig paramSysConfig);

    public abstract boolean update(SysConfig paramSysConfig);

    public abstract SysConfig getSysConfig();
}
