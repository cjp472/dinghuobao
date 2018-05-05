package com.javamalls.ctrl.admin.h5;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.domain.Goods;
import com.javamalls.platform.domain.MobileGoods;
import com.javamalls.platform.domain.query.MobileGoodsQueryObject;
import com.javamalls.platform.service.IGoodsService;
import com.javamalls.platform.service.IMobileGoodsService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**推荐至H5首页
 *                       
 * @Filename: H5LoadAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class MobileManageAction {
    @Autowired
    private ISysConfigService   configService;
    @Autowired
    private IUserConfigService  userConfigService;
    @Autowired
    private IGoodsService       goodsService;
    @Autowired
    private IMobileGoodsService mobileGoodsService;

    @RequestMapping({ "/admin/mobile/recommend_goods.htm" })
    public void recommend_goods(HttpServletRequest request, HttpServletResponse response, String id) {
        String msg = "推荐失败!";
        if (!"".equals(CommUtil.null2String(id))) {
            Goods goods = goodsService.getObjById(CommUtil.null2Long(id));
            if (goods != null && goods.getGoods_status() == 0
                && goods.getGoods_main_photo() != null) {
                MobileGoods mobileGood = mobileGoodsService.getObjByProperty("goods.id",
                    CommUtil.null2Long(id));
                if ("".equals(CommUtil.null2String(mobileGood))) {

                    MobileGoods mg = new MobileGoods();
                    mg.setGoods(goods);
                    mg.setCreatetime(new Date());
                    mg.setDisabled(false);
                    mobileGoodsService.save(mg);
                    msg = "推荐成功!";
                } else {
                    msg = "已经推荐过了!";
                }
            } else {
                msg = "没找到此商品或此商品未上架!\r\n已上架但没有主图的商品也不可以推荐!";
            }
        }
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/admin/mobile/remove_goods.htm" })
    public String remove_goods(HttpServletRequest request, HttpServletResponse response,
                               String mulitId) {
        String msg = "del_error";
        if (!"".equals(CommUtil.null2String(mulitId))) {

            boolean flag = mobileGoodsService.remove(mulitId);
            if (flag)
                msg = "del_success";
        } else {
            msg = "del_id_null";
        }
        return "redirect:/admin/mobile/goods_list.htm?cannotdelflag=" + msg;
    }

    @RequestMapping({ "/admin/mobile/sort_goods.htm" })
    public String sort_goods(HttpServletRequest request, HttpServletResponse response, String id,
                             String sort) {
        if (!"".equals(CommUtil.null2String(id))) {
            MobileGoods mg = mobileGoodsService.getObjById(CommUtil.null2Long(id));
            if (mg != null) {

                mg.setSort(CommUtil.null2Int(sort));
                mobileGoodsService.update(mg);
            }
        }
        return "redirect:/admin/mobile/goods_list.htm";
    }

    @RequestMapping({ "/admin/mobile/goods_list.htm" })
    public ModelAndView goods_list(HttpServletRequest request, HttpServletResponse response,
                                   String currentPage, String orderBy, String orderType,
                                   String cannotdelflag) {

        ModelAndView mv = new JModelAndView("admin/blue/h5/mobile_goods_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        orderBy = "sort";
        orderType = "asc";
        MobileGoodsQueryObject qo = new MobileGoodsQueryObject(currentPage, mv, orderBy, orderType);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");

        IPageList<MobileGoods> pList = this.mobileGoodsService.list(qo);
        CommUtil
            .saveIPageList2ModelAndView(url + "/admin/mobile/goods_list.htm", "", "", pList, mv);

        // 删除失败标识为1时javascript提示
        if (!"".equals(CommUtil.null2String(cannotdelflag))) {
            String msg = "";
            if ("del_success".equals(cannotdelflag))
                msg = "删除成功!";
            else if ("del_error".equals(cannotdelflag))
                msg = "删除失败!";
            else if ("del_id_null".equals(cannotdelflag))
                msg = "id不可以为空!";
            if (!"".equals(CommUtil.null2String(msg))) {
                mv.addObject("js", "alert('" + msg + "');");
            }
        }

        return mv;
    }

}
