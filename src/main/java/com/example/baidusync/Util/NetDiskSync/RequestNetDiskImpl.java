package com.example.baidusync.Util.NetDiskSync;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 杨名
 * 请求
 */
@Service
public class RequestNetDiskImpl implements RequestNetDiskService {

    @Resource
    private FileSettingMapping settingMapping;

    private static String DEVICE_CODE = null;

    private static String ACCESS_TOKEN = null;

    private static String REFRESH_TOKEN = null;
    //token过期时间  单位：秒
    private static Long EXPIRES = null;


    /**
     * 获取设备码，用户授权码，二维码
     *
     * @return device_code 设备码
     * @return user_code 用户身份id
     * @return verification_url 用户验证身份Id地址
     * @return qrcode_url 二维码
     */
    @Override
    public JSONObject deviceCode(String appKey) {
        String deviceURI = "https://openapi.baidu.com/oauth/2.0/device/code?" +
                "response_type=device_code&client_id=" + appKey + "&scope=basic,netdisk";
        HttpResponse deviceResponse = HttpRequest.get(deviceURI).execute();
        String bodyStr = deviceResponse.body();
        String body = bodyStr.replace("\\", "");
        JSONObject jsonObject = JSON.parseObject(body);
        if (jsonObject.getString("device_code") != null) DEVICE_CODE = jsonObject.getString("device_code");

        accessToken();
        return jsonObject;
    }


    /**
     * 获取token
     */
    private void accessToken( ){
        new Thread(()->{
            while (true){
                if (DEVICE_CODE == null){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else {
                    JSONObject result = getToken(DEVICE_CODE);
                    if (result.getString("error_description") == null){
                        ACCESS_TOKEN = result.getString("access_token");
                        REFRESH_TOKEN = result.getString("refresh_token");
                        EXPIRES = result.getLong("expires_in");
                        System.out.println(result.toString());
                        return;
                    }else {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }).start();
    }


    /**
     * 请求百度获取token
     */
    private JSONObject getToken(String deviceCode) {
        JSONObject jsonObject = null;
        LambdaQueryWrapper<FileSetting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderBy(true, false, FileSetting::getId).last("LIMIT 1");
        FileSetting settingEntry = settingMapping.selectOne(lambdaQueryWrapper);
        if (!settingEntry.isEmpty()) {
            String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&" +
                    "code=" + deviceCode + "&client_id=" + settingEntry.getAppKey() + "&client_secret=" + settingEntry.getSecretKey();
            HttpResponse response = HttpRequest.post(url).execute();
            String bodyStr = response.body();
            bodyStr = bodyStr.replace("\\", "");
            jsonObject = new JSONObject(JSON.parseObject(bodyStr));
            System.out.println(jsonObject);
        }
        return jsonObject;
    }


}
