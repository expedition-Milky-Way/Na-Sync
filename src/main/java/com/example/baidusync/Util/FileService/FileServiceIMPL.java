package com.example.baidusync.Util.FileService;

import com.example.baidusync.Util.FileUtil.ScanFileUtil;
import org.springframework.stereotype.Service;

import java.io.File;

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
}
