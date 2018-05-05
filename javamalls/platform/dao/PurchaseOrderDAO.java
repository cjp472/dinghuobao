package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.PurchaseOrder;

@Repository("purchaseOrderDAO")
public class PurchaseOrderDAO extends GenericDAO<PurchaseOrder> {
}
