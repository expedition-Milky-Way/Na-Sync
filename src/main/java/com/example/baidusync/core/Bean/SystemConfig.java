package com.example.baidusync.core.Bean;


import com.example.baidusync.Util.SystemLog.LogExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;



@Configuration
public class SystemConfig {


    /**
     * 运行启动日志插入
     */
    @Bean
    public void LogRun(){
        new Thread(()->{
            LogExecutor.takeLogQueue();
        }).start();

    }


}
