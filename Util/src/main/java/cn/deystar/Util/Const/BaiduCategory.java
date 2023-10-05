package cn.deystar.Util.Const;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 百度网盘资源类型
 */
public enum BaiduCategory {


    VIDEO(1,"视频"),
    MOVIE(2,"音频"),
    PICTURE(3,"图片"),
    DOCUMENTS(4,"文档"),

    APPLICATION(5,"文档"),

    ANOTHER(6,"其他"),

    TOR(7,"种子");

    public Integer category;

    public String detail;

    BaiduCategory(Integer category, String detail) {
        this.category = category;
        this.detail = detail;
    }
}
