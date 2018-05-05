package com.javamalls.platform.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.javamalls.base.domain.CommonEntity;
@Entity
@Table(name = "jm_goodslabel_goods")
public class GoodslabelGoods extends CommonEntity {
	private static final long serialVersionUID = 1L;
	
	//商品标志
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goodslabel_id")
 	private GoodsLabel goodslabel; 
	
	@ManyToOne(fetch = FetchType.LAZY)
 	//商品
	@JoinColumn(name = "goods_id")
 	private Goods goods;
	/**
     * 获取商品标志
     */
	public GoodsLabel getGoodslabel() {
		return goodslabel;
	}
	/**
     * 设置商品标志
     */
	public void setGoodslabel(GoodsLabel goodslabel) {
		this.goodslabel = goodslabel;
	}
	/**
     * 获取商品
     */
	
	public Goods getGoods() {
		return goods;
	}
	/**
     * 设置商品
     */
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
 	
 }