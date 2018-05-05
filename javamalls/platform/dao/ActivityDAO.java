package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Activity;

@Repository("activityDAO")
public class ActivityDAO extends GenericDAO<Activity> {
}
