package com.javamalls.ctrl.seller.action;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.Store;
import com.javamalls.platform.domain.WaterMark;
import com.javamalls.platform.service.IAccessoryService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.service.IWaterMarkService;

@Controller
public class WaterMarkSellerAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IWaterMarkService  watermarkService;
    @Autowired
    private IAccessoryService  accessoryService;
    @Autowired
    private IUserService       userService;

    @SecurityMapping(title = "图片水印", value = "/seller/watermark.htm*", rtype = "seller", rname = "图片管理", rcode = "album_seller", rgroup = "其他设置")
    @RequestMapping({ "/seller/watermark.htm" })
    public ModelAndView watermark(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/watermark.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        Store store = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId())
            .getStore();
        if (store != null) {
            Map params = new HashMap();
            params.put("store_id", store.getId());
            List<WaterMark> wms = this.watermarkService.query(
                "select obj from WaterMark obj where obj.store.id=:store_id", params, -1, -1);
            if (wms.size() > 0) {
                mv.addObject("obj", wms.get(0));
            }
        }
        return mv;
    }

    @SecurityMapping(title = "图片水印保存", value = "/seller/watermark_save.htm*", rtype = "seller", rname = "图片管理", rcode = "album_seller", rgroup = "其他设置")
    @RequestMapping({ "/seller/watermark_save.htm" })
    public ModelAndView watermark_save(HttpServletRequest request, HttpServletResponse response,
                                       String id, String currentPage, String cmd) {
        ModelAndView mv = null;
        if (SecurityUserHolder.getCurrentUser().getStore() != null) {
            WebForm wf = new WebForm();
            WaterMark watermark = null;
            if (id.equals("")) {
                watermark = (WaterMark) wf.toPo(request, WaterMark.class);
                watermark.setCreatetime(new Date());
            } else {
                WaterMark obj = this.watermarkService.getObjById(Long.valueOf(Long.parseLong(id)));
                watermark = (WaterMark) wf.toPo(request, obj);
            }
            watermark.setStore(SecurityUserHolder.getCurrentUser().getStore());
            String path = request.getSession().getServletContext().getRealPath("/") + "upload/wm";
            try {
                Map map = CommUtil.saveFileToServer(request, "wm_img", path, null, null);
                if (!map.get("fileName").equals("")) {
                    Accessory wm_image = new Accessory();
                    wm_image.setCreatetime(new Date());
                    wm_image.setHeight(CommUtil.null2Int(map.get("height")));
                    wm_image.setName(CommUtil.null2String(map.get("fileName")));
                    wm_image.setPath("upload/wm");
                    wm_image.setSize(CommUtil.null2Float(map.get("fileSize")));
                    wm_image.setUser(SecurityUserHolder.getCurrentUser());
                    wm_image.setWidth(CommUtil.null2Int("width"));
                    this.accessoryService.save(wm_image);
                    watermark.setWm_image(wm_image);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (id.equals("")) {
                this.watermarkService.save(watermark);
            } else {
                this.watermarkService.update(watermark);
            }
            mv = new JModelAndView("success.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "水印设置成功");
        } else {
            mv = new JModelAndView("error.html", this.configService.getSysConfig(),
                this.userConfigService.getUserConfig(), 1, request, response);
            mv.addObject("op_title", "您尚未开店");
        }
        mv.addObject("url", CommUtil.getURL(request) + "/seller/watermark.htm");
        return mv;
    }
}
