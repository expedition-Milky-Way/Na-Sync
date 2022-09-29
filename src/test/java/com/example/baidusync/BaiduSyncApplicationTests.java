package com.example.baidusync;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileService.NetDiskThreadPool;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.Util.SystemLog.LogService;
import com.example.baidusync.Util.TempFileService.TempFileService;
import com.mysql.cj.PreparedQuery;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BaiduSyncApplicationTests {

    @Resource
    private LogService logService;
    @Resource
    private RequestNetDiskService netDiskService;
    @Resource
    private FileSettingService settingService;
    @Resource
    private TempFileService tempFileService;
    @Test
    void contextLoads() {
    }

    @Test
    void testLog(){
        logService.getLog();
    }

    @Test
    void testThread(){
        new Thread(()->{
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setMaxPoolSize(10);
            executor.setCorePoolSize(8);
            executor.initialize();
            List<Integer> list =new ArrayList<>();
            for (int j = 0; j < 10 ; j++){
                list.add(j);
            }

            for (int i = 0 ; i< 8 ; i++){
                int task =  executor.getActiveCount();
                executor.execute(()->{
                    int x = 0;
                    while (true){
                        if (x > list.size() -1) x = 0;
                        System.out.println(Thread.currentThread().getName()+"::::::"+ list.get(x));
                        System.out.println(task);
                        x++;
                    }

                });

            }
        }).start();

    }

    /**
     * 获取用户信息
     */
    @Test
    public void getUserInfo(){
        netDiskService.getBaiduUsInfo();
    }


    /**
     * 设置定时线程
     */
    @Test
    public void testSch(){
        FileSetting fileSetting = new FileSetting();
        fileSetting.setDateTime("01:00");
        fileSetting.setPassword("123123");
        fileSetting.setCachePath("D:/JavaWorkSpace/g/zip_tryFile");
        fileSetting.setPath("D:/JavaWorkSpace/g/files");
        fileSetting.setTaskNum(2);
        fileSetting.setAppId("27205775");
        fileSetting.setAppKey("cjl9y2pbLPwlv3VQIa3OhPhVoxGr9Lzv");
        fileSetting.setSecretKey("1EvQX6yUnl5edTnkSa5WvyTUGQMABRzT");
        fileSetting.setSignKey("ov*p@DlS2V@L=91WVq#HpuQIM1cQLF~M");
        netDiskService.setSchTask(fileSetting);
    }

    @Test
    public void testLogic(){
        FileSetting setting = settingService.getSetting();
        JSONObject jsonObject = netDiskService.deviceCode(setting.getAppKey());
        System.out.printf(jsonObject.toString());
        netDiskService.accessToken();
        netDiskService.getBaiduUsInfo();
     /*   ScanFileUtil scanFileUtil = new ScanFileUtil(setting.getCachePath(),setting.getPassword());
        scanFileUtil.doSomething(setting.getPath());*/
        if (setting.getCachePath() != null){
            File cacheFile = new File(setting.getCachePath());
            tempFileService.scanZipFile(cacheFile.listFiles());
            NetDiskThreadPool.TurnOnSendFile();
        }else{
         LogEntity log = new LogEntity("","还没有设置缓存路径诶~",LogEntity.LOG_TYPE_WARN);
            LogExecutor.addSysLogQueue(log);
        }
        System.out.println("测试结束");
    }



}
