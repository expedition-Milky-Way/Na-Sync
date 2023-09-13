package vip.yzxh.BaiduPan.Admin;

import cn.hutool.core.util.ObjectUtil;

import com.alibaba.fastjson.JSONObject;


import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.yzxh.BaiduPan.AsyncResponses.AsyncResponses;
import vip.yzxh.BaiduPan.BaiduConst.BaiduConst;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.NetDisk.RequestNetDiskService;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;
import vip.yzxh.Util.HttpServerlet.RequestAndResponse;
import vip.yzxh.Util.HttpServerlet.ResponseData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;

@Controller
public class AdminController {

    @Resource
    private FileSettingService fileSettingService;
    @Resource
    private RequestNetDiskService requestNetDiskService;

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
        }
        List<Integer> taskNumList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11});

        modelMap.put("taskNumList", taskNumList);
        return "Admin/admin-index";
    }

    @PostMapping("/add")
    public void submit(FileSetting setting, HttpServletRequest request, HttpServletResponse response) {

            fileSettingService.settingFile(setting);
            TokenResponse tokenResponse = requestNetDiskService.deviceCode(setting.getAppKey());

            AccreditResponser responser = null;
            if (tokenResponse != null && tokenResponse.getToken() != null) {
                responser = new AccreditResponser(new ResponseData(), request,response);
            } else {
                responser = new AccreditResponser(new ResponseData(ResponseData.DEFAULT_SUCCESS_MESSAGE), request,response);
            }
            AsyncResponses.setAccreditQueue(responser);
    }






}
