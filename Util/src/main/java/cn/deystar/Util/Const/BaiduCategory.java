package cn.deystar.Util.Const;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 百度网盘资源类型
 */
public enum BaiduCategory {


    VIDEO(1,"视频",""),
    MOVIE(2,"音频","static/icon/MOVIE.png"),
    PICTURE(3,"图片",""),
    DOCUMENTS(4,"文档","static/icon/DOCUMENTS.png"),

    APPLICATION(5,"应用","static/icon/ANOTHER.png"),

    ANOTHER(6,"其他","static/icon/ANOTHER.png"),

    TOR(7,"种子",""),

    HAS_FILE_FOLDER(-1,"有文件的文件夹","static/icon/HASFILE_FOLDER.png"),
    NON_FILE_FOLDER(-2,"无文件文件夹","static/icon/NONFILE_FOLDER.png");

    public Integer category;

    public String detail;

    public String img;

    BaiduCategory(Integer category, String detail,String img) {
        this.category = category;
        this.detail = detail;
        this.img = img;
    }
}
