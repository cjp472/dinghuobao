package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;
import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.EntryOrderDetail;

import java.util.List;

@Repository("entryOrderDetailDao")
public class EntryOrderDetailDao extends GenericDAO<EntryOrderDetail>{
}