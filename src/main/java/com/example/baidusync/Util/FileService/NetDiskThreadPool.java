package com.example.baidusync.Util.FileService;

import cn.hutool.json.JSONObject;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.core.SystemCache;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 杨 名 (字 露煊)
 * 控制上传并发数量的线程池
 */
public class NetDiskThreadPool {

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
}
