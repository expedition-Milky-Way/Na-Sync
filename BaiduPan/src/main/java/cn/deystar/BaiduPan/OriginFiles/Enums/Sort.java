package cn.deystar.BaiduPan.OriginFiles.Enums;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public enum Sort{

    CHANGED_TIME_AUGMENT(1,"按照修改日期递增"),

    CHANGED_TIME_REDUCE(-1,"按照修改日期抵减"),

    SIZE_AUGMENT(2,"按照大小递增"),

    SIZE_REDUCE(-2,"按照大小递减");

    /**
     * 方法
     */
    public int method;

    /**
     * 详情
     */
    public String detail;

    Sort(int method,String detail){
        this.method = method;
        this.detail = detail;
    }
}