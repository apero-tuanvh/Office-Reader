/*
 * 文件名称:          MainConstant.java
 *  
 * 编译器:            android2.2
 * 时间:              下午2:29:36
 */
package com.apero.reader.office.constant;


/**
 * 总控常量
 * <p>
 * <p>
 * Read版本:        Read V1.0
 * <p>
 * 作者:            梁金晶
 * <p>
 * 日期:            2011-10-27
 * <p>
 * 负责人:          梁金晶
 * <p>
 * 负责小组:         
 * <p>
 * <p>
 */
public final class MainConstant
{
    // word应用
    public static final byte APPLICATION_TYPE_WP = 0;
    // excel应用
    public static final byte APPLICATION_TYPE_SS = 1;
    // powerpoint应用
    public static final byte APPLICATION_TYPE_PPT = 2;
    // pdf应用
    public static final byte APPLICATION_TYPE_PDF = 3;
    // text 应用
    public static final byte APPLICATION_TYPE_TXT = 4;
    // doc文档格式
    public static final String FILE_TYPE_DOC = "doc";
    // docx文档格式
    public static final String FILE_TYPE_DOCX = "docx";
    // xls文档格式
    public static final String FILE_TYPE_XLS = "xls";
    // xlsx文档格式
    public static final String FILE_TYPE_XLSX = "xlsx";
    // ppt文档格式
    public static final String FILE_TYPE_PPT = "ppt";
    // pptx文档格式
    public static final String FILE_TYPE_PPTX = "pptx";
    // txt文档格式
    public static final String FILE_TYPE_TXT = "txt";
    //
    public static final String  FILE_TYPE_PDF = "pdf";
    
    public static final String FILE_TYPE_DOT = "dot";
    public static final String FILE_TYPE_DOTX = "dotx";
    public static final String FILE_TYPE_DOTM = "dotm";
    public static final String FILE_TYPE_XLT = "xlt";
    public static final String FILE_TYPE_XLTX = "xltx";
    public static final String FILE_TYPE_XLSM = "xlsm";
    public static final String FILE_TYPE_XLTM = "xltm";
    public static final String FILE_TYPE_POT = "pot";
    public static final String FILE_TYPE_PPTM = "pptm";
    public static final String FILE_TYPE_POTX = "potx";
    public static final String FILE_TYPE_POTM = "potm";

    public static final String INTENT_FILED_FILE = "file";
    public static final String INTENT_FILED_FILE_URI = "fileUri";
    public static final String INTENT_FILED_FILE_NAME = "fileName";

    /* ============ Activity之间Intent传值字段名称 */
    // 文件路径
    public static final String INTENT_FILED_FILE_PATH = "filePath";
    // 文件列表类型
    public static final String INTENT_FILED_FILE_LIST_TYPE = "fileListType";
    // 标星文档
    public static final String INTENT_FILED_MARK_FILES = "markFiles";
    // 最近打开的文档
    public static final String INTENT_FILED_RECENT_FILES = "recentFiles";
    // sdcard文档
    public static final String INTENT_FILED_SDCARD_FILES = "sdcard";
    // 文档标星状态
    public static final String INTENT_FILED_MARK_STATUS = "markFileStatus";
    
    
    
    /* ======= 以下定义组件的高度 ========== */
    // 组件之前的问隙
    public static final int GAP = 5;
    //
    public static final int ZOOM_ROUND = 10000000;
    
    
    /* ======= 以下定义一些视图公用常量 ========= */
    // Points DPI (72 pixels per inch)
    public static final float POINT_DPI = 72.0f;
    //厘米到磅
    public static final float MM_TO_POINT = 2.835f;
    // 缇到磅
    public static final float TWIPS_TO_POINT = 1 / 20.0f;
    // 磅到缇
    public static final float POINT_TO_TWIPS = 20.0f;
    // default tab width, 21磅
    public static final float DEFAULT_TAB_WIDTH_POINT = 21;
    //
    public static final int EMU_PER_INCH = 914400;    
    // Pixels DPI (96 pixels per inch)
    public static final float PIXEL_DPI =  96.f;
    // 磅到像素
    public static final float POINT_TO_PIXEL = PIXEL_DPI / POINT_DPI;
    // 像素到磅
    public static final float PIXEL_TO_POINT = POINT_DPI / PIXEL_DPI;
    // 缇到像素
    public static final float TWIPS_TO_PIXEL = TWIPS_TO_POINT * POINT_TO_PIXEL;
    // 像素到缇
    public static final float PIXEL_TO_TWIPS = PIXEL_TO_POINT * POINT_TO_TWIPS;
    //// default tab width, 21磅
    public static final float DEFAULT_TAB_WIDTH_PIXEL = DEFAULT_TAB_WIDTH_POINT * POINT_TO_PIXEL;



    /* ============ 数据库中表名*/
    // recently opened files
    public static final String TABLE_RECENT = "openedfiles";
    // starred files
    public static final String TABLE_STAR = "starredfiles";
    // settings
    public static final String TABLE_SETTING ="settings";
    
    /* =========== 文件解析过程中message类型 =========*/
    // 文档解析成功+
    public static final int HANDLER_MESSAGE_SUCCESS = 0;
    // 文档解析失败
    public static final int HANDLER_MESSAGE_ERROR = HANDLER_MESSAGE_SUCCESS + 1;
    // 显示进度条对话框 
    public static final int HANDLER_MESSAGE_SHOW_PROGRESS = HANDLER_MESSAGE_ERROR + 1;
    // 关闭进度条对话框
    public static final int HANDLER_MESSAGE_DISMISS_PROGRESS = HANDLER_MESSAGE_SHOW_PROGRESS + 1;
    // 释放内存
    public static final int HANDLER_MESSAGE_DISPOSE = HANDLER_MESSAGE_DISMISS_PROGRESS + 1;
    // 传递IReader实例
    public static final int HANDLER_MESSAGE_SEND_READER_INSTANCE = HANDLER_MESSAGE_DISPOSE;
    
    //zoom
    public static final int STANDARD_RATE = 10000;
    public static final int MAXZOOM = 30000;
    public static final int MAXZOOM_THUMBNAIL = 5000;
    
    // Drawing mode
    //not callout mode
    public static final int DRAWMODE_NORMAL = 0;
    //draw callout
    public static final int DRAWMODE_CALLOUTDRAW = 1;
    //erase callout
    public static final int DRAWMODE_CALLOUTERASE = 2;
}
