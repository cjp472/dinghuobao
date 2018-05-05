package com.javamalls.platform.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.javamalls.base.domain.CommonEntity;
@Entity
@Table(name = "jm_entry_order_detail")
public class EntryOrderDetail extends CommonEntity {
 
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name = "createuser")
 	private      User 							createuser; 
 	//出入单
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name="entry_order_id")
 	private      EntryOrder 					entryOrder; 
 	//商品
	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name="goods_id")
 	private 	 Goods 							goods; 
 	//出货数量
 	private 	Integer 						number;
 	//规格
 	private 	String  						spec_info;
 	//移入入库商的商品Id
 	@ManyToOne(fetch = FetchType.LAZY)
 	@JoinColumn(name="new_goods_id")
 	private 	 Goods 							new_goods; 
	public User getCreateuser() {
		return createuser;
	}
	public void setCreateuser(User createuser) {
		this.createuser = createuser;
	}
	public EntryOrder getEntryOrder() {
		return entryOrder;
	}
	public void setEntryOrder(EntryOrder entryOrder) {
		this.entryOrder = entryOrder;
	}
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getSpec_info() {
		return spec_info;
	}
	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}
	public Goods getNew_goods() {
		return new_goods;
	}
	public void setNew_goods(Goods new_goods) {
		this.new_goods = new_goods;
	}
	
 }