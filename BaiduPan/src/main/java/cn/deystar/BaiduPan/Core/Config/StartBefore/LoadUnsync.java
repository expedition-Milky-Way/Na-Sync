package cn.deystar.BaiduPan.Core.Config.StartBefore;

import cn.deystar.Setting.UnSync.Entity.UnSyncEntity;
import cn.deystar.Setting.UnSync.MappedService.impl.UnSyncMappedServiceImpl;
import cn.deystar.Util.Util.ConfigFileTemplate;
import com.alibaba.fastjson.JSONArray;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 */
public class LoadUnsync implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {

    }

    @Override
    public int getOrder() {
        return 0;
    }
}
