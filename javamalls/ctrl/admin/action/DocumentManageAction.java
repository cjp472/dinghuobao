package com.javamalls.ctrl.admin.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.easyjf.beans.BeanUtils;
import com.easyjf.beans.BeanWrapper;
import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.base.tools.WebForm;
import com.javamalls.platform.domain.Document;
import com.javamalls.platform.domain.query.DocumentQueryObject;
import com.javamalls.platform.service.IDocumentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**系统文章管理
 *                       
 * @Filename: DocumentManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class DocumentManageAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IDocumentService   documentService;

    @SecurityMapping(title = "系统文章列表", value = "/admin/document_list.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_list.htm" })
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response,
                             String currentPage, String orderBy, String orderType) {
        ModelAndView mv = new JModelAndView("admin/blue/document_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        String url = this.configService.getSysConfig().getAddress();
        if ((url == null) || (url.equals(""))) {
            url = CommUtil.getURL(request);
        }
        String params = "";
        DocumentQueryObject qo = new DocumentQueryObject(currentPage, mv, orderBy, orderType);
        WebForm wf = new WebForm();
        wf.toQueryPo(request, qo, Document.class, mv);
        IPageList pList = this.documentService.list(qo);
        CommUtil
            .saveIPageList2ModelAndView(url + "/admin/document_list.htm", "", params, pList, mv);
        return mv;
    }

    @SecurityMapping(title = "系统文章新增", value = "/admin/document_add.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_add.htm" })
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response,
                            String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/document_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("currentPage", currentPage);
        return mv;
    }

    @SecurityMapping(title = "系统文章编辑", value = "/admin/document_edit.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_edit.htm" })
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage) {
        ModelAndView mv = new JModelAndView("admin/blue/document_add.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        if ((id != null) && (!id.equals(""))) {
            Document document = this.documentService.getObjById(Long.valueOf(Long.parseLong(id)));
            mv.addObject("obj", document);
            mv.addObject("currentPage", currentPage);
            mv.addObject("edit", Boolean.valueOf(true));
        }
        return mv;
    }

    @SecurityMapping(title = "系统文章保存", value = "/admin/document_save.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_save.htm" })
    public ModelAndView save(HttpServletRequest request, HttpServletResponse response, String id,
                             String currentPage, String cmd, String list_url, String add_url) {
        WebForm wf = new WebForm();
        Document document = null;
        if (id.equals("")) {
            document = (Document) wf.toPo(request, Document.class);
            document.setCreatetime(new Date());
        } else {
            Document obj = this.documentService.getObjById(Long.valueOf(Long.parseLong(id)));
            document = (Document) wf.toPo(request, obj);
        }
        if (id.equals("")) {
            this.documentService.save(document);
        } else {
            this.documentService.update(document);
        }
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        mv.addObject("list_url", list_url);
        mv.addObject("op_title", "保存系统文章成功");
        if (add_url != null) {
            mv.addObject("add_url", add_url + "?currentPage=" + currentPage);
        }
        return mv;
    }

    @SecurityMapping(title = "系统文章删除", value = "/admin/document_del.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_del.htm" })
    public String delete(HttpServletRequest request, String mulitId) {
        String[] ids = mulitId.split(",");
        for (String id : ids) {
            if (!id.equals("")) {
                Document document = this.documentService
                    .getObjById(Long.valueOf(Long.parseLong(id)));
                if (document != null)
                    this.documentService.delete(Long.valueOf(Long.parseLong(id)));
            }
        }
        return "redirect:document_list.htm";
    }

    @SecurityMapping(title = "系统文章AJAX更新", value = "/admin/document_ajax.htm*", rtype = "admin", rname = "系统文章", rcode = "document_manage", rgroup = "网站")
    @RequestMapping({ "/admin/document_ajax.htm" })
    public void ajax(HttpServletRequest request, HttpServletResponse response, String id,
                     String fieldName, String value) throws ClassNotFoundException {
        Document obj = this.documentService.getObjById(Long.valueOf(Long.parseLong(id)));
        Field[] fields = Document.class.getDeclaredFields();
        BeanWrapper wrapper = new BeanWrapper(obj);
        Object val = null;
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                Class clz = Class.forName("java.lang.String");
                if (field.getType().getName().equals("int")) {
                    clz = Class.forName("java.lang.Integer");
                }
                if (field.getType().getName().equals("boolean")) {
                    clz = Class.forName("java.lang.Boolean");
                }
                if (!value.equals("")) {
                    val = BeanUtils.convertType(value, clz);
                } else {
                    val = Boolean.valueOf(!CommUtil.null2Boolean(wrapper
                        .getPropertyValue(fieldName)));
                }
                wrapper.setPropertyValue(fieldName, val);
            }
        }
        this.documentService.update(obj);
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(val.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
