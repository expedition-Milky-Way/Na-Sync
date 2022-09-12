package com.example.baidusync.Util.NetDiskSync;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @author  杨名
 * 请求
 */
@Service
public class RequestNetDiskImpl implements RequestNetDiskService{

    //https://openapi.baidu.com/oauth/2.0/token?
    //grant_type=refresh_token&
    //refresh_token=Refresh Token的值&
    //client_id=您应用的AppKey&
    //client_secret=您应用的SecretKey

  /*  https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&
    code=第一步生成的设备码device_code&
    client_id=您应用的AppKey&
    client_secret=您应用的SecretKey*/

    /**
     * 获取设备码，用户授权码，二维码
     *
     */
    @Override
    public JSONObject accessToken(String appKey){
        String deviceURI = "https://openapi.baidu.com/oauth/2.0/device/code?" +
                "response_type=device_code&client_id="+appKey+"&scope=basic,netdisk";
        HttpResponse deviceResponse = HttpRequest.get(deviceURI).execute();
        String bodyStr = deviceResponse.body();
        String body = bodyStr.replace("\\","");
        JSONObject jsonObject = JSON.parseObject(body);
//        String deviceCode = jsonObject.getString("device_code");
//        String userCode = jsonObject.getString("user_code");
//        String verification = jsonObject.getString("verification_url");
//        String qrcodeUrl = jsonObject.getString("qrcode_url");
        System.out.println(body);
        return jsonObject;
    }


}
