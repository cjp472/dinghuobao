package com.javamalls.platform.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;
/**
 * 分销商的价格策略关联表
 * @author cjl
 *
 */
@Entity
@Table(name = "jm_user_strategy")
public class UserStrategy extends CommonEntity {
	private static final long serialVersionUID = -3023507474907793407L;
	@ManyToOne()
	@JoinColumn(name = "user_id")
 	private User user; //分销商用户id
 	private Long             store_id;//店铺(冗余)
	@ManyToOne()
	@JoinColumn(name = "strategy_id")
 	private Strategy strategy; //价格策略
	/**
	 * 分销商用户id
	 * @return
	 */
	public User getUser() {
		return user;
	}
	/**
	 * 分销商用户id
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * 店铺(冗余)
	 * @return
	 */
	public Long getStore_id() {
		return store_id;
	}
	/**
	 * 店铺(冗余)
	 * @param store_id
	 */
	public void setStore_id(Long store_id) {
		this.store_id = store_id;
	}
	/**
	 * 价格策略
	 * @return
	 */
	public Strategy getStrategy() {
		return strategy;
	}
	/**
	 * 价格策略
	 * @param strategy
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	
	 
	
 }