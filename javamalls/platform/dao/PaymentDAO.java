package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Payment;

@Repository("paymentDAO")
public class PaymentDAO extends GenericDAO<Payment> {
}
