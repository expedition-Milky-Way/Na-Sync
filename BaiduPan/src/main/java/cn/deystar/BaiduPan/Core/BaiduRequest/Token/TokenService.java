package cn.deystar.BaiduPan.Core.BaiduRequest.Token;

import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.DeviceCodeResponse;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import cn.deystar.Util.Util.SysConst;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
@Component
public class TokenService {

    @Resource
    private FileSettingService settingService;

    /**
     * 获取设备码，用户授权码，二维码
     *
     * @return device_code 设备码
     * @return user_code 用户身份id
     * @return verification_url 用户验证身份Id地址
     * @return qrcode_url 二维码
     */
    public DeviceCodeResponse deviceCode(String appKey) {
        String bodyStr = null;
        HttpResponse deviceResponse = null;
        try {
            String deviceURI = "https://openapi.baidu.com/oauth/2.0/device/code?" +
                    "response_type=device_code&client_id=" + appKey + "&scope=basic,netdisk";
            deviceResponse = HttpRequest.get(deviceURI).execute();
            bodyStr = deviceResponse.body();
        } finally {
            if (deviceResponse != null) {
                deviceResponse.close();
            }
        }
        return JSONObject.parseObject(bodyStr, DeviceCodeResponse.class);
    }


    /**
     * 请求百度获取token
     * https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&
     * code=第一步生成的设备码device_code&
     * client_id=您应用的AppKey&
     * client_secret=您应用的SecretKey
     * <p>
     * 关于应用的相关信息，您可在控制台，点进去您对应的应用，查看
     */
    public TokenResponse getToken(String deviceCode) {
        FileSetting setting = settingService.getSetting();

        HttpResponse response = null;
        try {
            String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&" +
                    "code=" + deviceCode + "&client_id=" + setting.getAppKey() + "&client_secret=" + setting.getSecretKey();

            response = HttpRequest.get(url).disableCache().execute();
        } finally {
            if (response != null) {
                response.close();
            }
        }
        TokenResponse tokenResponse = JSONObject.parseObject(response.body(), TokenResponse.class);
        if (tokenResponse.tokenBodyNotNull()) {
            tokenResponse.setCreateTime(new Date());
            return tokenResponse;
        }
        return null;
    }

    /**
     * 轮训获取token
     * 要先扫码，然后再去轮询
     *
     * @param deviceCode 设备码
     * @param expires    超时时间
     * @param sleep      间隔
     * @return
     */
    public TokenResponse getToken(String deviceCode, Integer expires, Integer sleep) {
        sleep *= 1000;
        TokenResponse tokenResponse = null;
        long tt = 0L;
        while (tt / 1000L <= expires) {
            long begin = System.currentTimeMillis();
            tokenResponse = this.getToken(deviceCode);
            if (tokenResponse != null) {
                break;
            }
            long end = System.currentTimeMillis();
            tt += (end - begin) + sleep;
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ignored) {

            }
        }
        return tokenResponse;
    }

    /**
     * 请求刷新百度网盘Token
     */
    public TokenResponse freshToken() {
        FileSetting setting = settingService.getSetting();
        TokenResponse tokenResponse = settingService.getToken();
        String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token&refresh_token=" + tokenResponse.getRefreshToken()
                + "&client_id=:cid&client_secret=:sec";
        if (setting.getAppId() != null) url = url.replace(":cid", setting.getAppId());
        if (setting.getSecretKey() != null) url = url.replace(":sec", setting.getSecretKey());
        HttpResponse response = HttpRequest.get(url).execute();
        TokenResponse resBody = JSON.parseObject(response.body(), TokenResponse.class);
        if (resBody != null) resBody.setCreateTime(new Date());
        return resBody;
    }
}
