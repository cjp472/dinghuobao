package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Sms;

@Repository("smsDAO")
public class SmsDAO extends GenericDAO<Sms> {
}
