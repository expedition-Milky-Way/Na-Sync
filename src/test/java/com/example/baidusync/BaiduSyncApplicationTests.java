package com.example.baidusync;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BaiduSyncApplicationTests {

    @Resource
    private LogService logService;
    @Resource
    private RequestNetDiskService netDiskService;

    @Test
    void contextLoads() {
    }

    @Test
    void testLog(){

        System.out.println( logService.getLog());
    }

    @Test
    void testThread(){
        new Thread(()->{
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setMaxPoolSize(10);
            executor.setCorePoolSize(8);
            executor.initialize();
            List<Integer> list =new ArrayList<>();
            for (int j = 0; j < 10 ; j++){
                list.add(j);
            }

            for (int i = 0 ; i< 8 ; i++){
                int task =  executor.getActiveCount();
                executor.execute(()->{
                    int x = 0;
                    while (true){
                        if (x > list.size() -1) x = 0;
                        System.out.println(Thread.currentThread().getName()+"::::::"+ list.get(x));
                        System.out.println(task);
                        x++;
                    }

                });

            }
        }).start();

    }

    /**
     * 获取用户信息
     */
    @Test
    public void getUserInfo(){
        netDiskService.getBaiduUsInfo();
    }





}
