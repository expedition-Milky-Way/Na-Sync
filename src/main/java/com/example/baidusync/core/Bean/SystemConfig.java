package com.example.baidusync.core.Bean;

import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Admin.Service.FileSettingMapper.FileSettingMapping;
import com.example.baidusync.Admin.Service.FileSettingService;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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
