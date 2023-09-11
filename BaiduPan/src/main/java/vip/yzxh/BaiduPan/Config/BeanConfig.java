package vip.yzxh.BaiduPan.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.yzxh.BaiduPan.Admin.AccreditResponser;
import vip.yzxh.BaiduPan.Admin.AdminController;
import vip.yzxh.BaiduPan.AsyncResponses.AsyncResponses;
import vip.yzxh.Util.HttpServerlet.RequestAndResponse;

import java.io.IOException;

/**
 * @Author YeungLuhyun
 **/
@Configuration
public class BeanConfig {


    /**
     * 异步回调认证
     */
    @Bean
    public void responseNotify() {
        Thread thread = new Thread(() ->
        {
            while (true) {
                AccreditResponser responser = AsyncResponses.getAccreditQueue();
                try {
                    if (responser!= null){
                        responser
                                .getRequestAndResponse()
                                .getResponse()
                                .getOutputStream()
                                .write(responser.getResponseData().toString().getBytes());
                    }

                    Thread.sleep(12000);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("异步response");
        thread.start();
    }
}
