package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.domain.CommonEntity;

/**
 * 
 * @author cjl
 * 货品表
 *Field		Type	Comment
idbig		int(20) NOT NULL货品表
createtime	datetime NULL
disabled	bit(1) NOT NULL
spec_combination	varchar(255) NULL规格id组合_结尾
goods_inventory	int(11) NOT NULL库存
goods_price		decimal(12,2) NOT NULL价格
spec_info	varchar(255) NULL规格组合后名称
bar_code	varchar(255) NULL条形码
self_code	varchar(255) NULL自编码
goods_id	bigint(20) NOT NULL商品id
status		int(10) NOT NULL有无规格 0：无，1：有
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods_item")
public class GoodsItem extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long        serialVersionUID    = 1L;

    private String                   spec_combination;                                         //规格id组合_结尾
    private int                      goods_inventory;                                          //库存
    private BigDecimal               goods_price;                                              //价格 销售价
    private String                   spec_info;                                                //规格组合后名称
    private String                   bar_code;                                                 //条形码
    private String                   self_code;                                                //自编码
    @ManyToOne
    @JsonIgnore
    private Goods                    goods;                                                    //对应商品
    private int                      status;                                                   //有无规格 0：无，1：有

    private BigDecimal               market_price;                                             //市场价，只是显示
    private BigDecimal               purchase_price;                                           //进货价
    private BigDecimal               dist_price;                                               //分销价
    /**
     * 价格策略规格列表
     */
    @OneToMany(mappedBy = "goods_item", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<StrategyGoodsItem>  strategyGoodsItems  = new ArrayList<StrategyGoodsItem>();
    /**
     * 规格所在仓库库存列表
     */
    @OneToMany(mappedBy = "goods_item", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<WarehouseGoodsItem> warehouseGoodsItems = new ArrayList<WarehouseGoodsItem>();

    private Integer                  step_price_state;                                         //是否设置阶梯报价：0未设置，1已设置

    @Transient
    private String                   color;                                                     //颜色
    @Transient
    private String                   size;                                                      //尺码

    public Integer getStep_price_state() {
        return step_price_state;
    }

    public void setStep_price_state(Integer step_price_state) {
        this.step_price_state = step_price_state;
    }

    /**
     * 分销价
     * @return
     */
    public BigDecimal getDist_price() {
        return dist_price;
    }

    /**
     * 分销价
     * @param dist_price
     */
    public void setDist_price(BigDecimal dist_price) {
        this.dist_price = dist_price;
    }

    /**
     * 规格所在仓库库存列表
     * @return
     */
    public List<WarehouseGoodsItem> getWarehouseGoodsItems() {
        return warehouseGoodsItems;
    }

    /**
     * 规格所在仓库库存列表
     * @param warehouseGoodsItems
     */
    public void setWarehouseGoodsItems(List<WarehouseGoodsItem> warehouseGoodsItems) {
        this.warehouseGoodsItems = warehouseGoodsItems;
    }

    /**
     * 价格策略规格列表
     */
    public List<StrategyGoodsItem> getStrategyGoodsItems() {
        return strategyGoodsItems;
    }

    /**
     * 价格策略规格列表
     */
    public void setStrategyGoodsItems(List<StrategyGoodsItem> strategyGoodsItems) {
        this.strategyGoodsItems = strategyGoodsItems;
    }

    /**
     * 市场价 
     * @return
     */
    public BigDecimal getMarket_price() {
        return market_price;
    }

    /**
     * 市场价
     * @param market_price
     */
    public void setMarket_price(BigDecimal market_price) {
        this.market_price = market_price;
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
     * 规格组合后名称
     * @return
     */
    public String getSpec_info() {
        return spec_info;
    }

    /**
     * 规格组合后名称
     * @param spec_info
     */
    public void setSpec_info(String spec_info) {
        this.spec_info = spec_info;
    }

    /**
     * 规格id组合_结尾
     * @return
     */
    public String getSpec_combination() {
        return spec_combination;
    }

    /**
     * 规格id组合_结尾
     * @param spec_combination
     */
    public void setSpec_combination(String spec_combination) {
        this.spec_combination = spec_combination;
    }

    /**
     * 库存
     * @return
     */
    public int getGoods_inventory() {
        return goods_inventory;
    }

    /**
     * 库存
     * @param goods_inventory
     */
    public void setGoods_inventory(int goods_inventory) {
        this.goods_inventory = goods_inventory;
    }

    /**
     * 价格
     * @return
     */
    public BigDecimal getGoods_price() {
        return goods_price;
    }

    /**
     * 价格
     * @param goods_price
     */
    public void setGoods_price(BigDecimal goods_price) {
        this.goods_price = goods_price;
    }

    /**
     * 条形码
     * @return
     */
    public String getBar_code() {
        return bar_code;
    }

    /**
     * 条形码
     * @param bar_code
     */
    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
    }

    /**
     * 自编码
     * @return
     */
    public String getSelf_code() {
        return self_code;
    }

    /**
     * 自编码
     * @param self_code
     */
    public void setSelf_code(String self_code) {
        this.self_code = self_code;
    }

    /**
     * 对应商品
     * @return
     */
    public Goods getGoods() {
        return goods;
    }

    /**
     * 对应商品
     * @param goods
     */
    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    /**
     * 有无规格 0：无，1：有
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 有无规格 0：无，1：有
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

}