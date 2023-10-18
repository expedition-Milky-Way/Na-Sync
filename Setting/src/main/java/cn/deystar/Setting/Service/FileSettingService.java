package cn.deystar.Setting.Service;


import cn.deystar.Setting.Entity.FileSetting;
import cn.deystar.Util.BaiduPanResponse.TokenResponse;

public interface FileSettingService {


    Object settingFile(FileSetting fileSetting);

    FileSetting getSetting();


    FileSetting updateSetting(FileSetting setting);

    TokenResponse getToken();

    void holdOn();

    void goOn();
}
