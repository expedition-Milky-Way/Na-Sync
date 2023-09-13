package com.deystar.Const;

/**
 * @Author YeungLuhyun
 **/
public class BaiduEnumsService  implements BaiduEnums{
    /***
     * 通过枚举获取对应的上传文件大小
     * @param message
     * @return
     */
    @Override
    public BaiduConstEnums getVipType(String message) {
        message = message.toUpperCase();
        for (BaiduConstEnums item : BaiduConstEnums.values()) {

            if (item.name().toUpperCase().equals(message)) {
                return item;
            }
        }
        return null;
    }
}
