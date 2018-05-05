package com.javamalls.ctrl.seller.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.security.support.SecurityUserHolder;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.GoodsLabel;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.domain.query.GoodsLabelQueryObject;
import com.javamalls.platform.domain.query.UserGoodsClassQueryObject;
import com.javamalls.platform.service.IGoodsLabelService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.service.IUserService;
import com.javamalls.platform.vo.RemoveIdJsonVo;
import com.javamalls.platform.vo.UserGoodsClassJsonVo;
import com.utils.SendReqAsync;
/**
 * 商品标签Action
 * @author zmw
 *
 */
@Controller
public class GoodsLabelSellerAction {
    @Autowired
    private ISysConfigService      configService;
    @Autowired
    private IUserConfigService     userConfigService;
    @Autowired
    private IUserGoodsClassService usergoodsclassService;
    @Autowired
    private IGoodsLabelService goodsLabelService;
    @Autowired
    private IUserService userService;
    @Autowired
    private SendReqAsync           sendReqAsync;

    @SecurityMapping(title = "卖家商品标签列表", value = "/seller/usergoodslabel_list.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_list.htm" })
    public ModelAndView usergoodsclass_list(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodslabel_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        if (SecurityUserHolder.getCurrentUser() == null) {
             mv = new JModelAndView("buyer/buyer_login.html",
                this.configService.getSysConfig(), this.userConfigService.getUserConfig(),
                JModelAndView.SHOP_PATH, request, response);
            return mv;
        }        
        String params = "";
        GoodsLabelQueryObject qo = new GoodsLabelQueryObject(currentPage, mv, orderBy,
            orderType);
        qo.setPageSize(Integer.valueOf(20));
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, GoodsLabel.class, mv);
        qo.addQuery("obj.disabled", new SysMap("disabled", false), "=");
        qo.addQuery("obj.createuser.id", new SysMap("user_id", SecurityUserHolder.getCurrentUser().getId()), "=");
        qo.setOrderBy("sequence");
        qo.setOrderType("asc");
        IPageList pList = this.goodsLabelService.list(qo);
        CommUtil.saveIPageList2ModelAndView(url + "/seller/usergoodslabel_list.htm", "", params,
            pList, mv);
        return mv;
    }

    @SecurityMapping(title = "卖家商品标签保存", value = "/seller/usergoodslabel_save.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_save.htm" })
    public String usergoodslabel_save(HttpServletRequest request, HttpServletResponse response,
    		 String name,String sequence,String status) {
    	GoodsLabel goodsLabel=new GoodsLabel();
    	goodsLabel.setCreatetime(new Date());
    	User user=SecurityUserHolder.getCurrentUser();
    	goodsLabel.setCreateuser(user);
    	goodsLabel.setName(name);
    	goodsLabel.setSequence(Integer.valueOf(sequence));
    	goodsLabel.setStatus(Integer.valueOf(status)); 
    	if (user!=null && !"".equals(user)) {
    		if (user.getSalesManState()!=null && user.getSalesManState()==1) {
				goodsLabel.setStore(user.getParent().getStore());
			}else{
				goodsLabel.setStore(user.getStore());
			}
		}
        goodsLabelService.save(goodsLabel);
        return "redirect:usergoodslabel_list.htm";
    }
    @SecurityMapping(title = "卖家商品标签编辑保存", value = "/seller/usergoodslabel_edit_save.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_edit_save.htm" })
    public String usergoodslabel_edit_save(HttpServletRequest request, HttpServletResponse response,
    		 String name,String sequence,String status,String id) {
    	GoodsLabel goodsLabel=goodsLabelService.getObjById(CommUtil.null2Long(id));
    	User user=SecurityUserHolder.getCurrentUser();
    	
    	if (goodsLabel!=null) {
    		goodsLabel.setUpdatetime(new Date());
        	
        	goodsLabel.setName(name);
        	goodsLabel.setSequence(Integer.valueOf(sequence));
        	goodsLabel.setStatus(Integer.valueOf(status)); 
        	if (user!=null && !"".equals(user)) {
        		if (user.getSalesManState()!=null && user.getSalesManState()==1) {
    				goodsLabel.setStore(user.getParent().getStore());
    			}else{
    				goodsLabel.setStore(user.getStore());
    			}
			}
        	goodsLabel.setUpdateuser(user);
            goodsLabelService.update(goodsLabel);
		}
        return "redirect:usergoodslabel_list.htm";
    }

    @SecurityMapping(title = "卖家商品标签删除", value = "/seller/usergoodslabel_del.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_del.htm" })
    public String usergoodslabel_del(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                GoodsLabel goodsLabel = this.goodsLabelService.getObjById(Long
                    .valueOf(Long.parseLong(id)));
                List<RemoveIdJsonVo> dellist = new ArrayList<RemoveIdJsonVo>();

                goodsLabel.setDisabled(true);
                this.goodsLabelService.update(goodsLabel);
                RemoveIdJsonVo vo = new RemoveIdJsonVo();
                vo.setId(goodsLabel.getId());
                dellist.add(vo);
                if (dellist != null && dellist.size() > 0) {
                    String write2JsonStr = JsonUtil.write2JsonStr(dellist);
                    sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_DEL, write2JsonStr,
                        "删除商品标签");

                }
            }
        }
        return "redirect:usergoodslabel_list.htm";
    }

    @SecurityMapping(title = "新增卖家商品标签", value = "/seller/usergoodslabel_add.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_add.htm" })
    public ModelAndView usergoodslabel_add(HttpServletRequest request,
                                           HttpServletResponse response, String currentPage
                                           ) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodslabel_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "编辑卖家商品标签", value = "/seller/usergoodslabel_edit.htm*", rtype = "seller", rname = "商品分类", rcode = "usergoodsclass_seller", rgroup = "商品管理")
    @RequestMapping({ "/seller/usergoodslabel_edit.htm" })
    public ModelAndView usergoodslabel_edit(HttpServletRequest request,
                                            HttpServletResponse response, String currentPage,
                                            String id) {
        ModelAndView mv = new JModelAndView("user/default/usercenter/usergoodslabel_edit.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
       
        GoodsLabel obj = this.goodsLabelService.getObjById(CommUtil.null2Long(id));
        mv.addObject("obj", obj);
        mv.addObject("currentPage", currentPage);
        return mv;
    }
}