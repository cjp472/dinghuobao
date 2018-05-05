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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.javamalls.base.domain.CommonEntity;

/**商品
 *                       
 * @Filename: Goods.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_goods")
public class Goods extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long       serialVersionUID  = 1L;
    private String                  seo_keywords;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  seo_description;
    private String                  goods_name;
    @Column(precision = 12, scale = 2)
    private BigDecimal              goods_price;//市场价
    @Column(precision = 12, scale = 2)
    private BigDecimal				dist_price;//分销价
    @Column(precision = 12, scale = 2)
    private BigDecimal              deposit_price;
    @Column(precision = 12, scale = 2)
    private BigDecimal              storeempty_price;
    @Column(precision = 12, scale = 2)
    private BigDecimal              store_price;//销售价
    private int                     goods_inventory;
    private String                  inventory_type;
    private int                     goods_salenum;
    private String                  goods_serial;
    @Column(precision = 12, scale = 2)
    private BigDecimal              goods_weight;
    @Column(precision = 12, scale = 2)
    private BigDecimal              goods_volume;
    private String                  goods_fee;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  goods_details;
    private boolean                 store_recommend;
    private Date                    store_recommend_time;
    private boolean                 goods_recommend;
    private int                     goods_click;
    @Column(columnDefinition = "int default 0")
    private int                     goods_collect;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store                   goods_store;
    private int                     goods_status;//0上架，1仓库中，-1已下架，-2违规下架，-9逻辑删除
    private Date                    goods_seller_time;
    private int                     goods_transfee;
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsClass              gc;
    @ManyToOne(cascade = { javax.persistence.CascadeType.REMOVE })
    private Accessory               goods_main_photo;
    @ManyToMany
    @JoinTable(name = "jm_goods_pic", joinColumns = { @javax.persistence.JoinColumn(name = "goods_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "photo_id") })
    private List<Accessory>         goods_photos      = new ArrayList<Accessory>();
    @ManyToMany
    @JoinTable(name = "jm_goods_ugt", joinColumns = { @javax.persistence.JoinColumn(name = "goods_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "class_id") })
    private List<UserGoodsClass>    goods_ugcs        = new ArrayList<UserGoodsClass>();
    @ManyToMany
    @JoinTable(name = "jm_goods_spec", joinColumns = { @javax.persistence.JoinColumn(name = "goods_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "spec_id") })
    @OrderBy("sequence asc")
    private List<GoodsSpecProperty> goods_specs       = new ArrayList<GoodsSpecProperty>();
    @ManyToOne(fetch = FetchType.LAZY)
    private GoodsBrand              goods_brand;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  goods_property;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                  goods_inventory_detail;
    private int                     ztc_status;
    private int                     ztc_pay_status;
    private int                     ztc_price;
    private int                     ztc_dredge_price;
    private Date                    ztc_apply_time;
    @Temporal(TemporalType.DATE)
    private Date                    ztc_begin_time;
    private int                     ztc_gold;
    private int                     ztc_click_num;
    @ManyToOne(fetch = FetchType.LAZY)
    private User                    ztc_admin;
    @Column(columnDefinition = "LongText")
    private String                  ztc_admin_content;
    @OneToMany(mappedBy = "gg_goods", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    private List<GroupGoods>        group_goods_list  = new ArrayList<GroupGoods>();
    @ManyToOne(fetch = FetchType.LAZY)
    private Group                   group;
    @Column(columnDefinition = "int default 0")
    private int                     group_buy;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Consult>           consults          = new ArrayList<Consult>();
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "evaluate_goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Evaluate>          evaluates         = new ArrayList<Evaluate>();
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Favorite>          favs              = new ArrayList<Favorite>();
    @Column(columnDefinition = "int default 0")
    private int                     goods_choice_type;
    @Column(columnDefinition = "int default 0")
    private int                     activity_status;
    @Column(precision = 12, scale = 2)
    private BigDecimal              goods_current_price;
    @Column(columnDefinition = "int default 0")
    private int                     bargain_status;
    @OneToMany(mappedBy = "bg_goods", fetch = FetchType.LAZY)
    private List<BargainGoods>      bgs               = new ArrayList<BargainGoods>();
    @Column(columnDefinition = "int default 0")
    private int                     delivery_status;
    @Column(columnDefinition = "int default 0")
    private int                     combin_status;
    @Column(columnDefinition = "int default 0")
    private int                     bind_status;
    @Temporal(TemporalType.DATE)
    private Date                    combin_begin_time;
    @Temporal(TemporalType.DATE)
    private Date                    combin_end_time;
    @Column(precision = 12, scale = 2)
    private BigDecimal              combin_price;
    @Column(precision = 12, scale = 2)
    private BigDecimal              binddiscount;
    @ManyToMany
    @JoinTable(name = "jm_goods_combinationsale")
    private List<Goods>             combin_goods      = new ArrayList<Goods>();
    @ManyToMany
    @JoinTable(name = "jm_goods_bind")
    private List<Goods>             bind_goods        = new ArrayList<Goods>();
  /*  @OneToOne(mappedBy = "d_goods", cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private DeliveryGoods           dg;*/
   /* @OneToOne(mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private MobileGoods             mg;*/
    @Column(precision = 12, scale = 2)
    private BigDecimal              mail_trans_fee;
    @Column(precision = 12, scale = 2)
    private BigDecimal              express_trans_fee;
    @Column(precision = 12, scale = 2)
    private BigDecimal              ems_trans_fee;
    @ManyToOne(fetch = FetchType.LAZY)
    private Transport               transport;
    @Column(precision = 4, scale = 1, columnDefinition = "Decimal default 5.0")
    private BigDecimal              description_evaluate;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<Dynamic>           dynamics          = new ArrayList<Dynamic>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "bg_goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<BargainGoods>      bargainGoods_list = new ArrayList<BargainGoods>();
   /* @OneToOne(mappedBy = "d_goods", cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private DeliveryGoods           d_main_goods;*/
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "d_delivery_goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<DeliveryGoods>     d_goods_list      = new ArrayList<DeliveryGoods>();
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "ag_goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<ActivityGoods>     ag_goods_list     = new ArrayList<ActivityGoods>();
    @OneToMany(mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE }, fetch = FetchType.LAZY)
    private List<GoodsReturnItem>   gri               = new ArrayList<GoodsReturnItem>();
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "evaluate_goods")
    private List<Evaluate>          evas              = new ArrayList<Evaluate>();
    @OneToMany(mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE })
    @Where(clause="disabled=0")
    private List<GoodsItem>        goods_item_list  = new ArrayList<GoodsItem>();//货品列表
    


  
    private Integer   goodsTypeId;//使用的模板编号
    private Integer   storage_status;//入库状态0未入库，1已入库过
    
    private String goods_units;//商品单位
    private Integer goods_news_status;//是否是新品0否，1是
    private Integer goods_hot_status;//是否是热卖0否，1是
    
    private String total_weight;//总件数或总重量(整包，走份)
    private String type_ratio;//类型比例(整包，走份)
    private String color_ratio;//颜色比例(整包，走份)
    private String size_ratio;//尺寸比例(整包，走份)
    private String single_weight;//单份量单份的件数，或者单份的重量
    private Integer goods_type;//商品类型：0单件商品 1,整包商品，2走份商品
    
    private String retrieval_ids;//检索属性id集合
    
    private String extendedAttributes;//扩展属性集合//json集合
    
    private String supplier_info;//供应商信息json串supplier_no,supplier_name,contact_name,contact_mobile
    
    
    
    
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
	public String getSupplier_info() {
		return supplier_info;
	}
	public void setSupplier_info(String supplier_info) {
		this.supplier_info = supplier_info;
	}
	public String getExtendedAttributes() {
		return extendedAttributes;
	}
	public void setExtendedAttributes(String extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}
	public String getRetrieval_ids() {
		return retrieval_ids;
	}
	public void setRetrieval_ids(String retrieval_ids) {
		this.retrieval_ids = retrieval_ids;
	}
	public String getSingle_weight() {
		return single_weight;
	}
	public void setSingle_weight(String single_weight) {
		this.single_weight = single_weight;
	}
	/**
     * 总件数或总重量(整包，走份)
     * @return
     */
    public String getTotal_weight() {
		return total_weight;
	}
    /**
     * 总件数或总重量(整包，走份)
     * @param total_weight
     */
	public void setTotal_weight(String total_weight) {
		this.total_weight = total_weight;
	}
	/**
	 * 类型比例(整包，走份)
	 * @return
	 */
	public String getType_ratio() {
		return type_ratio;
	}
	/**
	 * 类型比例(整包，走份)
	 * @param type_ratio
	 */
	public void setType_ratio(String type_ratio) {
		this.type_ratio = type_ratio;
	}
	/**
	 * 颜色比例(整包，走份)
	 * @return
	 */
	public String getColor_ratio() {
		return color_ratio;
	}
	/**
	 * 颜色比例(整包，走份)
	 * @param color_ratio
	 */
	public void setColor_ratio(String color_ratio) {
		this.color_ratio = color_ratio;
	}
	/**
	 * 尺寸比例(整包，走份)
	 * @return
	 */
	public String getSize_ratio() {
		return size_ratio;
	}
	/**
	 * 尺寸比例(整包，走份)
	 * @param size_ratio
	 */
	public void setSize_ratio(String size_ratio) {
		this.size_ratio = size_ratio;
	}
	/**
	 * 商品类型：0单件商品 1,整包商品，2走份商品
	 * @return
	 */
	public Integer getGoods_type() {
		return goods_type;
	}
	/**
	 * 商品类型：0单件商品 1,整包商品，2走份商品
	 * @param goods_type
	 */
	public void setGoods_type(Integer goods_type) {
		this.goods_type = goods_type;
	}

	@OneToMany(mappedBy = "goods", cascade = { javax.persistence.CascadeType.REMOVE })
    private List<StrategyGoodsItem>	strategyGoodsItems          = new ArrayList<StrategyGoodsItem>();
    
    public List<StrategyGoodsItem> getStrategyGoodsItems() {
		return strategyGoodsItems;
	}
	public void setStrategyGoodsItems(List<StrategyGoodsItem> strategyGoodsItems) {
		this.strategyGoodsItems = strategyGoodsItems;
	}
	/**
     * 商品单位
     * @return
     */
    public String getGoods_units() {
		return goods_units;
	}
    /**
     * 商品单位
     * @param goods_units
     */
	public void setGoods_units(String goods_units) {
		this.goods_units = goods_units;
	}
	
	/**
	 * 是否是新品
	 * @return
	 */
	public Integer getGoods_news_status() {
		return goods_news_status;
	}
	/**
	 * 是否是新品
	 * @param goods_news_status
	 */
	public void setGoods_news_status(Integer goods_news_status) {
		this.goods_news_status = goods_news_status;
	}
	/**
	 * 是否是热卖
	 * @return
	 */
	public Integer getGoods_hot_status() {
		return goods_hot_status;
	}
	/**
	 * 是否是热卖
	 * @param goods_hot_status
	 */
	public void setGoods_hot_status(Integer goods_hot_status) {
		this.goods_hot_status = goods_hot_status;
	}
	/**
     * 使用的模板编号 
     * @return
     */
    public Integer getGoodsTypeId() {
		return goodsTypeId;
	}
    /**
     * 使用的模板编号
     * @param goodsTypeId
     */
	public void setGoodsTypeId(Integer goodsTypeId) {
		this.goodsTypeId = goodsTypeId;
	}
	/**
	 * 入库状态0未入库，1已入库过
	 * @return
	 */
	public Integer getStorage_status() {
		return storage_status;
	}
	/**
	 * 入库状态0未入库，1已入库过
	 * @param storage_status
	 */
	public void setStorage_status(Integer storage_status) {
		this.storage_status = storage_status;
	}
	/**
     * 货品列表
     * @return
     */
	public List<GoodsItem> getGoods_item_list() {
		return goods_item_list;
	}
	/**
	 * 货品列表
	 * @param goods_item_list
	 */
	public void setGoods_item_list(List<GoodsItem> goods_item_list) {
		this.goods_item_list = goods_item_list;
	}



    public List<Evaluate> getEvas() {
        return this.evas;
    }

    public void setEvas(List<Evaluate> evas) {
        this.evas = evas;
    }

    public BigDecimal getDescription_evaluate() {
        return this.description_evaluate;
    }

    public void setDescription_evaluate(BigDecimal description_evaluate) {
        this.description_evaluate = description_evaluate;
    }

    public List<GoodsReturnItem> getGri() {
        return this.gri;
    }

    public void setGri(List<GoodsReturnItem> gri) {
        this.gri = gri;
    }

    public List<ActivityGoods> getAg_goods_list() {
        return this.ag_goods_list;
    }

    public void setAg_goods_list(List<ActivityGoods> ag_goods_list) {
        this.ag_goods_list = ag_goods_list;
    }

   /* public DeliveryGoods getD_main_goods() {
        return this.d_main_goods;
    }

    public void setD_main_goods(DeliveryGoods d_main_goods) {
        this.d_main_goods = d_main_goods;
    }*/

    public List<DeliveryGoods> getD_goods_list() {
        return this.d_goods_list;
    }

    public void setD_goods_list(List<DeliveryGoods> d_goods_list) {
        this.d_goods_list = d_goods_list;
    }

    public List<Dynamic> getDynamics() {
        return this.dynamics;
    }

    public void setDynamics(List<Dynamic> dynamics) {
        this.dynamics = dynamics;
    }

    public List<BargainGoods> getBargainGoods_list() {
        return this.bargainGoods_list;
    }

    public void setBargainGoods_list(List<BargainGoods> bargainGoods_list) {
        this.bargainGoods_list = bargainGoods_list;
    }

    public int getDelivery_status() {
        return this.delivery_status;
    }

    public void setDelivery_status(int delivery_status) {
        this.delivery_status = delivery_status;
    }

    public List<Consult> getConsults() {
        return this.consults;
    }

    public void setConsults(List<Consult> consults) {
        this.consults = consults;
    }

    public int getGroup_buy() {
        return this.group_buy;
    }

    public void setGroup_buy(int group_buy) {
        this.group_buy = group_buy;
    }

    public int getZtc_status() {
        return this.ztc_status;
    }

    public void setZtc_status(int ztc_status) {
        this.ztc_status = ztc_status;
    }

    public int getZtc_price() {
        return this.ztc_price;
    }

    public void setZtc_price(int ztc_price) {
        this.ztc_price = ztc_price;
    }

    public Date getZtc_begin_time() {
        return this.ztc_begin_time;
    }

    public void setZtc_begin_time(Date ztc_begin_time) {
        this.ztc_begin_time = ztc_begin_time;
    }

    public int getZtc_gold() {
        return this.ztc_gold;
    }

    public void setZtc_gold(int ztc_gold) {
        this.ztc_gold = ztc_gold;
    }

    public String getSeo_keywords() {
        return this.seo_keywords;
    }

    public void setSeo_keywords(String seo_keywords) {
        this.seo_keywords = seo_keywords;
    }

    public String getSeo_description() {
        return this.seo_description;
    }

    public void setSeo_description(String seo_description) {
        this.seo_description = seo_description;
    }

    public String getGoods_name() {
        return this.goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public int getGoods_inventory() {
        return this.goods_inventory;
    }

    public void setGoods_inventory(int goods_inventory) {
        this.goods_inventory = goods_inventory;
    }

    public String getInventory_type() {
        return this.inventory_type;
    }

    public void setInventory_type(String inventory_type) {
        this.inventory_type = inventory_type;
    }

    public int getGoods_salenum() {
        return this.goods_salenum;
    }

    public void setGoods_salenum(int goods_salenum) {
        this.goods_salenum = goods_salenum;
    }

    public String getGoods_serial() {
        return this.goods_serial;
    }

    public void setGoods_serial(String goods_serial) {
        this.goods_serial = goods_serial;
    }

    public BigDecimal getGoods_price() {
        return this.goods_price;
    }

    public void setGoods_price(BigDecimal goods_price) {
        this.goods_price = goods_price;
    }

    public BigDecimal getDeposit_price() {
        return this.deposit_price;
    }

    public void setDeposit_price(BigDecimal deposit_price) {
        this.deposit_price = deposit_price;
    }

    public BigDecimal getStoreempty_price() {
        return this.storeempty_price;
    }

    public void setStoreempty_price(BigDecimal storeempty_price) {
        this.storeempty_price = storeempty_price;
    }

    public BigDecimal getStore_price() {
        return this.store_price;
    }

    public void setStore_price(BigDecimal store_price) {
        this.store_price = store_price;
    }

    public BigDecimal getGoods_weight() {
        return this.goods_weight;
    }

    public void setGoods_weight(BigDecimal goods_weight) {
        this.goods_weight = goods_weight;
    }

    public String getGoods_fee() {
        return this.goods_fee;
    }

    public void setGoods_fee(String goods_fee) {
        this.goods_fee = goods_fee;
    }

    public String getGoods_details() {
        return this.goods_details;
    }

    public void setGoods_details(String goods_details) {
        this.goods_details = goods_details;
    }

    public boolean isStore_recommend() {
        return this.store_recommend;
    }

    public void setStore_recommend(boolean store_recommend) {
        this.store_recommend = store_recommend;
    }

    public Date getStore_recommend_time() {
        return this.store_recommend_time;
    }

    public void setStore_recommend_time(Date store_recommend_time) {
        this.store_recommend_time = store_recommend_time;
    }

    public boolean isGoods_recommend() {
        return this.goods_recommend;
    }

    public void setGoods_recommend(boolean goods_recommend) {
        this.goods_recommend = goods_recommend;
    }

    public int getGoods_click() {
        return this.goods_click;
    }

    public void setGoods_click(int goods_click) {
        this.goods_click = goods_click;
    }

    public Store getGoods_store() {
        return this.goods_store;
    }

    public void setGoods_store(Store goods_store) {
        this.goods_store = goods_store;
    }

    public int getGoods_status() {
        return this.goods_status;
    }

    public void setGoods_status(int goods_status) {
        this.goods_status = goods_status;
    }

    public Date getGoods_seller_time() {
        return this.goods_seller_time;
    }

    public void setGoods_seller_time(Date goods_seller_time) {
        this.goods_seller_time = goods_seller_time;
    }

    public int getGoods_transfee() {
        return this.goods_transfee;
    }

    public void setGoods_transfee(int goods_transfee) {
        this.goods_transfee = goods_transfee;
    }

    public GoodsClass getGc() {
        return this.gc;
    }

    public void setGc(GoodsClass gc) {
        this.gc = gc;
    }

    public Accessory getGoods_main_photo() {
        return this.goods_main_photo;
    }

    public void setGoods_main_photo(Accessory goods_main_photo) {
        this.goods_main_photo = goods_main_photo;
    }

    public List<Accessory> getGoods_photos() {
        return this.goods_photos;
    }

    public void setGoods_photos(List<Accessory> goods_photos) {
        this.goods_photos = goods_photos;
    }

    public List<UserGoodsClass> getGoods_ugcs() {
        return this.goods_ugcs;
    }

    public void setGoods_ugcs(List<UserGoodsClass> goods_ugcs) {
        this.goods_ugcs = goods_ugcs;
    }

    public List<GoodsSpecProperty> getGoods_specs() {
        return this.goods_specs;
    }

    public void setGoods_specs(List<GoodsSpecProperty> goods_specs) {
        this.goods_specs = goods_specs;
    }

    public GoodsBrand getGoods_brand() {
        return this.goods_brand;
    }

    public void setGoods_brand(GoodsBrand goods_brand) {
        this.goods_brand = goods_brand;
    }

    public String getGoods_property() {
        return this.goods_property;
    }

    public void setGoods_property(String goods_property) {
        this.goods_property = goods_property;
    }

    public String getGoods_inventory_detail() {
        return this.goods_inventory_detail;
    }

    public void setGoods_inventory_detail(String goods_inventory_detail) {
        this.goods_inventory_detail = goods_inventory_detail;
    }

    public int getZtc_pay_status() {
        return this.ztc_pay_status;
    }

    public void setZtc_pay_status(int ztc_pay_status) {
        this.ztc_pay_status = ztc_pay_status;
    }

    public User getZtc_admin() {
        return this.ztc_admin;
    }

    public void setZtc_admin(User ztc_admin) {
        this.ztc_admin = ztc_admin;
    }

    public String getZtc_admin_content() {
        return this.ztc_admin_content;
    }

    public void setZtc_admin_content(String ztc_admin_content) {
        this.ztc_admin_content = ztc_admin_content;
    }

    public Date getZtc_apply_time() {
        return this.ztc_apply_time;
    }

    public void setZtc_apply_time(Date ztc_apply_time) {
        this.ztc_apply_time = ztc_apply_time;
    }

    public int getZtc_click_num() {
        return this.ztc_click_num;
    }

    public void setZtc_click_num(int ztc_click_num) {
        this.ztc_click_num = ztc_click_num;
    }

    public int getZtc_dredge_price() {
        return this.ztc_dredge_price;
    }

    public void setZtc_dredge_price(int ztc_dredge_price) {
        this.ztc_dredge_price = ztc_dredge_price;
    }

    public int getGoods_collect() {
        return this.goods_collect;
    }

    public void setGoods_collect(int goods_collect) {
        this.goods_collect = goods_collect;
    }

    public List<GroupGoods> getGroup_goods_list() {
        return this.group_goods_list;
    }

    public void setGroup_goods_list(List<GroupGoods> group_goods_list) {
        this.group_goods_list = group_goods_list;
    }

    public Group getGroup() {
        return this.group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public List<Evaluate> getEvaluates() {
        return this.evaluates;
    }

    public void setEvaluates(List<Evaluate> evaluates) {
        this.evaluates = evaluates;
    }

    public int getGoods_choice_type() {
        return this.goods_choice_type;
    }

    public void setGoods_choice_type(int goods_choice_type) {
        this.goods_choice_type = goods_choice_type;
    }

    public int getActivity_status() {
        return this.activity_status;
    }

    public void setActivity_status(int activity_status) {
        this.activity_status = activity_status;
    }

    public BigDecimal getGoods_current_price() {
        return this.goods_current_price;
    }

    public void setGoods_current_price(BigDecimal goods_current_price) {
        this.goods_current_price = goods_current_price;
    }

    public List<Favorite> getFavs() {
        return this.favs;
    }

    public void setFavs(List<Favorite> favs) {
        this.favs = favs;
    }

    public int getBargain_status() {
        return this.bargain_status;
    }

    public void setBargain_status(int bargain_status) {
        this.bargain_status = bargain_status;
    }

    public BigDecimal getGoods_volume() {
        return this.goods_volume;
    }

    public void setGoods_volume(BigDecimal goods_volume) {
        this.goods_volume = goods_volume;
    }

    public BigDecimal getMail_trans_fee() {
        return this.mail_trans_fee;
    }

    public void setMail_trans_fee(BigDecimal mail_trans_fee) {
        this.mail_trans_fee = mail_trans_fee;
    }

    public BigDecimal getExpress_trans_fee() {
        return this.express_trans_fee;
    }

    public void setExpress_trans_fee(BigDecimal express_trans_fee) {
        this.express_trans_fee = express_trans_fee;
    }

    public BigDecimal getEms_trans_fee() {
        return this.ems_trans_fee;
    }

    public void setEms_trans_fee(BigDecimal ems_trans_fee) {
        this.ems_trans_fee = ems_trans_fee;
    }

    public Transport getTransport() {
        return this.transport;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public List<BargainGoods> getBgs() {
        return this.bgs;
    }

    public void setBgs(List<BargainGoods> bgs) {
        this.bgs = bgs;
    }

  /*  public DeliveryGoods getDg() {
        return this.dg;
    }

    public void setDg(DeliveryGoods dg) {
        this.dg = dg;
    }*/

    public List<Goods> getCombin_goods() {
        return this.combin_goods;
    }

    public void setCombin_goods(List<Goods> combin_goods) {
        this.combin_goods = combin_goods;
    }

    public List<Goods> getBind_goods() {
        return bind_goods;
    }

    public void setBind_goods(List<Goods> bind_goods) {
        this.bind_goods = bind_goods;
    }

    public int getCombin_status() {
        return this.combin_status;
    }

    public void setCombin_status(int combin_status) {
        this.combin_status = combin_status;
    }

    public int getBind_status() {
        return bind_status;
    }

    public void setBind_status(int bind_status) {
        this.bind_status = bind_status;
    }

    public Date getCombin_begin_time() {
        return this.combin_begin_time;
    }

    public void setCombin_begin_time(Date combin_begin_time) {
        this.combin_begin_time = combin_begin_time;
    }

    public Date getCombin_end_time() {
        return this.combin_end_time;
    }

    public void setCombin_end_time(Date combin_end_time) {
        this.combin_end_time = combin_end_time;
    }

    public BigDecimal getCombin_price() {
        return this.combin_price;
    }

    public void setCombin_price(BigDecimal combin_price) {
        this.combin_price = combin_price;
    }

    public BigDecimal getBinddiscount() {
        return binddiscount;
    }

    public void setBinddiscount(BigDecimal binddiscount) {
        this.binddiscount = binddiscount;
    }

}