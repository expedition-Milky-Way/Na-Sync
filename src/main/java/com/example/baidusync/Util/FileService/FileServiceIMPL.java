package com.example.baidusync.Util.FileService;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.core.SystemCache;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionService;

/**
 * @author 杨 名 (字 露煊)
 */
@Service
public class FileServiceIMPL implements FileService {

    @Resource
    private RequestNetDiskService netDiskService;


    /**
     * 开始扫描文件
     */
    @Override
    public void goBackup(String zipPath, String filePath, String password) {
        ScanFileUtil scanFileUtil = new ScanFileUtil(zipPath, password);
        scanFileUtil.read(new File(filePath).listFiles());
    }

    /**
     * 计算MD5，并扫描当前缓存临时文件夹下面的每一个tempfile
     */
    @Override
    public void computedMD5(String name, File Directory, Long fileSize, String parent) {
        if (Directory.exists()) {
            new LogEntity("", "md5计算：" + Directory, LogEntity.LOG_TYPE_INFO);
            File[] files = Directory.listFiles();
            String parentName = name.split("/")[name.length() - 1];
            FileAndDigsted fileAndDigsted = new FileAndDigsted();
            Map<String, Object> setMap = new HashMap<>();
            List<FileAndDigsted> fileAndDigstedList = new ArrayList<>();
            setMap.put("name", name);
            setMap.put("size", fileSize);
            setMap.put("parent", parent);
            for (int i = 0; i < files.length; i++) {
                fileAndDigsted.setIndex(i);
                fileAndDigsted.setName(files[i].getName());
                fileAndDigsted.setPath(files[i].getPath());
                fileAndDigsted.setParentName(files[i].getParent());
                fileAndDigsted.setDigsted(SecureUtil.md5(files[i]));
                fileAndDigstedList.add(fileAndDigsted);
            }
            setMap.put("fileList", fileAndDigstedList);
            SystemCache.set(setMap);
            new LogEntity("", "md5计算完毕，进入等待队列：" + Directory, LogEntity.LOG_TYPE_INFO);
        }
        new LogEntity("", "没有该文件夹：" + Directory, LogEntity.LOG_TYPE_ERROR);
        return;
    }

    /**
     * 获取队列
     */
    public void TurnOnSendFile() {
        new Thread(() -> {
            while (true){
                if (!SystemCache.isEmpty()) {
                    ThreadPoolTaskExecutor executor = NetDiskThreadPool.executor();
                    Map<String, Object> map = SystemCache.get();
                    if (executor.getActiveCount() > 8){
                        try {
                            map.wait(1200000);
                        } catch (InterruptedException e) {
                            run(map);
                        }
                    }
                    run(map);

                }else {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }, "获取缓存文件map").start();
    }

    /**
     * 执行发送视频文件
     */
    public void run(Map<String,Object> map){
        String name = (String) map.get("name");
        Long size = (Long) map.get("size");
        String parent = (String) map.get("parent");
        List<FileAndDigsted> digsteds = (List<FileAndDigsted>) map.get("fileList");
        netDiskService.goSend(name, parent, size, digsteds);
    }
}

