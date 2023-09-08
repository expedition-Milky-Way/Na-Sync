package vip.yzxh.BaiduPan.Admin;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.yzxh.BaiduPan.NetDisk.RequestNetDiskService;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;
import vip.yzxh.Setting.Service.FileSettingServiceImpl;
import vip.yzxh.Util.ResponseData;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Controller
public class AdminController {

    @Resource
    private  FileSettingService fileSettingService;
    @Resource
    private RequestNetDiskService requestNetDiskService;



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
        List<Integer> taskNumList = new ArrayList<>();
        for (Integer i = 1; i < 11; i++) {
            taskNumList.add(i);
        }
        modelMap.put("taskNumList", taskNumList);
        return "Admin/admin-index";
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseData submit(FileSetting setting, HttpServletRequest request) {
        String ctx = request.getHeader("host");
        if (setting.isEmpty()) {
            //返回报错信息
            return new ResponseData("设置不能为空");
        }
        JSONObject jsonObject = requestNetDiskService.deviceCode(setting.getAppKey());
        if (jsonObject.getString("error_description") != null) {
            //返回报错信息
            return new ResponseData("AppKey不正确，检查是否有空格存在或是否有误" + jsonObject.getString("error_description"));
        }

        fileSettingService.settingFile(setting);

        jsonObject.put("ctx", ctx);
        return new ResponseData(jsonObject);
    }


    /**
     * 下载日志
     */
    @PostMapping("/downLog")
    @ResponseBody
    public String down() {
        return null;
    }


    /**
     * 打开百度网盘验证
     *
     * @param
     */
    @RequestMapping("/netDiskConfirm")
    public String getNetDiskConrim(String url, HttpServletRequest request, ModelMap modelMap) {
        String ctx = "http://" + request.getHeader("host");
        modelMap.put("ctx", ctx);
        modelMap.put("url", url);
        return "Admin/confirmSettting";
    }

    @PostMapping("/netDiskConfirmOk")
    @ResponseBody
    public ResponseData confirmOk() {
        requestNetDiskService.setAuthIsOk();
        boolean isok = requestNetDiskService.accessToken();
        if (isok) {
            FileSetting fileSetting = fileSettingService.getSetting();
            requestNetDiskService.setSchTask(fileSetting);
            return new ResponseData();
        } else {
            return new ResponseData("请确认百度网盘扫描二维码完成后点击确认按钮");
        }
    }


}
