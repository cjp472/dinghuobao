package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Document;

@Repository("documentDAO")
public class DocumentDAO extends GenericDAO<Document> {
}
