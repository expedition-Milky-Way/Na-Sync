package vip.yzxh.BaiduPan.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vip.yzxh.BaiduPan.Admin.Controller.AccreditResponser;
import vip.yzxh.BaiduPan.AsyncResponses.AsyncResponses;

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

                    if (responser!= null){
                        try {
                            responser
                                    .getRequestAndResponse()
                                    .getResponse()
                                    .getOutputStream()
                                    .write(responser.getResponseData().toString().getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

            }
        });
        thread.setDaemon(true);
        thread.setName("异步response");
        thread.start();
    }
}
