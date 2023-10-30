package cn.deystar.Const;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 压缩任务状态枚举
 */
public enum CompressStatus {

    WAIT_FOR(0, "等待压缩"),

    COMPRESSING(1, "正在压缩"),

    SUCCESS(200, "压缩完成"),

    ERROR(500, "压缩失败");

    public Integer code;
    public String message;

    CompressStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
