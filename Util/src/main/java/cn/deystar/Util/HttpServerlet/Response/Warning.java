package cn.deystar.Util.HttpServerlet.Response;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class Warning extends ResponseData {

    public Warning(String message) {
        super(false, DEFAULT_ERROR_CODE, message, null, null);
    }


    public Warning(String message, String api) {
        super(false, DEFAULT_ERROR_CODE, message, null, api);
    }
}
