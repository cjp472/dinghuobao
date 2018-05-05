package com.javamalls.ctrl.admin.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.BlockingCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.javamalls.base.annotation.SecurityMapping;
import com.javamalls.base.mv.JModelAndView;
import com.javamalls.base.tools.CommUtil;
import com.javamalls.platform.service.ISysConfigService;
import com.javamalls.platform.service.IUserConfigService;

/**缓存管理
 *                       
 * @Filename: CacheManageAction.java
 * @Version: 2.7.0
 * @Author: 范光洲
 * @Email: goodfgz@163.com
 *
 */
@Controller
public class CacheManageAction {
    @Autowired
    private ISysConfigService  configService;
    @Autowired
    private IUserConfigService userConfigService;

    @SecurityMapping(title = "缓存列表", value = "/admin/cache_list.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
    @RequestMapping({ "/admin/cache_list.htm" })
    public ModelAndView cache_list(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new JModelAndView("admin/blue/cache_list.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        CacheManager manager = CacheManager.create();
        BlockingCache cache = new BlockingCache(
            manager.getEhcache("SimplePageFragmentCachingFilter"));
        int data_cache_size = 0;
        long cache_memory_size = 0L;
        for (String name : manager.getCacheNames()) {
            data_cache_size = data_cache_size
                              + (manager.getCache(name) != null ? manager.getCache(name).getSize()
                                  : 0);

            cache_memory_size = cache_memory_size
                                + (manager.getCache(name) != null ? manager.getCache(name)
                                    .getMemoryStoreSize() : 0L);
        }
        mv.addObject("cache_memory_size", Long.valueOf(cache_memory_size));
        mv.addObject("data_cache_size", Integer.valueOf(data_cache_size));
        mv.addObject("page_cache_size", Integer.valueOf(cache.getSize()));
        return mv;
    }

    @SecurityMapping(title = "更新缓存", value = "/admin/update_cache.htm*", rtype = "admin", rname = "更新缓存", rcode = "cache_manage", rgroup = "工具")
    @RequestMapping({ "/admin/update_cache.htm" })
    public ModelAndView update_cache(HttpServletRequest request, HttpServletResponse response,
                                     String data_cache, String page_cache) {
        ModelAndView mv = new JModelAndView("admin/blue/success.html",
            this.configService.getSysConfig(), this.userConfigService.getUserConfig(), 0, request,
            response);
        CacheManager manager = CacheManager.create();
        String[] names = manager.getCacheNames();
        if (CommUtil.null2Boolean(data_cache)) {
            for (String name : names) {
                if (!name.equalsIgnoreCase("SimplePageCachingFilter")) {
                    manager.clearAllStartingWith(name);
                }
            }
        }
        if (CommUtil.null2Boolean(page_cache)) {
            Ehcache cache = manager.getEhcache("SimplePageCachingFilter");
            manager.clearAllStartingWith("SimplePageCachingFilter");
        }
        mv.addObject("list_url", CommUtil.getURL(request) + "/admin/cache_list.htm");
        mv.addObject("op_title", "更新缓存成功");
        return mv;
    }
}
