package com.example.baidusync.Admin.Service;

import com.example.baidusync.Admin.Entity.FileSetting;

public interface FileSettingService {


    Object settingFile(FileSetting fileSetting);

    FileSetting getSetting();

    /**
     * 已经有了返回true 没有返回false
     * @param fileSetting
     * @return
     */
    boolean excites(FileSetting fileSetting);
}
