package com.example.baidusync.Util.FileService;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.core.SystemCache;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 杨 名 (字 露煊)
 * 控制上传并发数量的线程池
 */

public class NetDiskThreadPool {

    static Integer FINAL_TASK_NUM = 10;

    static final Integer MAX_TASK_NUM = 10;

    private static RequestNetDiskService diskService = SpringUtil.getBean(RequestNetDiskService.class);

    private static FileSettingService fileSettingService = SpringUtil.getBean(FileSettingService.class);

    static AtomicInteger activeTaskNum = new AtomicInteger(0);

    private static ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();


    private static void initExecutor() {
        FileSetting setting = fileSettingService.getSetting();
        Integer coreSize = setting.getTaskNum();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(MAX_TASK_NUM);
        executor.setBeanName("sendFileToNetDisk");
        executor.initialize();

    }


    /**
     * 获取队列
     */
    public static void TurnOnSendFile() {
        new Thread(() -> {
            initExecutor(); //初始化线程池
            while (true) {
                if (!SystemCache.isEmpty()) {
                    Integer canTaskNum = FINAL_TASK_NUM - activeTaskNum.get();
                    if (canTaskNum > 0){
                        Integer top = activeTaskNum.incrementAndGet();
                        for (int i = 0 ; i< FINAL_TASK_NUM ; i++){
                            Map<String, Object> map = SystemCache.get();
                            executor.execute(()->run(map, top));
                            LogEntity log = new LogEntity(
                                    "","获取文件"+(String)map.get("name")+"启动同步，当前任务数量"+activeTaskNum.get(),
                                    LogEntity.LOG_TYPE_INFO);
                            LogExecutor.addSysLogQueue(log);
                        }
                    }
                } else {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, "获取缓存文件map").start();
    }

    /**
     * 执行发送视频文件
     */
    public static void run(Map<String, Object> map,Integer top) {
        String name = (String) map.get("name");
        Long size = (Long) map.get("size");
        String parent = (String) map.get("parent");
        String tempPath = (String) map.get("temPath");
        List<FileAndDigsted> digsteds = (List<FileAndDigsted>) map.get("fileList");
        diskService.goSend(name, parent, size, digsteds,tempPath);
        Integer nowTask = activeTaskNum.decrementAndGet();
        if (top.intValue() < nowTask.intValue()){
            LogEntity log = new LogEntity("","发送视频任务执行任务进行中："+executor.getActiveCount(),LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(log);
        }
    }
}
