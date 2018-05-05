package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Template;

@Repository("templateDAO")
public class TemplateDAO extends GenericDAO<Template> {
}
