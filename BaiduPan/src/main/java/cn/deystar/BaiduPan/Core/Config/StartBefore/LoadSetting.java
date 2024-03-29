package cn.deystar.BaiduPan.Core.Config.StartBefore;

import cn.deystar.Setting.Setting.Service.FileSettingServiceImpl;
import cn.deystar.Setting.UnSync.MappedService.impl.UnSyncMappedServiceImpl;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import cn.deystar.Setting.Setting.Service.FileSettingService;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 加载设置到内存中
 */
public class LoadSetting implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {

    FileSettingService settingService = new FileSettingServiceImpl();
    UnSyncMappedServiceImpl unSyncMappedService = new UnSyncMappedServiceImpl();
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        settingService.getSetting();

        unSyncMappedService.readFile();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
