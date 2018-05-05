package com.javamalls.base.tools;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.junit.Test;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.javamalls.base.query.support.IPageList;
import com.javamalls.lucene.LuceneResult;
import com.javamalls.payment.chinabank.h5.util.JsonUtil;
import com.javamalls.payment.weixin.vo.ResultMsg;
import com.javamalls.platform.domain.Accessory;
import com.javamalls.platform.domain.GoodsSpecProperty;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class CommUtil {

    /**
     * 处理敏感信息
     * 
     * @param val
     *            处理的值
     * @param type
     *            处理类型
     * @return
     */
    public static String sensitiveInfo(String val, String type) {
        if (null == val || "".equals(val)) {
            return "";
        }
        //电话类型的取前三后四
        if (type.equals("tel")) {
            String firstThree = val.substring(0, 3);
            String lastFour = val.substring((val.length() - 4) < 0 ? 0 : (val.length() - 4));
            return firstThree + "****" + lastFour;
            //email类型的取前一后三
        } else if (type.equals("email")) {
            String address = val.substring(0, val.indexOf("@"));
            char[] cs = address.toCharArray();
            if (cs.length > 0) {
                address = "";
                for (int i = 0; i < cs.length; i++) {
                    if (i == 0 || i >= cs.length - 3)
                        address += cs[i];
                    else
                        address += "*";
                }
            }
            String suffix = val.substring(val.indexOf("@"), val.length());
            return address + suffix;
        } else if (type.equals("userName")) {
            String name = "";
            char[] cs = val.toCharArray();
            if (cs.length > 4) {
                for (int i = 0; i < cs.length; i++) {
                    if (i == 0)
                        name += cs[i];
                    else if (i < cs.length - 3)
                        name += "*";
                    else
                        name += cs[i];
                }
            } else {
                for (int i = 0; i < cs.length; i++) {
                    if (i == 0)
                        name += cs[i];
                    else if (i < cs.length - 1)
                        name += "*";
                    else
                        name += cs[i];
                }
            }
            return name;
        } else {
            throw new IllegalArgumentException("无效类型");
        }
    }

    /**
     * 以一个默认格式格式化时间显示
     * 
     * @param date
     * @return
     */
    public static String formatDate(Date date) {
        if (date != null) {
            return formatDate("yyyy年MM月dd日 HH:mm:ss E", date);
        } else {
            return "0000-00-00 00:00:00";
        }

    }

    public static String formatDateTime(Date date) {
        if (date != null) {
            return formatDate("yyyy-MM-dd HH:mm:ss", date);
        } else {
            return "0000-00-00 00:00:00";
        }

    }

    /**
     * 文字水印
     * @param filePath
     * @param outPath
     * @param text
     * @param markContentColor
     * @param font
     * @param left
     * @param top
     * @param qualNum
     * @return
     */
    public static boolean waterMarkWithText(String filePath, String outPath, String text,
                                            String markContentColor, Font font, int left, int top,
                                            float qualNum) {
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null);
        int height = theImg.getHeight(null);
        BufferedImage bimage = new BufferedImage(width, height, 1);
        Graphics2D g = bimage.createGraphics();
        if (font == null) {
            font = new Font("宋体", 1, 20);
            g.setFont(font);
        } else {
            g.setFont(font);
        }
        g.setColor(CommUtil.getColor(markContentColor));
        g.setComposite(AlphaComposite.getInstance(10, 1.0F));
        g.drawImage(theImg, 0, 0, null);
        g.drawString(text, left, top);
        g.dispose();
        try {
            FileOutputStream out = new FileOutputStream(outPath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
            param.setQuality(qualNum, true);
            encoder.encode(bimage, param);
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 格式化时间显示
     * 
     * @param date
     * @return
     */
    public static String formatDate(String type, Date date) {
        return new SimpleDateFormat(type).format(date);
    }

    /**
     * 处理金额显示
     * 
     * @param val
     * @return
     */
    public static String formatAmount(BigDecimal val) {
        DecimalFormat df = new DecimalFormat("###,###,###,###.##");
        return df.format(val);
    }

    @Test
    public void testEncryptStr() {
        String s = CommUtil.sensitiveInfo("13718848029", "tel");
        s = CommUtil.sensitiveInfo("zhadfefd04343@126.com", "email");
        s = CommUtil.formatAmount(new BigDecimal(1298032d));
        System.out.println(s);
    }

    public static String first2low(String str) {
        String s = "";
        s = str.substring(0, 1).toLowerCase() + str.substring(1);
        return s;
    }

    public static String first2upper(String str) {
        String s = "";
        s = str.substring(0, 1).toUpperCase() + str.substring(1);
        return s;
    }

    public static List<String> str2list(String s) throws IOException {
        List<String> list = new ArrayList<String>();
        if ((s != null) && (!s.equals(""))) {
            StringReader fr = new StringReader(s);
            BufferedReader br = new BufferedReader(fr);
            String aline = "";
            while ((aline = br.readLine()) != null) {
                list.add(aline);
            }
        }
        return list;
    }

    public static Date formatDate(String s) {
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            d = dateFormat.parse(s);
        } catch (Exception localException) {
        }
        return d;
    }

    public static Date formatMaxDate(String s) {
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = dateFormat.parse(s);
        } catch (Exception localException) {
        }
        return d;
    }

    public static Date formatDate(String s, String format) {
        Date d = null;
        try {
            SimpleDateFormat dFormat = new SimpleDateFormat(format);
            d = dFormat.parse(s);
        } catch (Exception localException) {
        }
        return d;
    }

    public static String formatTime(String format, Object v) {
        if (v == null) {
            return null;
        }
        if (v.equals("")) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(v);
    }

    public static String formatLongDate(Object v) {
        if ((v == null) || (v.equals(""))) {
            return "";
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(v);
    }

    public static String formatShortDate(Object v) {
        if (v == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(v);
    }

    public static String formatDay(Object v) {
        if (v == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("dd");
        return df.format(v);
    }

    public static String decode(String s) {
        String ret = s;
        try {
            ret = URLDecoder.decode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    public static String encode(String s) {
        String ret = s;
        try {
            ret = URLEncoder.encode(s.trim(), "UTF-8");
        } catch (Exception localException) {
        }
        return ret;
    }

    public static String convert(String str, String coding) {
        String newStr = "";
        if (str != null) {
            try {
                newStr = new String(str.getBytes("ISO-8859-1"), coding);
            } catch (Exception e) {
                return newStr;
            }
        }
        return newStr;
    }

    public static Map<String, Object> saveFileToServer(HttpServletRequest request, String filePath,
                                                       String saveFilePathName,
                                                       String saveFileName, String[] extendes)
                                                                                              throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePath);
        Map<String, Object> map = new HashMap<String, Object>();
        if ((file != null) && (!file.isEmpty())) {
            String extend = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            // if ((saveFileName == null) || (saveFileName.trim().equals(""))) {
            saveFileName = UUID.randomUUID().toString() + "." + extend;
            // }
            if (saveFileName.lastIndexOf(".") < 0) {
                saveFileName = saveFileName + "." + extend;
            }
            float fileSize = Float.valueOf((float) file.getSize()).floatValue();
            List<String> errors = new ArrayList<String>();
            boolean flag = true;
            if (extendes != null) {
                for (String s : extendes) {
                    if (extend.toLowerCase().equals(s)) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                File path = new File(saveFilePathName);
                if (!path.exists()) {
                    path.mkdir();
                }
                //				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                DataOutputStream out = new DataOutputStream(new FileOutputStream(saveFilePathName
                                                                                 + File.separator
                                                                                 + saveFileName));
                InputStream is = null;
                try {
                    is = file.getInputStream();
                    int size = (int) fileSize;
                    byte[] buffer = new byte[size];
                    while (is.read(buffer) > 0) {
                        out.write(buffer);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
                if (isImg(extend)) {
                    File img = new File(saveFilePathName + File.separator + saveFileName);
                    try {
                        BufferedImage bis = ImageIO.read(img);
                        int w = bis.getWidth();
                        int h = bis.getHeight();
                        map.put("width", Integer.valueOf(w));
                        map.put("height", Integer.valueOf(h));
                    } catch (Exception localException) {
                    }
                }
                map.put("mime", extend);
                map.put("fileName", saveFileName);
                map.put("fileSize", Float.valueOf(fileSize));
                map.put("error", errors);
                map.put("oldName", file.getOriginalFilename());
            } else {
                errors.add("不允许的扩展名");
            }
        } else {
            map.put("width", Integer.valueOf(0));
            map.put("height", Integer.valueOf(0));
            map.put("mime", "");
            map.put("fileName", "");
            map.put("fileSize", Float.valueOf(0.0F));
            map.put("oldName", "");
        }
        return map;
    }

    public static Map<String, Object> saveFileToServer2(HttpServletRequest request,
                                                        String filePath, String saveFilePathName,
                                                        String saveFileName, String[] extendes)
                                                                                               throws IOException {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(filePath);
        Map<String, Object> map = new HashMap<String, Object>();
        if ((file != null) && (!file.isEmpty())) {
            String extend = file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
            if ((saveFileName == null) || (saveFileName.trim().equals(""))) {
                saveFileName = UUID.randomUUID().toString() + "." + extend;
            }
            if (saveFileName.lastIndexOf(".") < 0) {
                saveFileName = saveFileName + "." + extend;
            }
            float fileSize = Float.valueOf((float) file.getSize()).floatValue();
            List<String> errors = new ArrayList<String>();
            boolean flag = true;
            if (extendes != null) {
                for (String s : extendes) {
                    if (extend.toLowerCase().equals(s)) {
                        flag = true;
                    }
                }
            }
            if (flag) {
                File path = new File(saveFilePathName);
                if (!path.exists()) {
                    path.mkdir();
                }
                DataOutputStream out = new DataOutputStream(new FileOutputStream(saveFilePathName
                                                                                 + File.separator
                                                                                 + saveFileName));
                InputStream is = null;
                try {
                    is = file.getInputStream();
                    int size = (int) fileSize;
                    byte[] buffer = new byte[size];
                    while (is.read(buffer) > 0) {
                        out.write(buffer);
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
                if (isImg(extend)) {
                    File img = new File(saveFilePathName + File.separator + saveFileName);
                    try {
                        BufferedImage bis = ImageIO.read(img);
                        int w = bis.getWidth();
                        int h = bis.getHeight();
                        map.put("width", Integer.valueOf(w));
                        map.put("height", Integer.valueOf(h));
                    } catch (Exception localException) {
                    }
                }
                map.put("mime", extend);
                map.put("fileName", saveFileName);
                map.put("fileSize", Float.valueOf(fileSize));
                map.put("error", errors);
                map.put("oldName", file.getOriginalFilename());
            } else {
                errors.add("不允许的扩展名");
            }
        } else {
            map.put("width", Integer.valueOf(0));
            map.put("height", Integer.valueOf(0));
            map.put("mime", "");
            map.put("fileName", "");
            map.put("fileSize", Float.valueOf(0.0F));
            map.put("oldName", "");
        }
        return map;
    }

    public static boolean isImg(String extend) {
        boolean ret = false;
        List<String> list = new ArrayList<String>();
        list.add("jpg");
        list.add("jpeg");
        list.add("bmp");
        list.add("gif");
        list.add("png");
        list.add("tif");
        for (String s : list) {
            if (s.equals(extend)) {
                ret = true;
            }
        }
        return ret;
    }

    public static final void waterMarkWithImage(String pressImg, String targetImg, int pos,
                                                float alpha) {
        try {
            File _file = new File(targetImg);
            Image src = ImageIO.read(_file);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, 1);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);

            File _filebiao = new File(pressImg);
            Image src_biao = ImageIO.read(_filebiao);
            g.setComposite(AlphaComposite.getInstance(10, alpha / 100.0F));
            int width_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            int x = 0;
            int y = 0;
            if (pos == 2) {
                x = (width - width_biao) / 2;
                y = 0;
            }
            if (pos == 3) {
                x = width - width_biao;
                y = 0;
            }
            if (pos == 4) {
                x = width - width_biao;
                y = (height - height_biao) / 2;
            }
            if (pos == 5) {
                x = width - width_biao;
                y = height - height_biao;
            }
            if (pos == 6) {
                x = (width - width_biao) / 2;
                y = height - height_biao;
            }
            if (pos == 7) {
                x = 0;
                y = height - height_biao;
            }
            if (pos == 8) {
                x = 0;
                y = (height - height_biao) / 2;
            }
            if (pos == 9) {
                x = (width - width_biao) / 2;
                y = (height - height_biao) / 2;
            }
            g.drawImage(src_biao, x, y, width_biao, height_biao, null);

            g.dispose();
            FileOutputStream out = new FileOutputStream(targetImg);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(image);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean createSmall(String source, String target, int width, int height) {
        try {
            File sourceFile = new File(source);
            File targetFile = new File(target);
            BufferedImage bis = ImageIO.read(sourceFile);
            int w = bis.getWidth();
            int h = bis.getHeight();
            int nw = width;
            int nh = nw * h / w;
            ImageCompress.ImageScale(source, target, width, height);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createSmall_old(String source, String target, int width) {
        try {
            File sourceFile = new File(source);
            //File targetFile = new File(target);
            BufferedImage bis = ImageIO.read(sourceFile);
            int w = bis.getWidth();
            int h = bis.getHeight();
            int nw = width;
            int nh = nw * h / w;
            ImageScale is = new ImageScale();
            is.saveImageAsJpg(source, target, nw, nh);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean waterMarkWithText(String filePath, String outPath, String text,
                                            String markContentColor, Font font, int pos,
                                            float qualNum) {
        ImageIcon imgIcon = new ImageIcon(filePath);
        Image theImg = imgIcon.getImage();
        int width = theImg.getWidth(null);
        int height = theImg.getHeight(null);
        BufferedImage bimage = new BufferedImage(width, height, 1);
        Graphics2D g = bimage.createGraphics();
        if (font == null) {
            font = new Font("黑体", 1, 30);
            g.setFont(font);
        } else {
            g.setFont(font);
        }
        g.setColor(getColor(markContentColor));
        g.setBackground(Color.white);
        g.drawImage(theImg, 0, 0, null);
        FontMetrics metrics = new FontMetrics(font) {
        };
        Rectangle2D bounds = metrics.getStringBounds(text, null);
        int widthInPixels = (int) bounds.getWidth();
        int heightInPixels = (int) bounds.getHeight();
        int left = 0;
        int top = heightInPixels;
        if (pos == 2) {
            left = width / 2;
            top = heightInPixels;
        }
        if (pos == 3) {
            left = width - widthInPixels;
            top = heightInPixels;
        }
        if (pos == 4) {
            left = width - widthInPixels;
            top = height / 2;
        }
        if (pos == 5) {
            left = width - widthInPixels;
            top = height - heightInPixels;
        }
        if (pos == 6) {
            left = width / 2;
            top = height - heightInPixels;
        }
        if (pos == 7) {
            left = 0;
            top = height - heightInPixels;
        }
        if (pos == 8) {
            left = 0;
            top = height / 2;
        }
        if (pos == 9) {
            left = width / 2;
            top = height / 2;
        }
        g.drawString(text, left, top);
        g.dispose();
        try {
            FileOutputStream out = new FileOutputStream(outPath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
            param.setQuality(qualNum, true);
            encoder.encode(bimage, param);
            out.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean createFolder(String folderPath) {
        boolean ret = true;
        try {
            File myFilePath = new File(folderPath);
            if ((!myFilePath.exists()) && (!myFilePath.isDirectory())) {
                ret = myFilePath.mkdirs();
                if (!ret) {
                    System.out.println("创建文件夹出错");
                }
            }
        } catch (Exception e) {
            System.out.println("创建文件夹出错");
            ret = false;
        }
        return ret;
    }

    public static List toRowChildList(List list, int perNum) {
        List l = new ArrayList();
        if (list == null) {
            return l;
        }
        for (int i = 0; i < list.size(); i += perNum) {
            List cList = new ArrayList();
            for (int j = 0; j < perNum; j++) {
                if (i + j < list.size()) {
                    cList.add(list.get(i + j));
                }
            }
            l.add(cList);
        }
        return l;
    }

    public static List copyList(List list, int begin, int end) {
        List l = new ArrayList();
        if (list == null) {
            return l;
        }
        if (end > list.size()) {
            end = list.size();
        }
        for (int i = begin; i < end; i++) {
            l.add(list.get(i));
        }
        return l;
    }

    public static boolean isNotNull(Object obj) {
        if ((obj != null) && (!obj.toString().equals(""))) {
            return true;
        }
        return false;
    }

    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(oldPath);
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错 ");
            e.printStackTrace();
        }
    }

    public static boolean deleteFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (file.isFile()) {
            return deleteFile(path);
        }
        return deleteDirectory(path);
    }

    public static boolean deleteFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if ((file.isFile()) && (file.exists())) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    public static boolean deleteDirectory(String path) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        File dirFile = new File(path);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;

        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }
        if (!flag) {
            return false;
        }
        if (dirFile.delete()) {
            return true;
        }
        return false;
    }

    public static String showPageStaticHtml(String url, int currentPage, int pages) {
        StringBuilder s = new StringBuilder("");
        if (pages > 0) {
            if (currentPage >= 1) {
                s.append("<a href='" + url + "_1.htm'>首页</a> ");
                if (currentPage > 1) {
                    s.append("<a href='" + url + "_" + (currentPage - 1) + ".htm'>上一页</a> ");
                }
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                s.append("第");
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 5); j++) {
                    if (i == currentPage) {
                        s.append("<a class='this' href='").append(url).append("_").append(i)
                            .append(".htm'>").append(i).append("</a> ");
                    } else {
                        s.append("<a href='").append(url).append("_").append(i).append(".htm'>")
                            .append(i).append("</a> ");
                    }
                    i++;
                }
                s.append("页　");
            }
            if (currentPage <= pages) {
                if (currentPage < pages) {
                    s.append("<a href='" + url + "_" + (currentPage + 1) + ".htm'>下一页</a> ");
                }
                s.append("<a href='" + url + "_" + pages + ".htm'>尾页</a> ");
            }
        }
        return s.toString();
    }

    public static String showPageHtml(String url, String params, int currentPage, int pages) {
        StringBuilder s = new StringBuilder("");
        if (pages > 0) {
            if (currentPage >= 1) {
                s.append("<a href='" + url + "?currentPage=1" + params + "'>首页</a> ");
                if (currentPage > 1) {
                    s.append("<a href='" + url + "?currentPage=" + (currentPage - 1) + params
                             + "'>上一页</a> ");
                }
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                s.append("第");
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 5); j++) {
                    if (i == currentPage) {
                        s.append("<a class='this' href='" + url + "?currentPage=" + i + params
                                 + "'>" + i + "</a> ");
                    } else {
                        s.append("<a href='" + url + "?currentPage=" + i + params + "'>" + i
                                 + "</a> ");
                    }
                    i++;
                }
                s.append("页　");
            }
            if (currentPage <= pages) {
                if (currentPage < pages) {
                    s.append("<a href='" + url + "?currentPage=" + (currentPage + 1) + params
                             + "'>下一页</a> ");
                }
                s.append("<a href='" + url + "?currentPage=" + pages + params + "'>尾页</a> ");
            }
        }
        return s.toString();
    }

    public static String showPageFormHtml(int currentPage, int pages) {
        StringBuilder s = new StringBuilder("<ul>");
        if (pages > 0) {
            if (currentPage >= 1) {
                s.append("<li><span><a href='javascript:void(0);' onclick='return gotoPage(1)'>首页</a> </span></li>");
                if (currentPage > 1) {
                    s.append("<li><span><a href='javascript:void(0);' onclick='return gotoPage("
                             + (currentPage - 1) + ")'>上一页</a></span></li> ");
                }
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 6); j++) {
                    if (i == currentPage) {
                        s.append("<li><span class=\"currentpage\">" + i + "</span></li> ");
                    } else {
                        s.append("<li><span><a class\"demo\" href='javascript:void(0);' onclick='return gotoPage("
                                 + i + ")'>" + i + "</a></span></li> ");
                    }
                    i++;
                }
            }
            if (currentPage <= pages) {
                if (currentPage < pages) {
                    s.append("<li><span><a href='javascript:void(0);' onclick='return gotoPage("
                             + (currentPage + 1) + ")'>下一页</a></span></li> ");
                }
                s.append("<li><span><a href='javascript:void(0);' onclick='return gotoPage("
                         + pages + ")'>尾页</a> </span></li>");
            }
        }
        s.append("</ul>");
        return s.toString();
    }

    /**
     * 异步分页
     * @param currentPage
     * @param pages
     * @return
     */
    public static String showPageFormAjaxHtml(int currentPage, int pages) {
        StringBuilder s = new StringBuilder("<ul>");
        if (pages > 0) {
            if (currentPage >= 1) {
                s.append("<li><span><a href='javascript:void(0);' onclick='return gotoAajxPage(1)'>首页</a> </span></li>");
                if (currentPage > 1) {
                    s.append("<li><span><a href='javascript:void(0);' onclick='return gotoAajxPage("
                             + (currentPage - 1) + ")'>上一页</a></span></li> ");
                }
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 6); j++) {
                    if (i == currentPage) {
                        s.append("<li><span class=\"currentpage\">" + i + "</span></li> ");
                    } else {
                        s.append("<li><span><a class\"demo\" href='javascript:void(0);' onclick='return gotoAajxPage("
                                 + i + ")'>" + i + "</a></span></li> ");
                    }
                    i++;
                }
            }
            if (currentPage <= pages) {
                if (currentPage < pages) {
                    s.append("<li><span><a href='javascript:void(0);' onclick='return gotoAajxPage("
                             + (currentPage + 1) + ")'>下一页</a></span></li> ");
                }
                s.append("<li><span><a href='javascript:void(0);' onclick='return gotoAajxPage("
                         + pages + ")'>尾页</a> </span></li>");
            }
        }
        s.append("</ul>");
        return s.toString();
    }

    /**
     * 新订货宝商品列表 分页
     * @param currentPage
     * @param pages
     * @return
     */
    public static String showPageFormNewHtml(int currentPage, int pages) {
        StringBuilder s = new StringBuilder("<span class='pageBtnWrap'>");
        if (pages > 0) {
            if (currentPage > 1) {
                s.append("<a href='javascript:;' onclick='return gotoPage(1)' title='首页'>首页</a> ");
                s.append("<a href='javascript:;' onclick='return gotoPage(" + (currentPage - 1)
                         + ")' title='上一页'>上一页</a> ");
            } else if (currentPage == 1) {
                s.append("<span class='disabled'>首页</span>");
                s.append("<span class='disabled'>上一页</span>");
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 6); j++) {
                    if (i == currentPage) {
                        s.append("<span class='curr'>" + i + "</span> ");
                    } else {
                        s.append("<a href='javascript:;' onclick='return gotoPage(" + i
                                 + ")' title='第" + i + "页'>" + i + "</a>");
                    }
                    i++;
                }
            }
            if (currentPage < pages) {
                s.append("<a href='javascript:;'  onclick='return gotoPage(" + (currentPage + 1)
                         + ")' title='下一页'>下一页</a>");
                s.append("<a href='javascript:;'  onclick='return gotoPage(" + pages + ")' >尾页</a>");
            } else if (currentPage == pages) {
                s.append("<span class='disabled'>下一页</span>");
                s.append("<span class='disabled'>尾页</span>");
            }

        }
        s.append("</span>");
        return s.toString();
    }

    /**
     * 新订货宝
     * 订单中心  分页
     * @param currentPage
     * @param pages
     * @return
     */
    public static String showPageFormOrdersHtml(int currentPage, int pages) {
        StringBuilder s = new StringBuilder("<ul class='pagination' id='page_wrap'>");
        if (pages > 0) {
            if (currentPage > 1) {
                s.append("<li><a class='disabled page-prev' href='javascript:;' onclick='return gotoPage(1)' >首页</a></li>");
                s.append("<li><a class='disabled page-prev' onclick='return gotoPage("
                         + (currentPage - 1) + ")' href='javascript:;'>上一页</a></li>");
            } else if (currentPage == 1) {
                s.append("<li><a class='disabled page-prev'  >首页</a></li>");
                s.append("<li><a class='disabled page-prev' >上一页</a></li>");
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 6); j++) {
                    if (i == currentPage) {
                        s.append("<li><a name='pageForSerch' class='on'  >" + i + "</a></li>");
                    } else {
                        s.append("<li><a name='pageForSerch'  href='javascript:;' onclick='return gotoPage("
                                 + i + ")' title='第" + i + "页'>" + i + "</a></li>");
                    }
                    i++;
                }
            }
            if (currentPage < pages) {
                s.append("<li><a class='page-next disabled' onclick='return gotoPage("
                         + (currentPage + 1) + ")' title='下一页' href='javascript:;'>下一页 </a></li>");
                s.append("<li><a class='page-next disabled'  onclick='return gotoPage(" + pages
                         + ")' href='javascript:;'>尾页</a></li>");
            } else if (currentPage == pages) {
                s.append("<li><a class='page-next disabled' >下一页 </a></li>");
                s.append("<li><a class='page-next disabled' >尾页 </a></li>");
            }

        }
        s.append("</ul>");
        return s.toString();
    }

    public static String showPageFormAtagHtml(int currentPage, int pages) {
        StringBuilder s = new StringBuilder("");
        if (pages > 0) {
            if (currentPage >= 1) {
                s.append("<a href='javascript:void(0);' onclick='return gotoPage(1)'>首页</a> ");
                if (currentPage > 1) {
                    s.append("<a href='javascript:void(0);' onclick='return gotoPage("
                             + (currentPage - 1) + ")'>上一页</a> ");
                }
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                s.append("第　");
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 6); j++) {
                    if (i == currentPage) {
                        s.append("<a class='this' href='javascript:void(0);' onclick='return gotoPage("
                                 + i + ")'>" + i + "</a> ");
                    } else {
                        s.append("<a href='javascript:void(0);' onclick='return gotoPage(" + i
                                 + ")'>" + i + "</a> ");
                    }
                    i++;
                }
                s.append("页　");
            }
            if (currentPage <= pages) {
                if (currentPage < pages) {
                    s.append("<a href='javascript:void(0);' onclick='return gotoPage("
                             + (currentPage + 1) + ")'>下一页</a> ");
                }
                s.append("<a href='javascript:void(0);' onclick='return gotoPage(" + pages
                         + ")'>尾页</a> ");
            }
        }
        return s.toString();
    }

    public static String showPageAjaxHtml(String url, String params, int currentPage, int pages) {
        StringBuilder s = new StringBuilder("");
        if (pages > 0) {
            String address = url + "?1=1" + params;
            if (currentPage >= 1) {
                s.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address
                         + "\",1,this)'>首页</a> ");
                s.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address
                         + "\"," + (currentPage - 1) + ",this)'>上一页</a> ");
            }
            int beginPage = currentPage - 3 < 1 ? 1 : currentPage - 3;
            if (beginPage <= pages) {
                s.append("第");
                int i = beginPage;
                for (int j = 0; (i <= pages) && (j < 5); j++) {
                    if (i == currentPage) {
                        s.append("<a class='this' href='javascript:void(0);' onclick='return ajaxPage(\""
                                 + address + "\"," + i + ",this)'>" + i + "</a> ");
                    } else {
                        s.append("<a href='javascript:void(0);' onclick='return ajaxPage(\""
                                 + address + "\"," + i + ",this)'>" + i + "</a> ");
                    }
                    i++;
                }
                s.append("页　");
            }
            if (currentPage <= pages) {
                s.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address
                         + "\"," + (currentPage + 1) + ",this)'>下一页</a> ");
                s.append("<a href='javascript:void(0);' onclick='return ajaxPage(\"" + address
                         + "\"," + pages + ",this)'>尾页</a> ");
            }
        }
        return s.toString();
    }

    public static void saveIPageList2ModelAndView(String url, String staticURL, String params,
                                                  IPageList pList, ModelAndView mv) {
        if (pList != null) {
            mv.addObject("objs", pList.getResult());
            mv.addObject("totalPage", new Integer(pList.getPages()));
            mv.addObject("pageSize", Integer.valueOf(pList.getPageSize()));
            mv.addObject("rows", new Integer(pList.getRowCount()));
            mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
            mv.addObject("gotoPageHTML",
                showPageHtml(url, params, pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageFormHTML",
                showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageFormAjaxHTML",
                showPageFormAjaxHtml(pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageFormNewHtml",
                showPageFormNewHtml(pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageFormOrdersHtml",
                showPageFormOrdersHtml(pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageFormAtagHTML",
                showPageFormAtagHtml(pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageStaticHTML",
                showPageStaticHtml(staticURL, pList.getCurrentPage(), pList.getPages()));
            mv.addObject("gotoPageAjaxHTML",
                showPageAjaxHtml(url, params, pList.getCurrentPage(), pList.getPages()));
        }
    }

    public static void saveLucene2ModelAndView(String type, LuceneResult pList, ModelAndView mv) {
        if (pList != null) {
            if (type.equals("goods")) {
                mv.addObject("objs", pList.getGoods_list());
            }
            if (type.equals("store")) {
                mv.addObject("objs", pList.getStore_list());
            }
            mv.addObject("totalPage", Integer.valueOf(pList.getPages()));
            mv.addObject("pageSize", Integer.valueOf(pList.getPageSize()));
            mv.addObject("rows", Integer.valueOf(pList.getRows()));
            mv.addObject("currentPage", new Integer(pList.getCurrentPage()));
            mv.addObject("gotoPageFormHTML",
                showPageFormHtml(pList.getCurrentPage(), pList.getPages()));
        }
    }

    public static char randomChar() {
        char[] chars = { 'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'h',
                'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'L', 'm', 'M', 'n', 'N', 'o', 'O', 'p',
                'P', 'q', 'Q', 'r', 'R', 's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x',
                'X', 'y', 'Y', 'z', 'Z' };
        int index = (int) (Math.random() * 52.0D) - 1;
        if (index < 0) {
            index = 0;
        }
        return chars[index];
    }

    public static String[] splitByChar(String s, String c) {
        String[] list = s.split("\\" + c);
        return list;
    }

    public static Object requestByParam(HttpServletRequest request, String param) {
        if (!request.getParameter(param).equals("")) {
            return request.getParameter(param);
        }
        return null;
    }

    public static String substring(String s, int maxLength) {
        if (!StringUtils.hasLength(s)) {
            return s;
        }
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength) + "...";
    }

    public static String substring(String s, int startLength, int maxLength) {
        if (!StringUtils.hasLength(s)) {
            return s;
        }
        if (s.length() <= maxLength) {
            return s;
        }
        return s.substring(startLength, maxLength);
    }

    public static String substringStartEnd(String s) {
        if (!StringUtils.hasLength(s) && s.length() > 2) {
            return s;
        }
        return s.substring(1, s.length() - 1);
    }

    public static String substringo2o(String s, int start) {
        if (null != s && !"".equals(s) && s.length() > 0 && s.length() > start) {
            return s.substring(start, s.length());
        } else {
            return "";
        }
    }

    public static String substringfrom(String s, String from) {
        if (s.indexOf(from) < 0) {
            return "";
        }
        return s.substring(s.indexOf(from) + from.length());
    }

    public static BigDecimal null2BigDecimal(Object s) {
        BigDecimal v = BigDecimal.ZERO;
        if (s != null) {
            try {
                v = BigDecimal.valueOf(null2Double(s));
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static int null2Int(Object s) {
        int v = 0;
        if (s != null) {
            try {
                v = Integer.parseInt(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static float null2Float(Object s) {
        float v = 0.0F;
        if (s != null) {
            try {
                v = Float.parseFloat(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static double null2Double(Object s) {
        double v = 0.0D;
        if (s != null) {
            try {
                v = Double.parseDouble(null2String(s));
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static boolean null2Boolean(Object s) {
        boolean v = false;
        if (s != null) {
            try {
                v = Boolean.parseBoolean(s.toString());
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static String null2String(Object s) {
        String str = s == null ? "" : s.toString().trim();
        str = "null".equals(s) ? "" : str.toString();
        return str;
    }

    public static Long null2Long(Object s) {
        Long v = Long.valueOf(-1L);
        if (s != null) {
            try {
                v = Long.valueOf(Long.parseLong(s.toString()));
            } catch (Exception localException) {
            }
        }
        return v;
    }

    public static String getTimeInfo(long time) {
        int hour = (int) time / 3600000;
        long balance = time - hour * 1000 * 60 * 60;
        int minute = (int) balance / 60000;
        balance -= minute * 1000 * 60;
        int seconds = (int) balance / 1000;
        String ret = "";
        if (hour > 0) {
            ret = ret + hour + "小时";
        }
        if (minute > 0) {
            ret = ret + minute + "分";
        } else if ((minute <= 0) && (seconds > 0)) {
            ret = ret + "零";
        }
        if (seconds > 0) {
            ret = ret + seconds + "秒";
        }
        return ret;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static int indexOf(String s, String sub) {
        return s.trim().indexOf(sub.trim());
    }

    /**商品修改时候    商品属性专用  select
     * @param s
     * @param info
     * @param id
     * @return
     */
    public static int indexOfPre(String s, String info, String id) {
        return s.trim().indexOf("{\"val\":\"" + info + "\",\"id\":\"" + id + "\"");
    }

    /**商品修改时候    商品属性专用  input
     * @param s
     * @param id
     * @return
     */
    public static String getValOfPre(String s, String id) {
        List<Map<String, Object>> obj = JsonUtil.jsonArray2List(s);
        String str = "";
        for (Map<String, Object> map : obj) {
            if (null2String(map.get("id")).equals(id)) {
                str = null2String(map.get("val"));
                break;
            }
        }
        return str;
    }

    public static Map<String, Object> cal_time_space(Date begin, Date end) {
        long l = end.getTime() - begin.getTime();
        long day = l / 86400000L;
        long hour = l / 3600000L - day * 24L;
        long min = l / 60000L - day * 24L * 60L - hour * 60L;
        long second = l / 1000L - day * 24L * 60L * 60L - hour * 60L * 60L - min * 60L;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("day", Long.valueOf(day));
        map.put("hour", Long.valueOf(hour));
        map.put("min", Long.valueOf(min));
        map.put("second", Long.valueOf(second));
        return map;
    }

    public static final String randomString(int length) {
        char[] numbersAndLetters = "0123456789abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();
        if (length < 1) {
            return "";
        }
        Random randGen = new Random();
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    public static final String randomInt(int length) {
        if (length < 1) {
            return null;
        }
        Random randGen = new Random();
        char[] numbersAndLetters = "0123456789".toCharArray();

        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(10)];
        }
        return new String(randBuffer);
    }

    public static long getDateDistance(String time1, String time2) {
        long quot = 0L;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date1 = ft.parse(time1);
            Date date2 = ft.parse(time2);
            quot = date1.getTime() - date2.getTime();
            quot = quot / 1000L / 60L / 60L / 24L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return quot;
    }

    public static double div(Object a, Object b) {
        double ret = 0.0D;
        if ((!null2String(a).equals("")) && (!null2String(b).equals(""))) {
            BigDecimal e = new BigDecimal(null2String(a));
            BigDecimal f = new BigDecimal(null2String(b));
            if (null2Double(f) > 0.0D) {
                ret = e.divide(f, 3, 1).doubleValue();
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double subtract(Object a, Object b) {
        double ret = 0.0D;
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        ret = e.subtract(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double add(Object a, Object b) {
        double ret = 0.0D;
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        ret = e.add(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double mul(Object a, Object b) {
        BigDecimal e = new BigDecimal(null2Double(a));
        BigDecimal f = new BigDecimal(null2Double(b));
        double ret = e.multiply(f).doubleValue();
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(ret)).doubleValue();
    }

    public static double formatMoney(Object money) {
        DecimalFormat df = new DecimalFormat("0.00");
        return Double.valueOf(df.format(money)).doubleValue();
    }

    public static int M2byte(float m) {
        float a = m * 1024.0F * 1024.0F;
        return (int) a;
    }

    public static boolean convertIntToBoolean(int intValue) {
        return intValue != 0;
    }

    public static String getURL(HttpServletRequest request) {
        String contextPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
        String url = "http://" + request.getServerName();
        String requestURI = request.getRequestURI();
        String str1 = "";
        if (requestURI.contains("/store/")
            && ("http://localhost".equals(url) || isIp(request.getServerName()) || "http://manager.sway365.com"
                .equals(url))) {
            String[] split = requestURI.split("/");
            if (split != null && split.length >= 3) {
                str1 = "/" + split[1] + "/" + split[2];
            }
        }

        if (null2Int(Integer.valueOf(request.getServerPort())) != 80) {
            url = url + ":" + null2Int(Integer.valueOf(request.getServerPort())) + contextPath
                  + str1;
        } else {
            url = url + contextPath + str1;
        }
        return url;
    }

    private static final Whitelist user_content_filter = Whitelist.relaxed();

    static {
        user_content_filter.addTags(new String[] { "embed", "object", "param", "span", "div",
            "font" });
        user_content_filter.addAttributes(":all", new String[] { "style", "class", "id", "name" });
        user_content_filter.addAttributes("object", new String[] { "width", "height", "classid",
            "codebase" });
        user_content_filter.addAttributes("param", new String[] { "name", "value" });
        user_content_filter.addAttributes("embed", new String[] { "src", "quality", "width",
            "height", "allowFullScreen", "allowScriptAccess", "flashvars", "name", "type",
            "pluginspage" });
    }

    public static String filterHTML(String content) {
        //Whitelist whiteList = new Whitelist();
        String s = Jsoup.clean(content, user_content_filter);
        return s;
    }

    public static int parseDate(String type, Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (type.equals("y")) {
            return cal.get(1);
        }
        if (type.equals("M")) {
            return cal.get(2) + 1;
        }
        if (type.equals("d")) {
            return cal.get(5);
        }
        if (type.equals("H")) {
            return cal.get(11);
        }
        if (type.equals("m")) {
            return cal.get(12);
        }
        if (type.equals("s")) {
            return cal.get(13);
        }
        return 0;
    }

    public static int[] readImgWH(String imgurl) {
        boolean b = false;
        try {
            URL url = new URL(imgurl);

            BufferedInputStream bis = new BufferedInputStream(url.openStream());

            byte[] bytes = new byte[100];

            OutputStream bos = new FileOutputStream(new File("C:\\thetempimg.gif"));
            int len;
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            bis.close();
            bos.flush();
            bos.close();

            b = true;
        } catch (Exception e) {
            b = false;
        }
        int[] a = new int[2];
        if (b) {
            File file = new File("C:\\thetempimg.gif");
            BufferedImage bi = null;
            boolean imgwrong = false;
            try {
                bi = ImageIO.read(file);
                try {
                    //int i = bi.getType();
                    imgwrong = true;
                } catch (Exception e) {
                    imgwrong = false;
                }
                if (!imgwrong) {
                    file.delete();
                    return null;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            a[0] = bi.getWidth();
            a[1] = bi.getHeight();
            file.delete();
        } else {
            a = null;
        }
        return a;
    }

    public static boolean del_acc(HttpServletRequest request, Accessory acc) {
        boolean ret = true;
        boolean ret1 = true;
        if (acc != null) {
            String path = request.getRealPath("/") + acc.getPath() + File.separator + acc.getName();
            String small_path = request.getRealPath("/") + acc.getPath() + File.separator
                                + acc.getName() + "_small." + acc.getExt();
            ret = deleteFile(path);
            ret1 = deleteFile(small_path);
        }
        return (ret) && (ret1);
    }

    public static boolean fileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int splitLength(String s, String c) {
        int v = 0;
        if (!s.trim().equals("")) {
            v = s.split(c).length;
        }
        return v;
    }

    static int totalFolder = 0;
    static int totalFile   = 0;

    public static double fileSize(File folder) {
        totalFolder += 1;

        long foldersize = 0L;
        File[] filelist = folder.listFiles();
        if (filelist == null)
            return 0d;
        for (int i = 0; i < filelist.length; i++) {
            if (filelist[i].isDirectory()) {
                foldersize = (long) (foldersize + fileSize(filelist[i]));
            } else {
                totalFile += 1;
                foldersize += filelist[i].length();
            }
        }
        return div(Long.valueOf(foldersize), Integer.valueOf(1024));
    }

    public static int fileCount(File file) {
        if (file == null) {
            return 0;
        }
        if (!file.isDirectory()) {
            return 1;
        }
        int fileCount = 0;
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isFile()) {
                fileCount++;
            } else if (f.isDirectory()) {
                fileCount++;
                fileCount += fileCount(file);
            }
        }
        return fileCount;
    }

    public static String get_all_url(HttpServletRequest request) {
        String query_url = request.getRequestURI();
        if ((request.getQueryString() != null) && (!request.getQueryString().equals(""))) {
            query_url = query_url + "?" + request.getQueryString();
        }
        return query_url;
    }

    public static Color getColor(String color) {
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if (color.length() != 6) {
            return null;
        }
        try {
            int r = Integer.parseInt(color.substring(0, 2), 16);
            int g = Integer.parseInt(color.substring(2, 4), 16);
            int b = Integer.parseInt(color.substring(4), 16);
            return new Color(r, g, b);
        } catch (NumberFormatException nfe) {
        }
        return null;
    }

    public static Set<Integer> randomInt(int a, int length) {
        Set<Integer> list = new TreeSet<Integer>();
        int size = length;
        if (length > a) {
            size = a;
        }
        while (list.size() < size) {
            Random random = new Random();
            int b = random.nextInt(a);
            list.add(Integer.valueOf(b));
        }
        return list;
    }

    public static Double formatDouble(Object obj, int len) {
        //Double ret = Double.valueOf(0.0D);
        String format = "0.0";
        for (int i = 1; i < len; i++) {
            format = format + "0";
        }
        DecimalFormat df = new DecimalFormat(format);
        return Double.valueOf(df.format(obj));
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if ((ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
            || (ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
            || (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
            || (ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
            || (ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
            || (ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS)) {
            return true;
        }
        return false;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0.0F;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count += 1.0F;
                    System.out.print(c);
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4D) {
            return true;
        }
        return false;
    }

    public static String trimSpaces(String IP) {
        while (IP.startsWith(" ")) {
            IP = IP.substring(1, IP.length()).trim();
        }
        while (IP.endsWith(" ")) {
            IP = IP.substring(0, IP.length() - 1).trim();
        }
        return IP;
    }

    public static boolean isIp(String IP) {
        boolean b = false;
        IP = trimSpaces(IP);
        if (IP.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
            String[] s = IP.split("\\.");
            if ((Integer.parseInt(s[0]) < 255) && (Integer.parseInt(s[1]) < 255)
                && (Integer.parseInt(s[2]) < 255) && (Integer.parseInt(s[3]) < 255)) {
                b = true;
            }
        }
        return b;
    }

    public static String generic_domain(HttpServletRequest request) {
        String system_domain = "localhost";
        String serverName = request.getServerName();
        if (isIp(serverName)) {
            system_domain = serverName;
        } else {
            system_domain = serverName.substring(serverName.indexOf(".") + 1);
        }
        return system_domain;
    }

    public static String getthekey(String l, int n) {
        if (!"".equals(l) && null != l) {
            return l.split(",")[n];
        }
        return "";

    }

    public static String compareId(List<GoodsSpecProperty> spec) {
        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < spec.size(); i++) {
            list.add(spec.get(i).getId());
        }
        String str1 = "";
        if (list != null && list.size() > 0) {
            Long[] str = (Long[]) list.toArray(new Long[list.size()]);
            Arrays.sort(str);
            for (int i = 0; i < str.length; i++) {
                str1 += str[i] + "_";
            }
        }
        return str1;

    }

    public static String formatMum(Integer num) {
        if (num != null) {
            if (num > 10000) {
                BigDecimal b = new BigDecimal(num);
                BigDecimal decimal = b.divide(new BigDecimal(10000)).setScale(2,
                    BigDecimal.ROUND_HALF_UP);
                return decimal.doubleValue() + "万";
            } else {
                return num + "";
            }
        } else {
            return "0";
        }

    }

    public static boolean isNullOrEmpty(Object obj) {
        return !isNotNull(obj);
    }

    /**
     * 获取本月一号
     * @return
     */
    public static Calendar getThisMonth() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }

    public static Date formatDateStrToLongDate(String s) {
        Date d = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            d = dateFormat.parse(s);
        } catch (Exception localException) {
        }
        return d;
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     * 
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    public static String getTimestamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
        * MD5加密
        * @param input
        * @return
        */
    public static String MD5(String input) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(input.getBytes());
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < md.length; i++) {
                String shaHex = Integer.toHexString(md[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String str = "";
        //3459774240FA3F2AE921B57E616E2866
        //FFF3C9D3353B383A2CBB37FFC1DAFC1D
        //FFF3C9D3353B383A2CBB37FFC1DAFC1D
        String sign = MD5(str).toUpperCase();
        System.out.println(sign);
    }

    /**
        * 生成统一下单签名
        * @param ufo
        * @param secret
        * @return
        */
    public static <T> String generateSign(T ufo, String secret, Class cls) {

        String sign = "";
        StringBuffer sb = new StringBuffer();
        Field[] fields = cls.getDeclaredFields();
        Map<String, Object> map = Maps.newHashMap();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object o = null;
            try {
                o = field.get(ufo);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!name.equals("sign") && null != o) {
                map.put(name, o);
            }
        }

        if (!map.isEmpty()) {
            Collection<String> keyset = map.keySet();
            List<String> list = new ArrayList<String>(keyset);
            Collections.sort(list);
            for (String s : list) {
                sb.append(s);
                sb.append("=");
                sb.append(map.get(s).toString());
                sb.append("&");
            }
            sb.append("key=");
            sb.append(secret);
            System.out.println("生成统一下单签名sign");

            String source = sb.toString();
            System.out.println("##" + source + "##");
            sign = MD5(source).toUpperCase();
            System.out.println(sign);
        }

        return sign;
    }

    public static String generatePaySign(Map<String, Object> map, String mchSecret) {

        String sign = "";
        StringBuffer sb = new StringBuffer();

        if (!map.isEmpty()) {
            Collection<String> keyset = map.keySet();
            List<String> list = new ArrayList<String>(keyset);
            Collections.sort(list);
            for (String s : list) {
                sb.append(s);
                sb.append("=");
                sb.append(map.get(s).toString());
                sb.append("&");
            }
            sb.append("key=");
            sb.append(mchSecret);
            String s = sb.toString();
            sign = CommUtil.MD5(s).toUpperCase();
        }

        return sign;
    }

    /**
       * 生成随机字符串
       * @return
       */
    public static String generateNonce_str() {
        String s = UUID.randomUUID().toString();
        s = s.replaceAll("\\-", "");
        return s;
    }

    /**
     * SHA1加密
     * @param decript
     * @return
     */
    public static String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**处理json字符串 返回指定key的值  获取供应商名称
     * @param s
     * @param id
     * @return
     */
    public static String getValByKeyFromJsonStr(String key, String json) {
        String str = "";
        if (CommUtil.isNullOrEmpty(key) || CommUtil.isNullOrEmpty(json)) {
            return "";
        }
        Map<String, Object> map = JsonUtil.json2Map(json);
        str = (String) map.get(key);
        return str;
    }
    /**
     * 将对象转换成json字符串,如果转换失败则返回null
     * @param o 需要转换为json的对象
     * @return String 转换后的json字符串
     *
     *
     * */
    public static String write2JsonStr(Object o) {
    	 ObjectMapper mapper = new ObjectMapper();
        String jsonStr = "";
        try {
            jsonStr = mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return jsonStr;
    }
    /**
     * 
     * @param rmg
     * @param result
     * @param msg
     * @return
     */
    
    public static ResultMsg setResultMsgData(ResultMsg rmg, boolean result, String msg) {
        if (rmg == null) {
            rmg = new ResultMsg();
        }
        rmg.setResult(result);
        rmg.setMsg(msg);
        return rmg;
    }
   
    //将集合里的元素按照 传递的分隔符来分隔，生成字符串
    public static String makeListToString(List<String> list, String separChar) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0, len = list.size(); i < len; i++) {
                sb.append(list.get(i));
                if (i < len - 1) {
                    sb.append(separChar);
                }
            }
        }
        return sb.toString();
    }
}
