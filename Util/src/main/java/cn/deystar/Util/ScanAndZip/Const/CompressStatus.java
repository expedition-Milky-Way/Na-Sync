package cn.deystar.Util.ScanAndZip.Const;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 压缩任务状态枚举
 */
public enum CompressStatus {

    WAIT_FOR(0, "等待压缩"),

    WAIT_UPLOAD(2,"等待上传"),

    UPLOADING(3,"正在上传"),

    UPLOAD_SUCCESS(200,"上传成功"),

    UPLOAD_ERROR(300,"上传失败"),


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
