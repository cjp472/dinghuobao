package com.javamalls.base.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;

/**
 * POI操作工具类
 * 
 * @author zhaihl
 */
public class POIUtil {
    private POIUtil() {
    }

    /**
     * 文件名分隔符
     */
    private static String SEPARATOR = File.separator;

    /**
     * 以给定的文件路径创建一个新的Excel文件
     * 
     * @param filepath
     *            相对于工程的文件路径
     * @return
     * @throws IOException
     */
    public static File createExcelFile(String excelTemplatePath, String filepath)
                                                                                 throws IOException {

        // 获取到文件的父目录
        String path = POIUtil.getFilePath(filepath);

        File file = new File(path);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        // 复制模板
        FileOutputStream fos = null;
        InputStream fis = null;
        byte[] bytes = new byte[1024];
        int len = 0;
        try {
            fos = new FileOutputStream(file);
            fis = POIUtil.class.getResourceAsStream(excelTemplatePath);
            while ((len = fis.read(bytes)) > 0) {
                fos.write(bytes, 0, len);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return file;
    }

    /**
     * 上下文路径
     */
    public static String getFilePath(String filepath) {
        if (filepath.startsWith(SEPARATOR)) {
            throw new IllegalArgumentException("【" + filepath + "】不是一个有效的路径");
        }
        return POIUtil.class.getResource("/").getPath() + filepath;
    }

    /**
     * 设置第一行标题
     * 
     * @param attendance
     * @param sheet
     */
    public static void setTitle(String title, HSSFWorkbook work, int i) {
        work.getSheetAt(i).getRow(0).getCell(0)
            .setCellValue(new HSSFRichTextString(String.valueOf(title)));
    }

    /**
     * 设置第一、二行标题
     * 
     * @param attendance
     * @param sheet
     */
    public static void setTitle(SXSSFSheet sheet, String titleName, String[] titleNames) {
        CellStyle titleCellStyle = sheet.getWorkbook().createCellStyle();
        // 设置文本居中对齐  
        titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        // 定义标题字体  
        Font titleFont = sheet.getWorkbook().createFont();
        // 字体名称  
        titleFont.setFontName("微软雅黑");
        titleCellStyle.setFont(titleFont);

        Row titleRow = sheet.createRow(0);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue(titleName);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleNames.length));

        titleRow = sheet.createRow(1);
        for (int i = 0; i < titleNames.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(titleNames[i]);
        }
    }

    /**
     * 设置第一、二、三行标题
     * 
     * @param attendance
     * @param sheet
     */
    public static void setTitle(SXSSFSheet sheet, String titleName, String row2Info,
                                String[] titleNames) {
        CellStyle titleCellStyle = sheet.getWorkbook().createCellStyle();
        // 设置文本居中对齐  
        titleCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        // 定义标题字体  
        Font titleFont = sheet.getWorkbook().createFont();
        // 字体名称  
        titleFont.setFontName("微软雅黑");
        titleCellStyle.setFont(titleFont);

        int rowIndex = -1;
        Row titleRow = sheet.createRow(++rowIndex);
        Cell cell = titleRow.createCell(0);
        cell.setCellValue(titleName);
        cell.setCellStyle(titleCellStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, titleNames.length));

        if (CommUtil.isNotNull(row2Info)) {

            CellStyle titleCellStyle2 = sheet.getWorkbook().createCellStyle();
            titleCellStyle2.setAlignment(CellStyle.ALIGN_LEFT);
            titleCellStyle2.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            titleCellStyle2.setWrapText(true);
            Font titleFont2 = sheet.getWorkbook().createFont();
            titleFont2.setFontName("宋体");
            titleFont2.setColor(HSSFColor.RED.index);
            titleCellStyle2.setFont(titleFont2);

            titleRow = sheet.createRow(++rowIndex);
            titleRow.setHeightInPoints(5 * sheet.getDefaultRowHeightInPoints());
            cell = titleRow.createCell(0);
            cell.setCellValue(row2Info);
            cell.setCellStyle(titleCellStyle2);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, titleNames.length));
        }

        titleRow = sheet.createRow(++rowIndex);
        for (int i = 0; i < titleNames.length; i++) {
            cell = titleRow.createCell(i);
            cell.setCellValue(titleNames[i]);
        }
    }

    /**
     * 获取单元格样式
     * 
     * @param sheet
     * @return
     */
    public static HSSFCellStyle getCellStyle(HSSFWorkbook workbook, Map<String, Object> param) {
        HSSFCellStyle style = workbook.createCellStyle();

        // 字体
        HSSFFont font = workbook.createFont();
        if (param.get("fontName") != null) {
            font.setFontName((String) param.get("fontName"));
        }
        if (param.get("fontHeight") != null)
            font.setFontHeight((Short) param.get("fontHeight"));
        style.setFont(font);

        // 设置边框
        if (param.get("borderLeft") != null)
            style.setBorderLeft((Short) param.get("borderLeft"));
        if (param.get("borderRight") != null)
            style.setBorderRight((Short) param.get("borderRight"));
        if (param.get("borderTop") != null)
            style.setBorderTop((Short) param.get("borderTop"));
        if (param.get("borderBottom") != null)
            style.setBorderBottom((Short) param.get("borderBottom"));

        // 设置居中
        if (param.get("alignment") != null)
            style.setAlignment((Short) param.get("alignment"));
        if (param.get("verticalAlignment") != null)
            style.setVerticalAlignment((Short) param.get("verticalAlignment"));

        return style;
    }

    /**
     * 获取单元格样式
     * 
     * @param sheet
     * @return
     */
    public static CellStyle getCellStyle(Workbook workbook, Map<String, Object> param) {
        CellStyle style = workbook.createCellStyle();

        // 字体
        Font font = workbook.createFont();
        if (param.get("fontName") != null) {
            font.setFontName((String) param.get("fontName"));
        }
        if (param.get("fontHeight") != null)
            font.setFontHeight((Short) param.get("fontHeight"));
        style.setFont(font);

        // 设置边框
        if (param.get("borderLeft") != null)
            style.setBorderLeft((Short) param.get("borderLeft"));
        if (param.get("borderRight") != null)
            style.setBorderRight((Short) param.get("borderRight"));
        if (param.get("borderTop") != null)
            style.setBorderTop((Short) param.get("borderTop"));
        if (param.get("borderBottom") != null)
            style.setBorderBottom((Short) param.get("borderBottom"));

        // 设置居中
        if (param.get("alignment") != null)
            style.setAlignment((Short) param.get("alignment"));
        if (param.get("verticalAlignment") != null)
            style.setVerticalAlignment((Short) param.get("verticalAlignment"));

        return style;
    }

    /**
     * 计算某列，从哪行到哪行连续数据的和
     * */
    public static int calcCount(int col, int sRow, int eRow, HSSFSheet sheet) {
        int sum = 0;
        for (int i = sRow; i <= eRow; i++) {
            if (sheet.getRow(i) != null && sheet.getRow(i).getCell(col) != null) {
                int j = (int) sheet.getRow(i).getCell(col).getNumericCellValue();

                sum += j;
            }
        }
        return sum;
    }

    /**
     * 设置单元格样式
     * 
     * @param cell
     * @param wb
     * @param style
     */
    public static void setCellStyle(HSSFCell cell, HSSFWorkbook wb, Map<String, Object> style) {
        cell.setCellStyle(getCellStyle(wb, style));
    }

    /**
     * 设置单元格样式
     * 
     * @param cell
     * @param wb
     * @param style
     */
    public static void setCellStyle(Cell cell, Workbook wb, Map<String, Object> style) {
        cell.setCellStyle(getCellStyle(wb, style));
    }
}
