package com.example.baidusync.Util.SystemLog;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author 杨 名 (字 露煊)
 * 插入日志队列
 */
public class LogExecutor {

    private static LogService logServiceBean = SpringUtil.getBean(LogService.class);

    private static Executor executor = Executors.newFixedThreadPool(2);

    private static LinkedBlockingQueue<LogEntity>  sysLogQueue = new LinkedBlockingQueue<>(100);


    public static boolean addSysLogQueue(LogEntity logEntity){
        if (sysLogQueue.size()<100){
            try {
                logEntity.wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
       return sysLogQueue.offer(logEntity);
    }

    public static void takeLogQueue(){
        executor.execute(()->{
            while (true){
                if (sysLogQueue.size()>0){
                    logServiceBean.InsertInto(sysLogQueue.poll());
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
