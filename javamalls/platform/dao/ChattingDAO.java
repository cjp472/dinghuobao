package com.javamalls.platform.dao;

import org.springframework.stereotype.Repository;

import com.javamalls.base.basedao.GenericDAO;
import com.javamalls.platform.domain.Chatting;

@Repository("chattingDAO")
public class ChattingDAO extends GenericDAO<Chatting> {
}
