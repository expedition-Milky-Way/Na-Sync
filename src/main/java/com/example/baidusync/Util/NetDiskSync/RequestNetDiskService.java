package com.example.baidusync.Util.NetDiskSync;

import com.alibaba.fastjson.JSONObject;

public interface RequestNetDiskService {
    JSONObject deviceCode(String appKey);

    void getBaiduUsInfo();
}
