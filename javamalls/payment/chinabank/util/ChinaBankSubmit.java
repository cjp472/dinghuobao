package com.javamalls.payment.chinabank.util;

import java.util.List;

import com.javamalls.base.domain.virtual.SysMap;
import com.javamalls.base.tools.CommUtil;

public class ChinaBankSubmit {
    public static String buildForm(List<SysMap> list, String server_url) {
        if ("".equals(CommUtil.null2String(server_url)))
            server_url = "https://pay3.chinabank.com.cn/PayGate";
        StringBuffer sb = new StringBuffer();
        sb.append("<body onLoad=\"javascript:document.E_FORM.submit()\">");
        sb.append("<form action=\"" + server_url + "\" method=\"POST\" name=\"E_FORM\">");
        for (SysMap sm : list) {
            sb.append("<input type=\"hidden\" name=\"" + CommUtil.null2String(sm.getKey())
                      + "\"    value=\"" + CommUtil.null2String(sm.getValue()) + "\" size=\"100\">");
        }
        sb.append("</form><body>");
        return sb.toString();
    }
}
