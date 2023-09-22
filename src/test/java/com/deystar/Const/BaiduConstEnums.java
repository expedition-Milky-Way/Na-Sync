package com.deystar.Const;

/**
 * @Author YeungLuhyun
 **/
public enum BaiduConstEnums {

    SVIP(3, 21474836480L),

    VIP(2, 10737418240L),

    NORMAL(0, 4294967296L);

    public Integer code;
    public Long size;

    BaiduConstEnums(Integer code, Long size) {
        this.code = code;
        this.size = size;
    }




}
