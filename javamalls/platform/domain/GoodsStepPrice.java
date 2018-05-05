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

/**商品阶梯报价
 *                       
 * @Filename: Goods.java
 * @Version: 1.0
 * @Author: hanmj
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_step_price")
public class GoodsStepPrice extends CommonEntity {
	private static final long serialVersionUID = 1L;
	private Integer goods_item_id;
	private Integer begin_num;//数量区间起始数量
	private Integer end_num;//数量区间结束数量
	private BigDecimal price;
	public Integer getGoods_item_id() {
		return goods_item_id;
	}
	public void setGoods_item_id(Integer goods_item_id) {
		this.goods_item_id = goods_item_id;
	}
	public Integer getBegin_num() {
		return begin_num;
	}
	public void setBegin_num(Integer begin_num) {
		this.begin_num = begin_num;
	}
	public Integer getEnd_num() {
		return end_num;
	}
	public void setEnd_num(Integer end_num) {
		this.end_num = end_num;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
}