package com.example.baidusync.core.SysBeanConfig;

import com.example.baidusync.Util.SystemLog.LogExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConstBean {


    /**
     * 启动插入日志队列线程池和线程
     */
    @Bean
    public void startLogExcutor(){
        new Thread(()->{
            LogExecutor.takeLogQueue();
        }).start();

    }
}
