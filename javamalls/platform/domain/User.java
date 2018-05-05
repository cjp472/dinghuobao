package com.javamalls.platform.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.javamalls.base.annotation.FormIgnore;
import com.javamalls.base.domain.CommonEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_user")
public class User extends CommonEntity implements UserDetails {
    private static final long      serialVersionUID     = 8026813053768023527L;
    private String                 userName;//用户名
    private String                 trueName;//真实名称
    @FormIgnore
    private String                 password;//密码
    private String                 userRole;//用户角色
    private Date                   birthday;//生日
    private String                 telephone;//电话
    private String                 QQ;//QQ
    @Column(columnDefinition = "int default 0")
    private int                    years;//年龄
    private String                 address;//详细地址
    private int                    sex;//性别
    private String                 email;//邮箱
    private String                 mobile;//手机号
    @OneToOne
    private Accessory              photo;//头像
    @OneToOne
    private Area                   area;//地区
    private int                    status;//状态

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinTable(name = "jm_user_role", joinColumns = { @javax.persistence.JoinColumn(name = "user_id") }, inverseJoinColumns = { @javax.persistence.JoinColumn(name = "role_id") })
    @JsonIgnore
    private Set<Role>              roles                = new TreeSet();
    
    
    @Transient
    @JsonIgnore
    private Map<String, List<Res>> roleResources;
    private Date                   lastLoginDate;//最后登录日期
    private Date                   loginDate;//登录日期
    private String                 lastLoginIp;//最后登录ip
    private String                 loginIp;//登录ip
    private int                    loginCount;//登录次数
    private int                    report;//允许举报
    @FormIgnore
    @Column(precision = 12, scale = 2)
    private BigDecimal             availableBalance;//可用余额
    @FormIgnore
    @Column(precision = 12, scale = 2)
    private BigDecimal             freezeBlance;//冻结金额
    @FormIgnore
    private int                    integral;//积分
    @FormIgnore
    private int                    gold;//金币
    @OneToOne(mappedBy = "user")
    private UserConfig             config;
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Accessory>        files                = new ArrayList<Accessory>();
    @OneToOne(cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private Store                  store;//商铺
    @ManyToOne
    @JsonIgnore
    private User                   parent;
    @OneToMany(mappedBy = "parent")
    @Where(clause = "disabled=0")
    @JsonIgnore
    private List<User>             childs               = new ArrayList<User>();
    @OneToMany(mappedBy = "salesMan")
    @JsonIgnore
    private List<User>             salesMan_clients     = new ArrayList<User>();     //业务员下的客户列表
    @FormIgnore
    private int                    user_credit;                                      // 买家信用
    @Transient
    private GrantedAuthority[]     authorities          = new GrantedAuthority[0];
    private String                 qq_openid;//qq身份标识 未使用
    private String                 sina_openid;//新浪身份标识 未使用
    private String				   wx_openid;//微信身份标识
    @Column(columnDefinition = "LongText")
    private String                 store_quick_menu;//商店菜单 未使用
    @OneToMany(mappedBy = "pd_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Predeposit>       posits               = new ArrayList();
    @OneToMany(mappedBy = "pd_log_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<PredepositLog>    user_predepositlogs  = new ArrayList();
    @OneToMany(mappedBy = "pd_log_admin", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<PredepositLog>    admin_predepositlogs = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Address>          addrs                = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Album>            albums               = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Favorite>         favs                 = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<UserGoodsClass>   ugcs                 = new ArrayList();
    @OneToMany(mappedBy = "fromUser", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Message>          from_msgs            = new ArrayList();
    @OneToMany(mappedBy = "toUser", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Message>          to_msgs              = new ArrayList();
    @OneToMany(mappedBy = "gold_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<GoldRecord>       gold_record          = new ArrayList();
    @OneToMany(mappedBy = "gold_admin", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<GoldRecord>       gold_record_admin    = new ArrayList();
    @OneToMany(mappedBy = "integral_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<IntegralLog>      integral_logs        = new ArrayList();
    @OneToMany(mappedBy = "operate_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<IntegralLog>      integral_admin_logs  = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<SysLog>           syslogs              = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Accessory>        accs                 = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<OrderForm>        ofs                  = new ArrayList();
    @OneToMany(mappedBy = "consult_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Consult>          user_consults        = new ArrayList();
    @OneToMany(mappedBy = "reply_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Consult>          seller_consults      = new ArrayList();
    @OneToMany(mappedBy = "evaluate_seller_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Evaluate>         seller_evaluate      = new ArrayList();
    @OneToMany(mappedBy = "evaluate_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<Evaluate>         user_evaluate        = new ArrayList();
    @OneToMany(mappedBy = "log_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<OrderFormLog>     ofls                 = new ArrayList();
    @OneToMany(mappedBy = "refund_user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<RefundLog>        rls                  = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<SpareGoods>       sgs                  = new ArrayList();
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @JsonIgnore
    private List<GoodsBrand>       brands               = new ArrayList();
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private CompanyInfo            companyInfo;

    private String                 bankName;                                         //订货商开户人名称
    private String                 accountName;                                      //订货商开户人名称
    private String                 bankAccount;                                      //订货商开户账号
    private String                 taxpayer_number;                                  //订货商纳税人识别号
    private String                 header_invoice;                                   //订货商发票抬头
    private String                 cus_ser_code;                                     //客服代码

    @Transient
    private String                 modelid;

    //   private Integer userOfstore;//会员所属店铺

    private Integer                salesManState;                                    //是否是业务员 1是，2是子账户
    @ManyToOne(fetch = FetchType.LAZY)
    private StoreDepartment        department;                                       //业务员所属部门
    @ManyToOne(fetch = FetchType.LAZY)
    private User                   salesMan;                                         //客户所属业务员
    @Transient
    private UserStoreRelation      relation;

    @ManyToOne
    private Strategy strategy;   //客户的价格策略  此字段已做废
    @OneToMany(mappedBy = "user", cascade = { javax.persistence.CascadeType.REMOVE })
    @Where(clause = "disabled=0")
    private List<UserStrategy>              userStrategys                = new ArrayList<UserStrategy>();//价格策略关联列表

    private String   client_name; //客户名称

   
    /**
     * 价格策略关联列表
     * @return
     */
    public List<UserStrategy> getUserStrategys() {
		return userStrategys;
	}

    /**
     * 价格策略关联列表
     * @param userStrategys
     */
	public void setUserStrategys(List<UserStrategy> userStrategys) {
		this.userStrategys = userStrategys;
	}


	/**
     * 价格策略列表
     * storeId
     * @return
     */
    public  Strategy getStrategy(Long storeId) {
    	Strategy ret = null;
    	if(userStrategys!=null&&userStrategys.size()>0){
    		for(UserStrategy ss:userStrategys){
    			if(ss.getStore_id().equals(storeId)){
    				return ss.getStrategy();
    			}
    			 
    		}
    	}
		return ret;
	}
    
    
	/**
     * 客户的价格策略
     * @return
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * 客户的价格策略
     * @param strategy
     */
    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
	/**
     * 客户名称
     * @return
     */
    public String getClient_name() {
        return client_name;
    }
    /**
     * 客户名称
     * @param client_name
     */
    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    
    
    /**
     * 微信身份标识
     * @return
     */
    public String getWx_openid() {
		return wx_openid;
	}
    /**
     * 微信身份标识
     * @param wx_openid
     */
	public void setWx_openid(String wx_openid) {
		this.wx_openid = wx_openid;
	}

	public UserStoreRelation getRelation() {
        return relation;
    }

    public void setRelation(UserStoreRelation relation) {
        this.relation = relation;
    }

    public String getTaxpayer_number() {
        return taxpayer_number;
    }

    public void setTaxpayer_number(String taxpayer_number) {
        this.taxpayer_number = taxpayer_number;
    }

    public String getHeader_invoice() {
        return header_invoice;
    }

    public void setHeader_invoice(String header_invoice) {
        this.header_invoice = header_invoice;
    }

   
    /**
     * 业务员所属部门
     * @return
     */
    public StoreDepartment getDepartment() {
        return department;
    }

    /**
     * 业务员所属部门
     * @param department
     */
    public void setDepartment(StoreDepartment department) {
        this.department = department;
    }

    /**
     * 业务员下的客户列表
     * @return
     */
    public List<User> getSalesMan_clients() {
        return salesMan_clients;
    }

    /**
     * 业务员下的客户列表
     * @param salesMan_clients
     */
    public void setSalesMan_clients(List<User> salesMan_clients) {
        this.salesMan_clients = salesMan_clients;
    }

    /**
     * 是否是业务员 1是，2是子账户
     * @return
     */
    public Integer getSalesManState() {
        return salesManState;
    }

    /**
     * 是否是业务员 1是，2是子账户
     * @param salesManState
     */
    public void setSalesManState(Integer salesManState) {
        this.salesManState = salesManState;
    }

    /**
     * 客户所属业务员
     * @return
     */
    public User getSalesMan() {
        return salesMan;
    }

    /**
     * 客户所属业务员
     * @param salesMan
     */
    public void setSalesMan(User salesMan) {
        this.salesMan = salesMan;
    }

    /*public Integer getUserOfstore() {
    	return userOfstore;
    }

    public void setUserOfstore(Integer userOfstore) {
    	this.userOfstore = userOfstore;
    }*/

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public List<GoodsBrand> getBrands() {
        return this.brands;
    }

    public void setBrands(List<GoodsBrand> brands) {
        this.brands = brands;
    }

    public List<SpareGoods> getSgs() {
        return this.sgs;
    }

    public void setSgs(List<SpareGoods> sgs) {
        this.sgs = sgs;
    }

    public int getYears() {
        return this.years;
    }

    public void setYears(int years) {
        this.years = years;
    }

    public Area getArea() {
        return this.area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public List<OrderFormLog> getOfls() {
        return this.ofls;
    }

    public void setOfls(List<OrderFormLog> ofls) {
        this.ofls = ofls;
    }

    public List<RefundLog> getRls() {
        return this.rls;
    }

    public void setRls(List<RefundLog> rls) {
        this.rls = rls;
    }

    public List<Evaluate> getSeller_evaluate() {
        return this.seller_evaluate;
    }

    public void setSeller_evaluate(List<Evaluate> seller_evaluate) {
        this.seller_evaluate = seller_evaluate;
    }

    public List<Evaluate> getUser_evaluate() {
        return this.user_evaluate;
    }

    public void setUser_evaluate(List<Evaluate> user_evaluate) {
        this.user_evaluate = user_evaluate;
    }

    public List<Consult> getUser_consults() {
        return this.user_consults;
    }

    public void setUser_consults(List<Consult> user_consults) {
        this.user_consults = user_consults;
    }

    public List<Consult> getSeller_consults() {
        return this.seller_consults;
    }

    public void setSeller_consults(List<Consult> seller_consults) {
        this.seller_consults = seller_consults;
    }

    public List<OrderForm> getOfs() {
        return this.ofs;
    }

    public void setOfs(List<OrderForm> ofs) {
        this.ofs = ofs;
    }

    public List<SysLog> getSyslogs() {
        return this.syslogs;
    }

    public void setSyslogs(List<SysLog> syslogs) {
        this.syslogs = syslogs;
    }

    public List<Accessory> getAccs() {
        return this.accs;
    }

    public void setAccs(List<Accessory> accs) {
        this.accs = accs;
    }

    public List<IntegralLog> getIntegral_logs() {
        return this.integral_logs;
    }

    public void setIntegral_logs(List<IntegralLog> integral_logs) {
        this.integral_logs = integral_logs;
    }

    public List<IntegralLog> getIntegral_admin_logs() {
        return this.integral_admin_logs;
    }

    public void setIntegral_admin_logs(List<IntegralLog> integral_admin_logs) {
        this.integral_admin_logs = integral_admin_logs;
    }

    public List<GoldRecord> getGold_record() {
        return this.gold_record;
    }

    public void setGold_record(List<GoldRecord> gold_record) {
        this.gold_record = gold_record;
    }

    public List<GoldRecord> getGold_record_admin() {
        return this.gold_record_admin;
    }

    public void setGold_record_admin(List<GoldRecord> gold_record_admin) {
        this.gold_record_admin = gold_record_admin;
    }

    public List<Message> getFrom_msgs() {
        return this.from_msgs;
    }

    public void setFrom_msgs(List<Message> from_msgs) {
        this.from_msgs = from_msgs;
    }

    public List<Message> getTo_msgs() {
        return this.to_msgs;
    }

    public void setTo_msgs(List<Message> to_msgs) {
        this.to_msgs = to_msgs;
    }

    public List<UserGoodsClass> getUgcs() {
        return this.ugcs;
    }

    public void setUgcs(List<UserGoodsClass> ugcs) {
        this.ugcs = ugcs;
    }

    public List<Favorite> getFavs() {
        return this.favs;
    }

    public void setFavs(List<Favorite> favs) {
        this.favs = favs;
    }

    public List<Album> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Address> getAddrs() {
        return this.addrs;
    }

    public void setAddrs(List<Address> addrs) {
        this.addrs = addrs;
    }

    public List<Predeposit> getPosits() {
        return this.posits;
    }

    public void setPosits(List<Predeposit> posits) {
        this.posits = posits;
    }

    public String getSina_openid() {
        return this.sina_openid;
    }

    public void setSina_openid(String sina_openid) {
        this.sina_openid = sina_openid;
    }

    public String getQq_openid() {
        return this.qq_openid;
    }

    public void setQq_openid(String qq_openid) {
        this.qq_openid = qq_openid;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Date getLoginDate() {
        return this.loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getLastLoginIp() {
        return this.lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLoginIp() {
        return this.loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLastLoginDate() {
        return this.lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    public GrantedAuthority[] get_all_Authorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList(this.roles.size());
        for (Role role : this.roles) {
            grantedAuthorities.add(new GrantedAuthorityImpl(role.getRoleCode()));
        }
        return (GrantedAuthority[]) grantedAuthorities.toArray(new GrantedAuthority[this.roles
            .size()]);
    }

    @JsonIgnore
    public GrantedAuthority[] get_common_Authorities() {
        List<GrantedAuthority> grantedAuthorities = new ArrayList(this.roles.size());
        for (Role role : this.roles) {
            if (!role.getType().equals("ADMIN")) {
                grantedAuthorities.add(new GrantedAuthorityImpl(role.getRoleCode()));
            }
        }
        return (GrantedAuthority[]) grantedAuthorities
            .toArray(new GrantedAuthority[grantedAuthorities.size()]);
    }

    @JsonIgnore
    public String getAuthoritiesString() {
        List<String> authorities = new ArrayList();
        for (GrantedAuthority authority : getAuthorities()) {
            authorities.add(authority.getAuthority());
        }
        return StringUtils.join(authorities.toArray(), ",");
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.userName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public Map<String, List<Res>> getRoleResources() {
        if (this.roleResources == null) {
            this.roleResources = new HashMap();
            for (Role role : this.roles) {
                String roleCode = role.getRoleCode();
                List<Res> ress = role.getReses();
                for (Res res : ress) {
                    String key = roleCode + "_" + res.getType();
                    if (!this.roleResources.containsKey(key)) {
                        this.roleResources.put(key, new ArrayList());
                    }
                    ((List) this.roleResources.get(key)).add(res);
                }
            }
        }
        return this.roleResources;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public static long getSerialVersionUID() {
        return 8026813053768023527L;
    }

    public void setRoleResources(Map<String, List<Res>> roleResources) {
        this.roleResources = roleResources;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isEnabled() {
        return true;
    }

    public String getTrueName() {
        return this.trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public String getUserRole() {
        return this.userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getQQ() {
        return this.QQ;
    }

    public void setQQ(String qq) {
        this.QQ = qq;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Accessory getPhoto() {
        return this.photo;
    }

    public void setPhoto(Accessory photo) {
        this.photo = photo;
    }

    public BigDecimal getAvailableBalance() {
        return this.availableBalance;
    }

    public void setAvailableBalance(BigDecimal availableBalance) {
        this.availableBalance = availableBalance;
    }

    public BigDecimal getFreezeBlance() {
        return this.freezeBlance;
    }

    public void setFreezeBlance(BigDecimal freezeBlance) {
        this.freezeBlance = freezeBlance;
    }

    public UserConfig getConfig() {
        return this.config;
    }

    public void setConfig(UserConfig config) {
        this.config = config;
    }

    public List<Accessory> getFiles() {
        return this.files;
    }

    public void setFiles(List<Accessory> files) {
        this.files = files;
    }

    public int getIntegral() {
        return this.integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public int getLoginCount() {
        return this.loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    @JsonIgnore
    public GrantedAuthority[] getAuthorities() {
        return this.authorities;
    }

    public void setAuthorities(GrantedAuthority[] authorities) {
        this.authorities = authorities;
    }

    public int getGold() {
        return this.gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getReport() {
        return this.report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public int getUser_credit() {
        return this.user_credit;
    }

    public void setUser_credit(int user_credit) {
        this.user_credit = user_credit;
    }

    public List<PredepositLog> getUser_predepositlogs() {
        return this.user_predepositlogs;
    }

    public void setUser_predepositlogs(List<PredepositLog> user_predepositlogs) {
        this.user_predepositlogs = user_predepositlogs;
    }

    public List<PredepositLog> getAdmin_predepositlogs() {
        return this.admin_predepositlogs;
    }

    public void setAdmin_predepositlogs(List<PredepositLog> admin_predepositlogs) {
        this.admin_predepositlogs = admin_predepositlogs;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public List<User> getChilds() {
        return this.childs;
    }

    public void setChilds(List<User> childs) {
        this.childs = childs;
    }

    public Store getStore() {
        if (getParent() == null) {
            return this.store;
        }
        return getParent().getStore();
    }

    public User getParent() {
        return this.parent;
    }

    public String getStore_quick_menu() {
        return this.store_quick_menu;
    }

    public void setStore_quick_menu(String store_quick_menu) {
        this.store_quick_menu = store_quick_menu;
    }

    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }

    public CompanyInfo getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(CompanyInfo companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getCus_ser_code() {
        return cus_ser_code;
    }

    public void setCus_ser_code(String cus_ser_code) {
        this.cus_ser_code = cus_ser_code;
    }

}
