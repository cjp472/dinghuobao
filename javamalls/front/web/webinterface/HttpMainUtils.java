package com.javamalls.front.web.webinterface;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.javamalls.payment.chinabank.h5.util.BASE64;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;

/**
 * Created by Elise on 2016/6/30.
 */
public class HttpMainUtils {

    protected static final Log         logger            = LogFactory.getLog(HttpMainUtils.class);

    private final static Object        syncLock          = new Object();

    static final int                   timeOut           = 7 * 1000;

    private static CloseableHttpClient httpClient        = null;

    private static final String        CHAR_SET          = "UTF-8";

    private final static String        warehouseUserName = "bjsd";
    private final static String        warehousePassword = "bjsd@1212";

    /**
     * post(key/value)
     * @param url
     * @param parameters
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String post(String url, Map<String, String> parameters, String encoding)
                                                                                          throws IOException {
        HttpPost post = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        post.setEntity(new UrlEncodedFormEntity(nvps));

        config(post);
        HttpResponse response = getHttpClient(url).execute(post, HttpClientContext.create());

        String content = EntityUtils.toString(response.getEntity(), encoding);
        return content;
    }

    /**
     * postJson(key/value)
     * @param url
     * @param parameters
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String postJson(String url, String json, String encoding) throws IOException {

        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        StringEntity se = new StringEntity(json);
        se.setContentType("text/json");
        post.setEntity(se);

        config(post);
        HttpResponse response = getHttpClient(url).execute(post, HttpClientContext.create());

        String content = EntityUtils.toString(response.getEntity(), encoding);
        return content;
    }

    /**
     * HTTP Basic Authentication
     * @param url
     * @param json
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String postJsonBasicAuth(String url, String json, String encoding)
                                                                                    throws IOException {
        String authString = warehouseUserName + ":" + warehousePassword;
        HttpPost post = new HttpPost(url);
        post.setHeader("Content-Type", "application/json;charset=UTF-8");
        post.setHeader("Authorization", "Basic " + BASE64.encode(authString.getBytes()));
        StringEntity se = new StringEntity(json);
        se.setContentType("text/json");
        post.setEntity(se);

        config(post);
        HttpResponse response = getHttpClient(url).execute(post, HttpClientContext.create());

        String content = EntityUtils.toString(response.getEntity(), encoding);
        return content;
    }

    /**
     * HTTP Basic Authentication
     * @param url
     * @param json
     * @param encoding
     * @return
     * @throws IOException
     */
    public static Map<String, Object> httpGetBasicAuth(String url) throws IOException {
        String authString = warehouseUserName + ":" + warehousePassword;
        //get请求返回结果
        Map<String, Object> jsonResult = null;
        try {
            //发送get请求
            HttpGet request = new HttpGet(url);
            request.setHeader("Authorization", "Basic " + BASE64.encode(authString.getBytes()));

            config(request);
            HttpResponse response = getHttpClient(url).execute(request, HttpClientContext.create());

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                jsonResult = JsonUtil.json2Map(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    /**
     * 发送get请求  接收转成 json 对象
     * @param url    路径
     * @return
     */
    public static Map<String, Object> httpGet(String url) {
        //get请求返回结果
        Map<String, Object> jsonResult = null;
        try {
            //发送get请求
            HttpGet request = new HttpGet(url);
            config(request);
            HttpResponse response = getHttpClient(url).execute(request, HttpClientContext.create());

            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                String strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                jsonResult = JsonUtil.json2Map(strResult);
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return jsonResult;
    }

    private static void config(HttpRequestBase httpRequestBase) {
        // 配置请求的超时设置
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timeOut)
            .setConnectTimeout(timeOut).setSocketTimeout(timeOut).build();
        httpRequestBase.setConfig(requestConfig);
    }

    /**
     * 获取HttpClient对象
     * 
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient getHttpClient(String url) {
        String hostname = url.split("/")[2];
        int port = 80;
        if (hostname.contains(":")) {
            String[] arr = hostname.split(":");
            hostname = arr[0];
            port = Integer.parseInt(arr[1]);
        }
        if (httpClient == null) {
            synchronized (syncLock) {
                if (httpClient == null) {
                    httpClient = createHttpClient(200, 40, 100, hostname, port);
                }
            }
        }
        return httpClient;
    }

    /**
     * 创建HttpClient对象
     * 
     * @return
     * @author SHANHY
     * @create 2015年12月18日
     */
    public static CloseableHttpClient createHttpClient(int maxTotal, int maxPerRoute, int maxRoute,
                                                       String hostname, int port) {
        ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslsf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder
            .<ConnectionSocketFactory> create().register("http", plainsf).register("https", sslsf)
            .build();
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // 将最大连接数增加
        cm.setMaxTotal(maxTotal);
        // 将每个路由基础的连接增加
        cm.setDefaultMaxPerRoute(maxPerRoute);
        HttpHost httpHost = new HttpHost(hostname, port);
        // 将目标主机的最大连接数增加
        cm.setMaxPerRoute(new HttpRoute(httpHost), maxRoute);

        // 请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount,
                                        HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
            .setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }

    public static Map<String, Object> get(String url, Map<String, ? extends Object> params)
                                                                                           throws Exception {
        if (url == null || url.trim().length() == 0) {
            throw new Exception(" url is null or empty!");
        }

        if (params != null && params.size() > 0) {
            if (!url.contains("?")) {
                url += "?";
            }

            if (url.charAt(url.length() - 1) != '?') {
                url += "&";
            }

            url += buildParams(params);
        }

        System.out.println("请求完整url:" + url);
        return httpGet(url);
    }

    public static String buildParams(Map<String, ? extends Object> params)
                                                                          throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null)
                builder.append(entry.getKey().trim()).append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), CHAR_SET)).append("&");
        }

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

    public static String buildParamsNotEncode(Map<String, ? extends Object> params)
                                                                                   throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, ? extends Object> entry : params.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null)
                builder.append(entry.getKey().trim()).append("=")
                    .append(entry.getValue().toString()).append("&");
        }

        if (builder.charAt(builder.length() - 1) == '&') {
            builder.deleteCharAt(builder.length() - 1);
        }

        return builder.toString();
    }

}
