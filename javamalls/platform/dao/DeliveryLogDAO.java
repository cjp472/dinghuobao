package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.DeliveryLog;

@Repository("deliveryLogDAO")
public class DeliveryLogDAO extends GenericDAO<DeliveryLog> {
}
