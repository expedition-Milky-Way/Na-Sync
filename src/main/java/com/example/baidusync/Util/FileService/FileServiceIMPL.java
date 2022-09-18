package com.example.baidusync.Util.FileService;

import cn.hutool.crypto.SecureUtil;
import com.example.baidusync.Util.FileAndDigsted;
import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import com.example.baidusync.core.SystemCache;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 杨 名 (字 露煊)
 */
@Service
public class FileServiceIMPL implements FileService{


    /**
     * 开始扫描文件
     */
    @Override
    public void goBackup(String zipPath,String filePath,String password){
        ScanFileUtil scanFileUtil = new ScanFileUtil(zipPath,password);
        scanFileUtil.read(new File(filePath).listFiles());
    }

    /**
     * 计算MD5
     */
    @Override
    public void computedMD5(String name,File Directory){
        File[]  files = Directory.listFiles();
        List<FileAndDigsted> mdMessage = new ArrayList<>();
        for (File item : files){
            String md5 = SecureUtil.md5(item);
            FileAndDigsted fileAndDigsted = new FileAndDigsted();
            fileAndDigsted.setPathAndName(name);
            fileAndDigsted.setDigsted(md5);
            mdMessage.add(fileAndDigsted);
        }
        String parentName = name.split("/")[name.length() -1];
        SystemCache.set(parentName,mdMessage);

    }
}
