package com.example.baidusync.core.SysBeanConfig;

import javafx.concurrent.ScheduledService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author YeungLuhyun
 * 定时线程池
 */
@Configuration
public class TimerTask {


    private ScheduledExecutorService executor = null;


    @Bean
    public ScheduledExecutorService executor(){
        if (executor == null){
            executor =Executors.newScheduledThreadPool(2);
        }
        return executor;
    }



}
