package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;
import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.EntryOrder;

import java.util.List;

@Repository("entryOrderDao")
public class EntryOrderDao extends GenericDAO<EntryOrder>{
}