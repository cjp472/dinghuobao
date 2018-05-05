package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;

/**系统设置
 *                       
 * @Filename: SysConfig.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_sys_config")
public class SysConfig extends CommonEntity {
    private String          title;
    private String          keywords;
    private String          description;
    private String          address;
    private String          copyRight;
    private String          uploadFilePath;
    private String          sysLanguage;
    private int             integralRate;
    private boolean         smsEnbale;
    private String          smsURL;
    private String          smsUserName;
    private String          smsPassword;
    private String          smsTest;
    private boolean         emailEnable;
    private String          emailHost;
    private int             emailPort;
    private String          emailUser;
    private String          emailUserName;
    private String          emailPws;
    private String          emailTest;
    private String          websiteName;
    private String          hotSearch;
    @Column(columnDefinition = "varchar(255) default 'blue' ")
    private String          websiteCss;
    @OneToOne(fetch = FetchType.EAGER)
    private Accessory       websiteLogo;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          codeStat;
    private boolean         websiteState;
    private boolean         visitorConsult;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          closeReason;
    private String          securityCodeType;
    private boolean         securityCodeRegister;
    private boolean         securityCodeLogin;
    private boolean         securityCodeConsult;
    private String          imageSuffix;
    private String          imageWebServer;
    private int             imageFilesize;
    private int             smallWidth;
    private int             smallHeight;
    private int             middleWidth;
    private int             middleHeight;
    private int             bigWidth;
    private int             bigHeight;
    private boolean         integral;
    private boolean         integralStore;
    private boolean         voucher;
    private boolean         deposit;
    private boolean         groupBuy;
    private boolean         gold;
    private int             goldMarketValue;
    private int             memberRegister;
    private int             memberDayLogin;
    private int             indentComment;
    private int             consumptionRatio;
    private int             everyIndentLimit;
    private String          imageSaveType;
    private int             complaint_time;
    @OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.LAZY)
    private Accessory       storeImage;
    @OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.LAZY)
    private Accessory       goodsImage;
    @OneToOne(cascade = { javax.persistence.CascadeType.ALL }, fetch = FetchType.LAZY)
    private Accessory       memberIcon;
    private boolean         store_allow;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          creditrule;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          user_creditrule;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          templates;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          store_payment;
    @Lob
    @Column(columnDefinition = "LongText")
    private String          share_code;
    private boolean         ztc_status;
    @Column(columnDefinition = "int default 0")
    private int             ztc_goods_view;
    private int             ztc_price;
    @Column(columnDefinition = "bit default 0")
    private boolean         second_domain_open;                    //是否开户二级域名   0为关  1为开
    @Column(columnDefinition = "int default 0")
    @FormIgnore
    private int             domain_allow_count;
    @Column(columnDefinition = "LongText")
    @FormIgnore
    private String          sys_domain;
    @Column(columnDefinition = "bit default 0")
    private boolean         qq_login;
    private String          qq_login_id;
    private String          qq_login_key;
    @Column(columnDefinition = "LongText")
    private String          qq_domain_code;
    @Column(columnDefinition = "bit default 0")
    private boolean         sina_login;
    private String          sina_login_id;
    private String          sina_login_key;
    @Column(columnDefinition = "LongText")
    private String          sina_domain_code;
    private Date            lucene_update;
    @Column(columnDefinition = "int default 0")
    @FormIgnore
    private int             alipay_fenrun;
    @Column(columnDefinition = "int default 0")
    @FormIgnore
    private int             balance_fenrun;
    private String          bargain_title;
    @Column(columnDefinition = "int default 0")
    private int             bargain_status;
    @Column(columnDefinition = "int default 3")
    private int             bargain_validity;
    @Column(precision = 3, scale = 2)
    private BigDecimal      bargain_rebate;
    @Column(columnDefinition = "int default 0")
    private int             bargain_maximum;
    @Column(columnDefinition = "LongText")
    private String          bargain_state;
    private String          delivery_title;
    @Column(columnDefinition = "int default 0")
    private int             delivery_status;
    @Column(columnDefinition = "int default 0")
    private int             delivery_amount;
    @Column(columnDefinition = "int default 0")
    private int             combin_amount;
    @Column(columnDefinition = "int default 3")
    private int             combin_count;
    @OneToMany(mappedBy = "config")
    private List<Accessory> login_imgs           = new ArrayList();
    @Column(columnDefinition = "LongText")
    private String          service_telphone_list;
    @Column(columnDefinition = "LongText")
    private String          service_qq_list;
    @Column(columnDefinition = "bit default 0")
    private boolean         uc_bbs;
    private String          uc_database          = "";
    private String          uc_table_preffix     = "";
    private String          uc_database_url      = "";
    private String          uc_database_port     = "";
    private String          uc_database_username = "";
    private String          uc_database_pws      = "";
    private String          uc_api;
    private String          uc_ip;
    private String          uc_key;
    private String          uc_appid;
    @Column(columnDefinition = "int default 3")
    @FormIgnore
    private int             auto_order_notice;
    @Column(columnDefinition = "int default 7")
    @FormIgnore
    private int             auto_order_confirm;
    @Column(columnDefinition = "int default 7")
    @FormIgnore
    private int             auto_order_return;
    @Column(columnDefinition = "int default 7")
    @FormIgnore
    private int             auto_order_evaluate;
    @Column(columnDefinition = "LongText")
    private String          kuaidi_id;
    @Column(columnDefinition = "varchar(255) default '¥'")
    private String          currency_code;

    @FormIgnore
    @Column(columnDefinition = "int default 0")
    private int             config_payment_type;

    private String          front_web_path;                        //自营店铺域名
    private String          front_web_after_path;                  //自营店铺域名后缀

    /**
     * 公司信息(页面最下方显示)
     */
    private String          copy_right;
    /**
     * 备案信息
     */
    private String          record;
    
    private String		   access_token;//平台微信支付accessToken
    
    
    /**
     * 平台微信支付accessToken
     * @return
     */
    public String getAccess_token() {
		return access_token;
	}
    /**
     * 平台微信支付accessToken
     * @param access_token
     */
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getCopy_right() {
        return copy_right;
    }

    public void setCopy_right(String copy_right) {
        this.copy_right = copy_right;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public int getConfig_payment_type() {
        return this.config_payment_type;
    }

    public void setConfig_payment_type(int config_payment_type) {
        this.config_payment_type = config_payment_type;
    }

    public int getAuto_order_return() {
        return this.auto_order_return;
    }

    public void setAuto_order_return(int auto_order_return) {
        this.auto_order_return = auto_order_return;
    }

    public int getAuto_order_evaluate() {
        return this.auto_order_evaluate;
    }

    public void setAuto_order_evaluate(int auto_order_evaluate) {
        this.auto_order_evaluate = auto_order_evaluate;
    }

    public int getZtc_goods_view() {
        return this.ztc_goods_view;
    }

    public void setZtc_goods_view(int ztc_goods_view) {
        this.ztc_goods_view = ztc_goods_view;
    }

    public String getWebsiteCss() {
        return this.websiteCss;
    }

    public void setWebsiteCss(String websiteCss) {
        this.websiteCss = websiteCss;
    }

    public String getCurrency_code() {
        return this.currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public boolean isUc_bbs() {
        return this.uc_bbs;
    }

    public void setUc_bbs(boolean uc_bbs) {
        this.uc_bbs = uc_bbs;
    }

    public List<Accessory> getLogin_imgs() {
        return this.login_imgs;
    }

    public void setLogin_imgs(List<Accessory> login_imgs) {
        this.login_imgs = login_imgs;
    }

    public int getBargain_status() {
        return this.bargain_status;
    }

    public void setBargain_status(int bargain_status) {
        this.bargain_status = bargain_status;
    }

    public int getBargain_validity() {
        return this.bargain_validity;
    }

    public void setBargain_validity(int bargain_validity) {
        this.bargain_validity = bargain_validity;
    }

    public BigDecimal getBargain_rebate() {
        return this.bargain_rebate;
    }

    public void setBargain_rebate(BigDecimal bargain_rebate) {
        this.bargain_rebate = bargain_rebate;
    }

    public int getBargain_maximum() {
        return this.bargain_maximum;
    }

    public void setBargain_maximum(int bargain_maximum) {
        this.bargain_maximum = bargain_maximum;
    }

    public Date getLucene_update() {
        return this.lucene_update;
    }

    public void setLucene_update(Date lucene_update) {
        this.lucene_update = lucene_update;
    }

    public boolean isSina_login() {
        return this.sina_login;
    }

    public void setSina_login(boolean sina_login) {
        this.sina_login = sina_login;
    }

    public String getSina_login_id() {
        return this.sina_login_id;
    }

    public void setSina_login_id(String sina_login_id) {
        this.sina_login_id = sina_login_id;
    }

    public String getSina_login_key() {
        return this.sina_login_key;
    }

    public void setSina_login_key(String sina_login_key) {
        this.sina_login_key = sina_login_key;
    }

    public String getSina_domain_code() {
        return this.sina_domain_code;
    }

    public void setSina_domain_code(String sina_domain_code) {
        this.sina_domain_code = sina_domain_code;
    }

    public boolean isQq_login() {
        return this.qq_login;
    }

    public void setQq_login(boolean qq_login) {
        this.qq_login = qq_login;
    }

    public String getQq_login_id() {
        return this.qq_login_id;
    }

    public void setQq_login_id(String qq_login_id) {
        this.qq_login_id = qq_login_id;
    }

    public String getQq_login_key() {
        return this.qq_login_key;
    }

    public void setQq_login_key(String qq_login_key) {
        this.qq_login_key = qq_login_key;
    }

    public int getDomain_allow_count() {
        return this.domain_allow_count;
    }

    public void setDomain_allow_count(int domain_allow_count) {
        this.domain_allow_count = domain_allow_count;
    }

    public String getSys_domain() {
        return this.sys_domain;
    }

    public void setSys_domain(String sys_domain) {
        this.sys_domain = sys_domain;
    }

    public boolean isZtc_status() {
        return this.ztc_status;
    }

    public void setZtc_status(boolean ztc_status) {
        this.ztc_status = ztc_status;
    }

    public int getZtc_price() {
        return this.ztc_price;
    }

    public void setZtc_price(int ztc_price) {
        this.ztc_price = ztc_price;
    }

    public String getTemplates() {
        return this.templates;
    }

    public void setTemplates(String templates) {
        this.templates = templates;
    }

    public boolean isStore_allow() {
        return this.store_allow;
    }

    public void setStore_allow(boolean store_allow) {
        this.store_allow = store_allow;
    }

    public Accessory getStoreImage() {
        return this.storeImage;
    }

    public void setStoreImage(Accessory storeImage) {
        this.storeImage = storeImage;
    }

    public Accessory getGoodsImage() {
        return this.goodsImage;
    }

    public void setGoodsImage(Accessory goodsImage) {
        this.goodsImage = goodsImage;
    }

    public Accessory getMemberIcon() {
        return this.memberIcon;
    }

    public void setMemberIcon(Accessory memberIcon) {
        this.memberIcon = memberIcon;
    }

    public String getEmailHost() {
        return this.emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public int getEmailPort() {
        return this.emailPort;
    }

    public void setEmailPort(int emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailUser() {
        return this.emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getEmailUserName() {
        return this.emailUserName;
    }

    public void setEmailUserName(String emailUserName) {
        this.emailUserName = emailUserName;
    }

    public String getEmailPws() {
        return this.emailPws;
    }

    public void setEmailPws(String emailPws) {
        this.emailPws = emailPws;
    }

    public String getSysLanguage() {
        return this.sysLanguage;
    }

    public void setSysLanguage(String sysLanguage) {
        this.sysLanguage = sysLanguage;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSmsURL() {
        return this.smsURL;
    }

    public void setSmsURL(String smsURL) {
        this.smsURL = smsURL;
    }

    public String getSmsUserName() {
        return this.smsUserName;
    }

    public void setSmsUserName(String smsUserName) {
        this.smsUserName = smsUserName;
    }

    public String getSmsPassword() {
        return this.smsPassword;
    }

    public void setSmsPassword(String smsPassword) {
        this.smsPassword = smsPassword;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIntegralRate() {
        return this.integralRate;
    }

    public void setIntegralRate(int integralRate) {
        this.integralRate = integralRate;
    }

    public String getCopyRight() {
        return this.copyRight;
    }

    public void setCopyRight(String copyRight) {
        this.copyRight = copyRight;
    }

    public String getWebsiteName() {
        return this.websiteName;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public String getHotSearch() {
        return this.hotSearch;
    }

    public void setHotSearch(String hotSearch) {
        this.hotSearch = hotSearch;
    }

    public Accessory getWebsiteLogo() {
        return this.websiteLogo;
    }

    public void setWebsiteLogo(Accessory websiteLogo) {
        this.websiteLogo = websiteLogo;
    }

    public String getCodeStat() {
        return this.codeStat;
    }

    public void setCodeStat(String codeStat) {
        this.codeStat = codeStat;
    }

    public boolean isWebsiteState() {
        return this.websiteState;
    }

    public void setWebsiteState(boolean websiteState) {
        this.websiteState = websiteState;
    }

    public String getCloseReason() {
        return this.closeReason;
    }

    public void setCloseReason(String closeReason) {
        this.closeReason = closeReason;
    }

    public boolean isEmailEnable() {
        return this.emailEnable;
    }

    public void setEmailEnable(boolean emailEnable) {
        this.emailEnable = emailEnable;
    }

    public String getEmailTest() {
        return this.emailTest;
    }

    public void setEmailTest(String emailTest) {
        this.emailTest = emailTest;
    }

    public boolean isSecurityCodeRegister() {
        return this.securityCodeRegister;
    }

    public void setSecurityCodeRegister(boolean securityCodeRegister) {
        this.securityCodeRegister = securityCodeRegister;
    }

    public boolean isSecurityCodeLogin() {
        return this.securityCodeLogin;
    }

    public void setSecurityCodeLogin(boolean securityCodeLogin) {
        this.securityCodeLogin = securityCodeLogin;
    }

    public boolean isSecurityCodeConsult() {
        return this.securityCodeConsult;
    }

    public void setSecurityCodeConsult(boolean securityCodeConsult) {
        this.securityCodeConsult = securityCodeConsult;
    }

    public boolean isVisitorConsult() {
        return this.visitorConsult;
    }

    public void setVisitorConsult(boolean visitorConsult) {
        this.visitorConsult = visitorConsult;
    }

    public String getImageSuffix() {
        return this.imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public int getImageFilesize() {
        return this.imageFilesize;
    }

    public void setImageFilesize(int imageFilesize) {
        this.imageFilesize = imageFilesize;
    }

    public int getSmallWidth() {
        return this.smallWidth;
    }

    public void setSmallWidth(int smallWidth) {
        this.smallWidth = smallWidth;
    }

    public int getSmallHeight() {
        return this.smallHeight;
    }

    public void setSmallHeight(int smallHeight) {
        this.smallHeight = smallHeight;
    }

    public int getMiddleWidth() {
        return this.middleWidth;
    }

    public void setMiddleWidth(int middleWidth) {
        this.middleWidth = middleWidth;
    }

    public int getMiddleHeight() {
        return this.middleHeight;
    }

    public void setMiddleHeight(int middleHeight) {
        this.middleHeight = middleHeight;
    }

    public int getBigWidth() {
        return this.bigWidth;
    }

    public void setBigWidth(int bigWidth) {
        this.bigWidth = bigWidth;
    }

    public int getBigHeight() {
        return this.bigHeight;
    }

    public void setBigHeight(int bigHeight) {
        this.bigHeight = bigHeight;
    }

    public String getImageSaveType() {
        return this.imageSaveType;
    }

    public void setImageSaveType(String imageSaveType) {
        this.imageSaveType = imageSaveType;
    }

    public String getSecurityCodeType() {
        return this.securityCodeType;
    }

    public void setSecurityCodeType(String securityCodeType) {
        this.securityCodeType = securityCodeType;
    }

    public boolean isIntegral() {
        return this.integral;
    }

    public void setIntegral(boolean integral) {
        this.integral = integral;
    }

    public boolean isIntegralStore() {
        return this.integralStore;
    }

    public void setIntegralStore(boolean integralStore) {
        this.integralStore = integralStore;
    }

    public boolean isVoucher() {
        return this.voucher;
    }

    public void setVoucher(boolean voucher) {
        this.voucher = voucher;
    }

    public boolean isDeposit() {
        return this.deposit;
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
    }

    public boolean isGroupBuy() {
        return this.groupBuy;
    }

    public void setGroupBuy(boolean groupBuy) {
        this.groupBuy = groupBuy;
    }

    public boolean isGold() {
        return this.gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }

    public int getGoldMarketValue() {
        return this.goldMarketValue;
    }

    public void setGoldMarketValue(int goldMarketValue) {
        this.goldMarketValue = goldMarketValue;
    }

    public int getMemberRegister() {
        return this.memberRegister;
    }

    public void setMemberRegister(int memberRegister) {
        this.memberRegister = memberRegister;
    }

    public int getMemberDayLogin() {
        return this.memberDayLogin;
    }

    public void setMemberDayLogin(int memberDayLogin) {
        this.memberDayLogin = memberDayLogin;
    }

    public int getIndentComment() {
        return this.indentComment;
    }

    public void setIndentComment(int indentComment) {
        this.indentComment = indentComment;
    }

    public int getConsumptionRatio() {
        return this.consumptionRatio;
    }

    public void setConsumptionRatio(int consumptionRatio) {
        this.consumptionRatio = consumptionRatio;
    }

    public int getEveryIndentLimit() {
        return this.everyIndentLimit;
    }

    public void setEveryIndentLimit(int everyIndentLimit) {
        this.everyIndentLimit = everyIndentLimit;
    }

    public boolean isSmsEnbale() {
        return this.smsEnbale;
    }

    public void setSmsEnbale(boolean smsEnbale) {
        this.smsEnbale = smsEnbale;
    }

    public String getSmsTest() {
        return this.smsTest;
    }

    public void setSmsTest(String smsTest) {
        this.smsTest = smsTest;
    }

    public String getCreditrule() {
        return this.creditrule;
    }

    public void setCreditrule(String creditrule) {
        this.creditrule = creditrule;
    }

    public String getUploadFilePath() {
        return this.uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStore_payment() {
        return this.store_payment;
    }

    public void setStore_payment(String store_payment) {
        this.store_payment = store_payment;
    }

    public String getShare_code() {
        return this.share_code;
    }

    public void setShare_code(String share_code) {
        this.share_code = share_code;
    }

    public String getUser_creditrule() {
        return this.user_creditrule;
    }

    public void setUser_creditrule(String user_creditrule) {
        this.user_creditrule = user_creditrule;
    }

    public int getComplaint_time() {
        return this.complaint_time;
    }

    public void setComplaint_time(int complaint_time) {
        this.complaint_time = complaint_time;
    }

    public boolean isSecond_domain_open() {
        return this.second_domain_open;
    }

    public void setSecond_domain_open(boolean second_domain_open) {
        this.second_domain_open = second_domain_open;
    }

    public String getQq_domain_code() {
        return this.qq_domain_code;
    }

    public void setQq_domain_code(String qq_domain_code) {
        this.qq_domain_code = qq_domain_code;
    }

    public String getImageWebServer() {
        return this.imageWebServer;
    }

    public void setImageWebServer(String imageWebServer) {
        this.imageWebServer = imageWebServer;
    }

    public int getAlipay_fenrun() {
        return this.alipay_fenrun;
    }

    public void setAlipay_fenrun(int alipay_fenrun) {
        this.alipay_fenrun = alipay_fenrun;
    }

    public int getBalance_fenrun() {
        return this.balance_fenrun;
    }

    public void setBalance_fenrun(int balance_fenrun) {
        this.balance_fenrun = balance_fenrun;
    }

    public String getBargain_title() {
        return this.bargain_title;
    }

    public void setBargain_title(String bargain_title) {
        this.bargain_title = bargain_title;
    }

    public String getBargain_state() {
        return this.bargain_state;
    }

    public void setBargain_state(String bargain_state) {
        this.bargain_state = bargain_state;
    }

    public String getDelivery_title() {
        return this.delivery_title;
    }

    public void setDelivery_title(String delivery_title) {
        this.delivery_title = delivery_title;
    }

    public int getDelivery_status() {
        return this.delivery_status;
    }

    public void setDelivery_status(int delivery_status) {
        this.delivery_status = delivery_status;
    }

    public String getService_telphone_list() {
        return this.service_telphone_list;
    }

    public void setService_telphone_list(String service_telphone_list) {
        this.service_telphone_list = service_telphone_list;
    }

    public String getService_qq_list() {
        return this.service_qq_list;
    }

    public void setService_qq_list(String service_qq_list) {
        this.service_qq_list = service_qq_list;
    }

    public int getAuto_order_confirm() {
        return this.auto_order_confirm;
    }

    public void setAuto_order_confirm(int auto_order_confirm) {
        this.auto_order_confirm = auto_order_confirm;
    }

    public int getAuto_order_notice() {
        return this.auto_order_notice;
    }

    public void setAuto_order_notice(int auto_order_notice) {
        this.auto_order_notice = auto_order_notice;
    }

    public String getKuaidi_id() {
        return this.kuaidi_id;
    }

    public void setKuaidi_id(String kuaidi_id) {
        this.kuaidi_id = kuaidi_id;
    }

    public String getUc_database() {
        return this.uc_database;
    }

    public void setUc_database(String uc_database) {
        this.uc_database = uc_database;
    }

    public String getUc_table_preffix() {
        return this.uc_table_preffix;
    }

    public void setUc_table_preffix(String uc_table_preffix) {
        this.uc_table_preffix = uc_table_preffix;
    }

    public String getUc_database_url() {
        return this.uc_database_url;
    }

    public void setUc_database_url(String uc_database_url) {
        this.uc_database_url = uc_database_url;
    }

    public String getUc_database_port() {
        return this.uc_database_port;
    }

    public void setUc_database_port(String uc_database_port) {
        this.uc_database_port = uc_database_port;
    }

    public String getUc_database_username() {
        return this.uc_database_username;
    }

    public void setUc_database_username(String uc_database_username) {
        this.uc_database_username = uc_database_username;
    }

    public String getUc_database_pws() {
        return this.uc_database_pws;
    }

    public void setUc_database_pws(String uc_database_pws) {
        this.uc_database_pws = uc_database_pws;
    }

    public String getUc_api() {
        return this.uc_api;
    }

    public void setUc_api(String uc_api) {
        this.uc_api = uc_api;
    }

    public String getUc_ip() {
        return this.uc_ip;
    }

    public void setUc_ip(String uc_ip) {
        this.uc_ip = uc_ip;
    }

    public String getUc_key() {
        return this.uc_key;
    }

    public void setUc_key(String uc_key) {
        this.uc_key = uc_key;
    }

    public String getUc_appid() {
        return this.uc_appid;
    }

    public void setUc_appid(String uc_appid) {
        this.uc_appid = uc_appid;
    }

    public int getDelivery_amount() {
        return this.delivery_amount;
    }

    public void setDelivery_amount(int delivery_amount) {
        this.delivery_amount = delivery_amount;
    }

    public int getCombin_amount() {
        return this.combin_amount;
    }

    public void setCombin_amount(int combin_amount) {
        this.combin_amount = combin_amount;
    }

    public int getCombin_count() {
        return this.combin_count;
    }

    public void setCombin_count(int combin_count) {
        this.combin_count = combin_count;
    }

    public String getFront_web_path() {
        return front_web_path;
    }

    public void setFront_web_path(String front_web_path) {
        this.front_web_path = front_web_path;
    }

    public String getFront_web_after_path() {
        return front_web_after_path;
    }

    public void setFront_web_after_path(String front_web_after_path) {
        this.front_web_after_path = front_web_after_path;
    }

}
