package com.javamalls.platform.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.platform.domain.Sms;
import com.javamalls.platform.service.ISmsService;

@Service
@Transactional
public class SmsService implements ISmsService {
    @Resource(name = "smsDAO")
    private IGenericDAO<Sms> smsDAO;

    public Sms getObjById(Long id) {
        return (Sms) this.smsDAO.get(id);
    }

    public boolean save(Sms sms) {
        try {
            this.smsDAO.save(sms);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public boolean update(Sms sms) {
        try {
            this.smsDAO.update(sms);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

}
