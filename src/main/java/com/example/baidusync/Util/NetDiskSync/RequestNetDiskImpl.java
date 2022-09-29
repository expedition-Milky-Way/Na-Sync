package com.example.baidusync.Util.NetDiskSync;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 杨名
 * 请求
 */
@Service
@Slf4j
public class RequestNetDiskImpl implements RequestNetDiskService {

    @Resource
    private FileSettingMapping settingMapping;
    @Resource
    private FileSettingService fileSettingService;
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
    /**
     * 百度网盘/阿里网盘 接受最大单次上传文件大小
     * 20G:20991366069L
     *
     * @unit Bytes
     */
    public static Long MAX_SIZE = null;
    /**
     * 一个用户最大上传的分片文件大小
     */
    public static Long MAX_TEMP_SIZE = null;
    /**
     * 通过netDiskService.VIP_TYPE获取分片最大大小
     */
    private static Long[] SIZE_BY_VIP_TYPE = {4194304L, 16777216L, 33554432L};
    /**
     * 获取用户可以上传的单个文件的最大大小
     */
    private static Long[] ONE_FILE_SIZE_BY_VIP = {4294967296L, 10737418240L, 21474836480L};

    private static FileSetting fileSetting = new FileSetting();

    private static AtomicInteger IS_AUTH_OK = new AtomicInteger(0);
    //定时任务  每一天
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

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
        if (jsonObject.getString("device_code") != null) {
            String deviceCode = jsonObject.getString("device_code");
            DEVICE_CODE = deviceCode;
        }else{
            //获取token失败，
            LogEntity log = new LogEntity("","没有扫码，获取Token失败"+jsonObject.toString(),LogEntity.LOG_TYPE_WARN);
            LogExecutor.addSysLogQueue(log);
        }
        log.info(jsonObject.toString());
        return jsonObject;
    }


    /**
     * 获取token
     */
    @Override
    public boolean accessToken() {
        if (DEVICE_CODE != null) {
            JSONObject result = getToken(DEVICE_CODE);
            if (result.getString("error_description") == null) {
                ACCESS_TOKEN = result.getString("access_token");
                REFRESH_TOKEN = result.getString("refresh_token");
                EXPIRES = result.getLong("expires_in");
                return true;
            } else {
                //记录日志
                LogExecutor.addSysLogQueue(new LogEntity(RequestNetDiskImpl.class.toString(), "请求百度网盘token报错：\n "
                        + result.toString(), LogEntity.LOG_TYPE_ERROR));
                return false;
            }
        } else {
            return false;
        }
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
        if (body.getInteger("errno") != 0) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");
            log.error(format.format(date) + "获取用户信息失败。或许是token为" + ACCESS_TOKEN + "的原因");
        }else{
            VIP_TYPE = body.getInteger("vip_type");
            MAX_SIZE = ONE_FILE_SIZE_BY_VIP[VIP_TYPE];
            MAX_TEMP_SIZE = SIZE_BY_VIP_TYPE[VIP_TYPE];
        }

    }


    /**
     * 请求百度用户信息连接
     */
    private JSONObject requestUsInfo(String accessToken) {
        String URL = "pan.baidu.com/rest/2.0/xpan/nas?method=uinfo" +
                "&access_token=" + accessToken;
        HttpResponse response = HttpRequest.get(URL).execute();
        String bodyStr = response.body();
        JSONObject body = JSON.parseObject(bodyStr);
        return body;
    }

    /**
     * 开始上传
     *
     * @param name           源文件名
     * @param parent         源文件父路径
     * @param size           源文件大小
     * @param fileAndDigsted
     */
    @Override
    public void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigsted, String tempPath) {
        List<String> md5 = null;
        Long totalTempSize = 0L;
        for (FileAndDigsted item : fileAndDigsted) {
            md5.add(item.getDigsted());
            totalTempSize += item.getSize();
        }
        LogEntity log = new LogEntity(
                "", "文件：" + name + "大小为" + size + "缓存文件总大小" + totalTempSize,
                LogEntity.LOG_TYPE_WARN);
        LogExecutor.addSysLogQueue(log);
        //预上传
        JSONObject responseJson = postNetDist(name, parent, md5, size.intValue());
        if (responseJson.getInteger("errno") != 0) {
            LogExecutor.addSysLogQueue(
                    new LogEntity(RequestNetDiskImpl.class.toString(), "预上传错误：" + responseJson.toString(),
                            LogEntity.LOG_TYPE_ERROR));
        }
        String netDiskPath = responseJson.getString("path");
        String uploadid = responseJson.getString("uploadid");
        List<Integer> blokList = responseJson.getObject("block_list", ArrayList.class);
        for (Integer item : blokList) {
            FileAndDigsted tempMessage = fileAndDigsted.get(item);
            File temFile = new File(tempMessage.getPath());
            postSendTemp(temFile, netDiskPath, uploadid);
        }
        //删除缓存文件，记录文件原名和改名后的文件名
        //i.删除缓存文件和目录
        //目录
        File delDir = new File(tempPath);
        try {
            for (FileAndDigsted item : fileAndDigsted) {
                File delFile = new File(item.getPath() + "/" + item.getName());
                boolean isDel = delFile.delete();
                LogEntity delLog = new LogEntity("", "上传任务结束,删除" + delFile.getName() + (isDel ? "成功" : "失败"), LogEntity.LOG_TYPE_INFO);
                LogExecutor.addSysLogQueue(delLog);
            }
            //删除目录
            boolean isDel = delDir.delete();
            LogEntity delLog = new LogEntity("", "上传任务结束,删除" + delDir.getName() + (isDel ? "成功" : "失败"), LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(delLog);
        } catch (SecurityException e) {
            LogEntity delLog = new LogEntity("", "上传任务结束,删除" + delDir.getName() + "删除失败，权限报错" + e.getMessage(), LogEntity.LOG_TYPE_ERROR);
            LogExecutor.addSysLogQueue(delLog);
        }
        //i. 记录文件上传到百度网盘后，百度网盘上的文件名字和原文件名

    }

    /**
     * 预上传
     *
     * @return
     */
    public JSONObject postNetDist(String fileName, String parent, List<String> md5, Integer size) {
        //新建目录
        if (!hasDir(DEFAULT_DISK_DIR)) {
            postCreateNetDisk(DEFAULT_DISK_DIR);
        }

        if (!hasDir(DEFAULT_DISK_DIR + parent)) postCreateNetDisk(DEFAULT_DISK_DIR + parent);
        //开始预上传
        String URL = "pan.baidu.com/rest/2.0/xpan/file?method=precreate&access_token=";
        if (ACCESS_TOKEN == null) {
            accessToken();
        }
        URL += ACCESS_TOKEN;
        JSONObject requestBody = new JSONObject();
        requestBody.put("path", parent + "/" + fileName);
        requestBody.put("size", size);
        requestBody.put("isdir", IS_NOT_DIR);
        requestBody.put("block_list", md5);
        requestBody.put("autoinit", 1);
        HttpResponse response = new HttpRequest(URL).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        return resBody;
    }

    public void postSendTemp(File file, String path, String uploadId) {
        String url = "d.pcs.baidu.com/rest/2.0/pcs/superfile2?method=upload&access_token=";
        if (ACCESS_TOKEN == null) {
            accessToken();
        }
        url += ACCESS_TOKEN;
        url += "&type=tmpfile&path=" + path + "&uploadid=" + uploadId + "&partsq=0";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("file", file);
        HttpResponse response = HttpRequest.post(url).body(jsonObject.toString()).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        if (resBody.getInteger("errno") > 0) {
            log.info(resBody.toString());
        }
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

    /**
     * 获取用户会员类型可以上传的单个文件大小
     */
    @Override
    public Long getMaxSize() {
        if (MAX_SIZE == null) this.getBaiduUsInfo();
        return MAX_SIZE;
    }

    /**
     * 获取用户会员类型可以上传分片文件大小
     */
    @Override
    public Long getMaxTempSize() {
        if (MAX_TEMP_SIZE == null) this.getBaiduUsInfo();
        return MAX_TEMP_SIZE;
    }

    @Override
    public Integer setAuthIsOk() {
        return IS_AUTH_OK.incrementAndGet();
    }

    /**
     * 设置定时任务的时间
     *
     * @return
     */
    @Override
    public Timer setSchTask(FileSetting fileSetting) {
        if (!fileSetting.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat Timeformat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm");
            Date date = null;
            try {
                date = Timeformat.parse(fileSetting.getDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (date != null) {
                calendar.set(Calendar.HOUR_OF_DAY, date.getHours());
                calendar.set(Calendar.MINUTE, date.getMinutes());
                Timer timer = new Timer();
                Date taskDate = calendar.getTime();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        String runDate = dateFormat.format(new Date());
                        LogEntity log = new LogEntity("", runDate + "开始运行", LogEntity.LOG_TYPE_INFO);
                        ScanFileUtil scanFileUtil = new ScanFileUtil(fileSetting.getCachePath(), fileSetting.getPassword());
                        scanFileUtil.doSomething(fileSetting.getPath());
                        LogExecutor.addSysLogQueue(log);
                    }
                }, taskDate, PERIOD_DAY);
                return timer;
            }

        }
        return null;
    }
}
