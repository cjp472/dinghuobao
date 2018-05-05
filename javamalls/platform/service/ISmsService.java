package com.javamalls.platform.service;

import com.javamalls.platform.domain.Sms;

public abstract interface ISmsService {
    public abstract boolean save(Sms sms);

    public abstract boolean update(Sms sms);

    public abstract Sms getObjById(Long id);

}
