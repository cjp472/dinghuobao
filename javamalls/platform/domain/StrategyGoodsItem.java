package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.javamalls.base.domain.CommonEntity;

/**
 *价格策略货品表
 * @author cjl
 * Field		Type	Comment
id				bigint(20) NOT NULL策略货品表
createtime		datetime NULL
disabled		bit(1) NOT NULL
strategy_id		bigint(20) NOT NULL策略id
goods_id		bigint(20) NOT NULL商品id
goods_item_id	bigint(20) NOT NULL货品id
price			decimal(12,2) NULL价格
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_strategy_goods_item")
public class StrategyGoodsItem extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
    @ManyToOne(fetch = FetchType.LAZY)
    private Strategy  strategy;//策略id
    @ManyToOne(fetch = FetchType.LAZY)
    private Goods  goods;//商品id
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsItem  goods_item;//货品id
    private BigDecimal price;//价格
    /**
     * 策略
     * @return
     */
	public Strategy getStrategy() {
		return strategy;
	}
	/**
	 * 策略
	 * @param strategy
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	/**
	 * 商品
	 * @return
	 */
	public Goods getGoods() {
		return goods;
	}
	/**
	 * 商品
	 * @param goods
	 */
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	/**
	 * 货品
	 * @return
	 */
	public GoodsItem getGoods_item() {
		return goods_item;
	}
	/**
	 * 货品
	 * @param goodsItem
	 */
	public void setGoods_item(GoodsItem goods_item) {
		this.goods_item = goods_item;
	}
	/**
	 * 价格
	 * @return
	 */
	public BigDecimal getPrice() {
		return price;
	}
	/**
	 * 价格
	 * @param price
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
    
    
    

}