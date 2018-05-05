package com.javamalls.platform.domain;
 
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
 
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;
/**
 * 
 * @author cjl
 * 价格策略表
Field			Type		Comment
id				bigint(20) NOT NULL策略表
createtime		datetime NULL
disabled		bit(1) NOT NULL
store_id		bigint(20) NOT NULL对应供应商id
title			varchar(255) NULL策略名称
description     varchar(255) NULL描述
status			int(11) NULL是否开启 0：禁用，1：启用
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_strategy")
public class Strategy extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long      serialVersionUID = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store  store;//对应供应商id
    private String title;//价格策略名称
    private String description;//描述
    private int    status;//是否开启 0：禁用，1：启用
    private int	   strategy_type;//类型 1：价格策略、2：按百分比
    private BigDecimal discount;//折扣  单位百分比
    
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    private List<StrategyGoodsItem>     strategyGoodsItems          = new ArrayList<StrategyGoodsItem>();//策略对应货品价格列表
    
    @OneToMany(mappedBy = "strategy", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REFRESH })
    private List<User>             user_list= new ArrayList<User>();//此策略下的客户列表
    
    
    @OneToMany(mappedBy = "strategy",   cascade = { javax.persistence.CascadeType.REFRESH })
    private List<UserStrategy>             userStrategys= new ArrayList<UserStrategy>();//此策略下的关联列表
    /**
     * 此策略下的关联列表
     * @return
     */
    public List<UserStrategy> getUserStrategys() {
		return userStrategys;
	}
    /**
     * 此策略下的关联列表
     * @param userStrategys
     */
	public void setUserStrategys(List<UserStrategy> userStrategys) {
		this.userStrategys = userStrategys;
	}
	/**
     * 类型 1：价格策略、2：按百分比
     * @return
     */
    public int getStrategy_type() {
		return strategy_type;
	}
    /**
     * 类型 1：价格策略、2：按百分比
     * @param strategy_type
     */
	public void setStrategy_type(int strategy_type) {
		this.strategy_type = strategy_type;
	}
	/**
	 * 折扣  单位百分比
	 * @return
	 */
	public BigDecimal getDiscount() {
		return discount;
	}
	/**
	 * 折扣  单位百分比
	 * @param discount
	 */
	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
	/**
     * 此策略下的客户列表
     * @return
     */
    public List<User> getUser_list() {
		return user_list;
	}
    /**
     * 此策略下的客户列表
     * @param user_list
     */
	public void setUser_list(List<User> user_list) {
		this.user_list = user_list;
	}
	/**
     * 描述
     * @return
     */
    public String getDescription() {
		return description;
	}
    /**
     * 描述
     * @param description
     */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
     * 策略对应货品价格列表
     * @return
     */
    public List<StrategyGoodsItem> getStrategyGoodsItems() {
		return strategyGoodsItems;
	}
    /**
     * 策略对应货品价格列表
     * @param strategyGoodsItems
     */
	public void setStrategyGoodsItems(List<StrategyGoodsItem> strategyGoodsItems) {
		this.strategyGoodsItems = strategyGoodsItems;
	}
	/**
     * 对应供应商
     */
    public Store getStore() {
		return store;
	}
    /**
     * 对应供应商
     * @param store
     */
	public void setStore(Store store) {
		this.store = store;
	}
	/**
     * 价格策略名称
     * @return
     */
	public String getTitle() {
		return title;
	}
	/**
	 * 价格策略名称
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 是否开启 0：禁用，1：启用
	 * @return
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * 是否开启 0：禁用，1：启用
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}
    
    
    
}
