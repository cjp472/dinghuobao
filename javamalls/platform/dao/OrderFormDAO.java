package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.OrderForm;

@Repository("orderFormDAO")
public class OrderFormDAO extends GenericDAO<OrderForm> {
}
