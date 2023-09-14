package vip.yzxh.BaiduPan.Admin.Controller;

import cn.hutool.core.util.ObjectUtil;


import com.alibaba.fastjson.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vip.yzxh.BaiduPan.BaiduConst.BaiduConst;
import vip.yzxh.BaiduPan.BaiduPanResponse.DeviceCodeResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.NetDisk.RequestNetDiskService;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;
import vip.yzxh.Util.HttpServerlet.RequestAndResponse;
import vip.yzxh.Util.HttpServerlet.Response.ResponseData;
import vip.yzxh.Util.HttpServerlet.Response.Success;
import vip.yzxh.Util.Util.ConfigFileTemplate;

import javax.annotation.Resource;
import javax.print.attribute.standard.MediaTray;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

@Controller
public class AdminController {

    @Resource
    private FileSettingService fileSettingService;
    @Resource
    private RequestNetDiskService requestNetDiskService;

    @Resource
    private ThreadPoolTaskExecutor executor;
    public static Queue<RequestAndResponse> requests = new LinkedBlockingDeque<>();

    @RequestMapping("/")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        FileSetting fileSetting = fileSettingService.getSetting();
        if (ObjectUtil.isNotNull(fileSetting)) {
            modelMap.put("path", fileSetting.getPath());
            modelMap.put("cachePath", fileSetting.getCachePath());
            modelMap.put("password", fileSetting.getPassword());
            modelMap.put("appId", fileSetting.getAppId());
            modelMap.put("appKey", fileSetting.getAppKey());
            modelMap.put("secretKey", fileSetting.getSecretKey());
            modelMap.put("signKey", fileSetting.getSignKey());
            modelMap.put("taskNum", fileSetting.getTaskNum());
            modelMap.put("dateTime", fileSetting.getDateTime());
            modelMap.put("uri", fileSetting.getUri());
            modelMap.put("version", fileSetting.getVersion());
        }
        List<Integer> taskNumList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});

        modelMap.put("taskNumList", taskNumList);
        return "Admin/admin-index";
    }

    @PostMapping(value = "/add")
    @ResponseBody
    public ResponseData submit(FileSetting setting,
                               HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {

        DeviceCodeResponse deviceCodeResponse = requestNetDiskService.deviceCode(setting.getAppKey());
        executor.execute(() -> {
            Thread.currentThread().setName("设置setting.json");
            fileSettingService.settingFile(setting);

            requestNetDiskService.getToken(
                    deviceCodeResponse.getDeviceCode(),
                    deviceCodeResponse.getExpires(),
                    deviceCodeResponse.getInterval());
        });
        return new Success(deviceCodeResponse);
    }


}
