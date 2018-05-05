package com.javamalls.platform.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.javamalls.base.basedao.IGenericDAO;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.query.GenericPageList;
import com.javamalls.base.query.PageObject;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.base.query.support.IQueryObject;
import com.javamalls.excel.util.ExcelRead;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.User;
import com.javamalls.platform.domain.UserGoodsClass;
import com.javamalls.platform.service.IUserGoodsClassService;
import com.javamalls.platform.vo.RemoveIdJsonVo;
import com.javamalls.platform.vo.UserGoodsClassJsonVo;
import com.utils.SendReqAsync;

@Service
@Transactional
public class UserGoodsClassServiceImpl implements IUserGoodsClassService {
    @Resource(name = "userGoodsClassDAO")
    private IGenericDAO<UserGoodsClass> userGoodsClassDao;
    
    @Autowired
    private SendReqAsync sendReqAsync;

    public boolean save(UserGoodsClass userGoodsClass) {
        try {
            this.userGoodsClassDao.save(userGoodsClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public UserGoodsClass getObjById(Long id) {
        UserGoodsClass userGoodsClass = (UserGoodsClass) this.userGoodsClassDao.get(id);
        if (userGoodsClass != null) {
            return userGoodsClass;
        }
        return null;
    }

    public boolean delete(Long id) {
        try {
            this.userGoodsClassDao.remove(id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean batchDelete(List<Serializable> userGoodsClassIds) {
        for (Serializable id : userGoodsClassIds) {
            delete((Long) id);
        }
        return true;
    }

    public IPageList list(IQueryObject properties) {
        if (properties == null) {
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(UserGoodsClass.class, query, params,
            this.userGoodsClassDao);
        if (properties != null) {
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null) {
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj.getCurrentPage()
                    .intValue(), pageObj.getPageSize() == null ? 0 : pageObj.getPageSize()
                    .intValue());
            }
        } else {
            pList.doList(0, -1);
        }
        return pList;
    }

    public boolean update(UserGoodsClass userGoodsClass) {
        try {
            this.userGoodsClassDao.update(userGoodsClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UserGoodsClass> query(String query, Map params, int begin, int max) {
        return this.userGoodsClassDao.query(query, params, begin, max);
    }

    @Override
    @Transactional
    public int importGoodClass(User user, MultipartFile excelFile) {
        if (user==null || excelFile==null) {
            return 1;//参数错误
        }
        //删除之前的分类
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("disabled", false);
        map.put("user", user.getId());
        List<RemoveIdJsonVo> del_list=new ArrayList<RemoveIdJsonVo>();
        String sql = "select obj from UserGoodsClass obj where obj.disabled=:disabled and obj.user.id=:user";
        List<UserGoodsClass> userGoodsClassList = userGoodsClassDao.query(sql, map, -1, -1);
        for (UserGoodsClass userGoodsClass : userGoodsClassList) {
            userGoodsClass.setDisabled(true);
            userGoodsClassDao.update(userGoodsClass);
            
            RemoveIdJsonVo vo=new RemoveIdJsonVo();
            vo.setId(userGoodsClass.getId());
            del_list.add(vo);
        }
        
       
        
        //导入新的分类
        List<ArrayList<String>> model = null;
        try {
            ExcelRead excelRead = new ExcelRead();
            model = excelRead.readExcel(excelFile);
            Date date = new Date();
            if (model!=null) {
            	Map<String,UserGoodsClass> map2=new HashMap<String, UserGoodsClass>();
            	List<UserGoodsClassJsonVo> jsonlist=new ArrayList<UserGoodsClassJsonVo>();
                for (ArrayList<String> arrayList : model) {
                    System.out.println(arrayList+"--");
                    UserGoodsClass userGoodsClass = new UserGoodsClass();
                    userGoodsClass.setCreatetime(date);
                    userGoodsClass.setDisabled(false);
                    userGoodsClass.setDisplay(true);
                    userGoodsClass.setClassName(arrayList.get(0));
                    userGoodsClass.setLevel(Integer.parseInt(arrayList.get(1)));
                    userGoodsClass.setUser(user);
                    if (arrayList.size()>2) {
                        userGoodsClass.setParent(map2.get(arrayList.get(2)));
                    }  
                    userGoodsClassDao.save(userGoodsClass);
                    map2.put(userGoodsClass.getClassName(), userGoodsClass);
                    
                    
                    //服装接口
                    UserGoodsClassJsonVo vo=new UserGoodsClassJsonVo();
                    vo.setClassName(userGoodsClass.getClassName());
                    vo.setCreatetime(userGoodsClass.getCreatetime());
                    vo.setDisabled(userGoodsClass.isDisabled());
                    vo.setDisplay(userGoodsClass.isDisplay());
                    vo.setId(userGoodsClass.getId());
                    vo.setLevel(userGoodsClass.getLevel());
                    if(userGoodsClass.getParent()!=null){
                        vo.setParent_id(userGoodsClass.getParent().getId());
                    }
                    vo.setSequence(userGoodsClass.getSequence());
                    vo.setUser_id(user.getId());
                    
                    jsonlist.add(vo);
                   
                }
                
                //服装接口
                if(del_list!=null&&del_list.size()>0){
                	String write2JsonStr = JsonUtil.write2JsonStr(del_list);
                	sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_DEL, write2JsonStr,"删除商品分类");
                }
                String write2JsonStr = JsonUtil.write2JsonStr(jsonlist);
                System.out.println(write2JsonStr);
                sendReqAsync.sendMessageUtil(Constant.STORE_GOODSCLASS_URL_BATCHADD, write2JsonStr,"批量添加商品分类");
                
                
                
                
                return 3;
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block            
            e.printStackTrace();
            return 2;
        }
        
        
        return 1;

    }

    @Override
    public UserGoodsClass getUserGoodsClassByName(User user,String name) {
        if (name!=null && !name.equals("")) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("disabled", false);
            map.put("className", name);
            map.put("user", user.getId());
            String sql = "select obj from UserGoodsClass obj where obj.disabled=:disabled and obj.user.id=:user and obj.className=:className ";
            List<UserGoodsClass> userGoodsClassList = userGoodsClassDao.query(sql, map, -1, -1);
            if (userGoodsClassList.size()>0) {
                return userGoodsClassList.get(0);
            }
        }
        return null;
    }
}
