package com.javamalls.platform.domain;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.simple.JSONObject;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.javamalls.base.constant.Constant;
import com.javamalls.base.domain.CommonEntity;
import com.javamalls.base.tools.HttpRequest;
import com.javamalls.base.tools.Md5Encrypt;
import com.javamalls.base.tools.StringUtils;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "jm_sms")
public class Sms extends CommonEntity {
    private static final long serialVersionUID = 8966057765477112659L;

    // 短信厂商或发送短信使用的签名
    private String            name;
    // 请求接口访问MNS服务的接入地址
    private String            httpaddr;
    // 请求方式 (暂时作废)
    private String            httpmothod;
    // 用户名参数 阿里云AccessId
    private String            usernameparam;
    // 用户密码参数（暂时作废）
    private String            userpwdparam;
    // 用户名值 阿里云AccessKey
    private String            usernamevalue;
    // 用户密码值（暂时作废）
    private String            userpwdvalue;
    // 手机参数名 发送短信使用的模板Code
    private String            mobileparam;
    // 内容参数名（暂时作废）
    private String            contentparam;
    // 要md5的参数（暂时作废）
    private String            tomd5;
    // 返回参数格式 0json，1text
    private String            returnstyle;
    // 返回参数（暂时作废）
    private String            returnparam;
    // 返回参数正确值（暂时作废）
    private String            returnvalue;
    // 转码格式（暂时作废）
    private String            encodevalue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttpaddr() {
        return httpaddr;
    }

    public void setHttpaddr(String httpaddr) {
        this.httpaddr = httpaddr;
    }

    /**
     * 用户名参数 阿里云AccessId
     * @return
     */
    public String getUsernameparam() {
        return usernameparam;
    }

    /**
     * 用户名参数 阿里云AccessId
     * @param usernameparam
     */
    public void setUsernameparam(String usernameparam) {
        this.usernameparam = usernameparam;
    }

    public String getUserpwdparam() {
        return userpwdparam;
    }

    public void setUserpwdparam(String userpwdparam) {
        this.userpwdparam = userpwdparam;
    }

    /**
     * 用户名值 阿里云AccessKey
     * @return
     */
    public String getUsernamevalue() {
        return usernamevalue;
    }

    /**
     * 用户名值 阿里云AccessKey
     * @param usernamevalue
     */
    public void setUsernamevalue(String usernamevalue) {
        this.usernamevalue = usernamevalue;
    }

    public String getUserpwdvalue() {
        return userpwdvalue;
    }

    public void setUserpwdvalue(String userpwdvalue) {
        this.userpwdvalue = userpwdvalue;
    }

    public String getMobileparam() {
        return mobileparam;
    }

    public void setMobileparam(String mobileparam) {
        this.mobileparam = mobileparam;
    }

    public String getContentparam() {
        return contentparam;
    }

    public void setContentparam(String contentparam) {
        this.contentparam = contentparam;
    }

    public String getTomd5() {
        return tomd5;
    }

    public void setTomd5(String tomd5) {
        this.tomd5 = tomd5;
    }

    public String getReturnparam() {
        return returnparam;
    }

    public void setReturnparam(String returnparam) {
        this.returnparam = returnparam;
    }

    public String getReturnvalue() {
        return returnvalue;
    }

    public void setReturnvalue(String returnvalue) {
        this.returnvalue = returnvalue;
    }

    public String getReturnstyle() {
        return returnstyle;
    }

    public void setReturnstyle(String returnstyle) {
        this.returnstyle = returnstyle;
    }

    public String getEncodevalue() {
        return encodevalue;
    }

    public void setEncodevalue(String encodevalue) {
        this.encodevalue = encodevalue;
    }

    public String getHttpmothod() {
        return httpmothod;
    }

    public void setHttpmothod(String httpmothod) {
        this.httpmothod = httpmothod;
    }

    public boolean SendSms(Sms sms, String mobile, String content)
                                                                  throws UnsupportedEncodingException {
        Long time = new Date().getTime();// 随机数用当前时间
        HttpURLConnection httpconn = null;
        String result = sms.getReturnvalue();
        boolean re = false;
        String memo = content.length() < 70 ? content.trim() : content.trim().substring(0, 70);// 取前70字符发送
        StringBuilder url = new StringBuilder(sms.getHttpaddr());
        if (sms.getHttpaddr().lastIndexOf("?") == -1) {
            url.append("?");
        }
        Map<String, String> parameters = new HashMap<String, String>();
        // 用户名，密码
        if (StringUtils.hasLength(sms.getUsernameparam())) {
            parameters.put(sms.getUsernameparam(), sms.getUsernamevalue());
        }
        if (StringUtils.hasLength(sms.getUserpwdparam())) {
            parameters.put(sms.getUserpwdparam(), sms.getUserpwdvalue());
        }
        // 手机
        if (StringUtils.hasLength(sms.getMobileparam())) {
            parameters.put(sms.getMobileparam(), mobile);
        }
        // 内容，判断转码
        parameters.put(sms.getContentparam(), content);
        if (StringUtils.hasLength(sms.getEncodevalue())) {
            if (sms.getEncodevalue().equals("1")) {
                parameters.put(sms.getContentparam(), new String(content.getBytes(), "gb2312"));
            }
        }
        // 判断md5
        if (StringUtils.hasLength(sms.getTomd5())) {
            String a[] = sms.getTomd5().split(",");
            String value = "";
            for (int i = a.length; i > 0; i--) {
                value = parameters.get(a[i]);
                if (StringUtils.hasLength(value)) {
                    parameters.put(a[i], Md5Encrypt.md5(value));
                }
            }
        }
        //请求方式
        if (sms.getHttpmothod().equals("0")) {
            // post
            result = HttpRequest.sendPost(url.toString(),
                HttpRequest.serialParameters(parameters, false));
        } else {
            // get
            result = HttpRequest.sendGet(url.toString(),
                HttpRequest.serialParameters(parameters, false));
        }
        //判断返回参数和值,格式
        if (StringUtils.hasLength(sms.getReturnparam())) {
            // 参数存在
            if (StringUtils.hasLength(sms.getReturnvalue())) {
                if (sms.getReturnstyle().equals("1")) {
                    // json
                    if (result.indexOf(sms.getReturnparam() + ":" + sms.getReturnvalue()) != -1) {
                        re = true;
                    }
                } else {
                    if (result.indexOf(sms.getReturnparam() + "=" + sms.getReturnvalue()) != -1) {
                        re = true;
                    }
                }
            }

        } else {
            // 参数不存在
            if (StringUtils.hasLength(sms.getReturnvalue())) {
                if (result.indexOf(sms.getReturnvalue()) != -1) {
                    re = true;
                }
            }
        }
        return re;
    }

    /**
     * 阿里云短信  迁移后接口
     * @param sms
     * @param mobile
     * @param templateCode
     * @param map
     * @return
     * @throws UnsupportedEncodingException
     * @throws ClientException
     */
    public boolean SendSms(Sms sms, String mobile, String templateCode, Map<String, String> map) {
        boolean re = false;
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                sms.getUsernameparam(), sms.getUsernamevalue());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", Constant.product,
                Constant.domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(mobile);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(sms.getName());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            String object = JSONObject.toJSONString(map);
            System.out.println("模板中的变量替换JSON串:" + object + ",短信模板code:" + templateCode);
            //request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"123\"}");
            request.setTemplateParam(object);
            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");

            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.setOutId("yourOutId");

            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            System.out.println("阿里云短信返回数据：" + sendSmsResponse.getMessage());
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                re = true;
            } else {
                re = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("发送短信失败");
        }

        return re;
    }

    /* public boolean SendSms(Sms sms, String mobile,String templateCode, Map<String, String> map)
             throws UnsupportedEncodingException {
     	 boolean re = false;
          
         // Step 1. 获取主题引用
         CloudAccount account = new CloudAccount(sms.getUsernameparam(), sms.getUsernamevalue(), sms.getHttpaddr());
         MNSClient client = account.getMNSClient();
         CloudTopic topic = client.getTopicRef(sms.getMobileparam());
          //Step 2. 设置SMS消息体（必须）
          //注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
         RawTopicMessage msg = new RawTopicMessage();
         msg.setMessageBody("sms-message");
          // Step 3. 生成SMS消息属性
         MessageAttributes messageAttributes = new MessageAttributes();
         BatchSmsAttributes batchSmsAttributes = new BatchSmsAttributes();
         // 3.1 设置发送短信的签名（SMSSignName）
         batchSmsAttributes.setFreeSignName(sms.getName());
         // 3.2 设置发送短信使用的模板（SMSTempateCode）
         batchSmsAttributes.setTemplateCode(templateCode);
         // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
         BatchSmsAttributes.SmsReceiverParams smsReceiverParams = new BatchSmsAttributes.SmsReceiverParams();
         if(map==null||map.size()==0){//参数为空直接返回
         	client.close();
         	return false;
         }
         Set<String> keySet = map.keySet();
         for (String str : keySet) {
         	  smsReceiverParams.setParam(str, map.get(str));
    	}
      //   smsReceiverParams.setParam("code", "2222");
      //   smsReceiverParams.setParam("product", "对方答复");
         // 3.4 增加接收短信的号码
         batchSmsAttributes.addSmsReceiver(mobile, smsReceiverParams);
       //  batchSmsAttributes.addSmsReceiver("$YourReceiverPhoneNumber2", smsReceiverParams);
         messageAttributes.setBatchSmsAttributes(batchSmsAttributes);
         try {
              // Step 4. 发布SMS消息
             TopicMessage ret = topic.publishMessage(msg, messageAttributes);
             re = true;
             System.out.println("MessageId: " + ret.getMessageId());
             System.out.println("MessageMD5: " + ret.getMessageBodyMD5());
         } catch (ServiceException se) {
         	 re = false;
             System.out.println(se.getErrorCode() + se.getRequestId());
             System.out.println(se.getMessage());
             se.printStackTrace();
         } catch (Exception e) {
         	 re = false;
             e.printStackTrace();
         }
         client.close();
     
     	return re;
     }*/
    public QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", this.getUsernameparam(),
            this.getUsernamevalue());
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", Constant.product, Constant.domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber("15000000000");
        //可选-流水号
        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

        return querySendDetailsResponse;
    }

}
