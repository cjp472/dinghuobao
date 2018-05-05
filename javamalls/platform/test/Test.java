package com.javamalls.platform.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.javamalls.payment.chinabank.h5.util.RSACoder;
import com.javamalls.payment.chinabank.h5.util.SHAUtil;

public class Test {
    public static void main(String[] args) {
        /*String path = "E:\\apache-tomcat-7.0.42\\luence";
        double size = CommUtil.fileSize(new File(path));
        System.out.println(size);
        long size1 = getFileSize(new File(path));
        System.out.println(size1);*/
        //        int a = new BigDecimal("1.00").multiply(new BigDecimal(100)).intValue();
        //        System.out.println(a + "");
        String rsaPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALXf6twUqul1TATO+5nA66p2wjnRd+g96IXpfV6Sf8WXxwizGj+L19LQYRBXpZHmRh82prJ48d0FcHboCiN8pKutnuZrrKYhvORysOc5bVli0hcCn1TfYDoUWJ1UhjUQloqZKWjUz6LV9QY6bIZ1W4+Hmw6HK1bfFwUq0WzIGkJNAgMBAAECgYBlIFQeev9tP+M86TnMjBB9f/sO2wGpCIM5slIbO6n/3By3IZ7+pmsitOrDg3h0X22t/V1C7yzMkDGwa+T3Rl7ogwc4UNVj0ZQorOTx3OEPx3nP1yT3zmJ9djKaHKAmee4XmhQHdqqIuMT2XQaqatBzcsnP+Jnw/WVOsIJIqMeFAQJBAP9yq4hE+UfM/YSXZ5JR33k9RolUUq8S/elmeJIDo/3N2qDmzLjOr9iEZHxioc8JOxubtZ0BxA+NdfKz4v0BSpkCQQC2RIrAPRj9vOk6GfT9W1hbJ4GdnzTb+4vp3RDQQ3x9JGXzWFlg8xJT1rNgM8R95Gkxn3KGnYHJQTLlCsIy2FnVAkAWXolM3pVhxz6wHL4SHx9Ns6L4payz7hrUFIgcaTs0H5G0o2FsEZVuhXFzPwPiaHGHomQOAriTkBSzEzOeaj2JAkEAtYUFefZfETQ2QbrgFgIGuKFboJKRnhOif8G9oOvU6vx43CS8vqTVN9G2yrRDl+0GJnlZIV9zhe78tMZGKUT2EQJAHQawBKGlXlMe49Fo24yOy5DvKeohobjYqzJAtbqaAH7iIQTpOZx91zUcL/yG4dWS6r+wGO7Z1RKpupOJLKG3lA==";
        String a = "currency=CNY&failCallbackUrl=http://test.muheda.com/mobile/chinabank_fail.htm&merchantNum=22294531&merchantRemark=91&notifyUrl=http://test.muheda.com/mobile/chinabank_notify.htm&successCallbackUrl=http://test.muheda.com/mobile/chinabank_return.htm&tradeAmount=1&tradeDescription=goods&tradeName=睦合达 购物&tradeNum=120150428165645_91_goods&tradeTime=2015-04-28 16:56:49";

        //System.out.println(sign(a, rsaPrivateKey));
        //System.out.println(sign(a, rsaPrivateKey));
        /*String tr = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><DATA>  <TRADE>    <TYPE>S</TYPE>    <ID>120150428160623_90_goods</ID>    <AMOUNT>1</AMOUNT>    <CURRENCY>CNY</CURRENCY>    <DATE>20150428</DATE>    <TIME>160809</TIME>    <NOTE>goods</NOTE>    <STATUS>0</STATUS>  </TRADE>  <RETURN>    <CODE>0000</CODE>    <DESC>成功</DESC>  </RETURN></DATA>";
        System.out.println(paseText(tr));*/

        /* try {
             System.out.println(HttpMainUtils
                 .httpGetBasicAuth("http://47.94.0.153:5011/api/v1/ping"));
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }*/
    }

    public static String sign(String sourceSignString, String rsaPriKey) {
        String result = "";
        try {
            //摘要
            String sha256SourceSignString = SHAUtil.Encrypt(sourceSignString, null);
            System.out.println("===============mpay:" + sourceSignString);
            //私钥对摘要进行加密
            byte[] newsks = RSACoder.encryptByPrivateKey(sha256SourceSignString.getBytes("UTF-8"),
                rsaPriKey);
            result = RSACoder.encryptBASE64(newsks);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String paseText(String strString) {
        if (strString.endsWith(","))
            strString = strString.substring(0, strString.length() - 1);
        Document document = null;
        try {
            byte[] xmlString = strString.getBytes("UTF-8");
            InputStream is = new ByteArrayInputStream(xmlString);
            SAXReader sax = new SAXReader(false);
            document = sax.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String order_no = "";
        Element rootElement = document.getRootElement();
        if (null != rootElement) {
            Iterator iter = rootElement.elementIterator("TRADE");
            while (iter.hasNext()) {

                Element recordEle = (Element) iter.next();
                order_no = recordEle.elementTextTrim("ID");
            }
        }
        return order_no;
    }

    public static void paseText() {
        String str = "<notify><payment_type>1</payment_type><subject>?????,?????120150427163359</subject><trade_no>2015042700001000960048715602</trade_no><buyer_email>zhang.2468@hotmail.com</buyer_email><gmt_create>2015-04-27 16:34:20</gmt_create><notify_type>trade_status_sync</notify_type><quantity>1</quantity><out_trade_no>73_goods</out_trade_no><notify_time>2015-04-27 16:34:25</notify_time><seller_id>2088611923079121</seller_id><trade_status>TRADE_SUCCESS</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>0.01</total_fee><gmt_payment>2015-04-27 16:34:25</gmt_payment><seller_email>muheda@muheda.com</seller_email><price>0.01</price><buyer_id>2088602036084960</buyer_id><notify_id>d3b551246cbe9245cd4eb431f262c6b37c</notify_id><use_coupon>N</use_coupon></notify>,";
        if (str.endsWith(","))
            str = str.substring(0, str.length() - 1);
        Document document = null;
        try {
            byte[] xmlString = str.getBytes("UTF-8");
            InputStream is = new ByteArrayInputStream(xmlString);
            SAXReader sax = new SAXReader(false);
            document = sax.read(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element rootElement = document.getRootElement();
        if (null != rootElement) {
            Element out_trade_noEliment = rootElement.element("out_trade_no");
            if (null != out_trade_noEliment) {
                System.out.println("=======" + out_trade_noEliment.getText());
                //                order_no = out_trade_noEliment.getText();
            }
            Element trade_statusEliment = rootElement.element("trade_status");
            if (null != trade_statusEliment) {
                //                trade_status = trade_statusEliment.getText();
            }
            Element trade_noEliment = rootElement.element("trade_no");
            if (null != trade_noEliment) {
                //                trade_no = trade_noEliment.getText();
            }
        } else {
            System.out.println("===============");
            //            throw new Exception("返回的参数不符合xml格式");
        }
    }

    static int totalFolder = 0;
    static int totalFile   = 0;

    public static long getFileSize(File folder) {
        totalFolder += 1;

        long foldersize = 0L;

        File[] filelist = folder.listFiles();
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                foldersize += getFileSize(filelist[i]);
            } else {
                totalFile += 1;

                foldersize += filelist[i].length();
            }
        }
        return foldersize;
    }

    private static void property() throws UnknownHostException {
        Runtime r = Runtime.getRuntime();
        Properties props = System.getProperties();

        InetAddress addr = InetAddress.getLocalHost();
        String ip = addr.getHostAddress();
        Map<String, String> map = System.getenv();
        String userName = (String) map.get("USERNAME");
        String computerName = (String) map.get("COMPUTERNAME");
        String userDomain = (String) map.get("USERDOMAIN");
        System.out.println("用户名:    " + userName);
        System.out.println("计算机名:    " + computerName);
        System.out.println("计算机域名:    " + userDomain);
        System.out.println("本地ip地址:    " + ip);
        System.out.println("本地主机名:    " + addr.getHostName());
        System.out.println("JVM可以使用的总内存:    " + r.totalMemory());
        System.out.println("JVM可以使用的剩余内存:    " + r.freeMemory());
        System.out.println("JVM可以使用的处理器个数:    " + r.availableProcessors());
        System.out.println("Java的运行环境版本：    " + props.getProperty("java.version"));
        System.out.println("Java的运行环境供应商：    " + props.getProperty("java.vendor"));
        System.out.println("Java供应商的URL：    " + props.getProperty("java.vendor.url"));
        System.out.println("Java的安装路径：    " + props.getProperty("java.home"));
        System.out
            .println("Java的虚拟机规范版本：    " + props.getProperty("java.vm.specification.version"));
        System.out
            .println("Java的虚拟机规范供应商：    " + props.getProperty("java.vm.specification.vendor"));
        System.out.println("Java的虚拟机规范名称：    " + props.getProperty("java.vm.specification.name"));
        System.out.println("Java的虚拟机实现版本：    " + props.getProperty("java.vm.version"));
        System.out.println("Java的虚拟机实现供应商：    " + props.getProperty("java.vm.vendor"));
        System.out.println("Java的虚拟机实现名称：    " + props.getProperty("java.vm.name"));
        System.out.println("Java运行时环境规范版本：    " + props.getProperty("java.specification.version"));
        System.out.println("Java运行时环境规范供应商：    " + props.getProperty("java.specification.vender"));
        System.out.println("Java运行时环境规范名称：    " + props.getProperty("java.specification.name"));
        System.out.println("Java的类格式版本号：    " + props.getProperty("java.class.version"));
        System.out.println("Java的类路径：    " + props.getProperty("java.class.path"));
        System.out.println("加载库时搜索的路径列表：    " + props.getProperty("java.library.path"));
        System.out.println("默认的临时文件路径：    " + props.getProperty("java.io.tmpdir"));
        System.out.println("一个或多个扩展目录的路径：    " + props.getProperty("java.ext.dirs"));
        System.out.println("操作系统的名称：    " + props.getProperty("os.name"));
        System.out.println("操作系统的构架：    " + props.getProperty("os.arch"));
        System.out.println("操作系统的版本：    " + props.getProperty("os.version"));
        System.out.println("文件分隔符：    " + props.getProperty("file.separator"));
        System.out.println("路径分隔符：    " + props.getProperty("path.separator"));
        System.out.println("行分隔符：    " + props.getProperty("line.separator"));
        System.out.println("用户的账户名称：    " + props.getProperty("user.name"));
        System.out.println("用户的主目录：    " + props.getProperty("user.home"));
        System.out.println("用户的当前工作目录：    " + props.getProperty("user.dir"));
    }
}
