package com.javamalls.payment.weixin.support;


import com.google.common.collect.Maps;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import javax.net.ssl.SSLContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by cjl on 2016-09-19 
 */
public class HttpUtil {

    /**
     * 发送https请求
     * @param url
     * @param method
     * @param data
     * @return
     */
    public static String execute(String url,String method,String data) {
        CloseableHttpClient httpClient = createSSLClientDefault();
        CloseableHttpResponse response = null;
        String content = "";
        try {
            if("post".equals(method)){
                HttpPost post = new HttpPost(url);
                post.addHeader("Content-Type", "text/html;charset=utf-8");
                StringEntity entity = new StringEntity(data,"utf-8");
                entity.setContentEncoding("utf-8");
                post.setEntity(entity);
                response = httpClient.execute(post);
            }else{
                HttpGet get = new HttpGet(url);
                get.addHeader("Content-Type", "text/html;charset=utf-8");
                response = httpClient.execute(get);
            }
            content = EntityUtils.toString(response.getEntity(), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    public static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                public boolean isTrusted(X509Certificate[] chain,
                                         String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return  HttpClients.createDefault();
    }

    /**
     * 发送http
     * @param url
     * @param method
     * @param map (key/value)
     * @return
     */
    public static String executeHttp(String url,String method,Map<String,String> map){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(20000).setConnectionRequestTimeout(20000)
                .setSocketTimeout(20000).build();
        CloseableHttpResponse response = null;
        String content = "";
        try {
            if("post".equals(method)){
                HttpPost post = new HttpPost(url);
                post.setConfig(requestConfig);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                for (String key : map.keySet()) {
                    nameValuePairs.add(new BasicNameValuePair(key, map.get(key)));
                }
                post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                response = httpClient.execute(post);
            }else{
                HttpGet get = new HttpGet(url);
                get.setConfig(requestConfig);
                response = httpClient.execute(get);
            }
            content = EntityUtils.toString(response.getEntity(), "utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        return content;
    }

    //test
    public static void main(String[] args) {
        String url = "http://123.57.184.252:8087/system/h5_cloudShop!updateAddWarehousePro.action";
        //String url = "http://192.168.1.111:8081";
        String order="{\"clientId\":123,\"companyId\":1,\"orderCode\":20160824154255698,\"goods\":[{\"count\":1,\"goodId\":1089}]}";
        Map<String,String> map = Maps.newHashMap();
        map.put("order",order);
        String content = HttpUtil.executeHttp(url, "post", map);
        System.out.print(content);
        //{"success":false,"message":"不能重复提交数据","total":0}
    }
}
