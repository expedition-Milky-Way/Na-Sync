package com.example.baidusync;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class BaiduSyncApplicationTests {

    @Resource
    private LogService logService;

    @Test
    void contextLoads() {
    }

    @Test
    void testLog(){

        System.out.println( logService.getLog());
    }





}
