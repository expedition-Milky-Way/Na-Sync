package com.example.baidusync.Admin.Service;

import com.example.baidusync.Admin.Entity.FileSetting;

public interface FileSettingService {


    Object settingFile(FileSetting fileSetting);

    FileSetting getSetting();
}
