package com.example.baidusync.Util.NetDiskSync;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface RequestNetDiskService {
    JSONObject deviceCode(String appKey);

    void getBaiduUsInfo();

    JSONObject postNetDist(String fileName, String filePath, List<String> md5, Integer size);
}
