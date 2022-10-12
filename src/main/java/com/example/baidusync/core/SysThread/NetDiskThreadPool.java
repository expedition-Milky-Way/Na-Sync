package com.example.baidusync.core.SysThread;

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
import com.example.baidusync.core.SendQueue;
import org.apache.ibatis.logging.LogException;
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
@Service
public class NetDiskThreadPool implements NetDiskThreadPoolService {

    static Integer FINAL_TASK_NUM = 1;
    //百度网盘最大允许10个Task
    static final Integer MAX_TASK_NUM = 10;

    private static RequestNetDiskService diskService = SpringUtil.getBean(RequestNetDiskService.class);

    private static FileSettingService fileSettingService = SpringUtil.getBean(FileSettingService.class);

    static AtomicInteger activeTaskNum = new AtomicInteger(0);

    private static ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();


    private void initExecutor() {
        FileSetting setting = fileSettingService.getSetting();
        Integer coreSize = setting.getTaskNum();
        FINAL_TASK_NUM = setting.getTaskNum();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(MAX_TASK_NUM);
        executor.setBeanName("sendFileToNetDisk");
        executor.initialize();

    }


    /**
     * 获取队列
     */
    @Override
    public void TurnOnSendFile() {
        new Thread(() -> {
            initExecutor(); //初始化线程池
            while (true) {
                Integer canTaskNum = FINAL_TASK_NUM - activeTaskNum.get();
                if (canTaskNum > 0) {
                    for (int i = 0; i < FINAL_TASK_NUM; i++) {
                        Map<String, Object> map = SendQueue.get();
                        if (map != null) {
                            executor.execute(() -> run(map));
                            LogEntity log = new LogEntity(
                                    "", "获取文件" + (String) map.get("name") + "启动同步，当前任务数量" + activeTaskNum.get(),
                                    LogEntity.LOG_TYPE_INFO);
                            LogExecutor.addSysLogQueue(log);
                        } else {
                            break;
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
    @Override
    public  void run(Map<String, Object> map) {
        Integer latTaskNum = activeTaskNum.incrementAndGet();
        String name = (String) map.get("name");
        Long size = (Long) map.get("size");
        String parent = (String) map.get("parent");
        String tempPath = (String) map.get("temPath");
        List<FileAndDigsted> digsteds = (List<FileAndDigsted>) map.get("fileList");
        diskService.goSend(name, parent, size, digsteds, tempPath);
        Integer nowTask = activeTaskNum.decrementAndGet();
        if (latTaskNum < nowTask) {
            LogEntity log = new LogEntity(
                    "", "上传任务执行任务进行中：" + executor.getActiveCount() + "线程池可能存在问题:nowTask=" + nowTask, LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(log);
        } else {
            LogEntity log = new LogEntity("", "文件上传任务（上传" + name + "任务）结束", LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(log);
        }
    }

    /**
     * 关闭线程池及任务
     */
    @Override
    public  boolean turnOff(){
        Map<String,Object> map = null;
        if (!SendQueue.isEmpty()){
            map = SendQueue.get();
        }
        if (map == null || map.isEmpty() && activeTaskNum.get() ==0 || executor.getActiveCount() == 0 ){
            executor.shutdown();
            return true;
        }
        return  false;
    }
}
