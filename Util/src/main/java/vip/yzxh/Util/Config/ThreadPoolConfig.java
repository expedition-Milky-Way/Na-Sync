package vip.yzxh.Util.Config;

import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 */
@Configuration

public class ThreadPoolConfig {

    @Value("${default-pool.core}")
    private Integer coreSize;
    @Value("${default-pool.max}")
    private Integer maxSize;
    @Value("${default-pool.queue}")
    private Integer queueSize;
    @Bean
    public ThreadPoolTaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(coreSize);
        executor.setMaxPoolSize(maxSize);
        executor.setQueueCapacity(queueSize);
        executor.setThreadGroupName("BaiduPanSync--DefaultThreadGroup");
        return executor;
    }


}
