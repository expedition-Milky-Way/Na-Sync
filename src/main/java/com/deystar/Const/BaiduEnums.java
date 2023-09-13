package com.deystar.Const;

/**
 * @Author YeungLuhyun
 **/
public interface BaiduEnums {


    /***
     * 通过枚举获取对应的上传文件大小
     * @Author YeungLuhyun
     * @param message 枚举：NORMAL非会员，VIP普通会员,SVIP超级会员
     * @return BaiduConstEnums.enums
     * @warning 如果没有该枚举，将会返回null
     */
    BaiduConstEnums getVipType(String message);


}
