package vip.yzxh.BaiduPan.NetDisk;

import com.alibaba.fastjson.JSONObject;
import vip.yzxh.Setting.Entity.FileSetting;
import vip.yzxh.Util.FileAndDigsted;

import java.util.List;


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
    void goSend(String name, String parent, Long size, List<FileAndDigsted> fileAndDigsted, String tempPath);


    boolean hasDir(String path);

    JSONObject postCreateFile(String path, Long size, Integer isDir,
                              List<String> blokList, String uploadId);

    boolean postCreateNetDisk(String path);

    Integer setAuthIsOk();


    void setSchTask(FileSetting fileSetting);
}
