package com.utils;


import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.javamalls.base.tools.CommUtil;
import com.javamalls.payment.chinabank.h5.util.BASE64;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.platform.domain.InterfaceLog;
import com.javamalls.platform.service.IInterfaceLogService;


@Component
public class SendReqAsync {
	 	private final static String        WAREHOUSEUSERNAME = "bjsd";
	    private final static String        WAREHOUSEPASSWORD = "bjsd@1212";
	    private static final Logger logger = Logger.getLogger(SendReqAsync.class);
	    static final int                   TIMEOUT           = 15000;
	    @Autowired
	    private IInterfaceLogService interfaceLogService;
	 /**
     * 发送http
     * @param url
     * @param method
     * @param map (key/value)
     * @return
     */
	@Async
	public void sendMessageUtil(String url,String method,Map<String,String> map,String interfaceName){
		
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        RequestConfig requestConfig = RequestConfig.custom()
	                .setConnectTimeout(20000).setConnectionRequestTimeout(20000)
	                .setSocketTimeout(20000).build();
	        CloseableHttpResponse response = null;
	        String content="";
	        try {
	             // 创建uri
	    			URIBuilder builder = new URIBuilder(url);
	    			if (map != null) {
	    				for (String key : map.keySet()) {
	    					builder.addParameter(key, map.get(key));
	    				}
	    			}
	    			URI uri = builder.build();
	    			logger.info(uri.toString());
	    			System.out.println(uri.toString());
	    			HttpGet get = new HttpGet(uri);
	    			String authString = WAREHOUSEUSERNAME + ":" + WAREHOUSEPASSWORD;
	    			get.setHeader("Authorization", "Basic " + BASE64.encode(authString.getBytes()));
	                get.setConfig(requestConfig);
	                response = httpClient.execute(get);
	                content = EntityUtils.toString(response.getEntity(), "utf-8");
	                
	                //保存接口调用日志
	                if(content!=null&&!"".equals(content)){
	                	Map<String, Object> json2Map = JsonUtil.json2Map(content);
	 	                InterfaceLog log=new InterfaceLog();
	 	                log.setCreatetime(new Date());
	 	                log.setDisabled(false);
	 	                log.setInterface_name(interfaceName);
	 	                log.setStatus(CommUtil.null2String(response.getStatusLine().getStatusCode()));
	 	                log.setSystem_type(1);
	 	                log.setRequest_parameter(JsonUtil.write2JsonStr(map));//请求参数
	 	                log.setResponse_parameter(json2Map.get("meta")+"");
	 	               this.interfaceLogService.save(log);
	                }
	               
	        }catch (Exception e){
	            e.printStackTrace();
	        }finally {
	        	try {
					httpClient.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	
	@Async
	public void sendMessageUtil(String url, String jsonstr,String interfaceName){  
		   
	       CloseableHttpClient httpclient = HttpClientBuilder.create().build();  
	       HttpPost post = new HttpPost(url);  
	       try {  
	           StringEntity s = new StringEntity(jsonstr,"UTF-8");  
	           s.setContentEncoding("UTF-8");  
	           s.setContentType("application/json");//发送json数据需要设置contentType  
	           String authString = WAREHOUSEUSERNAME + ":" + WAREHOUSEPASSWORD;
	           post.setHeader("Content-Type", "application/json;charset=UTF-8");
	           post.setHeader("Authorization", "Basic " + BASE64.encode(authString.getBytes()));
	           post.setEntity(s);  
	           config(post);//设置超时时间
	           HttpResponse res = httpclient.execute(post);  
	           
	          String  content = EntityUtils.toString(res.getEntity(), "utf-8");
               logger.info("调用"+interfaceName+"响应数据："+content);
               //保存接口调用日志
             //保存接口调用日志
               if(content!=null&&!"".equals(content)){
               	Map<String, Object> json2Map = JsonUtil.json2Map(content);
	                InterfaceLog log=new InterfaceLog();
	                log.setCreatetime(new Date());
	                log.setDisabled(false);
	                log.setInterface_name(interfaceName);
	                log.setStatus(CommUtil.null2String(res.getStatusLine().getStatusCode()));
	                log.setSystem_type(1);
	                
	                log.setRequest_parameter(jsonstr);//请求参数
	                log.setResponse_parameter(json2Map.get("meta")+"");
	                System.out.println(log.getResponse_parameter());
	                this.interfaceLogService.save(log);
               }
	       } catch (Exception e) {  
	           throw new RuntimeException(e);  
	       }  
	     
	   }  
	 private static void config(HttpRequestBase httpRequestBase) {
	        // 配置请求的超时设置
	        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(TIMEOUT)
	            .setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT).build();
	        httpRequestBase.setConfig(requestConfig);
	    }
	public static void main(String[] args) {
		/* try {
			//"http://47.94.0.153:5011/api/v1/ping"
			 SendReqAsync async=new SendReqAsync();
			 async.sendMessageUtil("http://47.94.0.153:5011/api/v1/call/user_del?id=1", "GET",null,"测试接口");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
