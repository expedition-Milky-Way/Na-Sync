package com.deystar.CustomException.Path;

/**
 * @Author YeungLuhyun
 **/
public enum PathExceptionEnums {

    NULL_PATH("请输入文件夹路径！"),

    HAS_NOT_DIR("没有该路径！");

    String msg;

    PathExceptionEnums(String message){
        this.msg = message;
    }


}
