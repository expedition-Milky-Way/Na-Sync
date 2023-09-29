package cn.deystar.BaiduPan.Core.Config.StartBefore;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import cn.deystar.BaiduPan.UserSetting.Controller.SettingController;
import cn.deystar.Setting.Service.FileSettingService;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 加载设置到内存中
 */
public class LoadSetting implements ApplicationListener<ApplicationContextInitializedEvent>, Ordered {

    FileSettingService settingService = (FileSettingService) SpringUtil.getBean(SettingController.class);
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        settingService.getSetting();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
