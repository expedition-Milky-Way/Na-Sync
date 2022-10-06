package com.example.baidusync.Util.NetDiskSync;

import com.alibaba.fastjson.JSONObject;
import com.example.baidusync.Admin.Entity.FileSetting;
import com.example.baidusync.Util.FileAndDigsted;

import java.util.List;
import java.util.Map;
import java.util.Timer;

public interface RequestNetDiskService {
    JSONObject deviceCode(String appKey);

    boolean accessToken();

    void getBaiduUsInfo();
    /**
     * 开始上传
     * @param name 源文件名
     * @param parent 源文件父路径
     * @param size 源文件大小
     * @param fileAndDigsted
     */
    void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigsted,String tempPath);



    Integer setAuthIsOk();


    Timer setSchTask(FileSetting fileSetting);
}
