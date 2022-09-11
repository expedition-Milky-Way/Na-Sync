package com.example.baidusync.Admin;

import cn.hutool.core.util.ObjectUtil;
import com.example.baidusync.Admin.Entity.FileSetting;

import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Controller
public class AdminController {

    @Resource
    private FileSettingService service;

    @RequestMapping("/")
    public String index(HttpServletRequest request, ModelMap modelMap){
        FileSetting fileSetting  = service.getSetting();
        if (ObjectUtil.isNotNull(fileSetting)){
            modelMap.put("path",fileSetting.getPath());
            modelMap.put("cachePath",fileSetting.getCachePath());
            modelMap.put("password",fileSetting.getPassword());
            modelMap.put("appId",fileSetting.getAppId());
            modelMap.put("appKey",fileSetting.getAppKey());
            modelMap.put("secretKey",fileSetting.getSecretKey());
            modelMap.put("signKey",fileSetting.getSignKey());
        }

        return "Admin/admin-index";
    }

    @PostMapping("/add")
    public ResponseData submit( FileSetting setting)
    {
        service.settingFile(setting);
        return new ResponseData();

    }

    public synchronized void read(File s){

        File[] files =s.listFiles();
        for (File file1 : files ) {
            if (file1.isDirectory()){
                new Thread(()->read(file1)).start();
            }
            if (file1.getPath().contains("pixiv"))   System.out.println(file1.toString());

        }
    }
    

}
