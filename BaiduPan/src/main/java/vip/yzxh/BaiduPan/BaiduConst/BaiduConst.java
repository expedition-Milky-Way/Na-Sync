package vip.yzxh.BaiduPan.BaiduConst;

import vip.yzxh.BaiduPan.BaiduPanResponse.DeviceCode;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.UserMsg;

/**
 * @Author YeungLuhyun
 * 有关于百度网盘的所有常量
 *  all const value about of baidu net storage
 **/
public class BaiduConst {

    private static UserMsg userMsg;

    private static DeviceCode deviceCode;

    private static TokenResponse tokenMsg;

    public static final String RESP_DEVICE_CODE = "device_code";

    public static final String RESP_USER_CODE = "user_code";

    public static final String RESP_ERROR_NO = "errno";

    public static final String RESP_INTERVAL = "interval";

    public static final String RESP_BAIDU_COOKIE = "BAIDUID";

    public static synchronized void setUserMsg(UserMsg val) {
        userMsg = val;
    }

    public static synchronized UserMsg getUserMsg() {
        return userMsg;
    }

    public static synchronized void setDeviceCode(DeviceCode val) {
        deviceCode = val;
    }

    public static synchronized DeviceCode getDeviceCode() {
        return deviceCode;
    }

    public static TokenResponse getTokenMsg() {
        return tokenMsg;
    }

    public static void setTokenMsg(TokenResponse tokenMsg) {
        BaiduConst.tokenMsg = tokenMsg;
    }
}
