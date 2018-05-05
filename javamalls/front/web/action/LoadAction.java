package com.javamalls.front.web.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.javamalls.platform.domain.Area;
import com.javamalls.platform.service.IAreaService;

/**根据父id加载地区   公用
 *                       
 * @Filename: LoadAction.java
 * @Version: 1.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class LoadAction {
    @Autowired
    private IAreaService areaService;

    @RequestMapping({ "/load_area.htm" })
    public void load_area(HttpServletRequest request, HttpServletResponse response, String pid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:pid", params, -1, -1);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Area area : areas) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", area.getId());
            map.put("areaName", area.getAreaName());
            list.add(map);
        }
        String temp = Json.toJson(list, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping({ "/store/{storeId}.htm/load_area.htm" })
    public void buyer_load_area(HttpServletRequest request, HttpServletResponse response, String pid) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pid", Long.valueOf(Long.parseLong(pid)));
        List<Area> areas = this.areaService.query(
            "select obj from Area obj where obj.parent.id=:pid", params, -1, -1);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Area area : areas) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", area.getId());
            map.put("areaName", area.getAreaName());
            list.add(map);
        }
        String temp = Json.toJson(list, JsonFormat.compact());
        response.setContentType("text/plain");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter writer = response.getWriter();
            writer.print(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
