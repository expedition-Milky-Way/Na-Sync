package cn.deystar.BaiduPan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "cn.deystar")
public class BaiduSyncApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaiduSyncApplication.class, args);
    }

}
