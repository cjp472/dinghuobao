package com.javamalls.platform.service.impl;

import com.javamalls.platform.dao.InterfaceLogDAO;
import com.javamalls.platform.domain.InterfaceLog;
import com.javamalls.platform.service.IInterfaceLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Created by Elise on 2016/7/14.
 */
@Service
@Transactional
public class InterfaceLogServiceImpl implements IInterfaceLogService {

    @Resource
    private InterfaceLogDAO interfaceLogDAO;
    @Override
    public void save(InterfaceLog log) {
        interfaceLogDAO.save(log);
    }
}
