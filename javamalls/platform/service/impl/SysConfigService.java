package com.javamalls.platform.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.SysConfig;
import com.javamalls.platform.service.ISysConfigService;

/**系统配置service
 *                       
 * @Filename: SysConfigService.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Service
@Transactional
public class SysConfigService implements ISysConfigService {
    @Resource(name = "sysConfigDAO")
    private IGenericDAO<SysConfig> sysConfigDAO;

    public boolean delete(SysConfig shopConfig) {
        return false;
    }

    /**获取配置信息
     * @return
     * @see com.javamalls.platform.service.ISysConfigService#getSysConfig()
     */
    public SysConfig getSysConfig() {
        List<SysConfig> configs = this.sysConfigDAO.query("select obj from SysConfig obj", null,
            -1, -1);
        if ((configs != null) && (configs.size() > 0)) {
            SysConfig sc = (SysConfig) configs.get(0);
            if (sc.getUploadFilePath() == null) {
                sc.setUploadFilePath("upload");
            }
            if (sc.getSysLanguage() == null) {
                sc.setSysLanguage("zh_cn");
            }
            if ((sc.getWebsiteName() == null) || (sc.getWebsiteName().equals(""))) {
                sc.setWebsiteName("javamalls");
            }
            if ((sc.getCloseReason() == null) || (sc.getCloseReason().equals(""))) {
                sc.setCloseReason("系统维护中...");
            }
            if ((sc.getTitle() == null) || (sc.getTitle().equals(""))) {
                sc.setTitle("多用户商城");
            }
            if ((sc.getImageSaveType() == null) || (sc.getImageSaveType().equals(""))) {
                sc.setImageSaveType("sidImg");
            }
            if (sc.getImageFilesize() == 0) {
                sc.setImageFilesize(1024);
            }
            if (sc.getSmallWidth() == 0) {
                sc.setSmallWidth(160);
            }
            if (sc.getSmallHeight() == 0) {
                sc.setSmallHeight(160);
            }
            if (sc.getMiddleWidth() == 0) {
                sc.setMiddleWidth(300);
            }
            if (sc.getMiddleHeight() == 0) {
                sc.setMiddleHeight(300);
            }
            if (sc.getBigHeight() == 0) {
                sc.setBigHeight(1024);
            }
            if (sc.getBigWidth() == 0) {
                sc.setBigWidth(1024);
            }
            if ((sc.getImageSuffix() == null) || (sc.getImageSuffix().equals(""))) {
                sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
            }
            if (sc.getStoreImage() == null) {
                Accessory storeImage = new Accessory();
                storeImage.setPath("resources/style/common/images");
                storeImage.setName("store.jpg");
                sc.setStoreImage(storeImage);
            }
            if (sc.getGoodsImage() == null) {
                Accessory goodsImage = new Accessory();
                goodsImage.setPath("resources/style/common/images");
                goodsImage.setName("good.jpg");
                sc.setGoodsImage(goodsImage);
            }
            if (sc.getMemberIcon() == null) {
                Accessory memberIcon = new Accessory();
                memberIcon.setPath("resources/style/common/images");
                memberIcon.setName("member.jpg");
                sc.setMemberIcon(memberIcon);
            }
            if ((sc.getSecurityCodeType() == null) || (sc.getSecurityCodeType().equals(""))) {
                sc.setSecurityCodeType("normal");
            }
            if ((sc.getWebsiteCss() == null) || (sc.getWebsiteCss().equals(""))) {
                sc.setWebsiteCss("default");
            }
            return sc;
        }
        SysConfig sc = new SysConfig();
        sc.setUploadFilePath("upload");
        sc.setWebsiteName("javamalls");
        sc.setSysLanguage("zh_cn");
        sc.setTitle("多用户商城");
        sc.setSecurityCodeType("normal");
        sc.setEmailEnable(true);
        sc.setCloseReason("系统维护中...");
        sc.setImageSaveType("sidImg");
        sc.setImageFilesize(1024);
        sc.setSmallWidth(160);
        sc.setSmallHeight(160);
        sc.setMiddleHeight(300);
        sc.setMiddleWidth(300);
        sc.setBigHeight(1024);
        sc.setBigWidth(1024);
        sc.setImageSuffix("gif|jpg|jpeg|bmp|png|tbi");
        sc.setComplaint_time(30);
        sc.setWebsiteCss("default");
        Accessory goodsImage = new Accessory();
        goodsImage.setPath("resources/style/common/images");
        goodsImage.setName("good.jpg");
        sc.setGoodsImage(goodsImage);
        Accessory storeImage = new Accessory();
        storeImage.setPath("resources/style/common/images");
        storeImage.setName("store.jpg");
        sc.setStoreImage(storeImage);
        Accessory memberIcon = new Accessory();
        memberIcon.setPath("resources/style/common/images");
        memberIcon.setName("member.jpg");
        sc.setMemberIcon(memberIcon);
        return sc;
    }

    /**保存系统配置
     * @param shopConfig
     * @return
     * @see com.javamalls.platform.service.ISysConfigService#save(com.javamalls.platform.domain.SysConfig)
     */
    public boolean save(SysConfig shopConfig) {
        try {
            this.sysConfigDAO.save(shopConfig);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**更新系统配置
     * @param shopConfig
     * @return
     * @see com.javamalls.platform.service.ISysConfigService#update(com.javamalls.platform.domain.SysConfig)
     */
    public boolean update(SysConfig shopConfig) {
        try {
            this.sysConfigDAO.update(shopConfig);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
