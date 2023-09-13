package com.deystar.CustomException.Size;

/**
 * @Author YeungLuhyun
 **/
public enum SizeExceptionEnums {
    CANT_NOT_FIND_VIP_TYPE("没有这个会员类型"),
    NON_SIZE("无法确定压缩文件大小");
    String message;

    SizeExceptionEnums(String message) {
        this.message = message;
    }
}
