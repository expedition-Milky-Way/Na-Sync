package cn.deystar.BaiduPan.Core.Config.StartBefore;

import cn.deystar.BaiduPan.Core.Compress.CompressUploadService;
import cn.deystar.Util.ScanAndZip.Zip.Bean.FileListBean;
import cn.hutool.extra.spring.SpringUtil;
import javafx.util.Pair;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;


/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class TaskStarter implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {


    private CompressUploadService compressUploadService = SpringUtil.getBean(CompressUploadService.class);

    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
      //生产者和消费者的启动
        Thread consumerThread = new Thread(compressUploadService::consumer);
        Thread producer = new Thread(compressUploadService::getFileOfChange);
        consumerThread.setDaemon(true);
        producer.setDaemon(true);
        consumerThread.start();
        producer.start();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
