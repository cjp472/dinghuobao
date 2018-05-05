package com.javamalls.platform.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.excel.util.ExcelRead;
import com.javamalls.front.web.action.StoreUserMangerAction;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Album;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.GoodsBrand;
import com.javamalls.platform.domain.GoodsCart;
import com.javamalls.platform.domain.GoodsItem;
import com.javamalls.platform.domain.GoodsRetrieve;
import com.javamalls.platform.domain.GoodsRetrieveProperty;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.javamalls.platform.domain.GoodsSpecification;
import com.javamalls.platform.domain.GoodsType;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.vo.GoodsBrandJsonVo;
import com.javamalls.platform.vo.GoodsJsonVo;
import com.javamalls.platform.vo.GoodsSpecPropertyJsonVo;
import com.javamalls.platform.vo.UserGoodsClassJsonVo;
import com.utils.SendReqAsync;

@Service
@Transactional
public class GoodsServiceImpl implements IGoodsService {
    @Resource(name = "goodsDAO")
    private IGenericDAO<Goods>                 goodsDao;
    @Resource(name = "userGoodsClassDAO")
    private IGenericDAO<UserGoodsClass>        userGoodsClassDao;
    @Resource(name = "goodsRetrieveDAO")
    private IGenericDAO<GoodsRetrieve>         goodsRetrieveDao;
    @Resource(name = "goodsBrandDAO")
    private IGenericDAO<GoodsBrand>            goodsBrandDao;
    @Resource(name = "goodsRetrievePropertyDAO")
    private IGenericDAO<GoodsRetrieveProperty> goodsRetrievePropertyDao;
    @Resource(name = "goodsTypeDAO")
    private IGenericDAO<GoodsType>             goodsTypeDao;
    @Resource(name = "goodsSpecPropertyDAO")
    private IGenericDAO<GoodsSpecProperty>     goodsSpecPropertyDao;
    @Resource(name = "goodsItemDAO")
    private IGenericDAO<GoodsItem>             goodsItemDao;
    @Resource(name = "albumDAO")
    private IGenericDAO<Album>                 albumDao;
    @Resource(name = "accessoryDAO")
    private IGenericDAO<Accessory>             accessoryDAO;
    @Autowired
    private SendReqAsync                       sendReqAsync;
    @Autowired
    private ISysConfigService                  configService;

    @Resource(name = "goodsCartDAO")
    private IGenericDAO<GoodsCart>             goodsCartDAO;
    @Resource(name = "goodsSpecificationDAO")
    private IGenericDAO<GoodsSpecification>    goodsSpecificationDAO;

    private static final Logger                logger = Logger
                                                          .getLogger(StoreUserMangerAction.class);

    public boolean save(Goods goods) {
        try {
            this.goodsDao.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Goods getObjById(Long id) {
        Goods goods = (Goods) this.goodsDao.get(id);
        if (goods != null) {
            return goods;
        }
        return null;
    }

    /**
     * 物理删除
     */
    public boolean delete_wuli(Long id) {
        try {

            this.goodsDao.remove(id);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(Long id) {
        try {
            // 由物理删除改为逻辑删除
            //this.goodsDao.remove(id);
            Goods goods = (Goods) this.goodsDao.get(id);
            if (goods != null) {
                goods.setGoods_status(-9);
                goods.setDisabled(true);
                this.goodsDao.update(goods);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> goodsIds) {
        for (Serializable id : goodsIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Goods.class, query, params, this.goodsDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public boolean update(Goods goods) {
        try {
            this.goodsDao.update(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Goods> query(String query, Map params, int begin, int max) {
        return this.goodsDao.query(query, params, begin, max);
    }

    public Goods getObjByProperty(String propertyName, Object value) {
        return (Goods) this.goodsDao.getBy(propertyName, value);
    }

    @Override
    public IPageList nolastlist(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map<String, Object> params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Goods.class, query, params, this.goodsDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doNolastList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doNolastList(0, -1);
        }
        return pList;
    }

    //导入单件商品
    @Override
    @Transactional
    public int importSingleGoods(User user, MultipartFile excelFile, HttpServletRequest request) {
        ExcelRead excelRead = new ExcelRead();
        Store store = user.getStore();
        SysConfig sysConfig = this.configService.getSysConfig();
        //查询该店家所有品牌
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("store_id", user.getStore().getId());
        List<GoodsBrand> gbs = this.goodsBrandDao
            .query(
                "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.id",
                queryMap, -1, -1);
        Map<String, GoodsBrand> brandMap = new HashMap<String, GoodsBrand>();
        if (gbs != null && gbs.size() > 0) {
            for (GoodsBrand goodsBrand : gbs) {
                brandMap.put(goodsBrand.getName(), goodsBrand);
            }
        }

        //查询模板
        queryMap.clear();
        queryMap.put("store_id", user.getStore().getId());
        List<GoodsType> goodsTypes = this.goodsTypeDao.query(
            "select obj from GoodsType obj where obj.disabled = false and obj.store_id=:store_id",
            queryMap, -1, -1);
        Map<String, GoodsType> goodsTypeMap = new HashMap<String, GoodsType>();
        if (goodsTypes != null && goodsTypes.size() > 0) {
            for (GoodsType goodsType : goodsTypes) {
                goodsTypeMap.put(goodsType.getName(), goodsType);
            }
        }

        //查询该店家所有分类       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());

        List<UserGoodsClass> ugcs = this.userGoodsClassDao
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id  order by obj.id",
                params, -1, -1);
        Map<String, UserGoodsClass> classMap0 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap1 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap2 = new HashMap<String, UserGoodsClass>();
        if (ugcs != null && ugcs.size() > 0) {
            for (UserGoodsClass userGoodsClass : ugcs) {
                if (userGoodsClass.getLevel() == 0) {
                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 1) {
                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 2) {
                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                }

            }
        }
        //查询扩展属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("type", 2);
        String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveDao.query(sql, map, -1, -1);
        //查询检索属性
        map.clear();
        map.put("disabled", false);
        map.put("type", 1);
        String sql2 = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsSearchRetrieves = this.goodsRetrieveDao.query(sql2, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsSearchRetrieves != null && goodsSearchRetrieves.size() > 0) {

            for (GoodsRetrieve goodsRetrieve : goodsSearchRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyDao
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }

        }

        Date now = new Date();
        try {
            List<ArrayList<String>> readExcel = excelRead.readExcel(excelFile,
                Constant.SINGLE_GOODS_NUM);
            if (readExcel != null && readExcel.size() > 0) {
                List<GoodsJsonVo> goodsJsonList = new ArrayList<GoodsJsonVo>();

                Map<String, String> pmap = (HashMap<String, String>) request.getSession(false)
                    .getAttribute("upload_pic_map");
                for (ArrayList<String> arrayList : readExcel) {
                    List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
                    Goods goods = new Goods();
                    String goodsName = arrayList.get(0);//商品名称
                    if (goodsName == null || "".equals(goodsName)) {
                        continue;
                    }
                    goods.setGoods_name(goodsName);
                    goods.setGoods_type(0);//商品类型：0单件商品 1,整包商品，2走份商品
                    String egoodsClassName = arrayList.get(1);//商品分类名称
                    //女装/衬衫

                    if (egoodsClassName != null && !"".equals(egoodsClassName)) {
                        String[] goodsClassNames = egoodsClassName.split("/");
                        //1级、2级、3级
                        UserGoodsClass parent = null;
                        for (int i = 0; i < goodsClassNames.length; i++) {
                            if (i < 3) {
                                String goodsClassName = goodsClassNames[i];
                                if (i == 0) {//一级分类
                                    if (classMap0.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap0.get(goodsClassName));
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap0
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 1) {//二级分类
                                    if (classMap1.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap1.get(goodsClassName));
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap1
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 2) {//三级分类
                                    if (classMap2.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap2.get(goodsClassName));
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap2
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    }

                                }

                            } else {
                                break;
                            }

                        }

                    }

                    goods.setGoods_units(arrayList.get(2));//商品单位
                    goods.setGoods_hot_status(CommUtil.null2Int(arrayList.get(3)));//是否热卖
                    goods.setGoods_news_status(CommUtil.null2Int(arrayList.get(4)));//是否新品
                    String recomd_status = arrayList.get(5);//推荐状态
                    if (recomd_status != null && "1".equals(recomd_status)) {
                        goods.setGoods_recommend(true);
                    } else {
                        goods.setGoods_recommend(false);
                    }
                    String brandName = arrayList.get(6);//品牌名称
                    if (brandName != null && !"".equals(brandName)) {
                        if (brandMap.containsKey(brandName)) {
                            goods.setGoods_brand(brandMap.get(brandName));
                        } else {
                            GoodsBrand brand = new GoodsBrand();
                            brand.setDisabled(false);
                            brand.setCreatetime(now);
                            brand.setAudit(1);
                            brand.setName(brandName);
                            brand.setRecommend(false);
                            brand.setSequence(0);
                            brand.setUserStatus(0);
                            brand.setUser(user);
                            brand.setStore(store);
                            //	
                            Accessory photo2 = new Accessory();
                            photo2.setName("default_brand.jpg");
                            photo2.setExt("jpg");
                            photo2.setSize(4462);
                            photo2.setPath("resources/dhb/images");
                            photo2.setWidth(240);
                            photo2.setHeight(150);
                            photo2.setCreatetime(now);
                            this.accessoryDAO.save(photo2);
                            brand.setBrandLogo(photo2);
                            //保存品牌
                            this.goodsBrandDao.save(brand);
                            goods.setGoods_brand(brand);

                            brandMap.put(brandName, brand);

                            //服装接口没有品牌则创建品牌
                            sendGoodsBrand(brand);

                        }
                    }
                    logger.info("单件商品导入：" + arrayList + "=============");
                    String marketPrice = arrayList.get(7);
                    goods.setGoods_price(new BigDecimal(marketPrice));
                    String storePrice = arrayList.get(8);
                    goods.setStore_price(new BigDecimal(storePrice));
                    goods.setGoods_current_price(goods.getStore_price());
                    String searchAttribute = "";
                    //检索属性11
                    String searchpro = arrayList.get(11);
                    if (searchpro != null && !"".equals(searchpro)) {
                        String[] searchpros = searchpro.split(";");
                        if (searchpros != null && searchpros.length > 0) {
                            for (String str : searchpros) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    if (retriePro.containsKey(split[0])) {
                                        List<GoodsRetrieveProperty> list = retriePro.get(split[0]);
                                        if (list != null && list.size() > 0) {
                                            for (GoodsRetrieveProperty goodsRetrieveProperty : list) {
                                                if (split[1].equals(goodsRetrieveProperty
                                                    .getValue())) {
                                                    searchAttribute += goodsRetrieveProperty
                                                        .getId() + ";";
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    goods.setRetrieval_ids(searchAttribute);
                    //扩展属性 12
                    //设置扩展属性
                    String extendedAttr = arrayList.get(12);
                    List<HashMap<String, Object>> extMaps = new ArrayList<HashMap<String, Object>>();
                    if (extendedAttr != null && !"".equals(extendedAttr)) {
                        String[] strings = extendedAttr.split(";");
                        if (strings != null && strings.length > 0) {
                            for (String str : strings) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    for (int i = 0; i < goodsRetrieves.size(); i++) {
                                        GoodsRetrieve goodsRetrieve = goodsRetrieves.get(i);
                                        if (split[0].equals(goodsRetrieve.getName())) {
                                            HashMap<String, Object> smap = new HashMap<String, Object>();
                                            smap.put("id", goodsRetrieve.getId() + "");
                                            smap.put("name", goodsRetrieve.getName());
                                            smap.put("val", split[1]);
                                            extMaps.add(smap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    goods.setExtendedAttributes(Json.toJson(extMaps, JsonFormat.compact()));

                    //商品图片上传13
                    //   Album album = (Album)this.albumDao.get(CommUtil.null2Long(user.getId()));
                    String pic_str = arrayList.get(13);
                    String[] pic_strs = pic_str.split(";");
                    if (pic_strs != null && pic_strs.length > 0 && pmap != null) {
                        int index = 0;
                        for (String pic : pic_strs) {
                            Map<String, Object> pic_map = JsonUtil.json2Map(pmap.get(pic));
                            if (pic_map != null) {
                                Accessory image = new Accessory();
                                image.setCreatetime(now);
                                image.setExt((String) pic_map.get("mime"));
                                image.setPath((String) pic_map.get("photo_url"));
                                image.setWidth(CommUtil.null2Int(pic_map.get("width")));
                                image.setHeight(CommUtil.null2Int(pic_map.get("height")));
                                image.setName(CommUtil.null2String(pic_map.get("fileName")));
                                //  image.setUser(user);
                                /*  if(album!=null){
                                  	image.setAlbum(album);
                                  }*/
                                this.accessoryDAO.save(image);

                                if (index == 0) {
                                    goods.setGoods_main_photo(image);
                                } else {
                                    goods.getGoods_photos().add(image);
                                }

                                String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt()
                                    : image.getExt();
                                String source = request.getSession().getServletContext()
                                    .getRealPath("/")
                                                + image.getPath()
                                                + File.separator
                                                + image.getName();
                                String target = source + "_small" + ext;
                                CommUtil.createSmall(source, target, sysConfig.getSmallWidth(),
                                    sysConfig.getSmallHeight());

                                String midtarget = source + "_middle" + ext;
                                CommUtil.createSmall(source, midtarget, sysConfig.getMiddleWidth(),
                                    sysConfig.getMiddleHeight());
                            }
                            index++;
                        }
                    }
                    //商品详情及其图片
                    String detail = arrayList.get(14);
                    String detail_pic = arrayList.get(15);
                    if (detail_pic != null && !"".equals(detail_pic)) {
                        String[] detail_pics = detail_pic.split(";");
                        if (detail_pics != null && detail_pics.length > 0 && pmap != null) {
                            for (int i = 0; i < detail_pics.length; i++) {
                                Map<String, Object> detail_pic_map = JsonUtil.json2Map(pmap
                                    .get(detail_pics[i]));
                                if (detail_pic_map != null) {
                                    Accessory image = new Accessory();
                                    image.setCreatetime(now);
                                    image.setExt((String) detail_pic_map.get("mime"));
                                    image.setPath((String) detail_pic_map.get("photo_url"));
                                    image.setWidth(CommUtil.null2Int(detail_pic_map.get("width")));
                                    image
                                        .setHeight(CommUtil.null2Int(detail_pic_map.get("height")));
                                    image.setName(CommUtil.null2String(detail_pic_map
                                        .get("fileName")));
                                    image.setUser(user);
                                    this.accessoryDAO.save(image);
                                    String url = CommUtil.getURL(request) + "/" + image.getPath()
                                                 + "/" + image.getName();
                                    //<img src="http://192.168.1.121:8080/upload/store/40/2017/12/14/b2127315-9e30-41b7-b6b4-dcf9a8e7145a.tbi" />
                                    detail = detail.replace("[img_" + i + "]", "<img src='" + url
                                                                               + "'/>");
                                }
                            }
                        }

                    }

                    goods.setGoods_details(detail);
                    goods.setDisabled(false);
                    goods.setCreatetime(now);
                    goods.setInventory_type("spec");
                    goods.setSeo_description("");
                    goods.setSeo_keywords("");
                    goods.setGoods_store(store);
                    goods.setEms_trans_fee(new BigDecimal(0));
                    goods.setExpress_trans_fee(new BigDecimal(0));
                    goods.setMail_trans_fee(new BigDecimal(0));
                    goods.setStorage_status(0);
                    goods.setGoods_status(1);
                    this.goodsDao.save(goods);

                    String sepcTemp = arrayList.get(9);
                    if (sepcTemp != null && !"".equals(sepcTemp)) {
                        //规格模板名称
                        if (goodsTypeMap.containsKey(sepcTemp)) {
                            GoodsType goodsType = goodsTypeMap.get(sepcTemp);
                            goods.setGoodsTypeId(CommUtil.null2Int(goodsType.getId()));

                            String specinfo = arrayList.get(10);
                            List<GoodsSpecification> gss = goodsType.getGss();
                            List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                            List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                            if (gss != null && gss.size() > 0) {
                                for (GoodsSpecification goodsSpecification : gss) {
                                    if ("尺码".equals(goodsSpecification.getName().trim())) {
                                        sizeproperties = goodsSpecification.getProperties();
                                        sizeGoodsSpecification = goodsSpecification;
                                    }
                                    if ("颜色".equals(goodsSpecification.getName().trim())) {
                                        colorproperties = goodsSpecification.getProperties();
                                        colorGoodsSpecification = goodsSpecification;
                                    }

                                }
                            }

                            String[] specinfos = specinfo.split(";");
                            if (specinfos != null && specinfos.length > 0) {
                                //生成货品
                                for (String str : specinfos) {
                                    System.out.println(str + "=================");
                                    String[] specs = str.split(",");
                                    if (specs != null && specs.length == 5) {
                                        //判断规格是否存在，不存在则创建
                                        //尺码:42码,颜色:黑色,库存:1000,销售价:98,市场价:100,进货价:50;尺码:42码,颜色:黑色,库存:1000,销售价:98,市场价:100,进货价:50 
                                        GoodsItem item = new GoodsItem();
                                        item.setCreatetime(now);
                                        item.setDisabled(false);
                                        String str2 = specs[2];
                                        if (str2 != null && !"".equals(str2)) {
                                            String[] str2s = str2.split(":");
                                            if (str2s != null && str2s.length == 2) {
                                                item.setGoods_inventory(CommUtil.null2Int(str2s[1]));
                                            } else {
                                                item.setGoods_inventory(0);
                                            }
                                        } else {
                                            item.setGoods_inventory(0);
                                        }

                                        String str3 = specs[3];
                                        if (str3 != null && !"".equals(str3)) {
                                            String[] str3s = str3.split(":");
                                            if (str3s != null && str3s.length == 2) {
                                                item.setGoods_price(CommUtil
                                                    .null2BigDecimal(str3s[1]));
                                            } else {
                                                item.setGoods_price(new BigDecimal(0));
                                            }
                                        } else {
                                            item.setGoods_price(new BigDecimal(0));
                                        }

                                        String str4 = specs[4];
                                        if (str4 != null && !"".equals(str4)) {
                                            String[] str4s = str4.split(":");
                                            if (str4s != null && str4s.length == 2) {
                                                item.setMarket_price(CommUtil
                                                    .null2BigDecimal(str4s[1]));
                                            } else {
                                                item.setMarket_price(new BigDecimal(0));
                                            }
                                        } else {
                                            item.setMarket_price(new BigDecimal(0));
                                        }
                                        String str5 = specs[5];//进货价
                                        if (str5 != null && !"".equals(str5)) {
                                            String[] str5s = str5.split(":");
                                            if (str5s != null && str5s.length == 2) {
                                                item.setPurchase_price(CommUtil
                                                    .null2BigDecimal(str5s[1]));
                                            } else {
                                                item.setPurchase_price(new BigDecimal(0));
                                            }
                                        } else {
                                            item.setPurchase_price(new BigDecimal(0));
                                        }

                                        item.setGoods(goods);
                                        item.setStep_price_state(0);
                                        item.setStatus(1);
                                        long size_spec_id = 0;
                                        long color_spec_id = 0;
                                        String spec_info = "";

                                        //判断尺码是否存在
                                        String spec_size = specs[0];
                                        String spec_0 = "0";
                                        if (spec_size != null && !"".equals(spec_size)
                                            && !"0".equals(spec_size)) {
                                            String[] split = spec_size.split(":");
                                            if (split != null && split.length == 2) {
                                                spec_0 = split[1];
                                            }
                                        }
                                        for (int i = 0; i < sizeproperties.size(); i++) {
                                            GoodsSpecProperty goodsSpecProperty = sizeproperties
                                                .get(i);
                                            //尺码存在
                                            if (spec_0.equals(goodsSpecProperty.getValue())) {
                                                size_spec_id = goodsSpecProperty.getId();
                                                spec_info += goodsSpecProperty.getValue() + " ";
                                                List<GoodsSpecProperty> goods_specs = goods
                                                    .getGoods_specs();
                                                boolean flag = true;
                                                if (goods_specs != null && goods_specs.size() > 0) {
                                                    for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                        if (goodsSpecProperty2.getValue().equals(
                                                            goodsSpecProperty.getValue())) {
                                                            flag = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag) {
                                                    goods.getGoods_specs().add(goodsSpecProperty);
                                                }

                                                break;
                                            }
                                        }
                                        if (!"0".equals(spec_0) && !"-1".equals(spec_0)
                                            && size_spec_id == 0) {
                                            //如果输入了尺码但是不存在，则创建
                                            //尺码不存在则创建
                                            GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                            nGoodsSpecProperty.setCreatetime(now);
                                            nGoodsSpecProperty.setDisabled(false);
                                            nGoodsSpecProperty.setSequence(0);
                                            nGoodsSpecProperty.setValue(spec_0);
                                            if (sizeGoodsSpecification != null
                                                && sizeGoodsSpecification.getId() != null) {
                                                nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                                this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                                sizeproperties.add(nGoodsSpecProperty);
                                                size_spec_id = nGoodsSpecProperty.getId();
                                                spec_info += nGoodsSpecProperty.getValue() + " ";
                                                List<GoodsSpecProperty> goods_specs = goods
                                                    .getGoods_specs();
                                                boolean flag = true;
                                                if (goods_specs != null && goods_specs.size() > 0) {
                                                    for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                        if (goodsSpecProperty2.getValue().equals(
                                                            nGoodsSpecProperty.getValue())) {
                                                            flag = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag) {
                                                    goods.getGoods_specs().add(nGoodsSpecProperty);
                                                }

                                                //服装接口如果没有尺码值则创建尺码
                                                sendSpecProperty(now, sizeGoodsSpecification,
                                                    nGoodsSpecProperty);
                                            }

                                        }

                                        //判断颜色是否存在
                                        String spec_color = specs[1];
                                        String spec_1 = "0";
                                        if (spec_color != null && !"".equals(spec_color)
                                            && !"0".equals(spec_color)) {
                                            String[] split = spec_color.split(":");
                                            if (split != null && split.length == 2) {
                                                spec_1 = split[1];
                                            }
                                        }

                                        for (int i = 0; i < colorproperties.size(); i++) {
                                            GoodsSpecProperty goodsSpecProperty = colorproperties
                                                .get(i);
                                            //颜色存在
                                            if (spec_1.equals(goodsSpecProperty.getValue())) {
                                                color_spec_id = goodsSpecProperty.getId();
                                                spec_info += goodsSpecProperty.getValue() + " ";
                                                List<GoodsSpecProperty> goods_specs = goods
                                                    .getGoods_specs();
                                                boolean flag = true;
                                                if (goods_specs != null && goods_specs.size() > 0) {
                                                    for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                        if (goodsSpecProperty2.getValue().equals(
                                                            goodsSpecProperty.getValue())) {
                                                            flag = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag) {
                                                    goods.getGoods_specs().add(goodsSpecProperty);
                                                }

                                                break;
                                            }
                                        }
                                        if (!"-1".equals(spec_1) && !"0".equals(spec_1)
                                            && color_spec_id == 0) {
                                            //如果输入了颜色但是不存在，则创建
                                            //尺码不存在则创建
                                            GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                            nGoodsSpecProperty.setCreatetime(now);
                                            nGoodsSpecProperty.setDisabled(false);
                                            nGoodsSpecProperty.setSequence(0);
                                            nGoodsSpecProperty.setValue(spec_1);
                                            if (colorGoodsSpecification != null
                                                && colorGoodsSpecification.getId() != null) {
                                                nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                                this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                                colorproperties.add(nGoodsSpecProperty);
                                                color_spec_id = nGoodsSpecProperty.getId();
                                                spec_info += nGoodsSpecProperty.getValue() + " ";
                                                List<GoodsSpecProperty> goods_specs = goods
                                                    .getGoods_specs();
                                                boolean flag = true;
                                                if (goods_specs != null && goods_specs.size() > 0) {
                                                    for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                        if (goodsSpecProperty2.getValue().equals(
                                                            nGoodsSpecProperty.getValue())) {
                                                            flag = false;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (flag) {
                                                    goods.getGoods_specs().add(nGoodsSpecProperty);
                                                }

                                                //服装接口如果没有颜色值则创建颜色
                                                sendSpecProperty(now, colorGoodsSpecification,
                                                    nGoodsSpecProperty);
                                            }
                                        }

                                        if (size_spec_id > 0 || color_spec_id > 0) {
                                            item.setSpec_info(spec_info);
                                            if (size_spec_id > 0 && color_spec_id == 0) {
                                                item.setSpec_combination(size_spec_id + "_");
                                            }
                                            if (color_spec_id > 0 && color_spec_id == 0) {
                                                item.setSpec_combination(color_spec_id + "_");
                                            }
                                            if (size_spec_id > 0 && color_spec_id > 0) {
                                                String combination = "";
                                                if (size_spec_id > color_spec_id) {
                                                    combination = color_spec_id + "_"
                                                                  + size_spec_id + "_";
                                                }
                                                if (size_spec_id < color_spec_id) {
                                                    combination = size_spec_id + "_"
                                                                  + color_spec_id + "_";
                                                }
                                                item.setSpec_combination(combination);
                                            }

                                            //保存货品
                                            this.goodsItemDao.save(item);

                                            goodsItems.add(item);
                                        }

                                    }
                                }
                            }

                            this.goodsDao.update(goods);

                            //服装接口创建商品

                            addGoodsJsonList(goodsJsonList, goods, goodsItems);

                        } else {
                            //如果模板名称不存在
                            GoodsItem item = new GoodsItem();
                            item.setCreatetime(now);
                            item.setDisabled(false);
                            item.setGoods_inventory(CommUtil.null2Int(0));
                            item.setGoods_price(CommUtil.null2BigDecimal(0));
                            item.setMarket_price(CommUtil.null2BigDecimal(0));
                            item.setPurchase_price(CommUtil.null2BigDecimal(0));
                            item.setGoods(goods);
                            item.setStatus(0);
                            item.setStep_price_state(0);
                            this.goodsItemDao.save(item);
                            goods.setGoods_inventory(item.getGoods_inventory());
                            this.goodsDao.update(goods);
                            goodsItems.add(item);
                            //服装接口创建商品
                            addGoodsJsonList(goodsJsonList, goods, goodsItems);
                        }
                    } else {//如果没有规格
                        String specinfo = arrayList.get(10);
                        String[] split = specinfo.split(",");
                        GoodsItem item = new GoodsItem();
                        item.setCreatetime(now);
                        item.setDisabled(false);
                        if (split != null && split.length == 5) {
                            String str2 = split[2];

                            if (str2 != null && !"".equals(str2)) {
                                String[] str2s = str2.split(":");
                                if (str2s != null && str2s.length == 2) {
                                    item.setGoods_inventory(CommUtil.null2Int(str2s[1]));
                                } else {
                                    item.setGoods_inventory(0);
                                }
                            } else {
                                item.setGoods_inventory(0);
                            }

                            String str3 = split[3];
                            if (str3 != null && !"".equals(str3)) {
                                String[] str3s = str3.split(":");
                                if (str3s != null && str3s.length == 2) {
                                    item.setGoods_price(CommUtil.null2BigDecimal(str3s[1]));
                                } else {
                                    item.setGoods_price(new BigDecimal(0));
                                }
                            } else {
                                item.setGoods_price(new BigDecimal(0));
                            }
                            String str4 = split[4];
                            if (str4 != null && !"".equals(str4)) {
                                String[] str4s = str4.split(":");
                                if (str4s != null && str4s.length == 2) {
                                    item.setMarket_price(CommUtil.null2BigDecimal(str4s[1]));
                                } else {
                                    item.setMarket_price(new BigDecimal(0));
                                }
                            } else {
                                item.setMarket_price(new BigDecimal(0));
                            }
                            String str5 = split[5];//进货价
                            if (str5 != null && !"".equals(str5)) {
                                String[] str5s = str5.split(":");
                                if (str5s != null && str5s.length == 2) {
                                    item.setPurchase_price(CommUtil.null2BigDecimal(str5s[1]));
                                } else {
                                    item.setPurchase_price(new BigDecimal(0));
                                }
                            } else {
                                item.setPurchase_price(new BigDecimal(0));
                            }

                        } else {
                            item.setGoods_inventory(CommUtil.null2Int(0));
                            item.setGoods_price(CommUtil.null2BigDecimal(0));
                            item.setMarket_price(CommUtil.null2BigDecimal(0));
                        }
                        item.setGoods(goods);
                        item.setStatus(0);
                        item.setStep_price_state(0);
                        this.goodsItemDao.save(item);
                        goods.setGoods_inventory(item.getGoods_inventory());
                        this.goodsDao.update(goods);
                        goodsItems.add(item);

                        //服装接口创建商品
                        addGoodsJsonList(goodsJsonList, goods, goodsItems);
                    }

                }

                //服装接口创建商品
                if (goodsJsonList != null && goodsJsonList.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonList);
                    sendReqAsync.sendMessageUtil(Constant.GOODS_URL_BATCHADD, write2JsonStr,
                        "批量导入商品");

                }
                request.getSession(false).removeAttribute("upload_pic_map");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void addGoodsJsonList(List<GoodsJsonVo> goodsJsonList, Goods goods,
                                  List<GoodsItem> goodsItems) {
        GoodsJsonVo goodsJsonVo = new GoodsJsonVo();
        goodsJsonVo.setId(goods.getId());
        goodsJsonVo.setCreatetime(goods.getCreatetime());
        goodsJsonVo.setDisabled(goods.isDisabled());
        goodsJsonVo.setSeo_keywords(goods.getSeo_keywords());
        goodsJsonVo.setSeo_description(goods.getSeo_description());
        goodsJsonVo.setGoods_name(goods.getGoods_name());
        goodsJsonVo.setGoods_price(goods.getGoods_price());
        goodsJsonVo.setStore_price(goods.getStore_price());
        goodsJsonVo.setGoods_inventory(goods.getGoods_inventory());
        goodsJsonVo.setInventory_type(goods.getInventory_type());
        goodsJsonVo.setGoods_salenum(goods.getGoods_salenum());
        goodsJsonVo.setGoods_details(goods.getGoods_details());
        goodsJsonVo.setGoods_recommend(goods.isGoods_recommend());
        goodsJsonVo.setGoods_click(goods.getGoods_click());
        goodsJsonVo.setGoods_collect(goods.getGoods_collect());

        goodsJsonVo.setGoods_store_id(goods.getGoods_store().getId());

        goodsJsonVo.setGoods_status(goods.getGoods_status());
        goodsJsonVo.setGoods_transfee(goods.getGoods_transfee());
        Accessory goods_main_photo = goods.getGoods_main_photo();
        if (goods_main_photo != null) {
            goodsJsonVo.setGoods_main_photo_url(goods_main_photo.getPath() + "/"
                                                + goods_main_photo.getName());
        }
        List<Accessory> goods_photos = goods.getGoods_photos();
        String goods_pic_url = "";
        if (goods_photos != null && goods_photos.size() > 0) {
            for (Accessory accessory : goods_photos) {
                goods_pic_url += accessory.getPath() + "/" + accessory.getName() + ";";
            }
        }
        goodsJsonVo.setGoods_photos_url(goods_pic_url);
        List<UserGoodsClass> goods_ugcs = goods.getGoods_ugcs();
        if (goods_ugcs != null && goods_ugcs.size() > 0) {
            goodsJsonVo.setGoods_ugcs_id(goods_ugcs.get(0).getId());
        }
        List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
        String str_spec = "";
        if (goods_specs != null && goods_specs.size() > 0) {
            for (GoodsSpecProperty goodsSpecProperty : goods_specs) {
                str_spec += goodsSpecProperty.getId() + ";";
            }
        }
        goodsJsonVo.setGoods_specs(str_spec);
        GoodsBrand goods_brand = goods.getGoods_brand();
        if (goods_brand != null) {
            goodsJsonVo.setGoods_brand_id(goods_brand.getId());
        }
        goodsJsonVo.setGoods_current_price(goods.getGoods_current_price());
        goodsJsonVo.setMail_trans_fee(goods.getMail_trans_fee());
        goodsJsonVo.setExpress_trans_fee(goods.getExpress_trans_fee());
        goodsJsonVo.setEms_trans_fee(goods.getEms_trans_fee());
        goodsJsonVo.setGoods_item_list(goodsItems);
        goodsJsonVo.setGoodsTypeId(goods.getGoodsTypeId());
        goodsJsonVo.setStorage_status(goods.getStorage_status());
        goodsJsonVo.setGoods_units(goods.getGoods_units());
        goodsJsonVo.setGoods_news_status(goods.getGoods_news_status());
        goodsJsonVo.setGoods_hot_status(goods.getGoods_hot_status());
        goodsJsonVo.setTotal_weight(goods.getTotal_weight());
        goodsJsonVo.setType_ratio(goods.getType_ratio());
        goodsJsonVo.setColor_ratio(goods.getColor_ratio());
        goodsJsonVo.setSize_ratio(goods.getSize_ratio());
        goodsJsonVo.setSingle_weight(goods.getSingle_weight());
        goodsJsonVo.setGoods_type(goods.getGoods_type());
        goodsJsonVo.setRetrieval_ids(goods.getRetrieval_ids());
        goodsJsonVo.setExtendedAttributes(goods.getExtendedAttributes());
        goodsJsonList.add(goodsJsonVo);
    }

    /**
     * 商品导入时创建规格值
     * @param now
     * @param sizeGoodsSpecification
     * @param nGoodsSpecProperty
     */
    private void sendSpecProperty(Date now, GoodsSpecification sizeGoodsSpecification,
                                  GoodsSpecProperty nGoodsSpecProperty) {
        try {
            GoodsSpecPropertyJsonVo goodsSpecPropertyJsonVo = new GoodsSpecPropertyJsonVo();
            goodsSpecPropertyJsonVo.setCreatetime(now);
            goodsSpecPropertyJsonVo.setDisabled(false);
            goodsSpecPropertyJsonVo.setId(nGoodsSpecProperty.getId());
            goodsSpecPropertyJsonVo.setSequence(0);
            goodsSpecPropertyJsonVo.setSpec_id(sizeGoodsSpecification.getId());
            goodsSpecPropertyJsonVo.setValue(nGoodsSpecProperty.getValue());
            String write2JsonStr = JsonUtil.write2JsonStr(goodsSpecPropertyJsonVo);
            sendReqAsync.sendMessageUtil(Constant.STORE_GOODSSPECPRO_URL_ADD, write2JsonStr,
                "规格值新增");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 商品导入时创建品牌
     * @param brand
     */
    private void sendGoodsBrand(GoodsBrand brand) {
        try {
            //服装接口
            GoodsBrandJsonVo vo = new GoodsBrandJsonVo();
            vo.setId(brand.getId());
            vo.setDisabled(brand.isDisabled());
            vo.setCreatetime(brand.getCreatetime());
            vo.setFirst_word(brand.getFirst_word());
            vo.setName(brand.getName());
            vo.setRemark(brand.getRemark());
            vo.setStoreId(brand.getStore().getId());
            vo.setUserStatus(brand.getUserStatus());
            Accessory brandLogo = brand.getBrandLogo();
            if (brandLogo != null) {
                vo.setBrandLogo(brandLogo.getPath() + "/" + brandLogo.getName());
            }

            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            sendReqAsync.sendMessageUtil(Constant.STORE_BRAND_URL_ADD, write2JsonStr, "新增品牌");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 商品导入时创建分类
     * @param userGoodsClass
     */
    private void sendUserGoodsClass(UserGoodsClass userGoodsClass) {
        try {
            //服装接口
            UserGoodsClassJsonVo vo = new UserGoodsClassJsonVo();
            vo.setClassName(userGoodsClass.getClassName());
            vo.setCreatetime(userGoodsClass.getCreatetime());
            vo.setDisabled(userGoodsClass.isDisabled());
            vo.setDisplay(userGoodsClass.isDisplay());
            vo.setId(userGoodsClass.getId());
            vo.setLevel(userGoodsClass.getLevel());
            if (userGoodsClass.getParent() != null) {
                vo.setParent_id(userGoodsClass.getParent().getId());
            }

            vo.setSequence(userGoodsClass.getSequence());
            vo.setUser_id(userGoodsClass.getUser().getId());
            String write2JsonStr = JsonUtil.write2JsonStr(vo);
            System.out.println(write2JsonStr);
            sendReqAsync
                .sendMessageUtil(Constant.STORE_GOODSCLASS_URL_ADD, write2JsonStr, "新增商品分类");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //导入整包商品
    @Override
    @Transactional
    public int importPackageGoods(User user, MultipartFile excelFile, HttpServletRequest request) {
        ExcelRead excelRead = new ExcelRead();
        Store store = user.getStore();
        //查询该店家所有品牌
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("store_id", user.getStore().getId());
        List<GoodsBrand> gbs = this.goodsBrandDao
            .query(
                "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.id",
                queryMap, -1, -1);
        Map<String, GoodsBrand> brandMap = new HashMap<String, GoodsBrand>();
        if (gbs != null && gbs.size() > 0) {
            for (GoodsBrand goodsBrand : gbs) {
                brandMap.put(goodsBrand.getName(), goodsBrand);
            }
        }

        //查询该店家所有分类       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());

        List<UserGoodsClass> ugcs = this.userGoodsClassDao
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id  order by obj.id asc",
                params, -1, -1);
        Map<String, UserGoodsClass> classMap0 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap1 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap2 = new HashMap<String, UserGoodsClass>();
        if (ugcs != null && ugcs.size() > 0) {
            for (UserGoodsClass userGoodsClass : ugcs) {
                if (userGoodsClass.getLevel() == 0) {
                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 1) {
                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 2) {
                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                }

            }
        }
        //查询扩展属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("type", 2);
        String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveDao.query(sql, map, -1, -1);
        //查询检索属性
        map.clear();
        map.put("disabled", false);
        map.put("type", 1);
        String sql2 = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsSearchRetrieves = this.goodsRetrieveDao.query(sql2, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsSearchRetrieves != null && goodsSearchRetrieves.size() > 0) {

            for (GoodsRetrieve goodsRetrieve : goodsSearchRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyDao
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }

        }

        Date now = new Date();
        try {
            List<ArrayList<String>> readExcel = excelRead.readExcel(excelFile,
                Constant.PACKGET_GOODS_NUM);
            if (readExcel != null && readExcel.size() > 0) {
                List<GoodsJsonVo> goodsJsonList = new ArrayList<GoodsJsonVo>();
                Map<String, String> pmap = (HashMap<String, String>) request.getSession(false)
                    .getAttribute("upload_pic_map");
                for (ArrayList<String> arrayList : readExcel) {
                    Goods goods = new Goods();
                    List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
                    String goodsName = arrayList.get(0);//商品名称
                    if (goodsName == null || "".equals(goodsName)) {
                        continue;
                    }
                    goods.setGoods_name(goodsName);
                    goods.setGoods_type(1);//商品类型：0单件商品 1,整包商品，2走份商品
                    String egoodsClassName = arrayList.get(1);//商品分类名称
                    //女装/衬衫

                    if (egoodsClassName != null && !"".equals(egoodsClassName)) {
                        String[] goodsClassNames = egoodsClassName.split("/");
                        //1级、2级、3级
                        UserGoodsClass parent = null;
                        for (int i = 0; i < goodsClassNames.length; i++) {
                            if (i < 3) {
                                String goodsClassName = goodsClassNames[i];
                                if (i == 0) {//一级分类
                                    if (classMap0.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap0.get(goodsClassName));
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap0
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 1) {//二级分类
                                    if (classMap1.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap1.get(goodsClassName));
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap1
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 2) {//三级分类
                                    if (classMap2.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap2.get(goodsClassName));
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap2
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    }

                                }

                            } else {
                                break;
                            }

                        }

                    }

                    goods.setGoods_units(arrayList.get(2));//商品单位
                    goods.setGoods_hot_status(CommUtil.null2Int(arrayList.get(3)));//是否热卖
                    goods.setGoods_news_status(CommUtil.null2Int(arrayList.get(4)));//是否新品
                    String recomd_status = arrayList.get(5);//推荐状态
                    if (recomd_status != null && "1".equals(recomd_status)) {
                        goods.setGoods_recommend(true);
                    } else {
                        goods.setGoods_recommend(false);
                    }
                    String brandName = arrayList.get(6);//品牌名称
                    if (brandName != null && !"".equals(brandName)) {
                        if (brandMap.containsKey(brandName)) {
                            goods.setGoods_brand(brandMap.get(brandName));
                        } else {
                            GoodsBrand brand = new GoodsBrand();
                            brand.setDisabled(false);
                            brand.setCreatetime(now);
                            brand.setAudit(1);
                            brand.setName(brandName);
                            brand.setRecommend(false);
                            brand.setSequence(0);
                            brand.setUserStatus(0);
                            brand.setUser(user);
                            brand.setStore(store);

                            Accessory photo2 = new Accessory();
                            photo2.setName("default_brand.jpg");
                            photo2.setExt("jpg");
                            photo2.setSize(4462);
                            photo2.setPath("resources/dhb/images");
                            photo2.setWidth(240);
                            photo2.setHeight(150);
                            photo2.setCreatetime(now);
                            this.accessoryDAO.save(photo2);
                            brand.setBrandLogo(photo2);
                            //保存品牌
                            this.goodsBrandDao.save(brand);
                            goods.setGoods_brand(brand);

                            brandMap.put(brandName, brand);

                            //服装接口没有品牌则创建品牌
                            sendGoodsBrand(brand);
                        }
                    }
                    logger.info("整包商品导入：" + arrayList + "=============");
                    String marketPrice = arrayList.get(7);
                    goods.setGoods_price(new BigDecimal(marketPrice));
                    String storePrice = arrayList.get(8);
                    goods.setStore_price(new BigDecimal(storePrice));
                    goods.setGoods_current_price(goods.getStore_price());
                    //进货价9
                    String purchasePrice = arrayList.get(9);
                    //总量10
                    goods.setTotal_weight(CommUtil.null2String(arrayList.get(10)));
                    //类型比例11
                    goods.setType_ratio(CommUtil.null2String(arrayList.get(11)));
                    //颜色比例12
                    goods.setColor_ratio(CommUtil.null2String(arrayList.get(12)));
                    //尺寸比例13
                    goods.setSize_ratio(CommUtil.null2String(arrayList.get(13)));
                    //库存14
                    goods.setGoods_inventory(CommUtil.null2Int(arrayList.get(14)));

                    String searchAttribute = "";
                    //检索属性15
                    String searchpro = arrayList.get(15);
                    if (searchpro != null && !"".equals(searchpro)) {
                        String[] searchpros = searchpro.split(";");
                        if (searchpros != null && searchpros.length > 0) {
                            for (String str : searchpros) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    if (retriePro.containsKey(split[0])) {
                                        List<GoodsRetrieveProperty> list = retriePro.get(split[0]);
                                        if (list != null && list.size() > 0) {
                                            for (GoodsRetrieveProperty goodsRetrieveProperty : list) {
                                                if (split[1].equals(goodsRetrieveProperty
                                                    .getValue())) {
                                                    searchAttribute += goodsRetrieveProperty
                                                        .getId() + ";";
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    goods.setRetrieval_ids(searchAttribute);
                    //扩展属性 16
                    //设置扩展属性
                    String extendedAttr = arrayList.get(16);
                    List<HashMap<String, Object>> extMaps = new ArrayList<HashMap<String, Object>>();
                    if (extendedAttr != null && !"".equals(extendedAttr)) {
                        String[] strings = extendedAttr.split(";");
                        if (strings != null && strings.length > 0) {
                            for (String str : strings) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    for (int i = 0; i < goodsRetrieves.size(); i++) {
                                        GoodsRetrieve goodsRetrieve = goodsRetrieves.get(i);
                                        if (split[0].equals(goodsRetrieve.getName())) {
                                            HashMap<String, Object> smap = new HashMap<String, Object>();
                                            smap.put("id", goodsRetrieve.getId() + "");
                                            smap.put("name", goodsRetrieve.getName());
                                            smap.put("val", split[1]);
                                            extMaps.add(smap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    goods.setExtendedAttributes(Json.toJson(extMaps, JsonFormat.compact()));

                    //商品图片上传17

                    String pic_str = arrayList.get(17);
                    String[] pic_strs = pic_str.split(";");
                    if (pic_strs != null && pic_strs.length > 0 && pmap != null) {
                        int index = 0;
                        for (String pic : pic_strs) {
                            Map<String, Object> pic_map = JsonUtil.json2Map(pmap.get(pic));
                            if (pic_map != null) {
                                Accessory image = new Accessory();
                                image.setCreatetime(now);
                                image.setExt((String) pic_map.get("mime"));
                                image.setPath((String) pic_map.get("photo_url"));
                                image.setWidth(CommUtil.null2Int(pic_map.get("width")));
                                image.setHeight(CommUtil.null2Int(pic_map.get("height")));
                                image.setName(CommUtil.null2String(pic_map.get("fileName")));
                                //  image.setUser(user);
                                /*  if(album!=null){
                                  	image.setAlbum(album);
                                  }*/
                                this.accessoryDAO.save(image);

                                if (index == 0) {
                                    goods.setGoods_main_photo(image);
                                } else {
                                    goods.getGoods_photos().add(image);
                                }

                                String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt()
                                    : image.getExt();
                                String source = request.getSession().getServletContext()
                                    .getRealPath("/")
                                                + image.getPath()
                                                + File.separator
                                                + image.getName();
                                String target = source + "_small" + ext;
                                CommUtil.createSmall(source, target, this.configService
                                    .getSysConfig().getSmallWidth(), this.configService
                                    .getSysConfig().getSmallHeight());

                                String midtarget = source + "_middle" + ext;
                                CommUtil.createSmall(source, midtarget, this.configService
                                    .getSysConfig().getMiddleWidth(), this.configService
                                    .getSysConfig().getMiddleHeight());
                            }
                            index++;
                        }
                    }
                    //商品详情及其图片
                    String detail = arrayList.get(18);
                    String detail_pic = arrayList.get(19);
                    if (detail_pic != null && !"".equals(detail_pic)) {
                        String[] detail_pics = detail_pic.split(";");
                        if (detail_pics != null && detail_pics.length > 0 && pmap != null) {
                            for (int i = 0; i < detail_pics.length; i++) {
                                Map<String, Object> detail_pic_map = JsonUtil.json2Map(pmap
                                    .get(detail_pics[i]));
                                if (detail_pic_map != null) {
                                    Accessory image = new Accessory();
                                    image.setCreatetime(now);
                                    image.setExt((String) detail_pic_map.get("mime"));
                                    image.setPath((String) detail_pic_map.get("photo_url"));
                                    image.setWidth(CommUtil.null2Int(detail_pic_map.get("width")));
                                    image
                                        .setHeight(CommUtil.null2Int(detail_pic_map.get("height")));
                                    image.setName(CommUtil.null2String(detail_pic_map
                                        .get("fileName")));
                                    image.setUser(user);
                                    this.accessoryDAO.save(image);
                                    String url = CommUtil.getURL(request) + "/" + image.getPath()
                                                 + "/" + image.getName();
                                    //<img src="http://192.168.1.121:8080/upload/store/40/2017/12/14/b2127315-9e30-41b7-b6b4-dcf9a8e7145a.tbi" />
                                    detail = detail.replace("[img_" + i + "]", "<img src='" + url
                                                                               + "'/>");
                                }
                            }
                        }

                    }

                    goods.setGoods_details(detail);
                    goods.setDisabled(false);
                    goods.setCreatetime(now);
                    goods.setInventory_type("spec");
                    goods.setSeo_description("");
                    goods.setSeo_keywords("");
                    goods.setGoods_store(store);
                    goods.setEms_trans_fee(new BigDecimal(0));
                    goods.setExpress_trans_fee(new BigDecimal(0));
                    goods.setMail_trans_fee(new BigDecimal(0));
                    goods.setStorage_status(0);
                    goods.setGoods_status(1);
                    this.goodsDao.save(goods);

                    //保存货品
                    GoodsItem item = new GoodsItem();
                    item.setCreatetime(now);
                    item.setDisabled(false);
                    item.setGoods_inventory(CommUtil.null2Int(arrayList.get(13)));
                    item.setGoods_price(CommUtil.null2BigDecimal(storePrice));
                    item.setMarket_price(CommUtil.null2BigDecimal(marketPrice));
                    item.setPurchase_price(CommUtil.null2BigDecimal(purchasePrice));
                    item.setGoods(goods);
                    item.setStatus(0);
                    item.setStep_price_state(0);
                    this.goodsItemDao.save(item);
                    goods.setGoods_inventory(item.getGoods_inventory());
                    this.goodsDao.update(goods);

                    goodsItems.add(item);
                    //服装接口创建商品
                    addGoodsJsonList(goodsJsonList, goods, goodsItems);
                }
                //服装接口创建商品
                if (goodsJsonList != null && goodsJsonList.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonList);
                    sendReqAsync.sendMessageUtil(Constant.GOODS_URL_BATCHADD, write2JsonStr,
                        "批量导入商品");

                }

                request.getSession(false).removeAttribute("upload_pic_map");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //走份
    @Override
    @Transactional
    public int importShareGoods(User user, MultipartFile excelFile, HttpServletRequest request) {
        ExcelRead excelRead = new ExcelRead();
        Store store = user.getStore();
        //查询该店家所有品牌
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("store_id", user.getStore().getId());
        List<GoodsBrand> gbs = this.goodsBrandDao
            .query(
                "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.id",
                queryMap, -1, -1);
        Map<String, GoodsBrand> brandMap = new HashMap<String, GoodsBrand>();
        if (gbs != null && gbs.size() > 0) {
            for (GoodsBrand goodsBrand : gbs) {
                brandMap.put(goodsBrand.getName(), goodsBrand);
            }
        }

        //查询该店家所有分类       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());

        List<UserGoodsClass> ugcs = this.userGoodsClassDao
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id  order by obj.id asc",
                params, -1, -1);
        Map<String, UserGoodsClass> classMap0 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap1 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap2 = new HashMap<String, UserGoodsClass>();
        if (ugcs != null && ugcs.size() > 0) {
            for (UserGoodsClass userGoodsClass : ugcs) {
                if (userGoodsClass.getLevel() == 0) {
                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 1) {
                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 2) {
                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                }

            }
        }
        //查询扩展属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("type", 2);
        String sql = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsRetrieves = this.goodsRetrieveDao.query(sql, map, -1, -1);
        //查询检索属性
        map.clear();
        map.put("disabled", false);
        map.put("type", 1);
        String sql2 = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsSearchRetrieves = this.goodsRetrieveDao.query(sql2, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsSearchRetrieves != null && goodsSearchRetrieves.size() > 0) {

            for (GoodsRetrieve goodsRetrieve : goodsSearchRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyDao
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }

        }

        Date now = new Date();
        try {
            List<ArrayList<String>> readExcel = excelRead.readExcel(excelFile,
                Constant.SHARE_GOODS_NUM);
            if (readExcel != null && readExcel.size() > 0) {
                List<GoodsJsonVo> goodsJsonList = new ArrayList<GoodsJsonVo>();
                Map<String, String> pmap = (HashMap<String, String>) request.getSession(false)
                    .getAttribute("upload_pic_map");
                for (ArrayList<String> arrayList : readExcel) {
                    Goods goods = new Goods();
                    List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();

                    String goodsName = arrayList.get(0);//商品名称
                    if (goodsName == null || "".equals(goodsName)) {
                        continue;
                    }
                    goods.setGoods_name(goodsName);
                    goods.setGoods_type(2);//商品类型：0单件商品 1,整包商品，2走份商品
                    String egoodsClassName = arrayList.get(1);//商品分类名称
                    //女装/衬衫

                    if (egoodsClassName != null && !"".equals(egoodsClassName)) {
                        String[] goodsClassNames = egoodsClassName.split("/");
                        //1级、2级、3级
                        UserGoodsClass parent = null;
                        for (int i = 0; i < goodsClassNames.length; i++) {
                            if (i < 3) {
                                String goodsClassName = goodsClassNames[i];
                                if (i == 0) {//一级分类
                                    if (classMap0.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap0.get(goodsClassName));
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap0
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 1) {//二级分类
                                    if (classMap1.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap1.get(goodsClassName));
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap1
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 2) {//三级分类
                                    if (classMap2.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap2.get(goodsClassName));
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap2
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    }

                                }

                            } else {
                                break;
                            }

                        }

                    }

                    goods.setGoods_units(arrayList.get(2));//商品单位
                    goods.setGoods_hot_status(CommUtil.null2Int(arrayList.get(3)));//是否热卖
                    goods.setGoods_news_status(CommUtil.null2Int(arrayList.get(4)));//是否新品
                    String recomd_status = arrayList.get(5);//推荐状态
                    if (recomd_status != null && "1".equals(recomd_status)) {
                        goods.setGoods_recommend(true);
                    } else {
                        goods.setGoods_recommend(false);
                    }
                    String brandName = arrayList.get(6);//品牌名称
                    if (brandName != null && !"".equals(brandName)) {
                        if (brandMap.containsKey(brandName)) {
                            goods.setGoods_brand(brandMap.get(brandName));
                        } else {
                            GoodsBrand brand = new GoodsBrand();
                            brand.setDisabled(false);
                            brand.setCreatetime(now);
                            brand.setAudit(1);
                            brand.setName(brandName);
                            brand.setRecommend(false);
                            brand.setSequence(0);
                            brand.setUserStatus(0);
                            brand.setUser(user);
                            brand.setStore(store);

                            Accessory photo2 = new Accessory();
                            photo2.setName("default_brand.jpg");
                            photo2.setExt("jpg");
                            photo2.setSize(4462);
                            photo2.setPath("resources/dhb/images");
                            photo2.setWidth(240);
                            photo2.setHeight(150);
                            photo2.setCreatetime(now);
                            this.accessoryDAO.save(photo2);
                            brand.setBrandLogo(photo2);
                            //保存品牌
                            this.goodsBrandDao.save(brand);
                            goods.setGoods_brand(brand);

                            brandMap.put(brandName, brand);

                            //服装接口没有品牌则创建品牌
                            sendGoodsBrand(brand);

                        }
                    }
                    logger.info("走份商品导入：" + arrayList + "=============");
                    String marketPrice = arrayList.get(7);
                    goods.setGoods_price(new BigDecimal(marketPrice));
                    String storePrice = arrayList.get(8);
                    goods.setStore_price(new BigDecimal(storePrice));
                    goods.setGoods_current_price(goods.getStore_price());
                    String purchasePrice = arrayList.get(9);//进货价
                    //总量10
                    goods.setTotal_weight(CommUtil.null2String(arrayList.get(10)));
                    //类型比例11
                    goods.setType_ratio(CommUtil.null2String(arrayList.get(11)));
                    //颜色比例12
                    goods.setColor_ratio(CommUtil.null2String(arrayList.get(12)));
                    //尺寸比例13
                    goods.setSize_ratio(CommUtil.null2String(arrayList.get(13)));
                    //单份量14
                    goods.setSingle_weight(CommUtil.null2String(arrayList.get(14)));
                    //库存15
                    goods.setGoods_inventory(CommUtil.null2Int(arrayList.get(15)));

                    String searchAttribute = "";
                    //检索属性16
                    String searchpro = arrayList.get(16);
                    if (searchpro != null && !"".equals(searchpro)) {
                        String[] searchpros = searchpro.split(";");
                        if (searchpros != null && searchpros.length > 0) {
                            for (String str : searchpros) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    if (retriePro.containsKey(split[0])) {
                                        List<GoodsRetrieveProperty> list = retriePro.get(split[0]);
                                        if (list != null && list.size() > 0) {
                                            for (GoodsRetrieveProperty goodsRetrieveProperty : list) {
                                                if (split[1].equals(goodsRetrieveProperty
                                                    .getValue())) {
                                                    searchAttribute += goodsRetrieveProperty
                                                        .getId() + ";";
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    goods.setRetrieval_ids(searchAttribute);
                    //扩展属性 17
                    //设置扩展属性
                    String extendedAttr = arrayList.get(17);
                    List<HashMap<String, Object>> extMaps = new ArrayList<HashMap<String, Object>>();
                    if (extendedAttr != null && !"".equals(extendedAttr)) {
                        String[] strings = extendedAttr.split(";");
                        if (strings != null && strings.length > 0) {
                            for (String str : strings) {
                                String[] split = str.split(":");
                                if (split != null && split.length == 2) {
                                    for (int i = 0; i < goodsRetrieves.size(); i++) {
                                        GoodsRetrieve goodsRetrieve = goodsRetrieves.get(i);
                                        if (split[0].equals(goodsRetrieve.getName())) {
                                            HashMap<String, Object> smap = new HashMap<String, Object>();
                                            smap.put("id", goodsRetrieve.getId() + "");
                                            smap.put("name", goodsRetrieve.getName());
                                            smap.put("val", split[1]);
                                            extMaps.add(smap);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    goods.setExtendedAttributes(Json.toJson(extMaps, JsonFormat.compact()));

                    //商品图片上传18

                    String pic_str = arrayList.get(18);
                    String[] pic_strs = pic_str.split(";");
                    if (pic_strs != null && pic_strs.length > 0 && pmap != null) {
                        int index = 0;
                        for (String pic : pic_strs) {
                            Map<String, Object> pic_map = JsonUtil.json2Map(pmap.get(pic));
                            if (pic_map != null) {
                                Accessory image = new Accessory();
                                image.setCreatetime(now);
                                image.setExt((String) pic_map.get("mime"));
                                image.setPath((String) pic_map.get("photo_url"));
                                image.setWidth(CommUtil.null2Int(pic_map.get("width")));
                                image.setHeight(CommUtil.null2Int(pic_map.get("height")));
                                image.setName(CommUtil.null2String(pic_map.get("fileName")));
                                //  image.setUser(user);
                                /*  if(album!=null){
                                  	image.setAlbum(album);
                                  }*/
                                this.accessoryDAO.save(image);

                                if (index == 0) {
                                    goods.setGoods_main_photo(image);
                                } else {
                                    goods.getGoods_photos().add(image);
                                }

                                String ext = image.getExt().indexOf(".") < 0 ? "." + image.getExt()
                                    : image.getExt();
                                String source = request.getSession().getServletContext()
                                    .getRealPath("/")
                                                + image.getPath()
                                                + File.separator
                                                + image.getName();
                                String target = source + "_small" + ext;
                                CommUtil.createSmall(source, target, this.configService
                                    .getSysConfig().getSmallWidth(), this.configService
                                    .getSysConfig().getSmallHeight());

                                String midtarget = source + "_middle" + ext;
                                CommUtil.createSmall(source, midtarget, this.configService
                                    .getSysConfig().getMiddleWidth(), this.configService
                                    .getSysConfig().getMiddleHeight());
                            }
                            index++;
                        }
                    }
                    //商品详情及其图片
                    String detail = arrayList.get(19);
                    String detail_pic = arrayList.get(20);
                    if (detail_pic != null && !"".equals(detail_pic)) {
                        String[] detail_pics = detail_pic.split(";");
                        if (detail_pics != null && detail_pics.length > 0 && pmap != null) {
                            for (int i = 0; i < detail_pics.length; i++) {
                                Map<String, Object> detail_pic_map = JsonUtil.json2Map(pmap
                                    .get(detail_pics[i]));
                                if (detail_pic_map != null) {
                                    Accessory image = new Accessory();
                                    image.setCreatetime(now);
                                    image.setExt((String) detail_pic_map.get("mime"));
                                    image.setPath((String) detail_pic_map.get("photo_url"));
                                    image.setWidth(CommUtil.null2Int(detail_pic_map.get("width")));
                                    image
                                        .setHeight(CommUtil.null2Int(detail_pic_map.get("height")));
                                    image.setName(CommUtil.null2String(detail_pic_map
                                        .get("fileName")));
                                    image.setUser(user);
                                    this.accessoryDAO.save(image);
                                    String url = CommUtil.getURL(request) + "/" + image.getPath()
                                                 + "/" + image.getName();
                                    //<img src="http://192.168.1.121:8080/upload/store/40/2017/12/14/b2127315-9e30-41b7-b6b4-dcf9a8e7145a.tbi" />
                                    detail = detail.replace("[img_" + i + "]", "<img src='" + url
                                                                               + "'/>");
                                }
                            }
                        }

                    }

                    goods.setGoods_details(detail);
                    goods.setDisabled(false);
                    goods.setCreatetime(now);
                    goods.setInventory_type("spec");
                    goods.setSeo_description("");
                    goods.setSeo_keywords("");
                    goods.setGoods_store(store);
                    goods.setEms_trans_fee(new BigDecimal(0));
                    goods.setExpress_trans_fee(new BigDecimal(0));
                    goods.setMail_trans_fee(new BigDecimal(0));
                    goods.setStorage_status(0);
                    goods.setGoods_status(1);
                    this.goodsDao.save(goods);

                    //保存货品
                    GoodsItem item = new GoodsItem();
                    item.setCreatetime(now);
                    item.setDisabled(false);
                    item.setGoods_inventory(CommUtil.null2Int(arrayList.get(14)));
                    item.setGoods_price(CommUtil.null2BigDecimal(storePrice));
                    item.setMarket_price(CommUtil.null2BigDecimal(marketPrice));
                    item.setGoods(goods);
                    item.setStatus(0);
                    item.setStep_price_state(0);
                    this.goodsItemDao.save(item);
                    goods.setGoods_inventory(item.getGoods_inventory());
                    this.goodsDao.update(goods);
                    goodsItems.add(item);
                    //服装接口创建商品
                    addGoodsJsonList(goodsJsonList, goods, goodsItems);

                }
                //服装接口创建商品
                if (goodsJsonList != null && goodsJsonList.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonList);
                    sendReqAsync.sendMessageUtil(Constant.GOODS_URL_BATCHADD, write2JsonStr,
                        "批量导入商品");

                }
                request.getSession(false).removeAttribute("upload_pic_map");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 导入商品
     */
    @Override
    @Transactional
    public int importBuyerGoods(User user, String[] gcIds, HttpServletRequest request,
                                String[] sale_pricesArr) {
        //查询该店家所有品牌
        Store store = user.getStore();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("store_id", store.getId());
        List<GoodsBrand> gbs = this.goodsBrandDao
            .query(
                "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.id",
                queryMap, -1, -1);
        Map<String, GoodsBrand> brandMap = new HashMap<String, GoodsBrand>();
        if (gbs != null && gbs.size() > 0) {
            for (GoodsBrand goodsBrand : gbs) {
                brandMap.put(goodsBrand.getName(), goodsBrand);
            }
        }

        //查询模板
        queryMap.clear();
        queryMap.put("store_id", store.getId());
        List<GoodsType> goodsTypes = this.goodsTypeDao.query(
            "select obj from GoodsType obj where obj.disabled = false and obj.store_id=:store_id",
            queryMap, -1, -1);
        Map<String, GoodsType> goodsTypeMap = new HashMap<String, GoodsType>();
        if (goodsTypes != null && goodsTypes.size() > 0) {
            for (GoodsType goodsType : goodsTypes) {
                goodsTypeMap.put(goodsType.getName(), goodsType);
            }
        }

        //查询该店家所有分类       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());
        List<UserGoodsClass> ugcs = this.userGoodsClassDao
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id  order by obj.id",
                params, -1, -1);
        Map<String, UserGoodsClass> classMap0 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap1 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap2 = new HashMap<String, UserGoodsClass>();
        if (ugcs != null && ugcs.size() > 0) {
            for (UserGoodsClass userGoodsClass : ugcs) {
                if (userGoodsClass.getLevel() == 0) {
                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 1) {
                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 2) {
                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                }

            }
        }
        //查询检索属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.clear();
        map.put("disabled", false);
        map.put("type", 1);
        String sql2 = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsSearchRetrieves = this.goodsRetrieveDao.query(sql2, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsSearchRetrieves != null && goodsSearchRetrieves.size() > 0) {
            for (GoodsRetrieve goodsRetrieve : goodsSearchRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyDao
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }
        }

        Date now = new Date();
        try {
            List<GoodsJsonVo> goodsJsonList = new ArrayList<GoodsJsonVo>();
            for (int k = 0; k < gcIds.length; k++) {

                GoodsCart goodsCart = (GoodsCart) this.goodsCartDAO.get(CommUtil
                    .null2Long(gcIds[k]));
                Goods importGoods = goodsCart.getGoods();
                Goods goods = new Goods();
                List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
                goods.setGoods_name(importGoods.getGoods_name());//商品名称
                goods.setGoods_type(importGoods.getGoods_type());//商品类型：0单件商品 1,整包商品，2走份商品
                goods.setSupplier_info(importGoods.getSupplier_info());
                String egoodsClassName = "";//商品分类名称
                //女装/衬衫
                List<UserGoodsClass> goods_ugcs = importGoods.getGoods_ugcs();
                if (goods_ugcs != null && goods_ugcs.size() > 0) {
                    UserGoodsClass userGoodsClass = goods_ugcs.get(0);
                    egoodsClassName = userGoodsClass.getClassName();
                    if (userGoodsClass.getParent() != null) {
                        UserGoodsClass puserGoodsClass = userGoodsClass.getParent();
                        egoodsClassName = puserGoodsClass.getClassName() + "/" + egoodsClassName;
                        if (puserGoodsClass.getParent() != null) {
                            UserGoodsClass ppuserGoodsClass = puserGoodsClass.getParent();
                            egoodsClassName = ppuserGoodsClass.getClassName() + "/"
                                              + egoodsClassName;
                        }
                    }
                }

                if (egoodsClassName != null && !"".equals(egoodsClassName)) {
                    String[] goodsClassNames = egoodsClassName.split("/");
                    //1级、2级、3级
                    UserGoodsClass parent = null;
                    for (int i = 0; i < goodsClassNames.length; i++) {
                        if (i < 3) {
                            String goodsClassName = goodsClassNames[i];
                            if (i == 0) {//一级分类
                                if (classMap0.containsKey(goodsClassName)) {
                                    if (i == goodsClassNames.length - 1) {
                                        goods.getGoods_ugcs().add(classMap0.get(goodsClassName));
                                    } else {
                                        parent = classMap0.get(goodsClassName);
                                    }
                                } else {//如果分类名称不存在则直接创建分类
                                    UserGoodsClass userGoodsClass = new UserGoodsClass();
                                    userGoodsClass.setClassName(goodsClassName);
                                    userGoodsClass.setCreatetime(now);
                                    userGoodsClass.setDisabled(false);
                                    userGoodsClass.setDisplay(true);
                                    userGoodsClass.setLevel(i);
                                    userGoodsClass.setSequence(0);
                                    userGoodsClass.setUser(user);
                                    this.userGoodsClassDao.save(userGoodsClass);

                                    //服装接口-没有分类创建分类
                                    sendUserGoodsClass(userGoodsClass);

                                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                                    if (i == goodsClassNames.length - 1) {//为商品设置分类
                                        goods.getGoods_ugcs().add(userGoodsClass);
                                    } else {
                                        parent = classMap0.get(goodsClassName);
                                    }
                                }

                            }
                            if (i == 1) {//二级分类
                                if (classMap1.containsKey(goodsClassName)) {
                                    if (i == goodsClassNames.length - 1) {
                                        goods.getGoods_ugcs().add(classMap1.get(goodsClassName));
                                    } else {
                                        parent = classMap1.get(goodsClassName);
                                    }
                                } else {//如果分类名称不存在则直接创建分类
                                    UserGoodsClass userGoodsClass = new UserGoodsClass();
                                    userGoodsClass.setClassName(goodsClassName);
                                    userGoodsClass.setCreatetime(now);
                                    userGoodsClass.setDisabled(false);
                                    userGoodsClass.setDisplay(true);
                                    userGoodsClass.setLevel(i);
                                    if (parent != null && parent.getId() != null) {
                                        userGoodsClass.setParent(parent);
                                    }
                                    userGoodsClass.setSequence(0);
                                    userGoodsClass.setUser(user);
                                    this.userGoodsClassDao.save(userGoodsClass);

                                    //服装接口-没有分类创建分类
                                    sendUserGoodsClass(userGoodsClass);

                                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                                    if (i == goodsClassNames.length - 1) {//为商品设置分类
                                        goods.getGoods_ugcs().add(userGoodsClass);
                                    } else {
                                        parent = classMap1.get(goodsClassName);
                                    }
                                }

                            }
                            if (i == 2) {//三级分类
                                if (classMap2.containsKey(goodsClassName)) {
                                    if (i == goodsClassNames.length - 1) {
                                        goods.getGoods_ugcs().add(classMap2.get(goodsClassName));
                                    } else {
                                        parent = classMap2.get(goodsClassName);
                                    }
                                } else {//如果分类名称不存在则直接创建分类
                                    UserGoodsClass userGoodsClass = new UserGoodsClass();
                                    userGoodsClass.setClassName(goodsClassName);
                                    userGoodsClass.setCreatetime(now);
                                    userGoodsClass.setDisabled(false);
                                    userGoodsClass.setDisplay(true);
                                    userGoodsClass.setLevel(i);
                                    if (parent != null && parent.getId() != null) {
                                        userGoodsClass.setParent(parent);
                                    }
                                    userGoodsClass.setSequence(0);
                                    userGoodsClass.setUser(user);
                                    this.userGoodsClassDao.save(userGoodsClass);

                                    //服装接口-没有分类创建分类
                                    sendUserGoodsClass(userGoodsClass);

                                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                                    if (i == goodsClassNames.length - 1) {//为商品设置分类
                                        goods.getGoods_ugcs().add(userGoodsClass);
                                    } else {
                                        parent = classMap2.get(goodsClassName);
                                    }
                                }

                            }

                        } else {
                            break;
                        }

                    }

                }

                goods.setGoods_units(importGoods.getGoods_units());//商品单位
                goods.setGoods_hot_status(importGoods.getGoods_hot_status());//是否热卖
                goods.setGoods_news_status(importGoods.getGoods_news_status());//是否新品
                goods.setGoods_recommend(importGoods.isGoods_recommend());

                String brandName = "";//品牌名称
                GoodsBrand impgoods_brand = importGoods.getGoods_brand();
                if (impgoods_brand != null) {
                    brandName = impgoods_brand.getName();
                }
                if (brandName != null && !"".equals(brandName)) {
                    if (brandMap.containsKey(brandName)) {
                        goods.setGoods_brand(brandMap.get(brandName));
                    } else {
                        GoodsBrand brand = new GoodsBrand();
                        brand.setDisabled(false);
                        brand.setCreatetime(now);
                        brand.setAudit(1);
                        brand.setName(impgoods_brand.getName());
                        brand.setRecommend(false);
                        brand.setSequence(0);
                        brand.setUserStatus(0);
                        brand.setUser(user);
                        brand.setStore(store);
                        brand.setFirst_word(impgoods_brand.getFirst_word());
                        //	
                        Accessory brandLogo = impgoods_brand.getBrandLogo();
                        Accessory photo2 = new Accessory();
                        photo2.setName(brandLogo.getName());
                        photo2.setExt(brandLogo.getExt());
                        photo2.setSize(brandLogo.getSize());
                        photo2.setPath(brandLogo.getPath());
                        photo2.setWidth(brandLogo.getWidth());
                        photo2.setHeight(brandLogo.getHeight());
                        photo2.setCreatetime(now);
                        this.accessoryDAO.save(photo2);
                        brand.setBrandLogo(photo2);
                        //保存品牌
                        this.goodsBrandDao.save(brand);
                        goods.setGoods_brand(brand);

                        brandMap.put(brandName, brand);

                        //服装接口没有品牌则创建品牌
                        sendGoodsBrand(brand);

                    }
                }
                //商品销售价按照前台设置的保存
                goods.setStore_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                goods.setGoods_price(importGoods.getGoods_price());
                goods.setGoods_current_price(importGoods.getGoods_current_price());

                //扩展属性 12
                //设置扩展属性
                goods.setExtendedAttributes(importGoods.getExtendedAttributes());
                //商品图片上传13
                List<Accessory> goods_photos = importGoods.getGoods_photos();
                if (goods_photos != null && goods_photos.size() > 0) {
                    int index = 0;
                    for (Accessory importPhoto : goods_photos) {
                        Accessory image = new Accessory();
                        image.setCreatetime(now);
                        image.setExt(importPhoto.getExt());
                        image.setPath(importPhoto.getPath());
                        image.setWidth(importPhoto.getWidth());
                        image.setHeight(importPhoto.getHeight());
                        image.setName(importPhoto.getName());
                        this.accessoryDAO.save(image);

                        if (index == 0) {
                            goods.setGoods_main_photo(image);
                        } else {
                            goods.getGoods_photos().add(image);
                        }
                    }
                    index++;

                }
                //商品详情及其图片
                String detail = importGoods.getGoods_details();
                goods.setGoods_details(detail);
                goods.setDisabled(false);
                goods.setCreatetime(now);
                goods.setInventory_type("spec");
                goods.setSeo_description("");
                goods.setSeo_keywords("");
                goods.setGoods_store(store);
                goods.setEms_trans_fee(new BigDecimal(0));
                goods.setExpress_trans_fee(new BigDecimal(0));
                goods.setMail_trans_fee(new BigDecimal(0));
                goods.setStorage_status(0);
                goods.setGoods_status(1);
                this.goodsDao.save(goods);
                GoodsType impGoodsType = (GoodsType) this.goodsTypeDao.get(CommUtil
                    .null2Long(importGoods.getGoodsTypeId()));
                String sepcTemp = "";
                if (impGoodsType != null) {
                    sepcTemp = impGoodsType.getName();
                }
                if (sepcTemp != null && !"".equals(sepcTemp) && goodsCart.getSpec_id() != null
                    && !"".equals(goodsCart.getSpec_id())) {
                    //规格模板名称
                    if (goodsTypeMap.containsKey(sepcTemp)) {
                        GoodsType goodsType = goodsTypeMap.get(sepcTemp);
                        goods.setGoodsTypeId(CommUtil.null2Int(goodsType.getId()));

                        String specinfo = goodsCart.getSpec_info();
                        List<GoodsSpecification> gss = goodsType.getGss();
                        List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                        GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                        List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                        GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                        if (gss != null && gss.size() > 0) {
                            for (GoodsSpecification goodsSpecification : gss) {
                                if ("尺码".equals(goodsSpecification.getName().trim())) {
                                    sizeproperties = goodsSpecification.getProperties();
                                    sizeGoodsSpecification = goodsSpecification;
                                }
                                if ("颜色".equals(goodsSpecification.getName().trim())) {
                                    colorproperties = goodsSpecification.getProperties();
                                    colorGoodsSpecification = goodsSpecification;
                                }

                            }
                        }

                        String[] specinfos = specinfo.split(" ");
                        if (specinfos != null && specinfos.length > 0) {

                            //判断规格是否存在，不存在则创建
                            //尺码:42码,颜色:黑色
                            GoodsItem item = new GoodsItem();
                            item.setCreatetime(now);
                            item.setDisabled(false);
                            item.setGoods_inventory(goodsCart.getCount());

                            //商品销售价按照前台设置的保存
                            item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                            //进货价即购物车里商品价格
                            item.setPurchase_price(goodsCart.getPrice());
                            item.setMarket_price(goodsCart.getPrice());

                            item.setGoods(goods);
                            item.setStep_price_state(0);
                            item.setStatus(1);
                            long size_spec_id = 0;
                            long color_spec_id = 0;
                            String spec_info = "";

                            //判断尺码是否存在

                            String spec_size = "";
                            for (int i = 0; i < specinfos.length; i++) {
                                if (specinfos[i].contains("尺码")) {
                                    spec_size = specinfos[i];
                                    break;
                                }
                            }
                            String spec_0 = "0";
                            if (spec_size != null && !"".equals(spec_size)
                                && !"0".equals(spec_size)) {
                                String[] split = spec_size.split(":");
                                if (split != null && split.length == 2) {
                                    spec_0 = split[1];
                                }
                            }
                            for (int i = 0; i < sizeproperties.size(); i++) {
                                GoodsSpecProperty goodsSpecProperty = sizeproperties.get(i);
                                //尺码存在
                                if (spec_0.equals(goodsSpecProperty.getValue())) {
                                    size_spec_id = goodsSpecProperty.getId();
                                    spec_info += goodsSpecProperty.getValue() + " ";
                                    List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                                    boolean flag = true;
                                    if (goods_specs != null && goods_specs.size() > 0) {
                                        for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                            if (goodsSpecProperty2.getValue().equals(
                                                goodsSpecProperty.getValue())) {
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        goods.getGoods_specs().add(goodsSpecProperty);
                                    }

                                    break;
                                }
                            }
                            if (!"0".equals(spec_0) && !"-1".equals(spec_0) && size_spec_id == 0) {
                                //如果输入了尺码但是不存在，则创建
                                //尺码不存在则创建
                                GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                nGoodsSpecProperty.setCreatetime(now);
                                nGoodsSpecProperty.setDisabled(false);
                                nGoodsSpecProperty.setSequence(0);
                                nGoodsSpecProperty.setValue(spec_0);
                                if (sizeGoodsSpecification != null
                                    && sizeGoodsSpecification.getId() != null) {
                                    nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                    this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                    sizeproperties.add(nGoodsSpecProperty);
                                    size_spec_id = nGoodsSpecProperty.getId();
                                    spec_info += nGoodsSpecProperty.getValue() + " ";
                                    List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                                    boolean flag = true;
                                    if (goods_specs != null && goods_specs.size() > 0) {
                                        for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                            if (goodsSpecProperty2.getValue().equals(
                                                nGoodsSpecProperty.getValue())) {
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        goods.getGoods_specs().add(nGoodsSpecProperty);
                                    }

                                    //服装接口如果没有尺码值则创建尺码
                                    sendSpecProperty(now, sizeGoodsSpecification,
                                        nGoodsSpecProperty);
                                }

                            }

                            //判断颜色是否存在
                            String spec_color = "";
                            for (int i = 0; i < specinfos.length; i++) {
                                if (specinfos[i].contains("颜色")) {
                                    spec_color = specinfos[i];
                                    break;
                                }
                            }
                            String spec_1 = "0";
                            if (spec_color != null && !"".equals(spec_color)
                                && !"0".equals(spec_color)) {
                                String[] split = spec_color.split(":");
                                if (split != null && split.length == 2) {
                                    spec_1 = split[1];
                                }
                            }

                            for (int i = 0; i < colorproperties.size(); i++) {
                                GoodsSpecProperty goodsSpecProperty = colorproperties.get(i);
                                //颜色存在
                                if (spec_1.equals(goodsSpecProperty.getValue())) {
                                    color_spec_id = goodsSpecProperty.getId();
                                    spec_info += goodsSpecProperty.getValue() + " ";
                                    List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                                    boolean flag = true;
                                    if (goods_specs != null && goods_specs.size() > 0) {
                                        for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                            if (goodsSpecProperty2.getValue().equals(
                                                goodsSpecProperty.getValue())) {
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        goods.getGoods_specs().add(goodsSpecProperty);
                                    }

                                    break;
                                }
                            }
                            if (!"-1".equals(spec_1) && !"0".equals(spec_1) && color_spec_id == 0) {
                                //如果输入了颜色但是不存在，则创建
                                //尺码不存在则创建
                                GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                nGoodsSpecProperty.setCreatetime(now);
                                nGoodsSpecProperty.setDisabled(false);
                                nGoodsSpecProperty.setSequence(0);
                                nGoodsSpecProperty.setValue(spec_1);
                                if (colorGoodsSpecification != null
                                    && colorGoodsSpecification.getId() != null) {
                                    nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                    this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                    colorproperties.add(nGoodsSpecProperty);
                                    color_spec_id = nGoodsSpecProperty.getId();
                                    spec_info += nGoodsSpecProperty.getValue() + " ";
                                    List<GoodsSpecProperty> goods_specs = goods.getGoods_specs();
                                    boolean flag = true;
                                    if (goods_specs != null && goods_specs.size() > 0) {
                                        for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                            if (goodsSpecProperty2.getValue().equals(
                                                nGoodsSpecProperty.getValue())) {
                                                flag = false;
                                                break;
                                            }
                                        }
                                    }
                                    if (flag) {
                                        goods.getGoods_specs().add(nGoodsSpecProperty);
                                    }

                                    //服装接口如果没有颜色值则创建颜色
                                    sendSpecProperty(now, colorGoodsSpecification,
                                        nGoodsSpecProperty);
                                }
                            }

                            if (size_spec_id > 0 || color_spec_id > 0) {
                                item.setSpec_info(spec_info);
                                if (size_spec_id > 0 && color_spec_id == 0) {
                                    item.setSpec_combination(size_spec_id + "_");
                                }
                                if (color_spec_id > 0 && color_spec_id == 0) {
                                    item.setSpec_combination(color_spec_id + "_");
                                }
                                if (size_spec_id > 0 && color_spec_id > 0) {
                                    String combination = "";
                                    if (size_spec_id > color_spec_id) {
                                        combination = color_spec_id + "_" + size_spec_id + "_";
                                    }
                                    if (size_spec_id < color_spec_id) {
                                        combination = size_spec_id + "_" + color_spec_id + "_";
                                    }
                                    item.setSpec_combination(combination);
                                }

                                //保存货品
                                this.goodsItemDao.save(item);

                                goodsItems.add(item);
                            }
                        }

                        this.goodsDao.update(goods);

                        //服装接口创建商品

                        addGoodsJsonList(goodsJsonList, goods, goodsItems);

                    } else {//改用户没有此模板则创建

                        System.out.println("创建模板***********");
                        GoodsType importGoodsType = (GoodsType) this.goodsTypeDao.get(CommUtil
                            .null2Long(importGoods.getGoodsTypeId()));
                        if (importGoodsType != null) {
                            GoodsType goodsType = new GoodsType();
                            goodsType.setCreatetime(now);
                            goodsType.setDisabled(false);
                            goodsType.setName(impGoodsType.getName());
                            goodsType.setStore_id(store.getId());
                            List<GoodsSpecification> impgss = impGoodsType.getGss();
                            if (impgss != null && impgss.size() > 0) {
                                for (GoodsSpecification goodsSpecification : impgss) {
                                    GoodsSpecification specification = new GoodsSpecification();
                                    specification.setCreatetime(now);
                                    specification.setDisabled(false);
                                    specification.setSequence(goodsSpecification.getSequence());
                                    specification.setStore_id(store.getId());
                                    specification.setName(goodsSpecification.getName());
                                    specification.setType(goodsSpecification.getType());
                                    this.goodsSpecificationDAO.save(specification);
                                    goodsType.getGss().add(specification);
                                }

                            }
                            this.goodsTypeDao.save(goodsType);//保存模板
                            System.out.println("创建模板***********11111");

                            goodsTypeMap.put(goodsType.getName(), goodsType);

                            goods.setGoodsTypeId(CommUtil.null2Int(goodsType.getId()));

                            String specinfo = goodsCart.getSpec_info();
                            List<GoodsSpecification> gss = goodsType.getGss();
                            List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                            List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                            if (gss != null && gss.size() > 0) {
                                for (GoodsSpecification goodsSpecification : gss) {
                                    if ("尺码".equals(goodsSpecification.getName().trim())) {
                                        sizeproperties = goodsSpecification.getProperties();
                                        sizeGoodsSpecification = goodsSpecification;
                                    }
                                    if ("颜色".equals(goodsSpecification.getName().trim())) {
                                        colorproperties = goodsSpecification.getProperties();
                                        colorGoodsSpecification = goodsSpecification;
                                    }

                                }
                            }

                            String[] specinfos = specinfo.split(" ");
                            if (specinfos != null && specinfos.length > 0) {

                                //判断规格是否存在，不存在则创建
                                //尺码:42码,颜色:黑色
                                GoodsItem item = new GoodsItem();
                                item.setCreatetime(now);
                                item.setDisabled(false);
                                item.setGoods_inventory(goodsCart.getCount());

                                //商品销售价按照前台设置的保存
                                item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                                //进货价即购物车里商品价格
                                item.setPurchase_price(goodsCart.getPrice());
                                item.setMarket_price(goodsCart.getPrice());

                                item.setGoods(goods);
                                item.setStep_price_state(0);
                                item.setStatus(1);
                                long size_spec_id = 0;
                                long color_spec_id = 0;
                                String spec_info = "";

                                //判断尺码是否存在

                                String spec_size = "";
                                for (int i = 0; i < specinfos.length; i++) {
                                    if (specinfos[i].contains("尺码")) {
                                        spec_size = specinfos[i];
                                        break;
                                    }
                                }
                                String spec_0 = "0";
                                if (spec_size != null && !"".equals(spec_size)
                                    && !"0".equals(spec_size)) {
                                    String[] split = spec_size.split(":");
                                    if (split != null && split.length == 2) {
                                        spec_0 = split[1];
                                    }
                                }
                                for (int i = 0; i < sizeproperties.size(); i++) {
                                    GoodsSpecProperty goodsSpecProperty = sizeproperties.get(i);
                                    //尺码存在
                                    if (spec_0.equals(goodsSpecProperty.getValue())) {
                                        size_spec_id = goodsSpecProperty.getId();
                                        spec_info += goodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    goodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(goodsSpecProperty);
                                        }

                                        break;
                                    }
                                }
                                if (!"0".equals(spec_0) && !"-1".equals(spec_0)
                                    && size_spec_id == 0) {
                                    //如果输入了尺码但是不存在，则创建
                                    //尺码不存在则创建
                                    GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                    nGoodsSpecProperty.setCreatetime(now);
                                    nGoodsSpecProperty.setDisabled(false);
                                    nGoodsSpecProperty.setSequence(0);
                                    nGoodsSpecProperty.setValue(spec_0);
                                    if (sizeGoodsSpecification != null
                                        && sizeGoodsSpecification.getId() != null) {
                                        nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                        this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                        sizeproperties.add(nGoodsSpecProperty);
                                        size_spec_id = nGoodsSpecProperty.getId();
                                        spec_info += nGoodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    nGoodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(nGoodsSpecProperty);
                                        }

                                        //服装接口如果没有尺码值则创建尺码
                                        sendSpecProperty(now, sizeGoodsSpecification,
                                            nGoodsSpecProperty);
                                    }

                                }

                                //判断颜色是否存在
                                String spec_color = "";
                                for (int i = 0; i < specinfos.length; i++) {
                                    if (specinfos[i].contains("颜色")) {
                                        spec_color = specinfos[i];
                                        break;
                                    }
                                }
                                String spec_1 = "0";
                                if (spec_color != null && !"".equals(spec_color)
                                    && !"0".equals(spec_color)) {
                                    String[] split = spec_color.split(":");
                                    if (split != null && split.length == 2) {
                                        spec_1 = split[1];
                                    }
                                }

                                for (int i = 0; i < colorproperties.size(); i++) {
                                    GoodsSpecProperty goodsSpecProperty = colorproperties.get(i);
                                    //颜色存在
                                    if (spec_1.equals(goodsSpecProperty.getValue())) {
                                        color_spec_id = goodsSpecProperty.getId();
                                        spec_info += goodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    goodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(goodsSpecProperty);
                                        }

                                        break;
                                    }
                                }
                                if (!"-1".equals(spec_1) && !"0".equals(spec_1)
                                    && color_spec_id == 0) {
                                    //如果输入了颜色但是不存在，则创建
                                    //尺码不存在则创建
                                    GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                    nGoodsSpecProperty.setCreatetime(now);
                                    nGoodsSpecProperty.setDisabled(false);
                                    nGoodsSpecProperty.setSequence(0);
                                    nGoodsSpecProperty.setValue(spec_1);
                                    if (colorGoodsSpecification != null
                                        && colorGoodsSpecification.getId() != null) {
                                        nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                        this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                        colorproperties.add(nGoodsSpecProperty);
                                        color_spec_id = nGoodsSpecProperty.getId();
                                        spec_info += nGoodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    nGoodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(nGoodsSpecProperty);
                                        }

                                        //服装接口如果没有颜色值则创建颜色
                                        sendSpecProperty(now, colorGoodsSpecification,
                                            nGoodsSpecProperty);
                                    }
                                }

                                if (size_spec_id > 0 || color_spec_id > 0) {
                                    item.setSpec_info(spec_info);
                                    if (size_spec_id > 0 && color_spec_id == 0) {
                                        item.setSpec_combination(size_spec_id + "_");
                                    }
                                    if (color_spec_id > 0 && color_spec_id == 0) {
                                        item.setSpec_combination(color_spec_id + "_");
                                    }
                                    if (size_spec_id > 0 && color_spec_id > 0) {
                                        String combination = "";
                                        if (size_spec_id > color_spec_id) {
                                            combination = color_spec_id + "_" + size_spec_id + "_";
                                        }
                                        if (size_spec_id < color_spec_id) {
                                            combination = size_spec_id + "_" + color_spec_id + "_";
                                        }
                                        item.setSpec_combination(combination);
                                    }

                                    //保存货品
                                    this.goodsItemDao.save(item);

                                    goodsItems.add(item);
                                }
                            }
                            this.goodsDao.update(goods);
                            //服装接口创建商品
                            addGoodsJsonList(goodsJsonList, goods, goodsItems);

                        }
                    }
                } else {//如果没有规格
                    GoodsItem item = new GoodsItem();
                    item.setCreatetime(now);
                    item.setDisabled(false);
                    item.setGoods_inventory(goodsCart.getCount());

                    //商品销售价按照前台设置的保存
                    item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                    //进货价即购物车里商品价格
                    item.setPurchase_price(goodsCart.getPrice());

                    item.setMarket_price(goodsCart.getPrice());

                    item.setGoods(goods);
                    item.setStatus(0);
                    item.setStep_price_state(0);
                    this.goodsItemDao.save(item);
                    goods.setGoods_inventory(item.getGoods_inventory());
                    goods.setTotal_weight(importGoods.getTotal_weight());
                    goods.setType_ratio(importGoods.getType_ratio());
                    goods.setColor_ratio(importGoods.getColor_ratio());
                    goods.setSize_ratio(importGoods.getSize_ratio());
                    goods.setSingle_weight(importGoods.getSingle_weight());

                    this.goodsDao.update(goods);
                    goodsItems.add(item);

                    //服装接口创建商品
                    addGoodsJsonList(goodsJsonList, goods, goodsItems);
                }

            }

            //服装接口创建商品
            if (goodsJsonList != null && goodsJsonList.size() > 0) {
                String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonList);
                sendReqAsync.sendMessageUtil(Constant.GOODS_URL_BATCHADD, write2JsonStr, "批量导入商品");

            }
            request.getSession(false).removeAttribute("upload_pic_map");

        } catch (Exception e) {

        }

        return 0;
    }

    /**
     * 合并商品
     */
    @Override
    @Transactional
    public int importMergeBuyerGoods(User user, String[] gcIds, HttpServletRequest request,
                                     String[] sale_pricesArr) {
        //查询该店家所有品牌
        Store store = user.getStore();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("store_id", store.getId());
        List<GoodsBrand> gbs = this.goodsBrandDao
            .query(
                "select obj from GoodsBrand obj where  obj.disabled = false and obj.audit=1 and obj.store.id=:store_id order by obj.id",
                queryMap, -1, -1);
        Map<String, GoodsBrand> brandMap = new HashMap<String, GoodsBrand>();
        if (gbs != null && gbs.size() > 0) {
            for (GoodsBrand goodsBrand : gbs) {
                brandMap.put(goodsBrand.getName(), goodsBrand);
            }
        }

        //查询模板
        queryMap.clear();
        queryMap.put("store_id", store.getId());
        List<GoodsType> goodsTypes = this.goodsTypeDao.query(
            "select obj from GoodsType obj where obj.disabled = false and obj.store_id=:store_id",
            queryMap, -1, -1);
        Map<String, GoodsType> goodsTypeMap = new HashMap<String, GoodsType>();
        if (goodsTypes != null && goodsTypes.size() > 0) {
            for (GoodsType goodsType : goodsTypes) {
                goodsTypeMap.put(goodsType.getName(), goodsType);
            }
        }

        //查询该店家所有分类       
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", user.getId());
        List<UserGoodsClass> ugcs = this.userGoodsClassDao
            .query(
                "select obj from UserGoodsClass obj where obj.disabled = false and obj.user.id=:user_id  order by obj.id",
                params, -1, -1);
        Map<String, UserGoodsClass> classMap0 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap1 = new HashMap<String, UserGoodsClass>();
        Map<String, UserGoodsClass> classMap2 = new HashMap<String, UserGoodsClass>();
        if (ugcs != null && ugcs.size() > 0) {
            for (UserGoodsClass userGoodsClass : ugcs) {
                if (userGoodsClass.getLevel() == 0) {
                    classMap0.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 1) {
                    classMap1.put(userGoodsClass.getClassName(), userGoodsClass);
                }
                if (userGoodsClass.getLevel() == 2) {
                    classMap2.put(userGoodsClass.getClassName(), userGoodsClass);
                }

            }
        }
        //查询检索属性
        Map<String, Object> map = new HashMap<String, Object>();
        map.clear();
        map.put("disabled", false);
        map.put("type", 1);
        String sql2 = "select obj from GoodsRetrieve obj where disabled=:disabled and type=:type order by id asc";
        List<GoodsRetrieve> goodsSearchRetrieves = this.goodsRetrieveDao.query(sql2, map, -1, -1);
        Map<String, List<GoodsRetrieveProperty>> retriePro = new HashMap<String, List<GoodsRetrieveProperty>>();
        if (goodsSearchRetrieves != null && goodsSearchRetrieves.size() > 0) {
            for (GoodsRetrieve goodsRetrieve : goodsSearchRetrieves) {
                //查询属性值
                Map<String, Object> qmap = new HashMap<String, Object>();
                qmap.put("store_id", store.getId());
                qmap.put("retrieve_id", goodsRetrieve.getId());
                List<GoodsRetrieveProperty> list = goodsRetrievePropertyDao
                    .query(
                        "select obj from GoodsRetrieveProperty obj where obj.disabled=false and obj.store.id=:store_id and obj.retrieve.id=:retrieve_id ",
                        qmap, -1, -1);
                if (list != null && list.size() > 0) {
                    retriePro.put(goodsRetrieve.getName(), list);
                }
            }
        }

        Date now = new Date();
        try {
            List<GoodsJsonVo> goodsJsonList = new ArrayList<GoodsJsonVo>();
            for (int k = 0; k < gcIds.length; k++) {

                GoodsCart goodsCart = (GoodsCart) this.goodsCartDAO.get(CommUtil
                    .null2Long(gcIds[k]));
                Goods importGoods = goodsCart.getGoods();
                //覆盖导入则判断标准是商品名称
                Map<String, Object> queryMap2 = new HashMap<String, Object>();
                queryMap2.put("store_id", store.getId());
                queryMap2.put("goods_name", importGoods.getGoods_name());
                List<Goods> goodslist = this.goodsDao
                    .query(
                        "select obj from Goods obj where obj.disabled=false and obj.goods_store.id=:store_id and obj.goods_name=:goods_name ",
                        queryMap2, 0, 1);
                //不存在则创建
                if (goodslist == null || goodslist.size() == 0) {
                    Goods goods = new Goods();
                    List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
                    goods.setGoods_name(importGoods.getGoods_name());//商品名称
                    goods.setGoods_type(importGoods.getGoods_type());//商品类型：0单件商品 1,整包商品，2走份商品
                    goods.setSupplier_info(importGoods.getSupplier_info());
                    String egoodsClassName = "";//商品分类名称
                    //女装/衬衫
                    List<UserGoodsClass> goods_ugcs = importGoods.getGoods_ugcs();
                    if (goods_ugcs != null && goods_ugcs.size() > 0) {
                        UserGoodsClass userGoodsClass = goods_ugcs.get(0);
                        egoodsClassName = userGoodsClass.getClassName();
                        if (userGoodsClass.getParent() != null) {
                            UserGoodsClass puserGoodsClass = userGoodsClass.getParent();
                            egoodsClassName = puserGoodsClass.getClassName() + "/"
                                              + egoodsClassName;
                            if (puserGoodsClass.getParent() != null) {
                                UserGoodsClass ppuserGoodsClass = puserGoodsClass.getParent();
                                egoodsClassName = ppuserGoodsClass.getClassName() + "/"
                                                  + egoodsClassName;
                            }
                        }
                    }

                    if (egoodsClassName != null && !"".equals(egoodsClassName)) {
                        String[] goodsClassNames = egoodsClassName.split("/");
                        //1级、2级、3级
                        UserGoodsClass parent = null;
                        for (int i = 0; i < goodsClassNames.length; i++) {
                            if (i < 3) {
                                String goodsClassName = goodsClassNames[i];
                                if (i == 0) {//一级分类
                                    if (classMap0.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap0.get(goodsClassName));
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap0
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap0.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 1) {//二级分类
                                    if (classMap1.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap1.get(goodsClassName));
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap1
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap1.get(goodsClassName);
                                        }
                                    }

                                }
                                if (i == 2) {//三级分类
                                    if (classMap2.containsKey(goodsClassName)) {
                                        if (i == goodsClassNames.length - 1) {
                                            goods.getGoods_ugcs()
                                                .add(classMap2.get(goodsClassName));
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    } else {//如果分类名称不存在则直接创建分类
                                        UserGoodsClass userGoodsClass = new UserGoodsClass();
                                        userGoodsClass.setClassName(goodsClassName);
                                        userGoodsClass.setCreatetime(now);
                                        userGoodsClass.setDisabled(false);
                                        userGoodsClass.setDisplay(true);
                                        userGoodsClass.setLevel(i);
                                        if (parent != null && parent.getId() != null) {
                                            userGoodsClass.setParent(parent);
                                        }
                                        userGoodsClass.setSequence(0);
                                        userGoodsClass.setUser(user);
                                        this.userGoodsClassDao.save(userGoodsClass);

                                        //服装接口-没有分类创建分类
                                        sendUserGoodsClass(userGoodsClass);

                                        classMap2
                                            .put(userGoodsClass.getClassName(), userGoodsClass);
                                        if (i == goodsClassNames.length - 1) {//为商品设置分类
                                            goods.getGoods_ugcs().add(userGoodsClass);
                                        } else {
                                            parent = classMap2.get(goodsClassName);
                                        }
                                    }

                                }

                            } else {
                                break;
                            }

                        }

                    }

                    goods.setGoods_units(importGoods.getGoods_units());//商品单位
                    goods.setGoods_hot_status(importGoods.getGoods_hot_status());//是否热卖
                    goods.setGoods_news_status(importGoods.getGoods_news_status());//是否新品
                    goods.setGoods_recommend(importGoods.isGoods_recommend());

                    String brandName = "";//品牌名称
                    GoodsBrand impgoods_brand = importGoods.getGoods_brand();
                    if (impgoods_brand != null) {
                        brandName = impgoods_brand.getName();
                    }
                    if (brandName != null && !"".equals(brandName)) {
                        if (brandMap.containsKey(brandName)) {
                            goods.setGoods_brand(brandMap.get(brandName));
                        } else {
                            GoodsBrand brand = new GoodsBrand();
                            brand.setDisabled(false);
                            brand.setCreatetime(now);
                            brand.setAudit(1);
                            brand.setName(impgoods_brand.getName());
                            brand.setRecommend(false);
                            brand.setSequence(0);
                            brand.setUserStatus(0);
                            brand.setUser(user);
                            brand.setStore(store);
                            brand.setFirst_word(impgoods_brand.getFirst_word());
                            //	
                            Accessory brandLogo = impgoods_brand.getBrandLogo();
                            Accessory photo2 = new Accessory();
                            photo2.setName(brandLogo.getName());
                            photo2.setExt(brandLogo.getExt());
                            photo2.setSize(brandLogo.getSize());
                            photo2.setPath(brandLogo.getPath());
                            photo2.setWidth(brandLogo.getWidth());
                            photo2.setHeight(brandLogo.getHeight());
                            photo2.setCreatetime(now);
                            this.accessoryDAO.save(photo2);
                            brand.setBrandLogo(photo2);
                            //保存品牌
                            this.goodsBrandDao.save(brand);
                            goods.setGoods_brand(brand);

                            brandMap.put(brandName, brand);

                            //服装接口没有品牌则创建品牌
                            sendGoodsBrand(brand);

                        }
                    }

                    //商品销售价按照前台设置的保存
                    goods.setStore_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                    goods.setGoods_price(importGoods.getGoods_price());
                    goods.setGoods_current_price(importGoods.getGoods_current_price());

                    //扩展属性 12
                    //设置扩展属性
                    goods.setExtendedAttributes(importGoods.getExtendedAttributes());
                    //商品图片上传13
                    List<Accessory> goods_photos = importGoods.getGoods_photos();
                    if (goods_photos != null && goods_photos.size() > 0) {
                        int index = 0;
                        for (Accessory importPhoto : goods_photos) {
                            Accessory image = new Accessory();
                            image.setCreatetime(now);
                            image.setExt(importPhoto.getExt());
                            image.setPath(importPhoto.getPath());
                            image.setWidth(importPhoto.getWidth());
                            image.setHeight(importPhoto.getHeight());
                            image.setName(importPhoto.getName());
                            this.accessoryDAO.save(image);

                            if (index == 0) {
                                goods.setGoods_main_photo(image);
                            } else {
                                goods.getGoods_photos().add(image);
                            }
                        }
                        index++;

                    }
                    //商品详情及其图片
                    String detail = importGoods.getGoods_details();
                    goods.setGoods_details(detail);
                    goods.setDisabled(false);
                    goods.setCreatetime(now);
                    goods.setInventory_type("spec");
                    goods.setSeo_description("");
                    goods.setSeo_keywords("");
                    goods.setGoods_store(store);
                    goods.setEms_trans_fee(new BigDecimal(0));
                    goods.setExpress_trans_fee(new BigDecimal(0));
                    goods.setMail_trans_fee(new BigDecimal(0));
                    goods.setStorage_status(0);
                    goods.setGoods_status(1);
                    this.goodsDao.save(goods);
                    GoodsType impGoodsType = (GoodsType) this.goodsTypeDao.get(CommUtil
                        .null2Long(importGoods.getGoodsTypeId()));
                    String sepcTemp = "";
                    if (impGoodsType != null) {
                        sepcTemp = impGoodsType.getName();
                    }
                    if (sepcTemp != null && !"".equals(sepcTemp) && goodsCart.getSpec_id() != null
                        && !"".equals(goodsCart.getSpec_id())) {
                        //规格模板名称
                        if (goodsTypeMap.containsKey(sepcTemp)) {
                            GoodsType goodsType = goodsTypeMap.get(sepcTemp);
                            goods.setGoodsTypeId(CommUtil.null2Int(goodsType.getId()));

                            String specinfo = goodsCart.getSpec_info();
                            List<GoodsSpecification> gss = goodsType.getGss();
                            List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                            List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                            GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                            if (gss != null && gss.size() > 0) {
                                for (GoodsSpecification goodsSpecification : gss) {
                                    if ("尺码".equals(goodsSpecification.getName().trim())) {
                                        sizeproperties = goodsSpecification.getProperties();
                                        sizeGoodsSpecification = goodsSpecification;
                                    }
                                    if ("颜色".equals(goodsSpecification.getName().trim())) {
                                        colorproperties = goodsSpecification.getProperties();
                                        colorGoodsSpecification = goodsSpecification;
                                    }

                                }
                            }

                            String[] specinfos = specinfo.split(" ");
                            if (specinfos != null && specinfos.length > 0) {

                                //判断规格是否存在，不存在则创建
                                //尺码:42码,颜色:黑色
                                GoodsItem item = new GoodsItem();
                                item.setCreatetime(now);
                                item.setDisabled(false);
                                item.setGoods_inventory(goodsCart.getCount());

                                //商品销售价按照前台设置的保存
                                item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                                item.setPurchase_price(goodsCart.getPrice());
                                item.setMarket_price(goodsCart.getPrice());

                                item.setGoods(goods);
                                item.setStep_price_state(0);
                                item.setStatus(1);
                                long size_spec_id = 0;
                                long color_spec_id = 0;
                                String spec_info = "";

                                //判断尺码是否存在

                                String spec_size = "";
                                for (int i = 0; i < specinfos.length; i++) {
                                    if (specinfos[i].contains("尺码")) {
                                        spec_size = specinfos[i];
                                        break;
                                    }
                                }
                                String spec_0 = "0";
                                if (spec_size != null && !"".equals(spec_size)
                                    && !"0".equals(spec_size)) {
                                    String[] split = spec_size.split(":");
                                    if (split != null && split.length == 2) {
                                        spec_0 = split[1];
                                    }
                                }
                                for (int i = 0; i < sizeproperties.size(); i++) {
                                    GoodsSpecProperty goodsSpecProperty = sizeproperties.get(i);
                                    //尺码存在
                                    if (spec_0.equals(goodsSpecProperty.getValue())) {
                                        size_spec_id = goodsSpecProperty.getId();
                                        spec_info += goodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    goodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(goodsSpecProperty);
                                        }

                                        break;
                                    }
                                }
                                if (!"0".equals(spec_0) && !"-1".equals(spec_0)
                                    && size_spec_id == 0) {
                                    //如果输入了尺码但是不存在，则创建
                                    //尺码不存在则创建
                                    GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                    nGoodsSpecProperty.setCreatetime(now);
                                    nGoodsSpecProperty.setDisabled(false);
                                    nGoodsSpecProperty.setSequence(0);
                                    nGoodsSpecProperty.setValue(spec_0);
                                    if (sizeGoodsSpecification != null
                                        && sizeGoodsSpecification.getId() != null) {
                                        nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                        this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                        sizeproperties.add(nGoodsSpecProperty);
                                        size_spec_id = nGoodsSpecProperty.getId();
                                        spec_info += nGoodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    nGoodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(nGoodsSpecProperty);
                                        }

                                        //服装接口如果没有尺码值则创建尺码
                                        sendSpecProperty(now, sizeGoodsSpecification,
                                            nGoodsSpecProperty);
                                    }

                                }

                                //判断颜色是否存在
                                String spec_color = "";
                                for (int i = 0; i < specinfos.length; i++) {
                                    if (specinfos[i].contains("颜色")) {
                                        spec_color = specinfos[i];
                                        break;
                                    }
                                }
                                String spec_1 = "0";
                                if (spec_color != null && !"".equals(spec_color)
                                    && !"0".equals(spec_color)) {
                                    String[] split = spec_color.split(":");
                                    if (split != null && split.length == 2) {
                                        spec_1 = split[1];
                                    }
                                }

                                for (int i = 0; i < colorproperties.size(); i++) {
                                    GoodsSpecProperty goodsSpecProperty = colorproperties.get(i);
                                    //颜色存在
                                    if (spec_1.equals(goodsSpecProperty.getValue())) {
                                        color_spec_id = goodsSpecProperty.getId();
                                        spec_info += goodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    goodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(goodsSpecProperty);
                                        }

                                        break;
                                    }
                                }
                                if (!"-1".equals(spec_1) && !"0".equals(spec_1)
                                    && color_spec_id == 0) {
                                    //如果输入了颜色但是不存在，则创建
                                    //尺码不存在则创建
                                    GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                    nGoodsSpecProperty.setCreatetime(now);
                                    nGoodsSpecProperty.setDisabled(false);
                                    nGoodsSpecProperty.setSequence(0);
                                    nGoodsSpecProperty.setValue(spec_1);
                                    if (colorGoodsSpecification != null
                                        && colorGoodsSpecification.getId() != null) {
                                        nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                        this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                        colorproperties.add(nGoodsSpecProperty);
                                        color_spec_id = nGoodsSpecProperty.getId();
                                        spec_info += nGoodsSpecProperty.getValue() + " ";
                                        List<GoodsSpecProperty> goods_specs = goods
                                            .getGoods_specs();
                                        boolean flag = true;
                                        if (goods_specs != null && goods_specs.size() > 0) {
                                            for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                if (goodsSpecProperty2.getValue().equals(
                                                    nGoodsSpecProperty.getValue())) {
                                                    flag = false;
                                                    break;
                                                }
                                            }
                                        }
                                        if (flag) {
                                            goods.getGoods_specs().add(nGoodsSpecProperty);
                                        }

                                        //服装接口如果没有颜色值则创建颜色
                                        sendSpecProperty(now, colorGoodsSpecification,
                                            nGoodsSpecProperty);
                                    }
                                }

                                if (size_spec_id > 0 || color_spec_id > 0) {
                                    item.setSpec_info(spec_info);
                                    if (size_spec_id > 0 && color_spec_id == 0) {
                                        item.setSpec_combination(size_spec_id + "_");
                                    }
                                    if (color_spec_id > 0 && color_spec_id == 0) {
                                        item.setSpec_combination(color_spec_id + "_");
                                    }
                                    if (size_spec_id > 0 && color_spec_id > 0) {
                                        String combination = "";
                                        if (size_spec_id > color_spec_id) {
                                            combination = color_spec_id + "_" + size_spec_id + "_";
                                        }
                                        if (size_spec_id < color_spec_id) {
                                            combination = size_spec_id + "_" + color_spec_id + "_";
                                        }
                                        item.setSpec_combination(combination);
                                    }

                                    //保存货品
                                    this.goodsItemDao.save(item);

                                    goodsItems.add(item);
                                }
                            }

                            this.goodsDao.update(goods);

                            //服装接口创建商品

                            addGoodsJsonList(goodsJsonList, goods, goodsItems);

                        } else {//改用户没有此模板则创建

                            System.out.println("创建模板***********");
                            GoodsType importGoodsType = (GoodsType) this.goodsTypeDao.get(CommUtil
                                .null2Long(importGoods.getGoodsTypeId()));
                            if (importGoodsType != null) {
                                GoodsType goodsType = new GoodsType();
                                goodsType.setCreatetime(now);
                                goodsType.setDisabled(false);
                                goodsType.setName(impGoodsType.getName());
                                goodsType.setStore_id(store.getId());
                                List<GoodsSpecification> impgss = impGoodsType.getGss();
                                if (impgss != null && impgss.size() > 0) {
                                    for (GoodsSpecification goodsSpecification : impgss) {
                                        GoodsSpecification specification = new GoodsSpecification();
                                        specification.setCreatetime(now);
                                        specification.setDisabled(false);
                                        specification.setSequence(goodsSpecification.getSequence());
                                        specification.setStore_id(store.getId());
                                        specification.setName(goodsSpecification.getName());
                                        specification.setType(goodsSpecification.getType());
                                        this.goodsSpecificationDAO.save(specification);
                                        goodsType.getGss().add(specification);
                                    }

                                }
                                this.goodsTypeDao.save(goodsType);//保存模板
                                System.out.println("创建模板***********11111");

                                goodsTypeMap.put(goodsType.getName(), goodsType);

                                goods.setGoodsTypeId(CommUtil.null2Int(goodsType.getId()));

                                String specinfo = goodsCart.getSpec_info();
                                List<GoodsSpecification> gss = goodsType.getGss();
                                List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                                GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                                List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                                GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                                if (gss != null && gss.size() > 0) {
                                    for (GoodsSpecification goodsSpecification : gss) {
                                        if ("尺码".equals(goodsSpecification.getName().trim())) {
                                            sizeproperties = goodsSpecification.getProperties();
                                            sizeGoodsSpecification = goodsSpecification;
                                        }
                                        if ("颜色".equals(goodsSpecification.getName().trim())) {
                                            colorproperties = goodsSpecification.getProperties();
                                            colorGoodsSpecification = goodsSpecification;
                                        }

                                    }
                                }

                                String[] specinfos = specinfo.split(" ");
                                if (specinfos != null && specinfos.length > 0) {

                                    //判断规格是否存在，不存在则创建
                                    //尺码:42码,颜色:黑色
                                    GoodsItem item = new GoodsItem();
                                    item.setCreatetime(now);
                                    item.setDisabled(false);
                                    item.setGoods_inventory(goodsCart.getCount());

                                    //商品销售价按照前台设置的保存
                                    item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                                    //进货价即购物车里商品价格
                                    item.setPurchase_price(goodsCart.getPrice());
                                    item.setMarket_price(goodsCart.getPrice());

                                    item.setGoods(goods);
                                    item.setStep_price_state(0);
                                    item.setStatus(1);
                                    long size_spec_id = 0;
                                    long color_spec_id = 0;
                                    String spec_info = "";

                                    //判断尺码是否存在

                                    String spec_size = "";
                                    for (int i = 0; i < specinfos.length; i++) {
                                        if (specinfos[i].contains("尺码")) {
                                            spec_size = specinfos[i];
                                            break;
                                        }
                                    }
                                    String spec_0 = "0";
                                    if (spec_size != null && !"".equals(spec_size)
                                        && !"0".equals(spec_size)) {
                                        String[] split = spec_size.split(":");
                                        if (split != null && split.length == 2) {
                                            spec_0 = split[1];
                                        }
                                    }
                                    for (int i = 0; i < sizeproperties.size(); i++) {
                                        GoodsSpecProperty goodsSpecProperty = sizeproperties.get(i);
                                        //尺码存在
                                        if (spec_0.equals(goodsSpecProperty.getValue())) {
                                            size_spec_id = goodsSpecProperty.getId();
                                            spec_info += goodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        goodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(goodsSpecProperty);
                                            }

                                            break;
                                        }
                                    }
                                    if (!"0".equals(spec_0) && !"-1".equals(spec_0)
                                        && size_spec_id == 0) {
                                        //如果输入了尺码但是不存在，则创建
                                        //尺码不存在则创建
                                        GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                        nGoodsSpecProperty.setCreatetime(now);
                                        nGoodsSpecProperty.setDisabled(false);
                                        nGoodsSpecProperty.setSequence(0);
                                        nGoodsSpecProperty.setValue(spec_0);
                                        if (sizeGoodsSpecification != null
                                            && sizeGoodsSpecification.getId() != null) {
                                            nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                            this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                            sizeproperties.add(nGoodsSpecProperty);
                                            size_spec_id = nGoodsSpecProperty.getId();
                                            spec_info += nGoodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        nGoodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(nGoodsSpecProperty);
                                            }

                                            //服装接口如果没有尺码值则创建尺码
                                            sendSpecProperty(now, sizeGoodsSpecification,
                                                nGoodsSpecProperty);
                                        }

                                    }

                                    //判断颜色是否存在
                                    String spec_color = "";
                                    for (int i = 0; i < specinfos.length; i++) {
                                        if (specinfos[i].contains("颜色")) {
                                            spec_color = specinfos[i];
                                            break;
                                        }
                                    }
                                    String spec_1 = "0";
                                    if (spec_color != null && !"".equals(spec_color)
                                        && !"0".equals(spec_color)) {
                                        String[] split = spec_color.split(":");
                                        if (split != null && split.length == 2) {
                                            spec_1 = split[1];
                                        }
                                    }

                                    for (int i = 0; i < colorproperties.size(); i++) {
                                        GoodsSpecProperty goodsSpecProperty = colorproperties
                                            .get(i);
                                        //颜色存在
                                        if (spec_1.equals(goodsSpecProperty.getValue())) {
                                            color_spec_id = goodsSpecProperty.getId();
                                            spec_info += goodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        goodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(goodsSpecProperty);
                                            }

                                            break;
                                        }
                                    }
                                    if (!"-1".equals(spec_1) && !"0".equals(spec_1)
                                        && color_spec_id == 0) {
                                        //如果输入了颜色但是不存在，则创建
                                        //尺码不存在则创建
                                        GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                        nGoodsSpecProperty.setCreatetime(now);
                                        nGoodsSpecProperty.setDisabled(false);
                                        nGoodsSpecProperty.setSequence(0);
                                        nGoodsSpecProperty.setValue(spec_1);
                                        if (colorGoodsSpecification != null
                                            && colorGoodsSpecification.getId() != null) {
                                            nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                            this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                            colorproperties.add(nGoodsSpecProperty);
                                            color_spec_id = nGoodsSpecProperty.getId();
                                            spec_info += nGoodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        nGoodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(nGoodsSpecProperty);
                                            }

                                            //服装接口如果没有颜色值则创建颜色
                                            sendSpecProperty(now, colorGoodsSpecification,
                                                nGoodsSpecProperty);
                                        }
                                    }

                                    if (size_spec_id > 0 || color_spec_id > 0) {
                                        item.setSpec_info(spec_info);
                                        if (size_spec_id > 0 && color_spec_id == 0) {
                                            item.setSpec_combination(size_spec_id + "_");
                                        }
                                        if (color_spec_id > 0 && color_spec_id == 0) {
                                            item.setSpec_combination(color_spec_id + "_");
                                        }
                                        if (size_spec_id > 0 && color_spec_id > 0) {
                                            String combination = "";
                                            if (size_spec_id > color_spec_id) {
                                                combination = color_spec_id + "_" + size_spec_id
                                                              + "_";
                                            }
                                            if (size_spec_id < color_spec_id) {
                                                combination = size_spec_id + "_" + color_spec_id
                                                              + "_";
                                            }
                                            item.setSpec_combination(combination);
                                        }

                                        //保存货品
                                        this.goodsItemDao.save(item);

                                        goodsItems.add(item);
                                    }
                                }
                                this.goodsDao.update(goods);
                                //服装接口创建商品
                                addGoodsJsonList(goodsJsonList, goods, goodsItems);

                            }
                        }
                    } else {//如果没有规格
                        GoodsItem item = new GoodsItem();
                        item.setCreatetime(now);
                        item.setDisabled(false);
                        item.setGoods_inventory(goodsCart.getCount());

                        //商品销售价按照前台设置的保存
                        item.setGoods_price(CommUtil.null2BigDecimal(sale_pricesArr[k]));
                        //进货价即购物车里商品价格
                        item.setPurchase_price(goodsCart.getPrice());
                        item.setMarket_price(goodsCart.getPrice());

                        item.setGoods(goods);
                        item.setStatus(0);
                        item.setStep_price_state(0);
                        this.goodsItemDao.save(item);
                        goods.setGoods_inventory(item.getGoods_inventory());
                        goods.setTotal_weight(importGoods.getTotal_weight());
                        goods.setType_ratio(importGoods.getType_ratio());
                        goods.setColor_ratio(importGoods.getColor_ratio());
                        goods.setSize_ratio(importGoods.getSize_ratio());
                        goods.setSingle_weight(importGoods.getSingle_weight());

                        this.goodsDao.update(goods);
                        goodsItems.add(item);

                        //服装接口创建商品
                        addGoodsJsonList(goodsJsonList, goods, goodsItems);
                    }
                } else {
                    Goods goods = goodslist.get(0);
                    if (goods.getGoods_type() == 0) {
                        //单件商品
                        //则看规格模板是否对应
                        GoodsType goodsType = (GoodsType) this.goodsTypeDao.get(CommUtil
                            .null2Long(goods.getGoodsTypeId()));
                        if (goodsType != null) {
                            if (goodsTypeMap.containsKey(goodsType.getName())) {
                                //判断规格是否存在如果都存在则忽略，否则新增
                                String specinfo = goodsCart.getSpec_info();
                                List<GoodsSpecification> gss = goodsType.getGss();
                                List<GoodsSpecProperty> sizeproperties = new ArrayList<GoodsSpecProperty>();
                                GoodsSpecification sizeGoodsSpecification = new GoodsSpecification();
                                List<GoodsSpecProperty> colorproperties = new ArrayList<GoodsSpecProperty>();
                                GoodsSpecification colorGoodsSpecification = new GoodsSpecification();
                                if (gss != null && gss.size() > 0) {
                                    for (GoodsSpecification goodsSpecification : gss) {
                                        if ("尺码".equals(goodsSpecification.getName().trim())) {
                                            sizeproperties = goodsSpecification.getProperties();
                                            sizeGoodsSpecification = goodsSpecification;
                                        }
                                        if ("颜色".equals(goodsSpecification.getName().trim())) {
                                            colorproperties = goodsSpecification.getProperties();
                                            colorGoodsSpecification = goodsSpecification;
                                        }

                                    }
                                }
                                String[] specinfos = specinfo.split(" ");
                                if (specinfos != null && specinfos.length > 0) {

                                    //判断规格是否存在，不存在则创建
                                    //尺码:42码,颜色:黑色

                                    long size_spec_id = 0;
                                    long color_spec_id = 0;
                                    String spec_info = "";

                                    //判断尺码是否存在

                                    String spec_size = "";
                                    for (int i = 0; i < specinfos.length; i++) {
                                        if (specinfos[i].contains("尺码")) {
                                            spec_size = specinfos[i];
                                            break;
                                        }
                                    }
                                    String spec_0 = "0";
                                    if (spec_size != null && !"".equals(spec_size)
                                        && !"0".equals(spec_size)) {
                                        String[] split = spec_size.split(":");
                                        if (split != null && split.length == 2) {
                                            spec_0 = split[1];
                                        }
                                    }
                                    for (int i = 0; i < sizeproperties.size(); i++) {
                                        GoodsSpecProperty goodsSpecProperty = sizeproperties.get(i);
                                        //尺码存在
                                        if (spec_0.equals(goodsSpecProperty.getValue())) {
                                            size_spec_id = goodsSpecProperty.getId();
                                            spec_info += goodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        goodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(goodsSpecProperty);
                                            }

                                            break;
                                        }
                                    }
                                    if (!"0".equals(spec_0) && !"-1".equals(spec_0)
                                        && size_spec_id == 0) {
                                        //如果输入了尺码但是不存在，则创建
                                        //尺码不存在则创建
                                        GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                        nGoodsSpecProperty.setCreatetime(now);
                                        nGoodsSpecProperty.setDisabled(false);
                                        nGoodsSpecProperty.setSequence(0);
                                        nGoodsSpecProperty.setValue(spec_0);
                                        if (sizeGoodsSpecification != null
                                            && sizeGoodsSpecification.getId() != null) {
                                            nGoodsSpecProperty.setSpec(sizeGoodsSpecification);
                                            this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                            sizeproperties.add(nGoodsSpecProperty);
                                            size_spec_id = nGoodsSpecProperty.getId();
                                            spec_info += nGoodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        nGoodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(nGoodsSpecProperty);
                                            }

                                            //服装接口如果没有尺码值则创建尺码
                                            sendSpecProperty(now, sizeGoodsSpecification,
                                                nGoodsSpecProperty);
                                        }

                                    }

                                    //判断颜色是否存在
                                    String spec_color = "";
                                    for (int i = 0; i < specinfos.length; i++) {
                                        if (specinfos[i].contains("颜色")) {
                                            spec_color = specinfos[i];
                                            break;
                                        }
                                    }
                                    String spec_1 = "0";
                                    if (spec_color != null && !"".equals(spec_color)
                                        && !"0".equals(spec_color)) {
                                        String[] split = spec_color.split(":");
                                        if (split != null && split.length == 2) {
                                            spec_1 = split[1];
                                        }
                                    }

                                    for (int i = 0; i < colorproperties.size(); i++) {
                                        GoodsSpecProperty goodsSpecProperty = colorproperties
                                            .get(i);
                                        //颜色存在
                                        if (spec_1.equals(goodsSpecProperty.getValue())) {
                                            color_spec_id = goodsSpecProperty.getId();
                                            spec_info += goodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        goodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(goodsSpecProperty);
                                            }

                                            break;
                                        }
                                    }
                                    if (!"-1".equals(spec_1) && !"0".equals(spec_1)
                                        && color_spec_id == 0) {
                                        //如果输入了颜色但是不存在，则创建
                                        //尺码不存在则创建
                                        GoodsSpecProperty nGoodsSpecProperty = new GoodsSpecProperty();
                                        nGoodsSpecProperty.setCreatetime(now);
                                        nGoodsSpecProperty.setDisabled(false);
                                        nGoodsSpecProperty.setSequence(0);
                                        nGoodsSpecProperty.setValue(spec_1);
                                        if (colorGoodsSpecification != null
                                            && colorGoodsSpecification.getId() != null) {
                                            nGoodsSpecProperty.setSpec(colorGoodsSpecification);
                                            this.goodsSpecPropertyDao.save(nGoodsSpecProperty);
                                            colorproperties.add(nGoodsSpecProperty);
                                            color_spec_id = nGoodsSpecProperty.getId();
                                            spec_info += nGoodsSpecProperty.getValue() + " ";
                                            List<GoodsSpecProperty> goods_specs = goods
                                                .getGoods_specs();
                                            boolean flag = true;
                                            if (goods_specs != null && goods_specs.size() > 0) {
                                                for (GoodsSpecProperty goodsSpecProperty2 : goods_specs) {
                                                    if (goodsSpecProperty2.getValue().equals(
                                                        nGoodsSpecProperty.getValue())) {
                                                        flag = false;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                goods.getGoods_specs().add(nGoodsSpecProperty);
                                            }

                                            //服装接口如果没有颜色值则创建颜色
                                            sendSpecProperty(now, colorGoodsSpecification,
                                                nGoodsSpecProperty);
                                        }
                                    }

                                    if (size_spec_id > 0 || color_spec_id > 0) {
                                        GoodsItem item = new GoodsItem();
                                        item.setCreatetime(now);
                                        item.setDisabled(false);
                                        item.setGoods_inventory(goodsCart.getCount());
                                        //商品销售价按照前台设置的保存
                                        item.setGoods_price(CommUtil
                                            .null2BigDecimal(sale_pricesArr[k]));
                                        //进货价即购物车里商品价格
                                        item.setPurchase_price(goodsCart.getPrice());
                                        item.setMarket_price(goodsCart.getPrice());

                                        item.setGoods(goods);
                                        item.setStep_price_state(0);
                                        item.setStatus(1);
                                        item.setSpec_info(spec_info);
                                        if (size_spec_id > 0 && color_spec_id == 0) {
                                            item.setSpec_combination(size_spec_id + "_");
                                        }
                                        if (color_spec_id > 0 && color_spec_id == 0) {
                                            item.setSpec_combination(color_spec_id + "_");
                                        }
                                        if (size_spec_id > 0 && color_spec_id > 0) {
                                            String combination = "";
                                            if (size_spec_id > color_spec_id) {
                                                combination = color_spec_id + "_" + size_spec_id
                                                              + "_";
                                            }
                                            if (size_spec_id < color_spec_id) {
                                                combination = size_spec_id + "_" + color_spec_id
                                                              + "_";
                                            }
                                            item.setSpec_combination(combination);
                                        }

                                        //保存货品
                                        boolean myflag = true;
                                        List<GoodsItem> goods_item_list = goods
                                            .getGoods_item_list();
                                        if (goods_item_list != null && goods_item_list.size() > 0) {
                                            for (GoodsItem goodsItem : goods_item_list) {
                                                String goods_spec_combination = goodsItem
                                                    .getSpec_combination();
                                                String spec_combination = item
                                                    .getSpec_combination();
                                                if (goods_spec_combination != null
                                                    && spec_combination != null
                                                    && goods_spec_combination
                                                        .equals(spec_combination)) {
                                                    //如果有这个货品则不新增了
                                                    myflag = false;
                                                }
                                            }
                                        }
                                        if (myflag) {
                                            this.goodsItemDao.save(item);
                                            this.goodsDao.update(goods);
                                            System.out.println("覆盖导入。。。。。");
                                        }

                                    }
                                }

                            }
                        }

                    }
                }

            }

            //服装接口创建商品
            if (goodsJsonList != null && goodsJsonList.size() > 0) {
                String write2JsonStr = JsonUtil.write2JsonStr(goodsJsonList);
                sendReqAsync.sendMessageUtil(Constant.GOODS_URL_BATCHADD, write2JsonStr, "批量导入商品");

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
