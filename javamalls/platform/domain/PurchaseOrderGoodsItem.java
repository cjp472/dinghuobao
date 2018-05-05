package com.javamalls.platform.domain;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

/**
 * 采购入库详情表
 * @author cjl
 Field		Type		Comment
id			bigint(20) NOT NULL
createtime	datetime NULL
disabled	bit(1) NOT NULL
goods_name	varchar(255) NULL商品名称
spec_info	varchar(255) NULL规格值
units		varchar(255) NULL单位
number		int(11) NULL入库数量
purchase_price	decimal(12,2) NULL进货价
remark			varchar(255) NULL备注
goods_item_id	bigint(20) NULL货品id
purchase_order_id	bigint(20) NULL
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_purchase_order_goods_item")
public class PurchaseOrderGoodsItem extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
    private String		goods_name;//商品名称
    private String      spec_info;//规格值
    private String      units;//单位
    private int			number;//入库数量
    private BigDecimal  purchase_price;//进货价
    private String      remark;//备注
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsItem   goods_item;//货品id
    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseOrder purchaseOrder;//采购入库单
    /**
     * 商品名称
     * @return
     */
	public String getGoods_name() {
		return goods_name;
	}
	/**
	 * 商品名称
	 * @param goods_name
	 */
	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}
	/**
	 * 规格值
	 * @return
	 */
	public String getSpec_info() {
		return spec_info;
	}
	/**
	 * 规格值
	 * @param spec_info
	 */
	public void setSpec_info(String spec_info) {
		this.spec_info = spec_info;
	}
	/**
	 * 单位
	 * @return
	 */
	public String getUnits() {
		return units;
	}
	/**
	 * 单位
	 * @param units
	 */
	public void setUnits(String units) {
		this.units = units;
	}
	/**
	 * 入库数量
	 * @return
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * 入库数量
	 * @param number
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * 进货价
	 * @return
	 */
	public BigDecimal getPurchase_price() {
		return purchase_price;
	}
	/**
	 * 进货价
	 * @param purchase_price
	 */
	public void setPurchase_price(BigDecimal purchase_price) {
		this.purchase_price = purchase_price;
	}
	/**
	 * 备注
	 * @return
	 */
	public String getRemark() {
		return remark;
	}
	/**
	 * 备注
	 * @param remark
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 货品id
	 * @return
	 */
	public GoodsItem getGoods_item() {
		return goods_item;
	}
	/**
	 * 货品id
	 * @param goods_item
	 */
	public void setGoods_item(GoodsItem goods_item) {
		this.goods_item = goods_item;
	}
	/**
	 * 采购入库单
	 * @return
	 */
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}
	/**
	 * 采购入库单
	 * @param purchaseOrder
	 */
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    

}