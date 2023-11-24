package cn.deystar.BaiduPan.UserSetting.Controller;


import cn.deystar.BaiduPan.Core.BaiduRequest.Token.TokenService;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.MonitorSerivce.WatchFileServiceImpl;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import cn.deystar.BaiduPan.AppBar.Entity.BarEntity;
import cn.deystar.BaiduPan.AppBar.Service.BarService;
import cn.deystar.Util.BaiduPanResponse.DeviceCodeResponse;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import cn.deystar.Setting.Setting.Entity.FileSetting;
import cn.deystar.Setting.Setting.Service.FileSettingService;
import cn.deystar.Util.HttpServerlet.Response.ResponseData;
import cn.deystar.Util.HttpServerlet.Response.Success;
import cn.deystar.Util.HttpServerlet.Response.Warning;
import cn.deystar.BaiduPan.Core.Client.DirectoryWatcher.Monitor.FileMonitorService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/setting")
public class SettingController {
    @Resource
    private FileSettingService fileSettingService;
    @Resource
    private TokenService tokenService;
    @Resource
    private BarService barService;
    @Resource
    private ThreadPoolTaskExecutor executor;
    @Resource
    private FileMonitorService fileMonitorService;
    private static final AtomicInteger tokenGetting = new AtomicInteger(0);

    /**
     * 百度设置页面
     *
     * @param request
     * @param modelMap
     * @return
     */
    @GetMapping(value = "baidu")
    public String setting(HttpServletRequest request, ModelMap modelMap) {

        FileSetting fileSetting = fileSettingService.getSetting();
        if (ObjectUtil.isNotNull(fileSetting)) {
            modelMap.put("pathEncryption", fileSetting.getPathEncryption());
            modelMap.put("appId", fileSetting.getAppId());
            modelMap.put("appKey", fileSetting.getAppKey());
            modelMap.put("secretKey", fileSetting.getSecretKey());
            modelMap.put("signKey", fileSetting.getSignKey());
            modelMap.put("taskNum", fileSetting.getTaskNum());
            modelMap.put("version", fileSetting.getVersion());
        }
        BarEntity bar = barService.getBar("/setting/baidu");
        modelMap.put("title", bar.getTitle());
        List<Integer> taskNumList = Arrays.asList(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        modelMap.put("taskNumList", taskNumList);
        return "Main/Setting/baidu";
    }


    @PutMapping(value = "baidu")
    @ResponseBody
    public ResponseData submit(@RequestBody FileSetting setting) {
        //关键参数判空
        if (setting.getAppId() != null && !setting.getAppId().trim().isEmpty() &&
                setting.getAppKey() != null && !setting.getAppKey().isEmpty() &&
                setting.getSecretKey() != null && !setting.getSecretKey().isEmpty() &&
                setting.getSignKey() != null && !setting.getSignKey().trim().isEmpty()) {


            DeviceCodeResponse deviceCodeResponse = tokenService.deviceCode(setting.getAppKey());

            if (deviceCodeResponse != null && deviceCodeResponse.isAllNotNull()) {
                fileSettingService.updateSetting(setting);
                FileSetting finalSetting = setting;
                executor.execute(() -> {
                    while (tokenGetting.get() > 0) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    tokenGetting.incrementAndGet();
                    Thread.currentThread().setName("设置setting.json");
                    TokenResponse tokenDetail = tokenService.getToken(
                            deviceCodeResponse.getDeviceCode(),
                            deviceCodeResponse.getExpires(),
                            deviceCodeResponse.getInterval());
                    if (tokenDetail != null && tokenDetail.getAccessToken() != null) {
                        finalSetting.setToken(tokenDetail);
                    }
                    tokenGetting.decrementAndGet();
                    fileSettingService.updateSetting(finalSetting);
                });
                return new Success(deviceCodeResponse);
            }

            return new Warning("请输入正确的appId appKey SecretKey SignKey");

        }
        return  new Warning("请天下所有参数");
    }

    /**
     * 前端在接收到了submit的response后，立刻请求该接口，该接口将会异步返回
     */
    @PostMapping(value = "baidu")
    @ResponseBody
    public ResponseData canLogin() {
        while (tokenGetting.get() != 0) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
        FileSetting setting = fileSettingService.getSetting();
        if (setting.isAllNotNull()) {
            return new Success("全部认证成功。启动监管，进行备份", null);
        }
        return new Success();
    }

    @GetMapping("zip")
    public String zipSetting(HttpServletRequest request, ModelMap modelMap) {
        String api = "/setting/zip";
        BarEntity bar = barService.getBar(api);
        FileSetting setting = fileSettingService.getSetting();
        if (setting != null) {
            modelMap.put("version", setting.getVersion());
            modelMap.put("path", setting.getPath());
            modelMap.put("cachePath", setting.getCachePath());
            modelMap.put("compressThread", setting.getCompressThread());
            modelMap.put("isListen", setting.getIsListen());
            modelMap.put("password", setting.getPassword());

            modelMap.put("pathEncryption", setting.getPathEncryption());
            modelMap.put("system", setting.getSystem());
        }

        modelMap.put("title", bar != null ? bar.getTitle() : null);

        //1. 获取可用的超线程数
        Integer cpuCore = Runtime.getRuntime().availableProcessors();
        List<Integer> coreOptions = new ArrayList<>();
        for (int i = 1; i <= cpuCore; i++) {
            coreOptions.add(i);
        }
        modelMap.put("compressCore", coreOptions);

        return "Main/Setting/zip";
    }

    @PostMapping("zip")
    @ResponseBody
    public ResponseData zipSettingSave(@RequestBody FileSetting setting) {
        if (setting != null && setting.getCachePath() != null && setting.getPath() != null) {
            if (setting.getPath().equals(setting.getCachePath())) {
                return new Warning("缓存路径不能与被监听的路径相同");
            }
            String path = setting.getPath().replace("\\", "/");
            String cachePath = setting.getCachePath().replace("\\", "/");
            String[] pathString = path.split("/");
            String[] cachePathString = cachePath.split("/");
            Integer containsNum = 0;
            for (int i = 0; i < Math.min(pathString.length, cachePathString.length); i++) {
                if (pathString[i].equals(cachePathString[i])) {
                    containsNum++;
                }
            }
            if (containsNum == pathString.length) {
                return new Warning("缓存路径不能是被监听路径的子目录");
            }
        }
        fileSettingService.updateSetting(setting);
        //更改文件监听
        String nowWatchPath = fileMonitorService.nowWatch();
        if (nowWatchPath != null &&
                setting.getPath() != null &&
                !setting.getPath().trim().isEmpty() &&
                !nowWatchPath.equals(setting.getPath())
        ) {
            try {
                fileMonitorService.monitor(new File(setting.getPath()), new WatchFileServiceImpl());
            } catch (Exception ignored) {

            }
        }
        setting = fileSettingService.getSetting();
        if (setting.isAllNotNull()) {
            return new Success("全部认证成功。启动监管，进行备份", null);
        }
        return new Success();
    }


}
