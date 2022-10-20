package com.example.baidusync.Util.FileService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.Util.NetDiskSync.RequestNetDiskService;
import com.example.baidusync.Util.SystemLog.LogEntity;
import com.example.baidusync.Util.SystemLog.LogExecutor;
import com.example.baidusync.Util.TempFileService.TempfileImpl;
import com.example.baidusync.core.Bean.SysConst;
import com.example.baidusync.core.SendQueue;
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




    /**
     * 计算MD5，并扫描当前缓存临时文件夹下面的每一个tempfile
     */
    @Override
    public void computedMD5(String name, File Directory, Long fileSize, String parent) {
        LogEntity log = new LogEntity("", "md5计算：" + Directory, LogEntity.LOG_TYPE_INFO);
        LogExecutor.addSysLogQueue(log);
        File[] files = Directory.listFiles();
        FileAndDigsted fileAndDigsted = new FileAndDigsted();
        Map<String, Object> setMap = new HashMap<>();
        List<FileAndDigsted> fileAndDigstedList = new ArrayList<>();
        setMap.put("name", name);
        setMap.put("size", fileSize);
        setMap.put("tempPath", Directory.getPath());
        setMap.put("parent", parent);
        if (fileSize <= SysConst.getMinSize()) {
            BeanUtil.copyProperties(computedMD5(name, Directory, parent), fileAndDigsted);
        } else {
            if (Directory.exists()) {
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
            } else {
                LogExecutor.addSysLogQueue(
                        new LogEntity("", "没有该文件夹：" + Directory, LogEntity.LOG_TYPE_ERROR));
            }
        }
        setMap.put("fileList", fileAndDigstedList);
        SendQueue.set(setMap);
        LogExecutor.addSysLogQueue(new LogEntity("", "md5计算完毕，进入等待队列：" + Directory, LogEntity.LOG_TYPE_INFO));
    }

    /**
     * 如果这个文件小于百度网盘最小分片。直接计算MD5
     * @param name
     * @param Directory
     * @param parent
     * @return
     */
    private FileAndDigsted computedMD5(String name, File Directory, String parent) {
        FileAndDigsted fileAndDigsted = new FileAndDigsted();
        fileAndDigsted.setIndex(0);
        fileAndDigsted.setName(name);
        fileAndDigsted.setPath(Directory.getPath());
        fileAndDigsted.setParentName(parent);
        fileAndDigsted.setDigsted(SecureUtil.md5(Directory));
        return fileAndDigsted;
    }


}

