package com.example.baidusync.Admin;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;

import com.example.baidusync.Admin.Service.FileSettingService;
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

@Controller
public class AdminController {

    @Resource
    private FileSettingService service;
    @Resource
    private RequestNetDiskService netDiskService;
    @Resource
    private LogService logService;

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
    public ResponseData submit(FileSetting setting) {
        if (setting.isEmpty()){
            //返回报错信息
            return new ResponseData("设置不能为空");
        }
        JSONObject jsonObject = netDiskService.deviceCode(setting.getAppKey());
        if (jsonObject.getString("error_description") != null) {
            //返回报错信息
            return new ResponseData(jsonObject.getString("error_description"));
        }
        service.settingFile(setting);
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



    public synchronized void read(File s) {

        File[] files = s.listFiles();
        for (File file1 : files) {
            if (file1.isDirectory()) {
                new Thread(() -> read(file1)).start();
            }
            if (file1.getPath().contains("pixiv")) System.out.println(file1.toString());

        }
    }


}
