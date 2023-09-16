package vip.yzxh.BaiduPan.Admin.Controller;

import cn.hutool.core.util.ObjectUtil;


import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import vip.yzxh.BaiduPan.AsyncResponses.AsyncResponses;
import vip.yzxh.BaiduPan.BaiduConst.BaiduConst;
import vip.yzxh.BaiduPan.BaiduPanResponse.DeviceCodeResponse;
import vip.yzxh.BaiduPan.BaiduPanResponse.TokenResponse;
import vip.yzxh.BaiduPan.NetDisk.RequestNetDiskService;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Setting.Service.FileSettingService;
import vip.yzxh.Util.HttpServerlet.RequestAndResponse;
import vip.yzxh.Util.HttpServerlet.Response.ResponseData;
import vip.yzxh.Util.HttpServerlet.Response.Success;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/author")
public class AdminController {

    @Resource
    private FileSettingService fileSettingService;
    @Resource
    private RequestNetDiskService requestNetDiskService;

    @Resource
    private ThreadPoolTaskExecutor executor;

    private static final AtomicInteger tokenGetting = new AtomicInteger(0);
    private  static ResponseData responseData = null;

    @GetMapping("/")
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
        modelMap.put("title", "登录");
        List<Integer> taskNumList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5});

        modelMap.put("taskNumList", taskNumList);
        return "Main/Setting/setting";
    }


    @PutMapping()
    @ResponseBody
    public ResponseData submit(FileSetting setting,
                               HttpServletRequest request, HttpServletResponse response) {

        DeviceCodeResponse deviceCodeResponse = requestNetDiskService.deviceCode(setting.getAppKey());

        executor.execute(() -> {
           tokenGetting.incrementAndGet();
            Thread.currentThread().setName("设置setting.json");
            fileSettingService.settingFile(setting);

            TokenResponse tokenDetail = requestNetDiskService.getToken(
                    deviceCodeResponse.getDeviceCode(),
                    deviceCodeResponse.getExpires(),
                    deviceCodeResponse.getInterval());
            if (tokenDetail != null && tokenDetail.getToken() != null) {
                BaiduConst.setTokenMsg(tokenDetail);
                responseData = new Success("验证成功，跳转到应用页面", null, "/index");
            } else {
                responseData = new ResponseData("验证失败");
            }
            tokenGetting.decrementAndGet();
        });
        return new Success(deviceCodeResponse);
    }

    /**
     * 前端在接收到了submit的response后，立刻请求该接口，该接口将会异步返回
     *
     * @param request
     * @param response
     */
    @PostMapping()
    @ResponseBody
    public ResponseData canLogin(HttpServletRequest request, HttpServletResponse response) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {

        }
        while (tokenGetting.get() != 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
        return responseData;
    }

}
