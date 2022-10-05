package com.example.baidusync.Util.FileService;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.core.SystemCache;
import org.apache.commons.io.FileUtils;
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
            LogEntity log = new LogEntity("", "md5计算：" + Directory, LogEntity.LOG_TYPE_INFO);
            LogExecutor.addSysLogQueue(log);
            File[] files = Directory.listFiles();
            FileAndDigsted fileAndDigsted = new FileAndDigsted();
            Map<String, Object> setMap = new HashMap<>();
            List<FileAndDigsted> fileAndDigstedList = new ArrayList<>();
            setMap.put("name", name);
            setMap.put("size", fileSize);
            setMap.put("tempPath",Directory.getPath());
            setMap.put("parent", parent);
            for (int i = 0; i < files.length; i++) {
                fileAndDigsted.setIndex(i);
                //获取第i个file的size
                fileAndDigsted.setSize(FileUtils.sizeOf(files[i]));
                fileAndDigsted.setName(files[i].getName());
                fileAndDigsted.setPath(files[i].getPath());
                fileAndDigsted.setParentName(files[i].getParent());
                fileAndDigsted.setDigsted(SecureUtil.md5(files[i]));
                fileAndDigstedList.add(fileAndDigsted);
            }
            setMap.put("fileList", fileAndDigstedList);
            SystemCache.set(setMap);
           LogExecutor.addSysLogQueue(new LogEntity("", "md5计算完毕，进入等待队列：" + Directory, LogEntity.LOG_TYPE_INFO));
        }else{
            LogExecutor.addSysLogQueue(
                    new LogEntity("", "没有该文件夹：" + Directory, LogEntity.LOG_TYPE_ERROR));
            return;
        }

    }


}

