package com.javamalls.front.web.h5.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.mv.JModelAndView;
import com.javamalls.platform.domain.Document;
import com.javamalls.platform.service.IDocumentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class H5DocumentViewAction {

    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IDocumentService   documentService;

    @RequestMapping({ "/mobile/doc.htm" })
    public ModelAndView doc(HttpServletRequest request, HttpServletResponse response, String mark) {
        ModelAndView mv = new JModelAndView("h5/docView.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Document obj = this.documentService.getObjByProperty("mark", mark);
        mv.addObject("obj", obj);
        return mv;
    }
}
