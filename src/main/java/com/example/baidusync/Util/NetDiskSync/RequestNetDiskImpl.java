package com.example.baidusync.Util.NetDiskSync;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author 杨名
 * 请求
 */
@Service
@Slf4j
public class RequestNetDiskImpl implements RequestNetDiskService {

    @Resource
    private FileSettingMapping settingMapping;

    private static String DEVICE_CODE = null;

    private static String ACCESS_TOKEN = null;

    private static String REFRESH_TOKEN = null;
    //token过期时间  单位：秒
    private static Long EXPIRES = null;
    //会员类型 ： 0 普通用户， 1：普通会员 2:超级会员
    public static Integer VIP_TYPE = null;
    //默认网盘备份文件夹(网盘上的)
    private static String DEFAULT_DISK_DIR = "/nasBackUpByYZXH";
    //是文件目录
    private static Integer IS_DIR = 1;
    //不是文件目录
    private static Integer IS_NOT_DIR = 0;
    //默认文件夹大小
    private static Integer DEFAULT_DIR_SIZE = 0;
    private static FileSetting fileSetting;

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
    private void accessToken() {
        new Thread(() -> {
            while (true) {
                if (DEVICE_CODE == null) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    JSONObject result = getToken(DEVICE_CODE);
                    if (result.getString("error_description") == null) {
                        ACCESS_TOKEN = result.getString("access_token");
                        REFRESH_TOKEN = result.getString("refresh_token");
                        EXPIRES = result.getLong("expires_in");
                        System.out.println(result.toString());
                        return;
                    } else {
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
        BeanUtil.copyProperties(settingMapping.selectOne(lambdaQueryWrapper), fileSetting);
        if (!fileSetting.isEmpty()) {
            String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=device_token&" +
                    "code=" + deviceCode + "&client_id=" + fileSetting.getAppKey() + "&client_secret=" + fileSetting.getSecretKey();
            HttpResponse response = HttpRequest.post(url).execute();
            String bodyStr = response.body();
            bodyStr = bodyStr.replace("\\", "");
            jsonObject = new JSONObject(JSON.parseObject(bodyStr));
            System.out.println(jsonObject);
        }
        return jsonObject;
    }

    /**
     * 获取用户信息
     */
    @Override
    public void getBaiduUsInfo() {
        JSONObject body = null;
        if (ACCESS_TOKEN != null) {
            body = requestUsInfo(ACCESS_TOKEN);
        } else {
            JSONObject tokenJson = this.getToken(DEVICE_CODE);
            String token = null;
            if (tokenJson.getString("access_token") != null) {
                token = tokenJson.getString("access_token");
            }
            ACCESS_TOKEN = token;
            body = requestUsInfo(token);
        }
        if (body.getString("errmsg") != null) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");
            log.error(format.format(date) + "获取用户信息失败。或许是token为" + ACCESS_TOKEN + "的原因");
        }
        VIP_TYPE = body.getInteger("vip_type");
    }


    /**
     * 请求百度用户信息连接
     */
    private JSONObject requestUsInfo(String accessToken) {
        String URL = "http://pan.baidu.com/rest/2.0/xpan/nas?method=uinfo" +
                "method=uinfo&access_token=" + accessToken;
        HttpResponse response = HttpRequest.get(URL).execute();
        String bodyStr = response.body();
        JSONObject body = JSON.parseObject(bodyStr);
        return body;
    }

    /**
     * 预上传
     *
     * @return
     */
    public JSONObject postNetDist(String fileName, String filePath, List<String> md5, Integer size) {
        //新建目录
        if (!hasDir(DEFAULT_DISK_DIR)) {
            postCreateNetDisk(DEFAULT_DISK_DIR);
        }
        String parentPath = filePath.split("/")[fileName.length() -1];
        if (!hasDir(DEFAULT_DISK_DIR+parentPath)) postCreateNetDisk(DEFAULT_DISK_DIR+parentPath);
        //开始预上传
        String URL = "pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=";
        if (ACCESS_TOKEN == null){
            accessToken();
        }
        URL+= ACCESS_TOKEN;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path",parentPath+"/"+fileName);
        requestBody.put("size",size);
        requestBody.put("isdir",IS_NOT_DIR);
        requestBody.put("block_list",md5);
        requestBody.put("autoinit",1);
        HttpResponse response = new HttpRequest(URL).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }

    /**
     * 看看有没有和这个目录
     *
     * @param path
     * @return
     */
    public boolean hasDir(String path) {
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=list&access_token=";
        if (ACCESS_TOKEN != null) {
            url += ACCESS_TOKEN;
        } else {
            accessToken();
            hasDir(path);
        }
        HttpResponse response = HttpRequest.get(url).execute();
        String bodyStr = response.body();
        JSONObject bodyJson = JSON.parseObject(bodyStr);
        JSONArray jsonArray = bodyJson.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonItem = (JSONObject) jsonArray.get(i);
            if (path == jsonItem.getString("path") && jsonItem.getInteger("isdir") == IS_DIR) {
                return true;
            }
        }

        return false;
    }

    /**
     * 新建目录
     */
    public boolean postCreateNetDisk(String path) {
        String url = "https://pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=";
        JSONObject responseBody = null;
        if (!fileSetting.isEmpty()) {
            JSONObject requestBody = new JSONObject();
            requestBody.put("path", path);
            requestBody.put("size", DEFAULT_DIR_SIZE);
            requestBody.put("isdir", IS_DIR);
            HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
            System.out.println(response);
            //到时候记log
            String bodyStr = response.body();
            responseBody = JSON.parseObject(bodyStr);
        }
        if (responseBody != null) {
            if (responseBody.getInteger("errno") > 0) {
                //新建失败  记log
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
