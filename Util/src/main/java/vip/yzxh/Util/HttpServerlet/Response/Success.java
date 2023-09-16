package vip.yzxh.Util.HttpServerlet.Response;

/**
 * @Author YeungLuhyun
 **/
public class Success extends ResponseData {


    public Success(String message, Object data) {
        super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public Success(Object data) {
        super(true, DEFAULT_SUCCESS_CODE, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public Success() {
        this(null);
    }


    public Success(String message, Object data, String api) {
        super(true, DEFAULT_SUCCESS_CODE, message, data);
        this.jump = api;
    }


}
