package com.example.baidusync.Util.SystemLog;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 杨 名 (字 露煊)
 * 插入日志队列
 */
public class LogExecutor {

    private static LogService logServiceBean = SpringUtil.getBean(LogService.class);


    private static LinkedBlockingQueue<LogEntity> sysLogQueue = new LinkedBlockingQueue<>(100);


    public static boolean addSysLogQueue(LogEntity logEntity) {
        if (sysLogQueue.size() > 100) {
            try {
                logEntity.wait(100);
            } catch (InterruptedException e) {
            }
        }
        return sysLogQueue.offer(logEntity);
    }

    public static void takeLogQueue() {
            while (true) {
                if (sysLogQueue.size() > 0) {
                    LogEntity entity = sysLogQueue.poll();
                    if (!Objects.isNull(entity)) logServiceBean.InsertInto(entity);
                }else {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

    }


}
