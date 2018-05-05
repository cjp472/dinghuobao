package com.javamalls.front.web.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.mv.JModelAndView;
import com.javamalls.platform.domain.Document;
import com.javamalls.platform.service.IDocumentService;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

@Controller
public class DocumentViewAction {

    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;
    @Autowired
    private IDocumentService   documentService;

    @RequestMapping({ "/doc.htm" })
    public ModelAndView doc(HttpServletRequest request, HttpServletResponse response, String mark) {
        ModelAndView mv = new JModelAndView("doc.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Document obj = this.documentService.getObjByProperty("mark", mark);
        mv.addObject("obj", obj);
        return mv;
    }
    
    @RequestMapping({ "/store/{storeId}/doc_{mark}.htm" })
    public ModelAndView buyer_doc(HttpServletRequest request, HttpServletResponse response,@PathVariable String mark) {
        ModelAndView mv = new JModelAndView("buyer/doc.html", this.configService.getSysConfig(),
            this.userConfigService.getUserConfig(), 1, request, response);
        Document obj = this.documentService.getObjByProperty("mark", mark);
        mv.addObject("obj", obj);
        return mv;
    }
}
