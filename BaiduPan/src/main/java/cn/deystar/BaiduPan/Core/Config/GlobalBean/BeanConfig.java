package cn.deystar.BaiduPan.Core.Config.GlobalBean;


import cn.deystar.BaiduPan.Core.Compress.CompressUploadService;
import cn.deystar.BaiduPan.Core.OS.watchFile.Service.impl.WatchFileServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.BaiduPan.Core.OS.watchFile.Monitor.FileMonitor;
import cn.deystar.BaiduPan.Core.OS.watchFile.Monitor.FileMonitorService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.annotation.Resource;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author YeungLuhyun
 **/
@Configuration
public class BeanConfig {

    @Resource
    private FileSettingService fileSettingService;


    @Value("${default-pool.core}")
    private Integer coreSize;
    @Value("${default-pool.max}")
    private Integer maxSize;
    @Value("${default-pool.queue}")
    private Integer queueSize;

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadGroupName("BaiduPanSync--DefaultThreadGroup");
        return executor;
    }



    /**
     * 装配文件监听
     */
    @Bean
    public FileMonitorService fileMonitorService() {
        FileMonitor service = new FileMonitor(500L);
        if (fileSettingService.getSetting() != null && fileSettingService.getSetting().getPath() != null) {
            File file = new File(fileSettingService.getSetting().getPath());
            service.monitor(file, new WatchFileServiceImpl());
            try {
                service.start();
            } catch (Exception ignored) {

            }
        }
        return service;
    }

    /**
     * 设置登录状态时的不可重入锁（每次并发只有一个用户能够成功登录）
     *
     * @return
     */
    @Bean
    public Lock loginLock() {
        return new ReentrantLock(false);
    }

    /**
     * WebSocket
     *
     * @return
     */
    @Bean
    public ServerEndpointExporter exporter() {
        return new ServerEndpointExporter();
    }


}
