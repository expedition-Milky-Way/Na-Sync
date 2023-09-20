package vip.yzxh.BaiduPan.NetDisk;

import com.alibaba.fastjson.JSONObject;

import vip.yzxh.Util.BaiduPanResponse.DeviceCodeResponse;
import vip.yzxh.Util.BaiduPanResponse.TokenResponse;

import vip.yzxh.Util.Util.FileAndDigsted;

import java.util.List;


public interface RequestNetDiskService {
    DeviceCodeResponse deviceCode(String appKey);




    TokenResponse getToken(String deviceCode);

    TokenResponse getToken(String deviceCode, Integer expires, Integer sleep);

    Object getBaiduUsInfo();
    /**
     * 开始上传
     * @param name 源文件名
     * @param parent 源文件父路径
     * @param size 源文件大小
     * @param fileAndDigested
     */
    void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigested, String tempPath);


    boolean hasDir(String path);

    JSONObject postCreateFile(String path, Long size, Integer isDir,
                              List<String> blokList, String uploadId);

    boolean postCreateNetDisk(String path);





}
