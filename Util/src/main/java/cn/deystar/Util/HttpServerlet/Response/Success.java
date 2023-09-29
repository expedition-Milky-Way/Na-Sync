package cn.deystar.Util.HttpServerlet.Response;

/**
 * @Author YeungLuhyun
 **/
public class Success extends ResponseData {


    public Success(String message, Object data) {
        super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data, null);
    }

    public Success(Object data) {
        super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data, null);
    }

    public Success() {
        this(null);
    }


    public Success(String message, Object data, String api) {
        super(true, DEFAULT_SUCCESS_CODE, message, data, api);
        this.jump = api;
    }


}
