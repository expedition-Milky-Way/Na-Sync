package vip.yzxh.BaiduPan.BaiduConst;

import org.springframework.util.ResourceUtils;
import vip.yzxh.BaiduPan.BaiduPanResponse.DeviceCodeResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.UserMsg;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @Author YeungLuhyun
 * 有关于百度网盘的所有常量
 *  all const value about of baidu net storage
 **/
public class BaiduConst {

    private static UserMsg userMsg;

    private static DeviceCodeResponse deviceCode;

    private static TokenResponse tokenMsg;

    public static final String RESP_DEVICE_CODE = "device_code";

    public static final String RESP_USER_CODE = "user_code";

    public static final String RESP_ERROR_NO = "errno";

    public static final String RESP_INTERVAL = "interval";

    public static final String RESP_BAIDU_COOKIE = "BAIDUID";
    public static String RESP_BAIDU_HTML(){
        String path = "";
        try {
            path = ResourceUtils.getURL("classpath:").getPath();
            path = new File(path).getAbsolutePath() + "/BaiduResp/";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return path;
    }
    public static synchronized void setUserMsg(UserMsg val) {
        userMsg = val;
    }

    public static synchronized UserMsg getUserMsg() {
        return userMsg;
    }



    public static TokenResponse getTokenMsg() {
        return tokenMsg;
    }

    public static void setTokenMsg(TokenResponse tokenMsg) {
        BaiduConst.tokenMsg = tokenMsg;
    }



}
