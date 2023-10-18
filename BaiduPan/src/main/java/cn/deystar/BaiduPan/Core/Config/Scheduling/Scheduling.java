package cn.deystar.BaiduPan.Core.Config.Scheduling;

import cn.deystar.BaiduPan.Core.BaiduRequest.Token.TokenService;
import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Setting.Service.FileSettingService;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Ming Yeung Luhyun (杨名 字 露煊)
 * 定时任务配置
 */
@Component
public class Scheduling {


    @Resource
    FileSettingService settingService;

    @Resource
    TokenService tokenService;


    /**
     * 每天晚上凌晨两点
     * 如果token还有26小时过期就刷新
     * 如果token过期就刷新
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void FlushToken() {
        FileSetting setting = settingService.getSetting();
        if (setting == null || !setting.isAllNotNull()) return;

        TokenResponse tokenDetail = settingService.getToken();


        Long createTimeSecond = tokenDetail.getCreateTimeForDate().getTime() / 1000L;
        Long expiresTimeSecond = tokenDetail.getExpires().longValue();

        Long expires = createTimeSecond + expiresTimeSecond;
        Long beAboutToExpires = (long) (26 * Math.pow(60, 2)); //26Hours

        Long now = new Date().getTime();
        if (now <= expires || (expires + beAboutToExpires) >= now) {
            settingService.holdOn();
            TokenResponse tokenResponse = tokenService.freshToken();
            if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                setting.setToken(tokenResponse);
                settingService.updateSetting(setting);
            }
            settingService.goOn();
        }

    }


}
