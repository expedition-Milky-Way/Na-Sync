package vip.yzxh.BaiduPan.NetDisk;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


import lombok.extern.slf4j.Slf4j;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;
import vip.yzxh.BaiduPan.BaiduConst.BaiduConst;
import vip.yzxh.BaiduPan.BaiduPanResponse.DeviceCodeResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.UserMsg;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;
import vip.yzxh.Util.Util.FileAndDigsted;
import vip.yzxh.Util.HttpServerlet.Response.ResponseData;
import vip.yzxh.Util.Util.SysConst;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;

/**
 * @author 杨名
 * 请求
 */
@Service
@Slf4j
public class RequestNetDiskServiceImpl implements RequestNetDiskService {

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
    @Override
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
     * 通过授权码模式进行授权
     *
     * @return
     */
    @Override
    public Object getAuthor(FileSetting setting) {
        String url = "https://openapi.baidu.com/oauth/2.0/authorize?response_type=code&client_id=:appKey" +
                "&redirect_uri=:uri&scope=basic,netdisk&qrcode=1";
        if (setting == null || setting.getAppKey() == null || setting.getAppId() == null || setting.getUri() == null) {
            return null;
        }
        url = url.replace(":appKey", setting.getAppKey())
                .replace(":uri", "http://deystar.com.cn:8828")
                .replace(":appId", setting.getAppId());

        // 创建http GET请求
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                //请求体内容

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                //相当于关闭浏览器
                httpclient.close();
            } catch (Exception e) {

            }
        }

        return null;
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
    @Override
    public TokenResponse getToken(String deviceCode) {
        FileSetting setting = settingService.getSetting();
        TokenResponse tokenResponse = null;
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
        if (response != null) {
            String bodyStr = response.body();
            bodyStr = bodyStr.replace("\\", "");
            JSONObject json = JSONObject.parseObject(bodyStr);
            if (json != null && !json.containsKey(BaiduConst.RESP_ERROR_NO)) {
                tokenResponse = JSONObject.parseObject(bodyStr, TokenResponse.class);
                return tokenResponse.getToken() == null ? null : tokenResponse;
            }
            System.out.println("请求token异常");
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
    @Override
    public TokenResponse getToken(String deviceCode, Integer expires, Integer sleep) {

        sleep *= 1000;
        TokenResponse tokenResponse = null;
        long tt = 0L;
        while (tt / 1000L <= expires) {
            long begin = System.currentTimeMillis();
            tokenResponse = this.getToken(deviceCode);
            if (tokenResponse != null) {
                BaiduConst.setTokenMsg(tokenResponse);
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
    public Object freshToken() {
        FileSetting setting = settingService.getSetting();
        String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token&refresh_token=" + SysConst.getRefreshToken()
                + "&client_id=:cid&client_secret=:sec";
        if (setting.getAppId() != null) url = url.replace(":cid", setting.getAppId());
        if (setting.getSecretKey() != null) url = url.replace(":sec", setting.getSecretKey());
        HttpResponse response = HttpRequest.get(url).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        if (resBody.getString("access_token") != null) {
            SysConst.setAccessToken(resBody.getString("access_token"));
            SysConst.setRefreshToken(resBody.getString("refresh_token"));
            SysConst.setExpireTime(resBody.getInteger("expires_in").longValue());
        }
        return null;
    }


    /**
     * 获取用户信息
     */
    @Override
    public Object getBaiduUsInfo() {
        TokenResponse tokenResponse = BaiduConst.getTokenMsg();
        if (tokenResponse == null) {
            throw new RuntimeException();
        }
        UserMsg userMsg = requestUsInfo(tokenResponse.getToken());
        if (userMsg == null) {
            this.freshToken();
            return getBaiduUsInfo();
        }
        Long tempSize = userMsg.getVipTypeEnums().tempSize;
        Long oneFileSize = userMsg.getVipTypeEnums().fileSize;
        SysConst.setMaxSize(oneFileSize);
        SysConst.setMaxTempSize(tempSize);
        BaiduConst.setUserMsg(userMsg);
        return null;
    }


    /**
     * 请求百度用户信息连接
     */
    private UserMsg requestUsInfo(String accessToken) {
        String URL = "pan.baidu.com/rest/2.0/xpan/nas?method=uinfo" +
                "&access_token=:token";
        if (accessToken != null && !accessToken.trim().isEmpty()) {
            return new UserMsg(false, ResponseData.DEFAULT_ERROR_CODE, "token为空");
        }
        URL = URL.replace(":token", accessToken);
        HttpResponse response = HttpRequest.get(URL).execute();
        String bodyStr = response.body();
        JSONObject body = JSON.parseObject(bodyStr);

        if (body != null &&
                !body.containsKey(BaiduConst.RESP_ERROR_NO) &&
                body.getInteger(BaiduConst.RESP_ERROR_NO) != 0) {
            UserMsg msg = JSONObject.parseObject(body.toJSONString(), UserMsg.class);
            return msg;
        }
        return new UserMsg(false, ResponseData.DEFAULT_ERROR_CODE, body.getString("errmsg"));


    }

    /**
     * 开始上传
     *
     * @param name            源文件名
     * @param parent          源文件父路径
     * @param size            源文件大小
     * @param fileAndDigested
     */
    @Override
    public void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigested, String tempPath) {
        List<String> md5 = null;
        Long totalTempSize = 0L;
        for (FileAndDigsted item : fileAndDigested) {
            md5.add(item.getDigsted());
            totalTempSize += item.getSize();
        }
        System.out.println("文件：" + name + "大小为" + size + "缓存文件总大小" + totalTempSize);
        //预上传

        //3.发起预上传请求
        JSONObject responseJson = postNetDist(name, parent, md5, size.intValue());
        if (responseJson.getInteger("errno") != 0) {
            System.out.println("预上传错误：" + responseJson.toString());
        }

        String netDiskPath = responseJson.getString("path");
        String uploadid = responseJson.getString("uploadid");
        List<Integer> blokList = responseJson.getObject("block_list", ArrayList.class);
        //i. 发送分片文件
        for (Integer item : blokList) {
            FileAndDigsted tempMessage = fileAndDigested.get(item);
            File temFile = new File(tempMessage.getPath());
            JSONObject sendTempRes = postSendTemp(temFile, netDiskPath, uploadid);
            if (sendTempRes.getInteger("errno") != 0) {
                System.out.println("上传分片出现了问题:" + name);

            }
        }
        //i.在网盘上面创建这个文件，完成上传
        String netDiskDir = SysConst.getDefaultNetDiskDir();
        String filePath = netDiskDir + "/" + parent + "/" + name;
        postCreateFile(filePath, size, SysConst.getIsNotDir(), md5, uploadid);
        //删除缓存文件，记录文件原名和改名后的文件名
        //i.删除缓存文件和目录l
        //目录
        File delDir = new File(tempPath);
        try {
            for (FileAndDigsted item : fileAndDigested) {
                File delFile = new File(item.getPath() + "/" + item.getName());
                boolean isDel = delFile.delete();
                System.out.println("上传任务结束,删除" + delFile.getName() + (isDel ? "成功" : "失败"));

            }
            //删除目录
            boolean isDel = delDir.delete();
            System.out.println("上传任务结束,删除" + delDir.getName() + (isDel ? "成功" : "失败"));

        } catch (SecurityException e) {
            System.out.println("上传任务结束,删除" + delDir.getName() + "删除失败，权限报错" + e.getMessage());

        }
    }

    /**
     * 预上传
     *
     * @return
     */
    public JSONObject postNetDist(String fileName, String parent, List<String> md5, Integer size) {
        //新建目录
        String netDiskDir = SysConst.getDefaultNetDiskDir();
        if (!hasDir(netDiskDir)) {
            postCreateNetDisk(netDiskDir);
        }
        String netDiskFile = parent;
        if (parent.contains("/")) netDiskFile = "/" + parent;
        if (!hasDir(netDiskDir + netDiskFile)) postCreateNetDisk(netDiskFile);
        //开始预上传
        String URL = "pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=";
        String token = SysConst.getAccessToken();

        URL += token;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", netDiskFile + "/" + fileName);
        requestBody.put("size", size);
        requestBody.put("isdir", SysConst.getIsNotDir());
        requestBody.put("block_list", md5);
        requestBody.put("autoinit", 1);
        HttpResponse response = HttpRequest.post(URL).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }

    /**
     * 发送切片文件
     *
     * @param file
     * @param path
     * @param uploadId
     */
    public JSONObject postSendTemp(File file, String path, String uploadId) {
        String url = "d.pcs.baidu.com/rest/2.0/pcs/superfile2?method=upload&access_token=";
        String token = SysConst.getAccessToken();
        if (token == null) {

            token = SysConst.getAccessToken();
        }
        url += token;
        url += "&type=tmpfile&path=" + path + "&uploadid=" + uploadId + "&partsq=0";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("file", file);
        HttpResponse response = HttpRequest.post(url).body(jsonObject.toString()).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }


    /**
     * 看看有没有和这个目录
     *
     * @param path
     * @return
     */
    @Override
    public boolean hasDir(String path) {
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=list&access_token=";
        String token = SysConst.getAccessToken();
        if (token == null) {

            token = SysConst.getAccessToken();
        }
        url += token;
        HttpResponse response = HttpRequest.get(url).execute();
        String bodyStr = response.body();
        JSONObject bodyJson = JSON.parseObject(bodyStr);
        JSONArray jsonArray = bodyJson.getJSONArray("list");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonItem = (JSONObject) jsonArray.get(i);
            if (path == jsonItem.getString("path") &&
                    jsonItem.getInteger("isdir").intValue() == SysConst.getIsDir().intValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在网盘上创建这个文件
     *
     * @param path
     * @param size
     * @param isDir
     * @param blokList
     * @param uploadId
     * @return
     */
    @Override
    public JSONObject postCreateFile(String path, Long size, Integer isDir,
                                     List<String> blokList, String uploadId) {
        String url = "pan.baidu.com/2.0/xpan/file?method=create&access_token=";
        String token = SysConst.getAccessToken();
        if (token == null) {

            token = SysConst.getAccessToken();
        }
        url += token;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", path);
        requestBody.put("size", String.valueOf(size));
        requestBody.put("isdir", String.valueOf(isDir.intValue()));
        requestBody.put("block_list", blokList);
        requestBody.put("uploadid", uploadId);
        HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }

    /**
     * 新建目录
     */
    @Override
    public boolean postCreateNetDisk(String path) {
        FileSetting setting = settingService.getSetting();
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=";
        JSONObject responseBody = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", path);
        requestBody.put("size", SysConst.getDefaultDirSize());
        requestBody.put("isdir", SysConst.getIsDir());
        HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
        String bodyStr = response.body();
        responseBody = JSON.parseObject(bodyStr);
        if (responseBody != null) {
            if (responseBody.getInteger("errno") > 0) {
                System.out.println("申请在网盘中创建目录异常：\n" + responseBody.getString("errmsg"));

                return false;
            } else {
                return true;
            }
        }
        return false;
    }


}
