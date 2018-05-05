package com.javamalls.platform.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.javamalls.platform.domain.GoodsItem;

public class GoodsJsonVo {
   private Long              id;
   private Date              createtime;
    private boolean           disabled;
    private String                  seo_keywords;
    private String                  seo_description;
    private String                  goods_name;
    private BigDecimal              goods_price;//市场价
    private BigDecimal              store_price;//销售价
    private int                     goods_inventory;
    private String                  inventory_type;
    private int                     goods_salenum;
    private String                  goods_details;
    private boolean                 goods_recommend;
    private int                     goods_click;
    private int                     goods_collect;

    private Long                   goods_store_id;
    private int                     goods_status;//0上架，1仓库中，-1已下架，-2违规下架，-9逻辑删除
    private int                     goods_transfee;
    private String               goods_main_photo_url;
    private String         			goods_photos_url;
   
    private Long    goods_ugcs_id;//店铺分类Id

    private String goods_specs ;
    private Long              goods_brand_id;
    private BigDecimal              goods_current_price;
    private BigDecimal              mail_trans_fee;
    private BigDecimal              express_trans_fee;
    private BigDecimal              ems_trans_fee;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getSeo_keywords() {
		return seo_keywords;
	}

	public void setSeo_keywords(String seo_keywords) {
		this.seo_keywords = seo_keywords;
	}

	public String getSeo_description() {
		return seo_description;
	}

	public void setSeo_description(String seo_description) {
		this.seo_description = seo_description;
	}

	public String getGoods_name() {
		return goods_name;
	}

	public void setGoods_name(String goods_name) {
		this.goods_name = goods_name;
	}

	public BigDecimal getGoods_price() {
		return goods_price;
	}

	public void setGoods_price(BigDecimal goods_price) {
		this.goods_price = goods_price;
	}

	public BigDecimal getStore_price() {
		return store_price;
	}

	public void setStore_price(BigDecimal store_price) {
		this.store_price = store_price;
	}

	public int getGoods_inventory() {
		return goods_inventory;
	}

	public void setGoods_inventory(int goods_inventory) {
		this.goods_inventory = goods_inventory;
	}

	public String getInventory_type() {
		return inventory_type;
	}

	public void setInventory_type(String inventory_type) {
		this.inventory_type = inventory_type;
	}

	public int getGoods_salenum() {
		return goods_salenum;
	}

	public void setGoods_salenum(int goods_salenum) {
		this.goods_salenum = goods_salenum;
	}

	public String getGoods_details() {
		return goods_details;
	}

	public void setGoods_details(String goods_details) {
		this.goods_details = goods_details;
	}

	public boolean isGoods_recommend() {
		return goods_recommend;
	}

	public void setGoods_recommend(boolean goods_recommend) {
		this.goods_recommend = goods_recommend;
	}

	public int getGoods_click() {
		return goods_click;
	}

	public void setGoods_click(int goods_click) {
		this.goods_click = goods_click;
	}

	public int getGoods_collect() {
		return goods_collect;
	}

	public void setGoods_collect(int goods_collect) {
		this.goods_collect = goods_collect;
	}

	public Long getGoods_store_id() {
		return goods_store_id;
	}

	public void setGoods_store_id(Long goods_store_id) {
		this.goods_store_id = goods_store_id;
	}

	public int getGoods_status() {
		return goods_status;
	}

	public void setGoods_status(int goods_status) {
		this.goods_status = goods_status;
	}

	public int getGoods_transfee() {
		return goods_transfee;
	}

	public void setGoods_transfee(int goods_transfee) {
		this.goods_transfee = goods_transfee;
	}

	public String getGoods_main_photo_url() {
		return goods_main_photo_url;
	}

	public void setGoods_main_photo_url(String goods_main_photo_url) {
		this.goods_main_photo_url = goods_main_photo_url;
	}

	public String getGoods_photos_url() {
		return goods_photos_url;
	}

	public void setGoods_photos_url(String goods_photos_url) {
		this.goods_photos_url = goods_photos_url;
	}

	public Long getGoods_ugcs_id() {
		return goods_ugcs_id;
	}

	public void setGoods_ugcs_id(Long goods_ugcs_id) {
		this.goods_ugcs_id = goods_ugcs_id;
	}

	public String getGoods_specs() {
		return goods_specs;
	}

	public void setGoods_specs(String goods_specs) {
		this.goods_specs = goods_specs;
	}

	public Long getGoods_brand_id() {
		return goods_brand_id;
	}

	public void setGoods_brand_id(Long goods_brand_id) {
		this.goods_brand_id = goods_brand_id;
	}

	public BigDecimal getGoods_current_price() {
		return goods_current_price;
	}

	public void setGoods_current_price(BigDecimal goods_current_price) {
		this.goods_current_price = goods_current_price;
	}

	public BigDecimal getMail_trans_fee() {
		return mail_trans_fee;
	}

	public void setMail_trans_fee(BigDecimal mail_trans_fee) {
		this.mail_trans_fee = mail_trans_fee;
	}

	public BigDecimal getExpress_trans_fee() {
		return express_trans_fee;
	}

	public void setExpress_trans_fee(BigDecimal express_trans_fee) {
		this.express_trans_fee = express_trans_fee;
	}

	public BigDecimal getEms_trans_fee() {
		return ems_trans_fee;
	}

	public void setEms_trans_fee(BigDecimal ems_trans_fee) {
		this.ems_trans_fee = ems_trans_fee;
	}

	public List<GoodsItem> getGoods_item_list() {
		return goods_item_list;
	}

	public void setGoods_item_list(List<GoodsItem> goods_item_list) {
		this.goods_item_list = goods_item_list;
	}

	public Integer getGoodsTypeId() {
		return goodsTypeId;
	}

	public void setGoodsTypeId(Integer goodsTypeId) {
		this.goodsTypeId = goodsTypeId;
	}

	public Integer getStorage_status() {
		return storage_status;
	}

	public void setStorage_status(Integer storage_status) {
		this.storage_status = storage_status;
	}

	public String getGoods_units() {
		return goods_units;
	}

	public void setGoods_units(String goods_units) {
		this.goods_units = goods_units;
	}

	public Integer getGoods_news_status() {
		return goods_news_status;
	}

	public void setGoods_news_status(Integer goods_news_status) {
		this.goods_news_status = goods_news_status;
	}

	public Integer getGoods_hot_status() {
		return goods_hot_status;
	}

	public void setGoods_hot_status(Integer goods_hot_status) {
		this.goods_hot_status = goods_hot_status;
	}

	public String getTotal_weight() {
		return total_weight;
	}

	public void setTotal_weight(String total_weight) {
		this.total_weight = total_weight;
	}

	public String getType_ratio() {
		return type_ratio;
	}

	public void setType_ratio(String type_ratio) {
		this.type_ratio = type_ratio;
	}

	public String getColor_ratio() {
		return color_ratio;
	}

	public void setColor_ratio(String color_ratio) {
		this.color_ratio = color_ratio;
	}

	public String getSize_ratio() {
		return size_ratio;
	}

	public void setSize_ratio(String size_ratio) {
		this.size_ratio = size_ratio;
	}

	public String getSingle_weight() {
		return single_weight;
	}

	public void setSingle_weight(String single_weight) {
		this.single_weight = single_weight;
	}

	public Integer getGoods_type() {
		return goods_type;
	}

	public void setGoods_type(Integer goods_type) {
		this.goods_type = goods_type;
	}

	public String getRetrieval_ids() {
		return retrieval_ids;
	}

	public void setRetrieval_ids(String retrieval_ids) {
		this.retrieval_ids = retrieval_ids;
	}

	public String getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(String extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}
    
    
   
}