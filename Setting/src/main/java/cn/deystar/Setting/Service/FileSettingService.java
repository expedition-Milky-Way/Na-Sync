package cn.deystar.Setting.Service;


import cn.deystar.Setting.Entity.FileSetting;

public interface FileSettingService {


    Object settingFile(FileSetting fileSetting);

    FileSetting getSetting();


    FileSetting updateSetting(FileSetting setting);
}
