package com.example.baidusync.Util.FileService;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskImpl;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.core.SystemCache;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

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

    static Integer FINAL_TASK_NUM;

    private static RequestNetDiskService diskService = SpringUtil.getBean(RequestNetDiskService.class);

    private static FileSettingService fileSettingService = SpringUtil.getBean(FileSettingService.class);

    static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

    /**
     * 百度网盘后端最大Task是10
     *
     * @return
     */
    public static ThreadPoolTaskExecutor executor() {
        if (atomicInteger.get() == 0) {
            FileSetting fileSetting = fileSettingService.getSetting();
            if (fileSetting.getTaskNum() == null) {
                initExecutor(2, 10);
                FINAL_TASK_NUM = 2;
            }
            FINAL_TASK_NUM = fileSetting.getTaskNum();
            initExecutor(FINAL_TASK_NUM, 10);

        }
        atomicInteger.incrementAndGet();
        return threadPoolTaskExecutor;
    }

    private static void initExecutor(Integer coreSize, Integer maxSize) {
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setBeanName("sendFileToNetDisk");
        threadPoolTaskExecutor.initialize();

    }


    /**
     * 获取队列
     */
    public static void TurnOnSendFile() {
        new Thread(() -> {
            while (true) {
                if (!SystemCache.isEmpty()) {
                    ThreadPoolTaskExecutor executor = NetDiskThreadPool.executor();
                    Map<String, Object> map = SystemCache.get();
                    if (executor.getActiveCount() > FINAL_TASK_NUM) {
                        try {
                            map.wait(1200000);
                        } catch (InterruptedException e) {
                            if (executor.getActiveCount() > FINAL_TASK_NUM) {
                                SystemCache.set(map);
                            }else{
                              executor.execute(()->run(map));
                            }
                        }
                    }
                    executor.execute(()->run(map));
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
    public static void run(Map<String, Object> map) {
        String name = (String) map.get("name");
        Long size = (Long) map.get("size");
        String parent = (String) map.get("parent");
        List<FileAndDigsted> digsteds = (List<FileAndDigsted>) map.get("fileList");
        diskService.goSend(name, parent, size, digsteds);
    }
}
