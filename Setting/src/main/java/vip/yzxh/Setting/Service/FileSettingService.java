package vip.yzxh.Setting.Service;


import vip.yzxh.Setting.Entity.FileSetting;

public interface FileSettingService {


    Object settingFile(FileSetting fileSetting);

    FileSetting getSetting();


}
