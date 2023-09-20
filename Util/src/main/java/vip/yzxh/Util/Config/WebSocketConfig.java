package vip.yzxh.Util.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author Yeung Ming Luhyun 杨名 字露煊
 * 双向统信
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter exporter() {
        return new ServerEndpointExporter();
    }
}
