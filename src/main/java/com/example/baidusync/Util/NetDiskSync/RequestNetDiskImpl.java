package com.example.baidusync.Util.NetDiskSync;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileLog.FileLogEntity;
import com.example.baidusync.Util.FileLog.FileLogService;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.core.Bean.SysConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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
    @Resource
    private FileLogService fileLogService;

    //会员类型 ： 0 普通用户， 1：普通会员 2:超级会员
    public static Integer VIP_TYPE = null;
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
            SysConst.setDeviceCode(deviceCode);
        } else {
            //获取token失败，
            LogEntity log = new LogEntity("", "没有扫码，获取Token失败" + jsonObject.toString(), LogEntity.LOG_TYPE_WARN);
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
        if (SysConst.getDeviceCode() != null) {
            JSONObject result = getToken(SysConst.getDeviceCode());
            if (result.getString("error_description") == null) {
                SysConst.setAccessToken(result.getString("access_token"));
                SysConst.setRefreshToken(result.getString("refresh_token"));
                SysConst.setExpireTime(result.getLong("expires_in"));
                //查看是否拥有备份目录
                boolean isDir = false;
                if (!hasDir(SysConst.getDefaultNetDiskDir())) {
                    isDir = postCreateNetDisk(SysConst.getDefaultNetDiskDir());
                }
                if (isDir) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            freshToken(SysConst.getRefreshToken());
                        }
                    }, SysConst.getExpireTime());
                    return true;
                }else{
                    LogEntity log = new LogEntity("RequestNetDiskImpl.accessToken() 118Row","初始化百度网盘文件夹失败",
                            LogEntity.LOG_TYPE_ERROR);
                    LogExecutor.addSysLogQueue(log);
                    return false;
                }
            } else {
                //记录日志
                LogExecutor.addSysLogQueue(new LogEntity(RequestNetDiskImpl.class.toString(), "请求百度网盘token报错：\n "
                        + result.toString(), LogEntity.LOG_TYPE_ERROR));
                return false;
            }
        }
        LogEntity log = new LogEntity("RequestNetDisk.accessToken():125 row","获取token失败", LogEntity.LOG_TYPE_ERROR);
        LogExecutor.addSysLogQueue(log);
        return false;
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

        }
        return jsonObject;
    }

    /**
     * 请求刷新百度网盘Token
     */
    public void freshToken(String refreshToken) {
        String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=refresh_token&refresh_token=" + SysConst.getRefreshToken()
                + "&client_id=:cid&client_secret=:sec";
        if (fileSetting.isEmpty()) BeanUtil.copyProperties(fileSettingService.getSetting(), fileSetting);
        if (fileSetting.getAppId() != null) url.replace(":cid", fileSetting.getAppId());
        if (fileSetting.getSecretKey() != null) url.replace(":sec", fileSetting.getSecretKey());
        HttpResponse response = HttpRequest.get(url).execute();
        JSONObject resBody = JSON.parseObject(response.body());
        if (resBody.getString("access_token") != null) {
            SysConst.setAccessToken(resBody.getString("access_token"));
            SysConst.setRefreshToken(resBody.getString("refresh_token"));
            SysConst.setExpireTime(resBody.getInteger("expires_in").longValue());
        }
    }


    /**
     * 获取用户信息
     */
    @Override
    public void getBaiduUsInfo() {
        JSONObject body = null;
        String token = SysConst.getAccessToken();
        if (token != null) {
            body = requestUsInfo(token);
        } else {
            JSONObject tokenJson = this.getToken(SysConst.getDeviceCode());
            if (tokenJson.getString("access_token") != null) {
                token = tokenJson.getString("access_token");
                SysConst.setAccessToken(token);
                body = requestUsInfo(token);
            } else {
                LogEntity log = new LogEntity("", "请求token有误", LogEntity.LOG_TYPE_ERROR);
                LogExecutor.addSysLogQueue(log);
            }

        }
        if (body.getInteger("errno") != 0) {
            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");
            log.error(format.format(date) + "获取用户信息失败。或许是token为" + SysConst.getAccessToken() + "的原因");
        } else {
            VIP_TYPE = body.getInteger("vip_type");
            SysConst.setMaxSize(ONE_FILE_SIZE_BY_VIP[VIP_TYPE]);
            SysConst.setMaxTempSize(SIZE_BY_VIP_TYPE[VIP_TYPE]);
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
        if (fileSetting.isEmpty()) {
            LambdaQueryWrapper<FileSetting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.orderBy(true, false, FileSetting::getId).last("LIMIT 1");
            BeanUtil.copyProperties(settingMapping.selectOne(lambdaQueryWrapper), fileSetting);
        }

        //3.发起预上传请求
        JSONObject responseJson = postNetDist(name, parent, md5, size.intValue());
        if (responseJson.getInteger("errno") != 0) {
            LogExecutor.addSysLogQueue(
                    new LogEntity(RequestNetDiskImpl.class.toString(), "预上传错误：" + responseJson.toString(),
                            LogEntity.LOG_TYPE_ERROR));
        }

        String netDiskPath = responseJson.getString("path");
        String uploadid = responseJson.getString("uploadid");
        List<Integer> blokList = responseJson.getObject("block_list", ArrayList.class);
        //i. 发送分片文件
        for (Integer item : blokList) {
            FileAndDigsted tempMessage = fileAndDigsted.get(item);
            File temFile = new File(tempMessage.getPath());
            JSONObject sendTempRes = postSendTemp(temFile, netDiskPath, uploadid);
            if (sendTempRes.getInteger("errno") != 0) {
                LogEntity logEntity = new LogEntity("", "上传分片出现了问题:" + name, LogEntity.LOG_TYPE_ERROR);
                LogExecutor.addSysLogQueue(logEntity);
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
            for (FileAndDigsted item : fileAndDigsted) {
                File delFile = new File(item.getPath() + "/" + item.getName());
                boolean isDel = delFile.delete();
                LogEntity delLog = new LogEntity("",
                        "上传任务结束,删除" + delFile.getName() + (isDel ? "成功" : "失败"),
                        LogEntity.LOG_TYPE_INFO);
                LogExecutor.addSysLogQueue(delLog);
            }
            //删除目录
            boolean isDel = delDir.delete();
            LogEntity delLog = new LogEntity("",
                    "上传任务结束,删除" + delDir.getName() + (isDel ? "成功" : "失败"),
                    LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(delLog);
        } catch (SecurityException e) {
            LogEntity delLog = new LogEntity("", "上传任务结束,删除" + delDir.getName() + "删除失败，权限报错" + e.getMessage(), LogEntity.LOG_TYPE_ERROR);
            LogExecutor.addSysLogQueue(delLog);
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
        if (token == null) {
            accessToken();
        }
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
            accessToken();
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
            accessToken();
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
            accessToken();
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
        String url = "pan.baidu.com/rest/2.0/xpan/file?method=create&access_token=";
        JSONObject responseBody = null;
        if (!fileSetting.isEmpty()) {
            JSONObject requestBody = new JSONObject();
            requestBody.put("path", path);
            requestBody.put("size", SysConst.getDefaultDirSize());
            requestBody.put("isdir", SysConst.getIsDir());
            HttpResponse response = HttpRequest.post(url).body(requestBody.toString()).execute();
            String bodyStr = response.body();
            responseBody = JSON.parseObject(bodyStr);
        }
        if (responseBody != null) {
            if (responseBody.getInteger("errno") > 0) {
                LogEntity log = new LogEntity("", "申请在网盘中创建目录异常：\n" + responseBody.getString("errmsg"),
                        LogEntity.LOG_TYPE_ERROR);
                LogExecutor.addSysLogQueue(log);
                return false;
            } else {
                return true;
            }
        }
        return false;
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
    public void setSchTask(FileSetting fileSetting) {
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
            }
        }
    }


}
