package com.example.baidusync.Util.FileService;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
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


    private RequestNetDiskService diskService = SpringUtil.getBean(RequestNetDiskService.class);

    static AtomicInteger atomicInteger = new AtomicInteger(0);

    private static ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

    public static ThreadPoolTaskExecutor executor (){
        if (atomicInteger.get() == 0){
            initExecutor(8,10);
        }
       return threadPoolTaskExecutor;
    }

    public static void initExecutor(Integer coreSize,Integer maxSize){
        threadPoolTaskExecutor.setCorePoolSize(coreSize);
        threadPoolTaskExecutor.setMaxPoolSize(maxSize);
        threadPoolTaskExecutor.setBeanName("sendFileToNetDisk");
        threadPoolTaskExecutor.initialize();

    }



    /**
     * 获取队列
     */
    public void TurnOnSendFile() {
        new Thread(() -> {
            while (true){
                if (!SystemCache.isEmpty()) {
                    ThreadPoolTaskExecutor executor = NetDiskThreadPool.executor();
                    Map<String, Object> map = SystemCache.get();
                    if (executor.getActiveCount() > 8){
                        try {
                            map.wait(1200000);
                        } catch (InterruptedException e) {
                            run(map);
                        }
                    }
                    run(map);

                }else {
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
    public void run(Map<String,Object> map){
        String name = (String) map.get("name");
        Long size = (Long) map.get("size");
        String parent = (String) map.get("parent");
        List<FileAndDigsted> digsteds = (List<FileAndDigsted>) map.get("fileList");
        diskService.goSend(name, parent, size, digsteds);
    }
}
