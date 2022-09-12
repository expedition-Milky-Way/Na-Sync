package com.example.baidusync.Util;

import lombok.Data;

@Data
public class ResponseData {

    public static Integer DEFAULT_SUCCESS_CODE = 200;
    public static String DEFAULT_SUCCESS_MESSAGE = "请求成功";

    public static Integer DEFAULT_ERROR_CODE = 500;

    public static String DEFAULT_ERROR_MESSAGE = "请求失败";


    public Boolean success;

    public Integer code;

    public String message;

    public Object data;

    public ResponseData(Boolean success,Integer code, String message, Object data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseData() {
        this.success = true;
        this.code= DEFAULT_SUCCESS_CODE;
        this.message=DEFAULT_SUCCESS_MESSAGE;
    }

    public ResponseData(Object data){
        this.success = true;
        this.code = DEFAULT_SUCCESS_CODE;
        this.message = DEFAULT_SUCCESS_MESSAGE;
        this.data = data;
    }

    public ResponseData(String message){
        this.success = false;
        this.code = DEFAULT_ERROR_CODE;
        this.message = message;
    }
}
