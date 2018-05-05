package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_payment")
public class Payment extends CommonEntity {
    /**
     *Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private boolean           install;
    private String            name;
    private String            mark;
    private String            safeKey;
    private String            partner;
    private String            seller_email;
    private int               interfaceType;
    @Column(precision = 12, scale = 2)
    private BigDecimal        alipay_rate;
    @Column(precision = 12, scale = 2)
    private BigDecimal        alipay_divide_rate;
    private String            merchantAcctId;
    private String            rmbKey;
    private String            pid;
    private String            spname;
    private String            tenpay_partner;
    private String            tenpay_key;
    private int               trade_mode;
    private String            chinabank_account;
    private String            chinabank_key;
    private String            chinabank_des_key;
    private String            chinabank_rsa_key;
    @Lob
    @Column(columnDefinition = "LongText")
    private String            content;
    @Column(precision = 12, scale = 2)
    private BigDecimal        balance_divide_rate;
    private String            paypal_userId;
    private String            currency_code;
    @Column(precision = 12, scale = 2)
    private BigDecimal        poundage;
    private String            type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Store             store;
    @OneToMany(mappedBy = "payment", fetch = FetchType.EAGER)
    private List<OrderForm>   ofs              = new ArrayList<OrderForm>();

    private String 			weixin_appId;//微信支付appid
    private String			weixin_appSecret;//微信支付appSecret
    private String			weixin_partnerId;//商户号partnerId
    private String			weixin_partnerKey;//商户号密钥
    private String			weixin_paySignKey;//
    
    /**
     * 微信支付appid
     * @return
     */
    public String getWeixin_appId() {
		return weixin_appId;
	}
    /**
     * 微信支付appid
     * @param weixin_appId
     */
	public void setWeixin_appId(String weixin_appId) {
		this.weixin_appId = weixin_appId;
	}
	/**
	 * 微信支付appSecret
	 * @return
	 */
	public String getWeixin_appSecret() {
		return weixin_appSecret;
	}
	/**
	 * 微信支付appSecret
	 * @param weixin_appSecret
	 */
	public void setWeixin_appSecret(String weixin_appSecret) {
		this.weixin_appSecret = weixin_appSecret;
	}
	/**
	 * 商户号partnerId
	 * @return
	 */
	public String getWeixin_partnerId() {
		return weixin_partnerId;
	}
	/**
	 * 商户号partnerId
	 * @return
	 */
	public void setWeixin_partnerId(String weixin_partnerId) {
		this.weixin_partnerId = weixin_partnerId;
	}
	/**
	 * 商户号密钥
	 * @return
	 */
	public String getWeixin_partnerKey() {
		return weixin_partnerKey;
	}
	/**
	 * 商户号密钥
	 * @param weixin_partnerKey
	 */
	public void setWeixin_partnerKey(String weixin_partnerKey) {
		this.weixin_partnerKey = weixin_partnerKey;
	}

	public String getWeixin_paySignKey() {
		return weixin_paySignKey;
	}

	public void setWeixin_paySignKey(String weixin_paySignKey) {
		this.weixin_paySignKey = weixin_paySignKey;
	}

	public String getPaypal_userId() {
        return this.paypal_userId;
    }

    public void setPaypal_userId(String paypal_userId) {
        this.paypal_userId = paypal_userId;
    }

    public String getCurrency_code() {
        return this.currency_code;
    }

    public void setCurrency_code(String currency_code) {
        this.currency_code = currency_code;
    }

    public BigDecimal getPoundage() {
        return this.poundage;
    }

    public void setPoundage(BigDecimal poundage) {
        this.poundage = poundage;
    }

    public BigDecimal getAlipay_rate() {
        return this.alipay_rate;
    }

    public void setAlipay_rate(BigDecimal alipay_rate) {
        this.alipay_rate = alipay_rate;
    }

    public BigDecimal getAlipay_divide_rate() {
        return this.alipay_divide_rate;
    }

    public void setAlipay_divide_rate(BigDecimal alipay_divide_rate) {
        this.alipay_divide_rate = alipay_divide_rate;
    }

    public BigDecimal getBalance_divide_rate() {
        return this.balance_divide_rate;
    }

    public void setBalance_divide_rate(BigDecimal balance_divide_rate) {
        this.balance_divide_rate = balance_divide_rate;
    }

    public List<OrderForm> getOfs() {
        return this.ofs;
    }

    public void setOfs(List<OrderForm> ofs) {
        this.ofs = ofs;
    }

    public boolean isInstall() {
        return this.install;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setInstall(boolean install) {
        this.install = install;
    }

    public String getMark() {
        return this.mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getSafeKey() {
        return this.safeKey;
    }

    public void setSafeKey(String safeKey) {
        this.safeKey = safeKey;
    }

    public String getPartner() {
        return this.partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getSeller_email() {
        return this.seller_email;
    }

    public void setSeller_email(String seller_email) {
        this.seller_email = seller_email;
    }

    public int getInterfaceType() {
        return this.interfaceType;
    }

    public void setInterfaceType(int interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getMerchantAcctId() {
        return this.merchantAcctId;
    }

    public void setMerchantAcctId(String merchantAcctId) {
        this.merchantAcctId = merchantAcctId;
    }

    public String getRmbKey() {
        return this.rmbKey;
    }

    public void setRmbKey(String rmbKey) {
        this.rmbKey = rmbKey;
    }

    public String getPid() {
        return this.pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChinabank_account() {
        return this.chinabank_account;
    }

    public void setChinabank_account(String chinabank_account) {
        this.chinabank_account = chinabank_account;
    }

    public String getChinabank_key() {
        return this.chinabank_key;
    }

    public void setChinabank_key(String chinabank_key) {
        this.chinabank_key = chinabank_key;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpname() {
        return this.spname;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }

    public String getTenpay_partner() {
        return this.tenpay_partner;
    }

    public void setTenpay_partner(String tenpay_partner) {
        this.tenpay_partner = tenpay_partner;
    }

    public String getTenpay_key() {
        return this.tenpay_key;
    }

    public void setTenpay_key(String tenpay_key) {
        this.tenpay_key = tenpay_key;
    }

    public int getTrade_mode() {
        return this.trade_mode;
    }

    public void setTrade_mode(int trade_mode) {
        this.trade_mode = trade_mode;
    }

    public String getChinabank_rsa_key() {
        return chinabank_rsa_key;
    }

    public void setChinabank_rsa_key(String chinabank_rsa_key) {
        this.chinabank_rsa_key = chinabank_rsa_key;
    }

    public String getChinabank_des_key() {
        return chinabank_des_key;
    }

    public void setChinabank_des_key(String chinabank_des_key) {
        this.chinabank_des_key = chinabank_des_key;
    }

}
