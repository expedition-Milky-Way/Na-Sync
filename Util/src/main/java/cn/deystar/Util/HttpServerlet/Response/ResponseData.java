package cn.deystar.Util.HttpServerlet.Response;

import com.alibaba.fastjson.JSONObject;

public class ResponseData {

    public static Integer DEFAULT_SUCCESS_CODE = 200;
    public static String DEFAULT_SUCCESS_MESSAGE = "请求成功";

    public static Integer DEFAULT_ERROR_CODE = 500;

    public static String DEFAULT_ERROR_MESSAGE = "请求失败";


    public Boolean success;

    public Integer code;

    public String message;

    public Object data;

    public String jump;

    public ResponseData(Boolean success,Integer code, String message, Object data,String api) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.jump = api;
    }




    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }


    public static Integer getDefaultSuccessCode() {
        return DEFAULT_SUCCESS_CODE;
    }

    public static void setDefaultSuccessCode(Integer defaultSuccessCode) {
        DEFAULT_SUCCESS_CODE = defaultSuccessCode;
    }

    public static String getDefaultSuccessMessage() {
        return DEFAULT_SUCCESS_MESSAGE;
    }

    public static void setDefaultSuccessMessage(String defaultSuccessMessage) {
        DEFAULT_SUCCESS_MESSAGE = defaultSuccessMessage;
    }

    public static Integer getDefaultErrorCode() {
        return DEFAULT_ERROR_CODE;
    }

    public static void setDefaultErrorCode(Integer defaultErrorCode) {
        DEFAULT_ERROR_CODE = defaultErrorCode;
    }

    public static String getDefaultErrorMessage() {
        return DEFAULT_ERROR_MESSAGE;
    }

    public static void setDefaultErrorMessage(String defaultErrorMessage) {
        DEFAULT_ERROR_MESSAGE = defaultErrorMessage;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getJump() {
        return jump;
    }

    public void setJump(String jump) {
        this.jump = jump;
    }
}
