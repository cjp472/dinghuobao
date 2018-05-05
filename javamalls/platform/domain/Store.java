package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;

/**店铺
 *                       
 * @Filename: Store.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_store")
public class Store extends CommonEntity implements java.io.Serializable {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long      serialVersionUID = 1L;
    private String                 store_name;
    private String                 store_ower;
    private String                 store_ower_card;
    private String                 store_telephone;
    private String                 store_qq;
    private String                 store_address;
    private String                 store_zip;
    private int                    store_status;//店铺状态      1:待审核,2: 正常,3:关闭,-1:审核拒绝,5延期开通
    @OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
    private User                   user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private StoreGrade             grade;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private StoreClass             sc;
    @ManyToOne(fetch=FetchType.EAGER)
    private Area                   area;
    private boolean                store_recommend;
    private Date                   store_recommend_time;
    private Date                   validity;
    private boolean                card_approve;
    @OneToOne(fetch = FetchType.EAGER)
    private Accessory              store_logo;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Accessory              store_banner;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Accessory              card;//实名认证（身份证）
    private boolean                realstore_approve;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Accessory              store_license;//实体店铺认证（执照信息）
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "goods_store")
    @Where(clause="goods_status!=-9")
    @JsonIgnore
    private List<Goods>            goods_list       = new ArrayList<Goods>();
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "store")
    @JsonIgnore
    private List<Supplier>         supplier_list    = new ArrayList<Supplier>();//供应商列表
    private int                    store_credit;
    private String                 template;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                 violation_reseaon;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                 store_seo_keywords;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                 store_seo_description;
    @Lob
    @Column(columnDefinition = "LongText")
    private String                 store_info;
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private StoreGrade             update_grade;
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<StoreSlide>       slides           = new ArrayList<StoreSlide>();
    @FormIgnore
    private String                 store_second_domain;
    @Column(columnDefinition = "int default 0")
    private int                    domain_modify_count;
    @Column(columnDefinition = "int default 0")
    private int                    favorite_count;
    @OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
    @JsonIgnore
    private StorePoint             point;
    @Column(columnDefinition = "varchar(255) default 'baidu'")
    private String                 map_type;
    @Column(precision = 18, scale = 15)
    private BigDecimal             store_lat;
    @Column(precision = 18, scale = 15)
    private BigDecimal             store_lng;
    private Date                   delivery_begin_time;
    private Date                   delivery_end_time;
    private Date                   combin_begin_time;
    private Date                   combin_end_time;
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<StoreGradeLog>    logs             = new ArrayList<StoreGradeLog>();
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Payment>          payments         = new ArrayList<Payment>();
    @OneToOne(mappedBy = "store", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private StorePoint             sp;
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<StoreNavigation>  navs             = new ArrayList<StoreNavigation>();
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<StoreDepartment>  departments      = new ArrayList<StoreDepartment>();//部门列表
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Favorite>         favs             = new ArrayList<Favorite>();
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<GoodsClassStaple> gcss             = new ArrayList<GoodsClassStaple>();
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<OrderForm>        ofs              = new ArrayList<OrderForm>();
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<DeliveryLog>      delivery_logs    = new ArrayList<DeliveryLog>();
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<CombinLog>        combin_logs      = new ArrayList<CombinLog>();
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Transport>        transport_list   = new ArrayList<Transport>();
    @OneToMany(mappedBy = "store", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Dynamic>          dynamics         = new ArrayList<Dynamic>();


    @FormIgnore
    private boolean                platform;//是否为自营店铺
    
    private String copyright_info;//版权所有信息
    private String record_info;//店铺备案信息
    private String domainName_info;//店铺域名
    private Date              domainName_updateTime;//域名修改时间默认为一天只能修改一次
    
    
    
    @OneToMany(fetch=FetchType.LAZY,mappedBy = "store")
    @JsonIgnore
    private List<PurchaseOrder>            warehouse_list   = new ArrayList<PurchaseOrder>();//采购入库单列表
    
    private Date                   validitybegin;//有效开始时间
    
    private int logon_access_state;//是限制登录后访问商城0不限制，1限制
    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Accessory              weixin_qr_img;

    private String		   access_token;//店铺微信支付accessToken
    
    /**
     * 店铺微信支付accessToken
     * @return
     */
    public String getAccess_token() {
		return access_token;
	}
    /**
     * 店铺微信支付accessToken
     * @param access_token
     */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public Accessory getWeixin_qr_img() {
		return weixin_qr_img;
	}
	public void setWeixin_qr_img(Accessory weixin_qr_img) {
		this.weixin_qr_img = weixin_qr_img;
	}
	/**
     * 是限制登录后访问商城0不限制，1限制
     * @return
     */
    public int getLogon_access_state() {
		return logon_access_state;
	}
    /**
     * 是限制登录后访问商城0不限制，1限制
     * @param logon_access_state
     */
	public void setLogon_access_state(int logon_access_state) {
		this.logon_access_state = logon_access_state;
	}
	public Date getValiditybegin() {
		return validitybegin;
	}
	public void setValiditybegin(Date validitybegin) {
		this.validitybegin = validitybegin;
	}
	/**
     * 采购入库单列表
     * @return
     */
    public List<PurchaseOrder> getWarehouse_list() {
		return warehouse_list;
	}
    /**
     * 采购入库单列表
     * @param warehouse_list
     */
	public void setWarehouse_list(List<PurchaseOrder> warehouse_list) {
		this.warehouse_list = warehouse_list;
	}
	/**
     * 供应商列表
     * @return
     */
    public List<Supplier> getSupplier_list() {
		return supplier_list;
	}
    /**
     * 供应商列表
     * @param supplier_list
     */
	public void setSupplier_list(List<Supplier> supplier_list) {
		this.supplier_list = supplier_list;
	}
	/**
     * 部门列表
     * @return
     */
    public List<StoreDepartment> getDepartments() {
		return departments;
	}
    /**
     * 部门列表
     * @param departments
     */
	public void setDepartments(List<StoreDepartment> departments) {
		this.departments = departments;
	}

	public Date getDomainName_updateTime() {
		return domainName_updateTime;
	}

	public void setDomainName_updateTime(Date domainName_updateTime) {
		this.domainName_updateTime = domainName_updateTime;
	}

	public String getCopyright_info() {
		return copyright_info;
	}

	public void setCopyright_info(String copyright_info) {
		this.copyright_info = copyright_info;
	}

	public String getRecord_info() {
		return record_info;
	}

	public void setRecord_info(String record_info) {
		this.record_info = record_info;
	}

	public String getDomainName_info() {
		return domainName_info;
	}

	public void setDomainName_info(String domainName_info) {
		this.domainName_info = domainName_info;
	}



    public List<Dynamic> getDynamics() {
        return this.dynamics;
    }

    public void setDynamics(List<Dynamic> dynamics) {
        this.dynamics = dynamics;
    }

    public List<Transport> getTransport_list() {
        return this.transport_list;
    }

    public void setTransport_list(List<Transport> transport_list) {
        this.transport_list = transport_list;
    }

    public List<CombinLog> getCombin_logs() {
        return this.combin_logs;
    }

    public void setCombin_logs(List<CombinLog> combin_logs) {
        this.combin_logs = combin_logs;
    }

    public List<OrderForm> getOfs() {
        return this.ofs;
    }

    public void setOfs(List<OrderForm> ofs) {
        this.ofs = ofs;
    }

    public List<GoodsClassStaple> getGcss() {
        return this.gcss;
    }

    public void setGcss(List<GoodsClassStaple> gcss) {
        this.gcss = gcss;
    }

    public List<Favorite> getFavs() {
        return this.favs;
    }

    public void setFavs(List<Favorite> favs) {
        this.favs = favs;
    }

    public List<StoreNavigation> getNavs() {
        return this.navs;
    }

    public void setNavs(List<StoreNavigation> navs) {
        this.navs = navs;
    }

    public StorePoint getSp() {
        return this.sp;
    }

    public void setSp(StorePoint sp) {
        this.sp = sp;
    }

    public List<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public BigDecimal getStore_lat() {
        return this.store_lat;
    }

    public void setStore_lat(BigDecimal store_lat) {
        this.store_lat = store_lat;
    }

    public BigDecimal getStore_lng() {
        return this.store_lng;
    }

    public void setStore_lng(BigDecimal store_lng) {
        this.store_lng = store_lng;
    }

    public int getFavorite_count() {
        return this.favorite_count;
    }

    public void setFavorite_count(int favorite_count) {
        this.favorite_count = favorite_count;
    }

    public String getStore_second_domain() {
        return this.store_second_domain;
    }

    public void setStore_second_domain(String store_second_domain) {
        this.store_second_domain = store_second_domain;
    }

    public int getDomain_modify_count() {
        return this.domain_modify_count;
    }

    public void setDomain_modify_count(int domain_modify_count) {
        this.domain_modify_count = domain_modify_count;
    }

    public List<StoreSlide> getSlides() {
        return this.slides;
    }

    public void setSlides(List<StoreSlide> slides) {
        this.slides = slides;
    }

    public String getViolation_reseaon() {
        return this.violation_reseaon;
    }

    public void setViolation_reseaon(String violation_reseaon) {
        this.violation_reseaon = violation_reseaon;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getStore_credit() {
        return this.store_credit;
    }

    public void setStore_credit(int store_credit) {
        this.store_credit = store_credit;
    }

    public List<Goods> getGoods_list() {
        return this.goods_list;
    }

    public void setGoods_list(List<Goods> goods_list) {
        this.goods_list = goods_list;
    }

    public StoreClass getSc() {
        return this.sc;
    }

    public void setSc(StoreClass sc) {
        this.sc = sc;
    }

    public Area getArea() {
        return this.area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public String getStore_address() {
        return this.store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }

    public String getStore_name() {
        return this.store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_ower() {
        return this.store_ower;
    }

    public void setStore_ower(String store_ower) {
        this.store_ower = store_ower;
    }

    public String getStore_ower_card() {
        return this.store_ower_card;
    }

    public void setStore_ower_card(String store_ower_card) {
        this.store_ower_card = store_ower_card;
    }

    public StoreGrade getGrade() {
        return this.grade;
    }

    public void setGrade(StoreGrade grade) {
        this.grade = grade;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isStore_recommend() {
        return this.store_recommend;
    }

    public void setStore_recommend(boolean store_recommend) {
        this.store_recommend = store_recommend;
    }

    public Date getValidity() {
        return this.validity;
    }

    public void setValidity(Date validity) {
        this.validity = validity;
    }

    public boolean isCard_approve() {
        return this.card_approve;
    }

    public void setCard_approve(boolean card_approve) {
        this.card_approve = card_approve;
    }
    /**
     * 实名认证（身份证）
     * @return
     */
    public Accessory getCard() {
        return this.card;
    }
    /**
     * 实名认证（身份证）
     * @param card
     */
    public void setCard(Accessory card) {
        this.card = card;
    }

    public boolean isRealstore_approve() {
        return this.realstore_approve;
    }

    public void setRealstore_approve(boolean realstore_approve) {
        this.realstore_approve = realstore_approve;
    }
    /**
     * 实体店铺认证（执照信息）
     * @return
     */
    public Accessory getStore_license() {
        return this.store_license;
    }
    /**
     * 实体店铺认证（执照信息）
     * @param store_license
     */
    public void setStore_license(Accessory store_license) {
        this.store_license = store_license;
    }
    /**
     * 店铺状态      1:待审核,2: 正常,3:关闭,-1:审核拒绝
     * @return
     */
    public int getStore_status() {
        return this.store_status;
    }

    /**
     * 店铺状态      1:待审核,2: 正常,3:关闭,-1:审核拒绝
     * @param store_status
     */
    public void setStore_status(int store_status) {
        this.store_status = store_status;
    }

    public String getStore_telephone() {
        return this.store_telephone;
    }

    public void setStore_telephone(String store_telephone) {
        this.store_telephone = store_telephone;
    }

    public String getStore_zip() {
        return this.store_zip;
    }

    public void setStore_zip(String store_zip) {
        this.store_zip = store_zip;
    }

    public Accessory getStore_logo() {
        return this.store_logo;
    }

    public void setStore_logo(Accessory store_logo) {
        this.store_logo = store_logo;
    }

    public Accessory getStore_banner() {
        return this.store_banner;
    }

    public void setStore_banner(Accessory store_banner) {
        this.store_banner = store_banner;
    }

    public String getStore_seo_keywords() {
        return this.store_seo_keywords;
    }

    public void setStore_seo_keywords(String store_seo_keywords) {
        this.store_seo_keywords = store_seo_keywords;
    }

    public String getStore_seo_description() {
        return this.store_seo_description;
    }

    public void setStore_seo_description(String store_seo_description) {
        this.store_seo_description = store_seo_description;
    }

    public String getStore_info() {
        return this.store_info;
    }

    public void setStore_info(String store_info) {
        this.store_info = store_info;
    }

    public StoreGrade getUpdate_grade() {
        return this.update_grade;
    }

    public void setUpdate_grade(StoreGrade update_grade) {
        this.update_grade = update_grade;
    }

    public Date getStore_recommend_time() {
        return this.store_recommend_time;
    }

    public void setStore_recommend_time(Date store_recommend_time) {
        this.store_recommend_time = store_recommend_time;
    }

    public String getStore_qq() {
        return this.store_qq;
    }

    public void setStore_qq(String store_qq) {
        this.store_qq = store_qq;
    }



    public StorePoint getPoint() {
        return this.point;
    }

    public void setPoint(StorePoint point) {
        this.point = point;
    }

    public List<StoreGradeLog> getLogs() {
        return this.logs;
    }

    public void setLogs(List<StoreGradeLog> logs) {
        this.logs = logs;
    }


    public String getMap_type() {
        return this.map_type;
    }

    public void setMap_type(String map_type) {
        this.map_type = map_type;
    }

    public Date getDelivery_begin_time() {
        return this.delivery_begin_time;
    }

    public void setDelivery_begin_time(Date delivery_begin_time) {
        this.delivery_begin_time = delivery_begin_time;
    }

    public Date getDelivery_end_time() {
        return this.delivery_end_time;
    }

    public void setDelivery_end_time(Date delivery_end_time) {
        this.delivery_end_time = delivery_end_time;
    }

    public List<DeliveryLog> getDelivery_logs() {
        return this.delivery_logs;
    }

    public void setDelivery_logs(List<DeliveryLog> delivery_logs) {
        this.delivery_logs = delivery_logs;
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
    /**
     * 是否为自营店铺
     * @return
     */
    public boolean isPlatform() {
        return platform;
    }
    /**
     * 是否为自营店铺
     * @param platform
     */
    public void setPlatform(boolean platform) {
        this.platform = platform;
    }

}
