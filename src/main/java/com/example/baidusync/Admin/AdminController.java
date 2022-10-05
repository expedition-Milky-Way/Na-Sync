package com.example.baidusync.Admin;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;

import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileService.FileService;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.ResponseData;
import com.example.baidusync.Util.SystemLog.LogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Controller
public class AdminController {

    @Resource
    private FileSettingService service;
    @Resource
    private RequestNetDiskService netDiskService;
    @Resource
    private LogService logService;
    @Resource
    private FileService fileService;

    @RequestMapping("/")
    public String index(HttpServletRequest request, ModelMap modelMap) {
        FileSetting fileSetting = service.getSetting();
        if (ObjectUtil.isNotNull(fileSetting)) {
            modelMap.put("path", fileSetting.getPath());
            modelMap.put("cachePath", fileSetting.getCachePath());
            modelMap.put("password", fileSetting.getPassword());
            modelMap.put("appId", fileSetting.getAppId());
            modelMap.put("appKey", fileSetting.getAppKey());
            modelMap.put("secretKey", fileSetting.getSecretKey());
            modelMap.put("signKey", fileSetting.getSignKey());
            modelMap.put("taskNum",fileSetting.getTaskNum());
            modelMap.put("dateTime",fileSetting.getDateTime());
            modelMap.put("btnState",FileSetting.HAS_SETTING);
        }else{
            modelMap.put("btnState",FileSetting.NO_SETTING);
        }
        List<Integer> taskNumList = new ArrayList<>();
        for (Integer i = 1; i < 11; i++) {
            taskNumList.add(i);
        }
        modelMap.put("taskNumList",taskNumList);
        return "Admin/admin-index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseData submit(FileSetting setting,HttpServletRequest request) {
       String ctx = request.getHeader("host");
        if (setting.isEmpty()){
            //返回报错信息
            return new ResponseData("设置不能为空");
        }
        JSONObject jsonObject = netDiskService.deviceCode(setting.getAppKey());
        if (jsonObject.getString("error_description") != null) {
            //返回报错信息
            return new ResponseData("AppKey不正确，检查是否有空格存在或是否有误"+jsonObject.getString("error_description"));
        }
        if (!service.excites(setting)){
            service.settingFile(setting);
        }else{
            service.updateSetting(setting);
        }
        jsonObject.put("ctx",ctx);
        return new ResponseData(jsonObject);
    }


    /**
     * 加载日志
     */
    @PostMapping("/getLog")
    @ResponseBody
    public ResponseData getLog(){
        return new ResponseData(logService.getLog());
    }

    /**
     * 下载日志
     *
     */
    @PostMapping("/downLog")
    @ResponseBody
    public String down(){
        return null;
    }


    /**
     * 打开百度网盘验证
     * @param
     */
    @RequestMapping("/netDiskConfirm")
    public String getNetDiskConrim(String url,HttpServletRequest request,ModelMap modelMap){
        String ctx = "http://"+request.getHeader("host");
        modelMap.put("ctx",ctx);
        modelMap.put("url",url);
        return "Admin/confirmSettting";
    }

    @PostMapping("/netDiskConfirmOk")
    @ResponseBody
    public ResponseData  confirmOk(){
        netDiskService.setAuthIsOk();
        boolean isok = netDiskService.accessToken();
        if (isok){
            FileSetting fileSetting = service.getSetting();
           Timer timer = netDiskService.setSchTask(fileSetting);
           if (timer==null )  return new ResponseData("定时任务出现问题");
           return new ResponseData();
        }else{
            return new ResponseData("请确认百度网盘扫描二维码完成后点击确认按钮");
        }
    }




}
